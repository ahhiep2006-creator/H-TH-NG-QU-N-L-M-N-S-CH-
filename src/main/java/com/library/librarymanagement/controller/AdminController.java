package com.library.librarymanagement.controller;

import com.library.librarymanagement.model.Book;
import com.library.librarymanagement.model.BorrowTicketDetail;
import com.library.librarymanagement.repository.BookRepository;
import com.library.librarymanagement.repository.BorrowTicketDetailRepository;
import com.library.librarymanagement.repository.BorrowTicketRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class AdminController {

    private static final String ADMIN_SESSION_ATTRIBUTE = "adminLoggedIn";
    private static final String CSRF_SESSION_ATTRIBUTE = "adminCsrfToken";

    private final BookRepository bookRepository;
    private final BorrowTicketRepository borrowTicketRepository;
    private final BorrowTicketDetailRepository borrowTicketDetailRepository;
    private final String adminUsername;
    private final String adminPassword;

    public AdminController(
            BookRepository bookRepository,
            BorrowTicketRepository borrowTicketRepository,
            BorrowTicketDetailRepository borrowTicketDetailRepository,
            @Value("${library.admin.username:24100072@st.phenikaa-uni.edu.vn}") String adminUsername,
            @Value("${library.admin.password:admin}") String adminPassword) {
        this.bookRepository = bookRepository;
        this.borrowTicketRepository = borrowTicketRepository;
        this.borrowTicketDetailRepository = borrowTicketDetailRepository;
        this.adminUsername = adminUsername == null || adminUsername.isBlank() ? "24100072@st.phenikaa-uni.edu.vn" : adminUsername;
        this.adminPassword = adminPassword == null || adminPassword.isBlank() ? "admin" : adminPassword;
    }

    @GetMapping("/admin/login")
    public String loginPage(
            @RequestParam(required = false, defaultValue = "") String error,
            HttpSession session,
            Model model) {
        if (isAdmin(session)) {
            return "redirect:/";
        }
        if (!error.isBlank()) {
            model.addAttribute("errorMessage", error);
        }
        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String login(
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "") String password,
            HttpServletRequest request,
            Model model) {
        if (!matches(username, adminUsername) || !matches(password, adminPassword)) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng.");
            return "admin-login";
        }

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(ADMIN_SESSION_ATTRIBUTE, true);
        session.setAttribute(CSRF_SESSION_ATTRIBUTE, UUID.randomUUID().toString());
        return "redirect:/";
    }

    @GetMapping("/admin/statistics")
    public String statistics(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        List<BorrowTicketDetail> details = borrowTicketDetailRepository.findAll();
        Map<String, Integer> borrowCounts = new HashMap<>();
        int totalBorrowedCopies = 0;
        for (BorrowTicketDetail detail : details) {
            String bookId = detail.getBook().getId();
            int quantity = detail.getQuantity();
            borrowCounts.put(bookId, borrowCounts.getOrDefault(bookId, 0) + quantity);
            totalBorrowedCopies += quantity;
        }

        List<Book> books = bookRepository.findAll();
        int maximumBorrowCount = books.stream()
                .mapToInt(book -> borrowCounts.getOrDefault(book.getId(), 0))
                .max()
                .orElse(0);
        List<Map<String, Object>> bookStatistics = new ArrayList<>();
        books.stream()
                .sorted(Comparator.comparingInt(
                        (Book book) -> borrowCounts.getOrDefault(book.getId(), 0)
                ).reversed().thenComparing((Book book) -> book.getTitle()))
                .forEach(book -> {
                    int count = borrowCounts.getOrDefault(book.getId(), 0);
                    Map<String, Object> statistic = new HashMap<>();
                    statistic.put("book", book);
                    statistic.put("borrowCount", count);
                    statistic.put("percent", maximumBorrowCount == 0 ? 0 : count * 100 / maximumBorrowCount);
                    bookStatistics.add(statistic);
                });

        long borrowingCount = borrowTicketRepository.countByStatus("BORROWING");
        long returnedCount = borrowTicketRepository.countByStatus("RETURNED");
        long trackedTicketCount = borrowingCount + returnedCount;
        int returnedPercentage = trackedTicketCount == 0
                ? 0 : (int) (returnedCount * 100 / trackedTicketCount);

        model.addAttribute("bookStatistics", bookStatistics);
        model.addAttribute("totalBorrowedCopies", totalBorrowedCopies);
        model.addAttribute("borrowingCount", borrowingCount);
        model.addAttribute("returnedCount", returnedCount);
        model.addAttribute("totalTicketCount", borrowTicketRepository.count());
        model.addAttribute("returnedPercentage", returnedPercentage);
        model.addAttribute("borrowingPercentage", trackedTicketCount == 0 ? 0 : 100 - returnedPercentage);
        return "admin-statistics";
    }

    @GetMapping("/admin/books")
    public String addBookPage(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        if (!model.containsAttribute("bookForm")) {
            model.addAttribute("bookForm", new BookForm());
        }
        model.addAttribute("csrfToken", csrfToken(session));
        return "admin-books";
    }

    @PostMapping("/admin/books")
    public String addBook(
            @Valid @ModelAttribute("bookForm") BookForm form,
            BindingResult bindingResult,
            @RequestParam(defaultValue = "") String csrfToken,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        if (!matches(csrfToken, csrfToken(session))) {
            bindingResult.reject("admin.csrf", "Phiên làm việc không hợp lệ. Hãy tải lại trang và thử lại.");
        }

        String bookId = form.getId() == null ? "" : form.getId().trim();
        if (bindingResult.hasErrors()) {
            model.addAttribute("csrfToken", csrfToken(session));
            return "admin-books";
        }
        if (bookRepository.existsById(bookId)) {
            bindingResult.rejectValue("id", "book.id.duplicate", "Mã sách này đã tồn tại.");
            model.addAttribute("csrfToken", csrfToken(session));
            return "admin-books";
        }

        Book book = new Book(
                bookId,
                form.getTitle().trim(),
                form.getAuthor().trim(),
                form.getCategory() == null || form.getCategory().isBlank()
                        ? null : form.getCategory().trim(),
                form.getQuantity(),
                form.getValue()
        );
        bookRepository.save(book);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sách vào kho.");
        return "redirect:/admin/books";
    }

    @PostMapping("/admin/logout")
    public String logout(
            @RequestParam(defaultValue = "") String csrfToken,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (isAdmin(session) && matches(csrfToken, csrfToken(session))) {
            session.invalidate();
        }
        return "redirect:/admin/login";
    }

    private boolean isAdmin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_ATTRIBUTE));
    }

    private String csrfToken(HttpSession session) {
        Object token = session.getAttribute(CSRF_SESSION_ATTRIBUTE);
        if (token instanceof String value) {
            return value;
        }
        String newToken = UUID.randomUUID().toString();
        session.setAttribute(CSRF_SESSION_ATTRIBUTE, newToken);
        return newToken;
    }

    private boolean matches(String provided, String expected) {
        return MessageDigest.isEqual(
                provided.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8)
        );
    }
}

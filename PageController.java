package com.library.librarymanagement.controller;

import com.library.librarymanagement.model.Book;
import com.library.librarymanagement.model.Reader;
import com.library.librarymanagement.repository.BookRepository;
import com.library.librarymanagement.repository.BorrowTicketRepository;
import com.library.librarymanagement.repository.ReaderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class PageController {

    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final BorrowTicketRepository borrowTicketRepository;

    public PageController(BookRepository bookRepository,
                          ReaderRepository readerRepository,
                          BorrowTicketRepository borrowTicketRepository) {
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.borrowTicketRepository = borrowTicketRepository;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        List<Book> books = bookRepository.findAll();

        // Sắp xếp sách theo lượt mượn để đưa vào bảng xếp hạng nổi bật trong tháng
        List<Book> rankedBooks = books.stream()
                .sorted(Comparator.comparingInt((Book book) -> book.getBorrowCount()).reversed())
                .limit(5)
                .toList();

        Book spotlightBook = rankedBooks.isEmpty() ? null : rankedBooks.get(0);

        model.addAttribute("books", books);
        model.addAttribute("featuredBooks", rankedBooks);
        model.addAttribute("spotlightBook", spotlightBook);
        model.addAttribute("bookCount", books.size());
        model.addAttribute(
                "copyCount",
                books.stream().mapToInt(book -> book.getQuantity()).sum()
        );
        model.addAttribute("readerCount", readerRepository.count());
        model.addAttribute(
                "activeTicketCount",
                borrowTicketRepository.countByStatus("BORROWING")
        );
        return "index";
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        // Lấy độc giả mẫu RD001 (hoặc đầu tiên) để hiển thị thông tin trang cá nhân
        Reader reader = readerRepository.findById("RD001")
                .or(() -> readerRepository.findAll().stream().findFirst())
                .orElse(null);

        model.addAttribute("reader", reader);
        return "profile";
    }

    @GetMapping("/rules")
    public String rules() {
        return "rules";
    }

}
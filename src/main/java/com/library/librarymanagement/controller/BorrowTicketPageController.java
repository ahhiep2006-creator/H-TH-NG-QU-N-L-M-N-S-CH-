package com.library.librarymanagement.controller;

import com.library.librarymanagement.exception.LibraryBusinessException;
import com.library.librarymanagement.model.BorrowTicket;
import com.library.librarymanagement.model.BorrowTicketDetail;
import com.library.librarymanagement.model.ReturnRecord;
import com.library.librarymanagement.repository.BorrowTicketDetailRepository;
import com.library.librarymanagement.repository.BorrowTicketRepository;
import com.library.librarymanagement.service.BorrowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Controller
public class BorrowTicketPageController {

    private final BorrowTicketRepository borrowTicketRepository;
    private final BorrowTicketDetailRepository borrowTicketDetailRepository;
    private final BorrowService borrowService;

    public BorrowTicketPageController(
            BorrowTicketRepository borrowTicketRepository,
            BorrowTicketDetailRepository borrowTicketDetailRepository,
            BorrowService borrowService) {

        this.borrowTicketRepository = borrowTicketRepository;
        this.borrowTicketDetailRepository = borrowTicketDetailRepository;
        this.borrowService = borrowService;
    }

    @GetMapping("/tickets")
    public String tickets(HttpSession session, Model model) {
        List<BorrowTicket> tickets = borrowTicketRepository.findAll();
        List<BorrowTicketItemDto> ticketDtos = toDtos(tickets);

        model.addAttribute("tickets", ticketDtos);
        model.addAttribute("isAdmin", isAdmin(session));
        if (isAdmin(session)) {
            model.addAttribute("csrfToken", session.getAttribute("adminCsrfToken"));
        }
        return "tickets";
    }

    @PostMapping("/tickets/pickup")
    public String pickupTicket(
            @RequestParam String ticketId,
            @RequestParam(defaultValue = "") String csrfToken,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        if (!hasValidCsrfToken(csrfToken, session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên làm việc hết hạn. Hãy tải lại trang rồi thử lại.");
            return "redirect:/tickets";
        }
        try {
            borrowService.markPickedUp(ticketId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xác nhận bạn đọc nhận sách thành công!");
        } catch (LibraryBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tickets";
    }

    @PostMapping("/tickets/revert")
    public String revertTicket(
            @RequestParam String ticketId,
            @RequestParam(defaultValue = "") String csrfToken,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        if (!hasValidCsrfToken(csrfToken, session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên làm việc hết hạn. Hãy tải lại trang rồi thử lại.");
            return "redirect:/tickets";
        }
        try {
            borrowService.revertPickup(ticketId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hoàn tác trạng thái yêu cầu mượn sách!");
        } catch (LibraryBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tickets";
    }

    @PostMapping("/tickets/return")
    public String returnBook(
            @RequestParam String ticketId,
            @RequestParam(defaultValue = "") String csrfToken,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        if (!hasValidCsrfToken(csrfToken, session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên làm việc hết hạn. Hãy tải lại trang rồi thử lại.");
            return "redirect:/tickets";
        }

        try {
            ReturnRecord record = borrowService.returnBook(ticketId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Trả sách thành công! Tiền phạt quá hạn: "
                            + record.getFineAmount()
                            + " VNĐ"
            );

        } catch (LibraryBusinessException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/tickets";
    }

    private boolean isAdmin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
    }

    private boolean hasValidCsrfToken(String provided, HttpSession session) {
        Object stored = session.getAttribute("adminCsrfToken");
        return stored instanceof String expected && MessageDigest.isEqual(
                provided.getBytes(StandardCharsets.UTF_8), expected.getBytes(StandardCharsets.UTF_8));
    }

    private List<BorrowTicketItemDto> toDtos(List<BorrowTicket> tickets) {
        List<BorrowTicketItemDto> dtos = new ArrayList<>();
        for (BorrowTicket ticket : tickets) {
            List<BorrowTicketDetail> details = borrowTicketDetailRepository.findByBorrowTicketId(ticket.getId());
            String bookTitle = "Sách thư viện";
            String bookImageUrl = "/images/books/default-book.svg";
            int quantity = 1;

            if (!details.isEmpty()) {
                BorrowTicketDetail detail = details.get(0);
                if (detail.getBook() != null) {
                    bookTitle = detail.getBook().getTitle();
                    bookImageUrl = detail.getBook().getImageUrl();
                }
                quantity = detail.getQuantity();
            }

            dtos.add(new BorrowTicketItemDto(
                    ticket.getId(),
                    ticket.getReader().getFullName(),
                    ticket.getReader().getId(),
                    bookTitle,
                    bookImageUrl,
                    quantity,
                    ticket.getBorrowDate(),
                    ticket.getDueDate(),
                    ticket.getStatus()
            ));
        }
        return dtos;
    }
}
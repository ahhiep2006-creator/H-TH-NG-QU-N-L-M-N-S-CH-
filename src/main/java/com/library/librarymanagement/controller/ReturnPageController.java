package com.library.librarymanagement.controller;

import com.library.librarymanagement.model.ReturnRecord;
import com.library.librarymanagement.exception.LibraryBusinessException;
import com.library.librarymanagement.service.BorrowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReturnPageController {

    private final BorrowService borrowService;

    public ReturnPageController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping("/return")
    public String returnPage() {
        return "return";
    }

    @PostMapping("/return")
    public String returnBook(
            @RequestParam String ticketId,
            Model model) {

        try {
            ReturnRecord record = borrowService.returnBook(ticketId);

            model.addAttribute(
                    "successMessage",
                    "Trả sách thành công! Tiền phạt: "
                            + record.getFineAmount()
                            + " VNĐ"
            );

        } catch (LibraryBusinessException e) {

            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "return";
    }
}
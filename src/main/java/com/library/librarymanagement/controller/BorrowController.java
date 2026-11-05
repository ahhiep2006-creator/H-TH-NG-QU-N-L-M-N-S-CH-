package com.library.librarymanagement.controller;

import com.library.librarymanagement.model.BorrowTicket;
import com.library.librarymanagement.model.ReturnRecord;
import com.library.librarymanagement.service.BorrowService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping
    public BorrowTicket borrowBook(
            @RequestParam String readerId,
            @RequestParam String bookId,
            @RequestParam int quantity) {

        return borrowService.borrowBook(readerId, bookId, quantity);
    }

    @PostMapping("/return")
    public ReturnRecord returnBook(
            @RequestParam String ticketId) {

        return borrowService.returnBook(ticketId);
    }
}
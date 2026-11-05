package com.library.librarymanagement.controller;

import com.library.librarymanagement.model.Book;
import com.library.librarymanagement.model.BorrowTicket;
import com.library.librarymanagement.model.Reader;
import com.library.librarymanagement.exception.LibraryBusinessException;
import com.library.librarymanagement.repository.BookRepository;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.service.BorrowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BorrowPageController {

    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final BorrowService borrowService;

    public BorrowPageController(BookRepository bookRepository,
                                ReaderRepository readerRepository,
                                BorrowService borrowService) {
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.borrowService = borrowService;
    }

    @GetMapping("/borrow")
    public String borrowPage(
            @RequestParam(required = false) String bookId,
            Model model) {

        loadData(model);
        model.addAttribute("selectedBookId", bookId);

        return "borrow";
    }

    @PostMapping("/borrow")
    public String borrowBook(
            @RequestParam String readerId,
            @RequestParam String bookId,
            @RequestParam int quantity,
            Model model) {

        try {

            BorrowTicket ticket = borrowService.borrowBook(
                    readerId,
                    bookId,
                    quantity
            );

            model.addAttribute(
                    "successMessage",
                    "Mượn sách thành công! Mã phiếu: " + ticket.getId()
            );

        } catch (LibraryBusinessException e) {

            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        loadData(model);

        return "borrow";
    }

    private void loadData(Model model) {

        List<Book> books = bookRepository.findAll();
        List<Reader> readers = readerRepository.findAll();

        model.addAttribute("books", books);
        model.addAttribute("readers", readers);
    }
}
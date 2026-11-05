package com.library.librarymanagement.controller;

import com.library.librarymanagement.model.Book;
import com.library.librarymanagement.repository.BookRepository;
import com.library.librarymanagement.service.BookCsvExportService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@Controller
public class BookController {

    private final BookRepository bookRepository;
    private final BookCsvExportService bookCsvExportService;

    public BookController(BookRepository bookRepository,
                          BookCsvExportService bookCsvExportService) {
        this.bookRepository = bookRepository;
        this.bookCsvExportService = bookCsvExportService;
    }

    @GetMapping("/books")
    public String books(
            @RequestParam(required = false, defaultValue = "") String keyword,
            Model model) {

        List<Book> books;

        if (keyword.trim().isEmpty()) {
            books = bookRepository.findAll();
        } else {
            books = bookRepository
                    .findByIdContainingIgnoreCaseOrTitleContainingIgnoreCase(
                            keyword,
                            keyword
                    );
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);

        return "books";
    }

    @GetMapping("/books/export")
    public ResponseEntity<Resource> exportBooks() {
        Path csvPath = bookCsvExportService.exportBooks();
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("books.csv", StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(new FileSystemResource(csvPath));
    }
}
package com.library.librarymanagement.controller;

import com.library.librarymanagement.model.Reader;
import com.library.librarymanagement.repository.ReaderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ReaderController {

    private final ReaderRepository readerRepository;

    public ReaderController(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @GetMapping("/readers")
    public String readers(Model model) {

        List<Reader> readers = readerRepository.findAll();

        model.addAttribute("readers", readers);

        return "readers";
    }
}
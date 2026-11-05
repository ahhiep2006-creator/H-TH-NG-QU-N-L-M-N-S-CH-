package com.library.librarymanagement.service;

import com.library.librarymanagement.model.Book;
import com.library.librarymanagement.repository.BookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class BookCsvExportService {

    private final BookRepository bookRepository;
    private final Path exportDirectory;

    public BookCsvExportService(
            BookRepository bookRepository,
            @Value("${library.export.directory:exports}") String exportDirectory) {
        this.bookRepository = bookRepository;
        this.exportDirectory = Path.of(exportDirectory);
    }

    public Path exportBooks() {
        Path exportFile = exportDirectory.resolve("books.csv");
        try {
            Files.createDirectories(exportDirectory);
            try (BufferedWriter writer = Files.newBufferedWriter(
                    exportFile, StandardCharsets.UTF_8)) {
                writer.write("Ma sach,Ten sach,Tac gia,The loai,So luong,Gia tri");
                writer.newLine();
                for (Book book : bookRepository.findAll()) {
                    writer.write(String.join(",",
                            csv(book.getId()),
                            csv(book.getTitle()),
                            csv(book.getAuthor()),
                            csv(book.getCategory()),
                            csv(Integer.toString(book.getQuantity())),
                            csv(book.getValue().toPlainString())
                    ));
                    writer.newLine();
                }
            }
            return exportFile;
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Không thể tạo file CSV danh sách sách tại "
                            + exportFile.toAbsolutePath(),
                    e
            );
        }
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}

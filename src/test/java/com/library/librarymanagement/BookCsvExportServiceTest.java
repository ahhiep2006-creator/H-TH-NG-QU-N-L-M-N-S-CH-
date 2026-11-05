package com.library.librarymanagement;

import com.library.librarymanagement.model.Book;
import com.library.librarymanagement.repository.BookRepository;
import com.library.librarymanagement.service.BookCsvExportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookCsvExportServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void writesBookDataAsEscapedUtf8Csv() throws Exception {
        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.findAll()).thenReturn(List.of(
                new Book("B001", "Java, co ban", "Tac gia \"A\"",
                        "Cong nghe", 3, new BigDecimal("125000.00"))
        ));
        BookCsvExportService exportService = new BookCsvExportService(
                bookRepository,
                temporaryDirectory.toString()
        );

        Path exportedFile = exportService.exportBooks();
        List<String> lines = Files.readAllLines(exportedFile);

        assertEquals(2, lines.size());
        assertEquals(
                "\"B001\",\"Java, co ban\",\"Tac gia \"\"A\"\"\","
                        + "\"Cong nghe\",\"3\",\"125000.00\"",
                lines.get(1)
        );
    }
}

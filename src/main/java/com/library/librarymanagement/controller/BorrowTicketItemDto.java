package com.library.librarymanagement.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BorrowTicketItemDto {
    private String id;
    private String readerName;
    private String readerId;
    private String bookTitle;
    private String bookImageUrl;
    private int quantity;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private String status;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public BorrowTicketItemDto() {
    }

    public BorrowTicketItemDto(String id, String readerName, String readerId,
                               String bookTitle, String bookImageUrl, int quantity,
                               LocalDate borrowDate, LocalDate dueDate, String status) {
        this.id = id;
        this.readerName = readerName;
        this.readerId = readerId;
        this.bookTitle = bookTitle;
        this.bookImageUrl = bookImageUrl;
        this.quantity = quantity;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getReaderName() {
        return readerName;
    }

    public String getReaderId() {
        return readerId;
    }

    public String getBookTitle() {
        return bookTitle != null ? bookTitle : "Sách thư viện";
    }

    public String getBookImageUrl() {
        return bookImageUrl != null && !bookImageUrl.isBlank() ? bookImageUrl : "/images/books/default-book.svg";
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public String getBorrowDateFormatted() {
        return borrowDate != null ? borrowDate.format(FORMATTER) : "";
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getDueDateFormatted() {
        return dueDate != null ? dueDate.format(FORMATTER) : "";
    }

    public String getStatus() {
        return status;
    }

    public String getStatusLabel() {
        if ("RETURNED".equalsIgnoreCase(status)) return "Đã trả";
        if ("WAITING_PICKUP".equalsIgnoreCase(status) || "PENDING".equalsIgnoreCase(status)) return "Chờ lấy sách";
        return "Đã lấy sách";
    }

    public String getStatusClass() {
        if ("RETURNED".equalsIgnoreCase(status)) return "status-returned";
        if ("WAITING_PICKUP".equalsIgnoreCase(status) || "PENDING".equalsIgnoreCase(status)) return "status-waiting";
        return "status-borrowing";
    }
}

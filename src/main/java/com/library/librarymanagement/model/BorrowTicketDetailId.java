package com.library.librarymanagement.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BorrowTicketDetailId implements Serializable {

    private String ticketId;

    private String bookId;

    public BorrowTicketDetailId() {
    }

    public BorrowTicketDetailId(String ticketId, String bookId) {
        this.ticketId = ticketId;
        this.bookId = bookId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BorrowTicketDetailId)) {
            return false;
        }

        BorrowTicketDetailId that = (BorrowTicketDetailId) o;

        return Objects.equals(ticketId, that.ticketId)
                && Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId, bookId);
    }
}
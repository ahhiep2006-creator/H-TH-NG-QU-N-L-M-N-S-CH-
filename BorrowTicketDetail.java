package com.library.librarymanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "borrow_ticket_details")
public class BorrowTicketDetail {

    @EmbeddedId
    private BorrowTicketDetailId id;

    @ManyToOne
    @MapsId("ticketId")
    @JoinColumn(name = "ticket_id", nullable = false)
    private BorrowTicket borrowTicket;

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public BorrowTicketDetail() {
    }

    public BorrowTicketDetail(BorrowTicket borrowTicket,
                              Book book,
                              int quantity) {

        this.borrowTicket = borrowTicket;
        this.book = book;
        this.quantity = quantity;

        this.id = new BorrowTicketDetailId(
                borrowTicket.getId(),
                book.getId()
        );
    }

    public BorrowTicketDetailId getId() {
        return id;
    }

    public void setId(BorrowTicketDetailId id) {
        this.id = id;
    }

    public BorrowTicket getBorrowTicket() {
        return borrowTicket;
    }

    public void setBorrowTicket(BorrowTicket borrowTicket) {
        this.borrowTicket = borrowTicket;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

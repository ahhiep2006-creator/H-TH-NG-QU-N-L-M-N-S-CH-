package com.library.librarymanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "return_records")
public class ReturnRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ticket_id", nullable = false, unique = true)
    private BorrowTicket borrowTicket;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(name = "fine_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal fineAmount;

    public ReturnRecord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BorrowTicket getBorrowTicket() {
        return borrowTicket;
    }

    public void setBorrowTicket(BorrowTicket borrowTicket) {
        this.borrowTicket = borrowTicket;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }
}
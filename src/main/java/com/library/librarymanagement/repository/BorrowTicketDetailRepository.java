package com.library.librarymanagement.repository;

import com.library.librarymanagement.model.BorrowTicketDetail;
import com.library.librarymanagement.model.BorrowTicketDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowTicketDetailRepository
        extends JpaRepository<BorrowTicketDetail, BorrowTicketDetailId> {

    List<BorrowTicketDetail> findByBorrowTicketId(String ticketId);
}
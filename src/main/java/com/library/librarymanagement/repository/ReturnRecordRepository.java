package com.library.librarymanagement.repository;

import com.library.librarymanagement.model.ReturnRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnRecordRepository
        extends JpaRepository<ReturnRecord, Long> {

    Optional<ReturnRecord> findByBorrowTicketId(String ticketId);
}
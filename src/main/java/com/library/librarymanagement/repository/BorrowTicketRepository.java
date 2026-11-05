package com.library.librarymanagement.repository;

import com.library.librarymanagement.model.BorrowTicket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BorrowTicketRepository extends JpaRepository<BorrowTicket, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ticket from BorrowTicket ticket where ticket.id = :id")
    Optional<BorrowTicket> findByIdForUpdate(@Param("id") String id);

    List<BorrowTicket> findByReaderId(String readerId);

    long countByStatus(String status);

    List<BorrowTicket> findByReaderIdAndStatusIn(String readerId, List<String> statuses);

    List<BorrowTicket> findByReaderIdAndStatus(
            String readerId,
            String status
    );
}
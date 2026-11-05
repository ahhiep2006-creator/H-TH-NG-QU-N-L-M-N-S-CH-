package com.library.librarymanagement.repository;

import com.library.librarymanagement.model.Reader;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select reader from Reader reader where reader.id = :id")
    Optional<Reader> findByIdForUpdate(@Param("id") String id);
}
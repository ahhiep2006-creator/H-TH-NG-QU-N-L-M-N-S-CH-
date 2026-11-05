package com.library.librarymanagement.repository;

import com.library.librarymanagement.model.SupportFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportFeedbackRepository extends JpaRepository<SupportFeedback, Long> {
    List<SupportFeedback> findAllByOrderByCreatedAtDesc();
    List<SupportFeedback> findAllByEmailIgnoreCaseOrderByCreatedAtDesc(String email);
    long countByResolvedFalse();
}

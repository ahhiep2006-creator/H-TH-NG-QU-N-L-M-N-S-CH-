
package com.library.librarymanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "support_feedback")
public class SupportFeedback {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_RESPONDED = "RESPONDED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_name", nullable = false, length = 100)
    private String studentName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, length = 3000)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean resolved = false;

    @Column(name = "admin_reply", length = 3000)
    private String adminReply;

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    @Column(length = 24)
    private String status = STATUS_NEW;

    protected SupportFeedback() {
    }

    public SupportFeedback(
            String studentName,
            String email,
            String category,
            String message
    ) {
        this.studentName = studentName;
        this.email = email;
        this.category = category;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.resolved = false;
        this.status = STATUS_NEW;
    }

    @PrePersist
    void setCreatedAtIfMissing() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null || status.isBlank()) {
            status = STATUS_NEW;
        }
    }

    public Long getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getEmail() {
        return email;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtFormatted() {
        return createdAt == null
                ? ""
                : createdAt.format(DISPLAY_FORMAT);
    }

    public boolean isResolved() {
        return resolved;
    }

    public String getAdminReply() {
        return adminReply;
    }

    public LocalDateTime getRepliedAt() {
        return repliedAt;
    }

    public String getRepliedAtFormatted() {
        return repliedAt == null
                ? ""
                : repliedAt.format(DISPLAY_FORMAT);
    }

    public String getStatus() {
        return status == null || status.isBlank()
                ? STATUS_NEW
                : status;
    }

    public String getStatusLabel() {
        return switch (getStatus()) {
            case STATUS_IN_PROGRESS -> "Đang xử lý";
            case STATUS_RESPONDED -> "Đã phản hồi";
            default -> "Đã tiếp nhận";
        };
    }

    public void markInProgress() {
        if (resolved) {
            return;
        }

        if (adminReply != null && !adminReply.isBlank()) {
            return;
        }

        status = STATUS_IN_PROGRESS;
    }

    public void reply(String adminReply) {
        if (adminReply == null || adminReply.isBlank()) {
            throw new IllegalArgumentException(
                    "Nội dung phản hồi không được để trống"
            );
        }

        if (resolved) {
            throw new IllegalStateException(
                    "Yêu cầu hỗ trợ đã được giải quyết"
            );
        }

        this.adminReply = adminReply.trim();
        this.repliedAt = LocalDateTime.now();
        this.status = STATUS_RESPONDED;
        this.resolved = true;
    }
}

package com.library.librarymanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "priority_student_readers")
@PrimaryKeyJoinColumn(name = "id")
public class PriorityStudentReader extends Reader {

    public PriorityStudentReader() {
    }

    public PriorityStudentReader(String id, String fullName, String phone) {
        super(id, fullName, phone, "PRIORITY");
    }

    @Override
    public int getMaxBorrowQuantity() {
        return 5;
    }
}
package com.library.librarymanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_readers")
@PrimaryKeyJoinColumn(name = "id")
public class StudentReader extends Reader {

    public StudentReader() {
    }

    public StudentReader(String id, String fullName, String phone) {
        super(id, fullName, phone, "NORMAL");
    }

    public StudentReader(String id, String fullName, String phone, String email, String address, String studentCode) {
        super(id, fullName, phone, "NORMAL");
        setEmail(email);
        setAddress(address);
        setStudentCode(studentCode);
    }

    @Override
    public int getMaxBorrowQuantity() {
        return 3;
    }
}
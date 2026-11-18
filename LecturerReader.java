package com.library.librarymanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "lecturer_readers")
@PrimaryKeyJoinColumn(name = "id")
public class LecturerReader extends Reader {

    public LecturerReader() {
    }

    public LecturerReader(String id, String fullName, String phone) {
        super(id, fullName, phone, "LECTURER");
    }

    @Override
    public int getMaxBorrowQuantity() {
        return 10;
    }
}
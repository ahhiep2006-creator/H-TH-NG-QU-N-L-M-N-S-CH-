package com.library.librarymanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "readers")
@PrimaryKeyJoinColumn(name = "id")
public class Reader extends User {

    @Column(name = "reader_type", nullable = false, length = 20)
    private String readerType;

    protected Reader() {
    }

    public Reader(String id, String fullName, String phone, String readerType) {
        super(id, fullName, phone);
        this.readerType = readerType;
    }

    public String getReaderType() {
        return readerType;
    }

    public void setReaderType(String readerType) {
        this.readerType = readerType;
    }

    public String getReaderTypeLabel() {
        switch (readerType) {
            case "NORMAL":
                return "Sinh viên thường";
            case "PRIORITY":
                return "Sinh viên ưu tiên";
            case "LECTURER":
                return "Giảng viên";
            default:
                return "Chưa xác định";
        }
    }

    public int getMaxBorrowQuantity() {
        switch (readerType) {
            case "NORMAL":
                return 3;
            case "PRIORITY":
                return 5;
            case "LECTURER":
                return 10;
            default:
                return 0;
        }
    }
}
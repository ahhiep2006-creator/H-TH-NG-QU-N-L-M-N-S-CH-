package com.library.librarymanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "student_code", length = 50)
    private String studentCode;

    protected User() {
    }

    public User(String id, String fullName, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
    }

    public User(String id, String fullName, String phone, String email, String address, String studentCode) {
        this(id, fullName, phone);
        this.email = email;
        this.address = address;
        this.studentCode = studentCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email != null && !email.isBlank() ? email : "nam_2251220175@dau.edu.vn";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address != null && !address.isBlank() ? address : "Tp Hue";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStudentCode() {
        return studentCode != null && !studentCode.isBlank() ? studentCode : "2251220171";
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }
}
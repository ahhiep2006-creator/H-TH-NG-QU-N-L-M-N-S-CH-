package com.library.librarymanagement.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ReaderForm {

    private String id;

    @NotBlank(message = "Vui lòng nhập họ và tên.")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự.")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập số điện thoại.")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại phải bắt đầu bằng 0 và có 10-11 chữ số.")
    private String phone;

    @Email(message = "Email không đúng định dạng.")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự.")
    private String email;

    @Size(max = 200, message = "Địa chỉ không được vượt quá 200 ký tự.")
    private String address;

    @Size(max = 50, message = "Mã sinh viên không được vượt quá 50 ký tự.")
    private String studentCode;

    @NotBlank(message = "Vui lòng chọn loại bạn đọc.")
    @Pattern(regexp = "NORMAL|PRIORITY|LECTURER", message = "Loại bạn đọc không hợp lệ.")
    private String readerType = "NORMAL";

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getReaderType() { return readerType; }
    public void setReaderType(String readerType) { this.readerType = readerType; }
}

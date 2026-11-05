package com.library.librarymanagement.controller;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class BookForm {

    @NotBlank(message = "Vui lòng nhập mã sách.")
    @Size(max = 20, message = "Mã sách không được vượt quá 20 ký tự.")
    private String id;

    @NotBlank(message = "Vui lòng nhập tên sách.")
    @Size(max = 200, message = "Tên sách không được vượt quá 200 ký tự.")
    private String title;

    @NotBlank(message = "Vui lòng nhập tên tác giả.")
    @Size(max = 100, message = "Tên tác giả không được vượt quá 100 ký tự.")
    private String author;

    @Size(max = 100, message = "Thể loại không được vượt quá 100 ký tự.")
    private String category;

    @NotNull(message = "Vui lòng nhập số lượng.")
    @Min(value = 0, message = "Số lượng không được nhỏ hơn 0.")
    private Integer quantity;

    @NotNull(message = "Vui lòng nhập giá trị sách.")
    @DecimalMin(value = "0.00", message = "Giá trị sách không được nhỏ hơn 0.")
    @Digits(integer = 13, fraction = 2, message = "Giá trị tối đa 13 chữ số nguyên và 2 chữ số thập phân.")
    private BigDecimal value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}

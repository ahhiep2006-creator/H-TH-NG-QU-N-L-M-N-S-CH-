package com.library.librarymanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "books")
public class Book implements Borrowable {
    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "author", nullable = false, length = 100)
    private String author;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "`value`", nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    @Column(name = "publisher", length = 100)
    private String publisher;

    @Column(name = "pages")
    private Integer pages;

    @Column(name = "publish_year")
    private Integer publishYear;

    @Column(name = "cover_type", length = 50)
    private String coverType;

    @Column(name = "language", length = 50)
    private String language;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "summary", length = 2000)
    private String summary;

    @Column(name = "borrow_count")
    private Integer borrowCount;

    public Book() {
    }

    public Book(String id, String title, String author,
                String category, int quantity, BigDecimal value) {

        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
        this.value = value;
    }

    public Book(String id, String title, String author,
                String category, int quantity, BigDecimal value,
                String publisher, Integer pages, Integer publishYear,
                String coverType, String language, String imageUrl,
                String summary, Integer borrowCount) {

        this(id, title, author, category, quantity, value);
        this.publisher = publisher;
        this.pages = pages;
        this.publishYear = publishYear;
        this.coverType = coverType;
        this.language = language;
        this.imageUrl = imageUrl;
        this.summary = summary;
        this.borrowCount = borrowCount;
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getPublisher() {
        return publisher != null && !publisher.isBlank() ? publisher : "NXB Tri Thức";
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPages() {
        return pages != null && pages > 0 ? pages : 200;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public int getPublishYear() {
        return publishYear != null && publishYear > 0 ? publishYear : 2023;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    public String getCoverType() {
        return coverType != null && !coverType.isBlank() ? coverType : "Bìa mềm";
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }

    public String getLanguage() {
        return language != null && !language.isBlank() ? language : "Tiếng Việt";
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getImageUrl() {
        return imageUrl != null && !imageUrl.isBlank() ? imageUrl : "/images/books/default-book.svg";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSummary() {
        return summary != null && !summary.isBlank()
                ? summary
                : "Tác phẩm giá trị cung cấp kiến thức toàn diện và chuyên sâu, phù hợp cho sinh viên và độc giả quan tâm.";
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getBorrowCount() {
        return borrowCount != null ? borrowCount : 0;
    }

    public void setBorrowCount(Integer borrowCount) {
        this.borrowCount = borrowCount;
    }

    @Override
    public boolean isAvailable(int quantity) {
        return quantity > 0 && this.quantity >= quantity;
    }

    @Override
    public void borrow(int quantity) {
        if (!isAvailable(quantity)) {
            throw new IllegalArgumentException("Số lượng sách không đủ.");
        }

        this.quantity -= quantity;
    }

    @Override
    public void returnBook(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng trả phải lớn hơn 0.");
        }

        this.quantity += quantity;
    }
}
package com.library.librarymanagement.model;

public interface Borrowable {

    boolean isAvailable(int quantity);

    void borrow(int quantity);

    void returnBook(int quantity);
}

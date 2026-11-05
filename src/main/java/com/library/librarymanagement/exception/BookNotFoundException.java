package com.library.librarymanagement.exception;

public class BookNotFoundException extends LibraryBusinessException {

    public BookNotFoundException(String message) {
        super(message);
    }
}
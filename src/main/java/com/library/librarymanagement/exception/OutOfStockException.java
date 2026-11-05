package com.library.librarymanagement.exception;

public class OutOfStockException extends LibraryBusinessException {

    public OutOfStockException(String message) {
        super(message);
    }
}
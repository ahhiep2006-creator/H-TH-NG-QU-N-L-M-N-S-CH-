package com.library.librarymanagement.exception;

public class InvalidBorrowQuantityException extends LibraryBusinessException {

    public InvalidBorrowQuantityException(String message) {
        super(message);
    }
}
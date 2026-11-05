package com.library.librarymanagement.exception;

public class BorrowLimitExceededException extends LibraryBusinessException {

    public BorrowLimitExceededException(String message) {
        super(message);
    }
}
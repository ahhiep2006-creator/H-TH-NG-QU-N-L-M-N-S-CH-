package com.library.librarymanagement.exception;

public class InvalidBorrowTicketStatusException extends LibraryBusinessException {

    public InvalidBorrowTicketStatusException(String message) {
        super(message);
    }
}

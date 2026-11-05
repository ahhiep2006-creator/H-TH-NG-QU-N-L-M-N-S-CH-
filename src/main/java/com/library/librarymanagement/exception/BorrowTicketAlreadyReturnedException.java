package com.library.librarymanagement.exception;

public class BorrowTicketAlreadyReturnedException extends LibraryBusinessException {

    public BorrowTicketAlreadyReturnedException(String message) {
        super(message);
    }
}
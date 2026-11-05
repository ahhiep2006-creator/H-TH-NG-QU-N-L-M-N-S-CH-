package com.library.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookNotFound(BookNotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "BookNotFoundException");
        response.put("message", e.getMessage());
        return response;
    }

    @ExceptionHandler(OutOfStockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleOutOfStock(OutOfStockException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "OutOfStockException");
        response.put("message", e.getMessage());
        return response;
    }

    @ExceptionHandler(BorrowLimitExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBorrowLimit(BorrowLimitExceededException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "BorrowLimitExceededException");
        response.put("message", e.getMessage());
        return response;
    }

    @ExceptionHandler(InvalidBorrowQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidQuantity(InvalidBorrowQuantityException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "InvalidBorrowQuantityException");
        response.put("message", e.getMessage());
        return response;
    }

    @ExceptionHandler(ReaderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleReaderNotFound(ReaderNotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "ReaderNotFoundException");
        response.put("message", e.getMessage());
        return response;
    }

    @ExceptionHandler(BorrowTicketNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleTicketNotFound(BorrowTicketNotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "BorrowTicketNotFoundException");
        response.put("message", e.getMessage());
        return response;
    }

    @ExceptionHandler(BorrowTicketAlreadyReturnedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleAlreadyReturned(BorrowTicketAlreadyReturnedException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "BorrowTicketAlreadyReturnedException");
        response.put("message", e.getMessage());
        return response;
    }

    @ExceptionHandler(InvalidBorrowTicketStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidTicketStatus(
            InvalidBorrowTicketStatusException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "InvalidBorrowTicketStatusException");
        response.put("message", e.getMessage());
        return response;
    }

    @ExceptionHandler(LibraryBusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBusinessRuleViolation(
            LibraryBusinessException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getClass().getSimpleName());
        response.put("message", e.getMessage());
        return response;
    }
}
package com.library.librarymanagement.service;

import com.library.librarymanagement.model.Reader;

import java.math.BigDecimal;

public interface FinePolicy {

    boolean supports(Reader reader);

    BigDecimal calculateFine(Reader reader, long overdueDays);
}
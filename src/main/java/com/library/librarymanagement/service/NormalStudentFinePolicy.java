package com.library.librarymanagement.service;

import com.library.librarymanagement.model.Reader;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class NormalStudentFinePolicy implements FinePolicy {

    @Override
    public boolean supports(Reader reader) {
        return "NORMAL".equals(reader.getReaderType());
    }

    @Override
    public BigDecimal calculateFine(Reader reader, long overdueDays) {

        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(overdueDays)
                .multiply(BigDecimal.valueOf(5000));
    }
}
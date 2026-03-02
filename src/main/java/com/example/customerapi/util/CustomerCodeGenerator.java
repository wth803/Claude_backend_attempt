package com.example.customerapi.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum CustomerCodeGenerator {
    INSTANCE;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public String generateCode(int nextCount) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        return timestamp + "-" + nextCount;
    }
}

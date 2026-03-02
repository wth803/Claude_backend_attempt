package com.example.customerapi.util;

public final class ValidationUtil {

    private ValidationUtil() {}

    public static boolean isValidEmail(String email) {
        if (!isNotBlank(email)) return false;
        return email.contains("@") && email.contains(".");
    }

    public static boolean isValidPhone(String phone) {
        if (!isNotBlank(phone)) return false;
        return phone.matches("\\d{10}");
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

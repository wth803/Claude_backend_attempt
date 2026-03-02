package com.example.customerapi.util;

public final class ValidationUtil {

    private ValidationUtil() {}

    public static boolean isValidEmail(String email) {
        if (!isNotBlank(email)) return false;
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) return false;                    // must have local part before @
        String domain = email.substring(atIndex + 1);
        int dotIndex = domain.lastIndexOf('.');
        return dotIndex > 0 && dotIndex < domain.length() - 1; // must have non-empty domain and TLD
    }

    public static boolean isValidPhone(String phone) {
        if (!isNotBlank(phone)) return false;
        return phone.matches("\\d{10}");
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

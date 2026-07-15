package com.bankmanagement;

final class InputValidators {
    private InputValidators() {
    }

    static long parsePositiveAmount(String value) {
        String normalized = value == null ? "" : value.trim().replace(".", "").replace(",", "").replace(" ", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập số tiền.");
        }
        if (!normalized.matches("\\d+")) {
            throw new IllegalArgumentException("Số tiền chỉ được chứa chữ số.");
        }
        try {
            long amount = Long.parseLong(normalized);
            if (amount <= 0) throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
            return amount;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số tiền vượt quá giới hạn cho phép.");
        }
    }

    static boolean isSixDigitPin(String value) {
        return value != null && value.matches("\\d{6}");
    }
}

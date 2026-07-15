package com.bankmanagement;

/** @deprecated Use the card-management route in {@link Main}. */
@Deprecated
public final class ChangeCardNumber {
    public ChangeCardNumber(int accountId) {
        LegacyRouteLauncher.open(accountId, AppRoute.CARD_MANAGEMENT);
    }

    public static void main(String[] args) {
        new ChangeCardNumber(0);
    }
}

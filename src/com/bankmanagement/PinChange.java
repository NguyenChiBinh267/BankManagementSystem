package com.bankmanagement;

/** @deprecated Use the PIN-management route in {@link Main}. */
@Deprecated
public final class PinChange {
    public PinChange(int accountId) {
        LegacyRouteLauncher.open(accountId, AppRoute.PIN_CHANGE);
    }

    public static void main(String[] args) {
        new PinChange(0);
    }
}

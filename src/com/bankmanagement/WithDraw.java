package com.bankmanagement;

/** @deprecated Use the withdrawal route in {@link Main}. */
@Deprecated
public final class WithDraw {
    public WithDraw(int accountId) {
        LegacyRouteLauncher.open(accountId, AppRoute.WITHDRAW);
    }

    public static void main(String[] args) {
        new WithDraw(0);
    }
}

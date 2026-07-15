package com.bankmanagement;

/** @deprecated Use the deposit route in {@link Main}. */
@Deprecated
public final class Deposit {
    public Deposit(int accountId) {
        LegacyRouteLauncher.open(accountId, AppRoute.DEPOSIT);
    }

    public static void main(String[] args) {
        new Deposit(0);
    }
}

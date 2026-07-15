package com.bankmanagement;

/** @deprecated Use the transaction-history route in {@link Main}. */
@Deprecated
public final class MiniStatement {
    public MiniStatement(int accountId) {
        LegacyRouteLauncher.open(accountId, AppRoute.TRANSACTIONS);
    }

    public static void main(String[] args) {
        new MiniStatement(0);
    }
}

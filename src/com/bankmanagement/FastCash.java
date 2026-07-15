package com.bankmanagement;

/** @deprecated Use the fast-cash route in {@link Main}. */
@Deprecated
public final class FastCash {
    public FastCash(int accountId) {
        LegacyRouteLauncher.open(accountId, AppRoute.FAST_CASH);
    }

    public static void main(String[] args) {
        new FastCash(0);
    }
}

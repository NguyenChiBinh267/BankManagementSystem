package com.bankmanagement;

/** @deprecated Use the transfer route in {@link Main}. */
@Deprecated
public final class TransferByCardNumber {
    public TransferByCardNumber(int accountId) {
        LegacyRouteLauncher.open(accountId, AppRoute.TRANSFER);
    }

    public static void main(String[] args) {
        new TransferByCardNumber(0);
    }
}

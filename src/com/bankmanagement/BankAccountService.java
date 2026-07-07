package com.bankmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

final class BankAccountService {
    private static final String DEPOSIT_TYPE = "Nạp tiền";
    private static final String WITHDRAW_TYPE = "Rút tiền";
    static final String TRANSFER_IN_PREFIX = "Nhận tiền từ thẻ ";
    static final String TRANSFER_OUT_PREFIX = "Chuyển tiền đến thẻ ";
    private static final String OLD_TRANSFER_IN_PREFIX = "Transfer In from card ";
    private static final String OLD_TRANSFER_OUT_PREFIX = "Transfer Out to card ";

    private BankAccountService() {
    }

    static final class AccountSummary {
        final int accountId;
        final String formId;
        final String cardNumber;
        final String customerName;

        AccountSummary(int accountId, String formId, String cardNumber, String customerName) {
            this.accountId = accountId;
            this.formId = formId;
            this.cardNumber = cardNumber;
            this.customerName = customerName;
        }

        String displayName() {
            return customerName == null || customerName.trim().isEmpty() ? "Khách hàng không xác định" : customerName;
        }
    }

    static final class TransferResult {
        final AccountSummary sender;
        final AccountSummary receiver;
        final long amount;
        final long senderBalanceAfter;

        TransferResult(AccountSummary sender, AccountSummary receiver, long amount, long senderBalanceAfter) {
            this.sender = sender;
            this.receiver = receiver;
            this.amount = amount;
            this.senderBalanceAfter = senderBalanceAfter;
        }
    }

    static boolean isValidCardNumberFormat(String cardNumber) {
        return cardNumber != null && cardNumber.matches("[1-9][0-9]{15}");
    }

    static boolean isCardNumberUnique(Connection connection, String cardNumber) throws SQLException {
        String query = """
                SELECT 1
                FROM Login
                WHERE CardNumber = ?
                UNION
                SELECT 1
                FROM SignUp3
                WHERE CardNumber = ?
                LIMIT 1
        """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, cardNumber);
            ps.setString(2, cardNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next();
            }
        }
    }

    static AccountSummary findAccountById(Connection connection, int accountId) throws SQLException {
        String query = """
                SELECT l.AccountID, l.FormID, l.CardNumber, s.CustomerName
                FROM Login l
                LEFT JOIN SignUp s ON l.FormID = s.FormID
                WHERE l.AccountID = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return readAccountSummary(rs);
            }
        }
    }

    static AccountSummary findAccountByCardNumber(Connection connection, String cardNumber) throws SQLException {
        String query = """
                SELECT l.AccountID, l.FormID, l.CardNumber, s.CustomerName
                FROM Login l
                LEFT JOIN SignUp s ON l.FormID = s.FormID
                WHERE l.CardNumber = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, cardNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return readAccountSummary(rs);
            }
        }
    }

    static long calculateBalance(Connection connection, int accountId) throws SQLException {
        String query = """
                SELECT TransactionType, Amount
                FROM Bank
                WHERE AccountID = ?
        """;

        long balance = 0;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String transactionType = rs.getString("TransactionType");
                    long amount = rs.getLong("Amount");
                    if (rs.wasNull() || transactionType == null) {
                        continue;
                    }

                    if (isMoneyIn(transactionType)) {
                        balance += amount;
                    } else if (isMoneyOut(transactionType)) {
                        balance -= amount;
                    }
                }
            }
        }
        return balance;
    }

    static void changeCardNumber(Connection connection, int accountId, String newCardNumber) throws SQLException {
        if (!isValidCardNumberFormat(newCardNumber)) {
            throw new IllegalArgumentException("Số thẻ phải gồm 16 chữ số và không bắt đầu bằng 0");
        }

        boolean previousAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            AccountSummary account = lockAccountById(connection, accountId);
            if (account == null) {
                throw new IllegalStateException("Không tìm thấy tài khoản đang đăng nhập");
            }
            if (newCardNumber.equals(account.cardNumber)) {
                throw new IllegalArgumentException("Số thẻ mới phải khác số thẻ hiện tại");
            }
            if (!isCardNumberUnique(connection, newCardNumber)) {
                throw new IllegalStateException("Số thẻ mới đã tồn tại. Vui lòng nhập số thẻ khác.");
            }

            updateLoginCardNumber(connection, accountId, newCardNumber);
            updateSignUpCardNumber(connection, account.formId, newCardNumber);

            connection.commit();
        } catch (SQLException | RuntimeException ex) {
            rollbackQuietly(connection);
            throw ex;
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    static TransferResult transferByCardNumber(Connection connection, int senderAccountId, String receiverCardNumber, long amount, String note) throws SQLException {
        if (!isValidCardNumberFormat(receiverCardNumber)) {
            throw new IllegalArgumentException("Số thẻ người nhận phải gồm 16 chữ số và không bắt đầu bằng 0");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền chuyển phải lớn hơn 0");
        }

        boolean previousAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            List<AccountSummary> lockedAccounts = lockSenderAndReceiver(connection, senderAccountId, receiverCardNumber);
            AccountSummary sender = null;
            AccountSummary receiver = null;

            for (AccountSummary account : lockedAccounts) {
                if (account.accountId == senderAccountId) {
                    sender = account;
                }
                if (receiverCardNumber.equals(account.cardNumber)) {
                    receiver = account;
                }
            }

            if (sender == null) {
                throw new IllegalStateException("Không tìm thấy tài khoản đang đăng nhập");
            }
            if (receiver == null) {
                throw new IllegalStateException("Số thẻ người nhận không tồn tại");
            }
            if (sender.accountId == receiver.accountId) {
                throw new IllegalStateException("Không thể chuyển tiền cho chính số thẻ của bạn");
            }

            long senderBalance = calculateBalance(connection, sender.accountId);
            if (amount > senderBalance) {
                throw new IllegalStateException("Số dư không đủ để thực hiện giao dịch");
            }

            Timestamp transactionDate = new Timestamp(System.currentTimeMillis());
            insertTransaction(
                    connection,
                    sender.accountId,
                    transactionDate,
                    TRANSFER_OUT_PREFIX + receiver.cardNumber + ": -" + amount,
                    amount,
                    note
            );
            insertTransaction(
                    connection,
                    receiver.accountId,
                    transactionDate,
                    TRANSFER_IN_PREFIX + sender.cardNumber + ": +" + amount,
                    amount,
                    note
            );

            connection.commit();
            return new TransferResult(sender, receiver, amount, senderBalance - amount);
        } catch (SQLException | RuntimeException ex) {
            rollbackQuietly(connection);
            throw ex;
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    static long withdraw(Connection connection, int accountId, long amount) throws SQLException {
        return withdraw(connection, accountId, amount, null);
    }

    static long withdraw(Connection connection, int accountId, long amount, String note) throws SQLException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0");
        }

        boolean previousAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            AccountSummary account = lockAccountById(connection, accountId);
            if (account == null) {
                throw new IllegalStateException("Không tìm thấy tài khoản đang đăng nhập");
            }

            long currentBalance = calculateBalance(connection, accountId);
            if (amount > currentBalance) {
                throw new IllegalStateException("Số dư không đủ để rút tiền");
            }

            insertTransaction(
                    connection,
                    accountId,
                    new Timestamp(System.currentTimeMillis()),
                    WITHDRAW_TYPE,
                    amount,
                    note
            );

            connection.commit();
            return currentBalance - amount;
        } catch (SQLException | RuntimeException ex) {
            rollbackQuietly(connection);
            throw ex;
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    private static AccountSummary lockAccountById(Connection connection, int accountId) throws SQLException {
        String query = """
                SELECT l.AccountID, l.FormID, l.CardNumber, s.CustomerName
                FROM Login l
                LEFT JOIN SignUp s ON l.FormID = s.FormID
                WHERE l.AccountID = ?
                FOR UPDATE OF l
        """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return readAccountSummary(rs);
            }
        }
    }

    private static List<AccountSummary> lockSenderAndReceiver(Connection connection, int senderAccountId, String receiverCardNumber) throws SQLException {
        String query = """
                SELECT l.AccountID, l.FormID, l.CardNumber, s.CustomerName
                FROM Login l
                LEFT JOIN SignUp s ON l.FormID = s.FormID
                WHERE l.AccountID = ? OR l.CardNumber = ?
                ORDER BY l.AccountID
                FOR UPDATE OF l
        """;

        List<AccountSummary> accounts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, senderAccountId);
            ps.setString(2, receiverCardNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(readAccountSummary(rs));
                }
            }
        }
        return accounts;
    }

    private static void updateLoginCardNumber(Connection connection, int accountId, String newCardNumber) throws SQLException {
        String query = """
                UPDATE Login
                SET CardNumber = ?
                WHERE AccountID = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, newCardNumber);
            ps.setInt(2, accountId);
            int updatedRows = ps.executeUpdate();
            if (updatedRows != 1) {
                throw new SQLException("Không thể cập nhật số thẻ đăng nhập");
            }
        }
    }

    private static void updateSignUpCardNumber(Connection connection, String formId, String newCardNumber) throws SQLException {
        if (formId == null || formId.trim().isEmpty()) {
            throw new SQLException("Không tìm thấy mã hồ sơ tài khoản");
        }

        String query = """
                UPDATE SignUp3
                SET CardNumber = ?
                WHERE FormID = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, newCardNumber);
            ps.setString(2, formId);
            int updatedRows = ps.executeUpdate();
            if (updatedRows != 1) {
                throw new SQLException("Không thể cập nhật số thẻ tài khoản");
            }
        }
    }

    private static void insertTransaction(Connection connection, int accountId, Timestamp transactionDate, String transactionType, long amount, String note) throws SQLException {
        String query = """
                INSERT INTO Bank(AccountID, TransactionDate, TransactionType, Amount, Note)
                VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, accountId);
            ps.setTimestamp(2, transactionDate);
            ps.setString(3, transactionType);
            ps.setLong(4, amount);
            ps.setString(5, normalizeNote(note));
            ps.executeUpdate();
        }
    }

    private static AccountSummary readAccountSummary(ResultSet rs) throws SQLException {
        return new AccountSummary(
                rs.getInt("AccountID"),
                rs.getString("FormID"),
                rs.getString("CardNumber"),
                rs.getString("CustomerName")
        );
    }

    private static boolean isMoneyIn(String transactionType) {
        return DEPOSIT_TYPE.equals(transactionType) || isTransferIn(transactionType);
    }

    private static boolean isMoneyOut(String transactionType) {
        return WITHDRAW_TYPE.equals(transactionType) || isTransferOut(transactionType);
    }

    static String toDisplayTransactionType(String transactionType) {
        if (transactionType == null) {
            return "";
        }
        if (transactionType.startsWith(OLD_TRANSFER_IN_PREFIX)) {
            return TRANSFER_IN_PREFIX + transactionType.substring(OLD_TRANSFER_IN_PREFIX.length());
        }
        if (transactionType.startsWith(OLD_TRANSFER_OUT_PREFIX)) {
            return TRANSFER_OUT_PREFIX + transactionType.substring(OLD_TRANSFER_OUT_PREFIX.length());
        }
        return transactionType;
    }

    static String toShortDisplayTransactionType(String transactionType) {
        if (transactionType == null) {
            return "";
        }
        if (DEPOSIT_TYPE.equals(transactionType)) {
            return DEPOSIT_TYPE;
        }
        if (WITHDRAW_TYPE.equals(transactionType)) {
            return WITHDRAW_TYPE;
        }
        if (isTransferOut(transactionType)) {
            return "Chuyển tiền";
        }
        if (isTransferIn(transactionType)) {
            return "Nhận tiền";
        }
        return toDisplayTransactionType(transactionType);
    }

    static String extractRelatedCardNumber(String transactionType) {
        if (transactionType == null) {
            return "-";
        }

        String cardNumber = extractCardNumberAfterPrefix(transactionType, TRANSFER_OUT_PREFIX);
        if (cardNumber == null) {
            cardNumber = extractCardNumberAfterPrefix(transactionType, TRANSFER_IN_PREFIX);
        }
        if (cardNumber == null) {
            cardNumber = extractCardNumberAfterPrefix(transactionType, OLD_TRANSFER_OUT_PREFIX);
        }
        if (cardNumber == null) {
            cardNumber = extractCardNumberAfterPrefix(transactionType, OLD_TRANSFER_IN_PREFIX);
        }
        return cardNumber == null || cardNumber.isEmpty() ? "-" : cardNumber;
    }

    private static boolean isTransferIn(String transactionType) {
        return transactionType.startsWith(TRANSFER_IN_PREFIX) || transactionType.startsWith(OLD_TRANSFER_IN_PREFIX);
    }

    private static boolean isTransferOut(String transactionType) {
        return transactionType.startsWith(TRANSFER_OUT_PREFIX) || transactionType.startsWith(OLD_TRANSFER_OUT_PREFIX);
    }

    private static String extractCardNumberAfterPrefix(String transactionType, String prefix) {
        if (!transactionType.startsWith(prefix)) {
            return null;
        }

        String rest = transactionType.substring(prefix.length()).trim();
        int separatorIndex = rest.indexOf(':');
        if (separatorIndex >= 0) {
            rest = rest.substring(0, separatorIndex);
        }

        int whitespaceIndex = rest.indexOf(' ');
        if (whitespaceIndex >= 0) {
            rest = rest.substring(0, whitespaceIndex);
        }

        return rest.trim();
    }

    private static String normalizeNote(String note) {
        if (note == null) {
            return null;
        }

        String trimmedNote = note.trim();
        return trimmedNote.isEmpty() ? null : trimmedNote;
    }

    private static void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }
}

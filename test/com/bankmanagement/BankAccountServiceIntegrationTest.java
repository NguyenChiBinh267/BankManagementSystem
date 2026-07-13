package com.bankmanagement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountServiceIntegrationTest {

    private DBConnect db;
    private Connection connection;

    private String senderFormId;
    private String receiverFormId;

    private String senderCard;
    private String receiverCard;

    private int senderAccountId;
    private int receiverAccountId;

    @BeforeEach
    void setUp() throws Exception {
        db = new DBConnect();
        connection = db.connection;

        assertNotNull(
                connection,
                "Không kết nối được PostgreSQL"
        );

        assertFalse(
                connection.isClosed(),
                "Connection đang bị đóng"
        );

        connection.setAutoCommit(true);

        // Tạo tài khoản gửi
        senderFormId = createFormId();
        senderCard = createUniqueCardNumber();

        senderAccountId = createTestAccount(
                senderFormId,
                senderCard,
                "JUnit Sender"
        );

        // Tạo tài khoản nhận
        receiverFormId = createFormId();
        receiverCard = createUniqueCardNumber();

        receiverAccountId = createTestAccount(
                receiverFormId,
                receiverCard,
                "JUnit Receiver"
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection == null || connection.isClosed()) {
            return;
        }

        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }

            deleteTestData();
        } finally {
            db.close();
        }
    }

    @Test
    void shouldConnectToDatabase() throws Exception {
        assertNotNull(connection);
        assertFalse(connection.isClosed());
    }

    @Test
    void isValidCardNumberFormat() {
        assertAll(
                () -> assertTrue(
                        BankAccountService.isValidCardNumberFormat(
                                "123456789"
                        )
                ),

                () -> assertTrue(
                        BankAccountService.isValidCardNumberFormat(
                                "123456789012"
                        )
                ),

                () -> assertFalse(
                        BankAccountService.isValidCardNumberFormat(
                                "12345678"
                        )
                ),

                () -> assertFalse(
                        BankAccountService.isValidCardNumberFormat(
                                "12345abcd"
                        )
                ),

                () -> assertFalse(
                        BankAccountService.isValidCardNumberFormat(null)
                ),

                () -> assertFalse(
                        BankAccountService.isValidCardNumberFormat("")
                )
        );
    }

    @Test
    void isCardNumberUnique() throws Exception {
        assertFalse(
                BankAccountService.isCardNumberUnique(
                        connection,
                        senderCard
                ),
                "Số thẻ đã tồn tại nhưng hệ thống lại báo là duy nhất"
        );

        String newCardNumber = createUniqueCardNumber();

        assertTrue(
                BankAccountService.isCardNumberUnique(
                        connection,
                        newCardNumber
                ),
                "Số thẻ chưa tồn tại nhưng hệ thống lại báo bị trùng"
        );
    }

    @Test
    void findAccountById() throws Exception {
        BankAccountService.AccountSummary account =
                BankAccountService.findAccountById(
                        connection,
                        senderAccountId
                );

        assertNotNull(account);

        assertAll(
                () -> assertEquals(
                        senderAccountId,
                        account.accountId
                ),

                () -> assertEquals(
                        senderFormId,
                        account.formId
                ),

                () -> assertEquals(
                        senderCard,
                        account.cardNumber
                ),

                () -> assertEquals(
                        "JUnit Sender",
                        account.customerName
                ),

                () -> assertEquals(
                        "JUnit Sender",
                        account.displayName()
                )
        );
    }

    @Test
    void findAccountByCardNumber() throws Exception {
        BankAccountService.AccountSummary account =
                BankAccountService.findAccountByCardNumber(
                        connection,
                        receiverCard
                );

        assertNotNull(account);

        assertAll(
                () -> assertEquals(
                        receiverAccountId,
                        account.accountId
                ),

                () -> assertEquals(
                        receiverFormId,
                        account.formId
                ),

                () -> assertEquals(
                        receiverCard,
                        account.cardNumber
                ),

                () -> assertEquals(
                        "JUnit Receiver",
                        account.customerName
                )
        );
    }

    @Test
    void calculateBalance() throws Exception {
        insertTransaction(
                senderAccountId,
                "Nạp tiền",
                500_000,
                "JUNIT_BALANCE"
        );

        insertTransaction(
                senderAccountId,
                "Rút tiền",
                100_000,
                "JUNIT_BALANCE"
        );

        insertTransaction(
                senderAccountId,
                "Nhận tiền từ thẻ " + receiverCard + ": +50000",
                50_000,
                "JUNIT_BALANCE"
        );

        insertTransaction(
                senderAccountId,
                "Chuyển tiền đến thẻ " + receiverCard + ": -20000",
                20_000,
                "JUNIT_BALANCE"
        );

        long actualBalance =
                BankAccountService.calculateBalance(
                        connection,
                        senderAccountId
                );

        // 500.000 - 100.000 + 50.000 - 20.000
        assertEquals(
                430_000,
                actualBalance
        );
    }

    @Test
    void changeCardNumber() throws Exception {
        String oldCardNumber = senderCard;
        String newCardNumber = createUniqueCardNumber();

        BankAccountService.changeCardNumber(
                connection,
                senderAccountId,
                newCardNumber
        );

        BankAccountService.AccountSummary account =
                BankAccountService.findAccountById(
                        connection,
                        senderAccountId
                );

        assertNotNull(account);

        assertEquals(
                newCardNumber,
                account.cardNumber
        );

        assertNull(
                BankAccountService.findAccountByCardNumber(
                        connection,
                        oldCardNumber
                ),
                "Số thẻ cũ vẫn còn tồn tại trong bảng Login"
        );

        assertEquals(
                newCardNumber,
                findCardNumberInSignUp3(senderFormId),
                "Số thẻ trong SignUp3 chưa được cập nhật"
        );
    }

    @Test
    void transferByCardNumber() throws Exception {
        insertTransaction(
                senderAccountId,
                "Nạp tiền",
                500_000,
                "JUNIT_INITIAL_MONEY"
        );

        String note = "JUNIT_TRANSFER";

        BankAccountService.TransferResult result =
                BankAccountService.transferByCardNumber(
                        connection,
                        senderAccountId,
                        receiverCard,
                        120_000,
                        note
                );

        assertAll(
                () -> assertEquals(
                        senderAccountId,
                        result.sender.accountId
                ),

                () -> assertEquals(
                        receiverAccountId,
                        result.receiver.accountId
                ),

                () -> assertEquals(
                        120_000,
                        result.amount
                ),

                () -> assertEquals(
                        380_000,
                        result.senderBalanceAfter
                ),

                () -> assertEquals(
                        380_000,
                        BankAccountService.calculateBalance(
                                connection,
                                senderAccountId
                        )
                ),

                () -> assertEquals(
                        120_000,
                        BankAccountService.calculateBalance(
                                connection,
                                receiverAccountId
                        )
                ),

                () -> assertEquals(
                        2,
                        countTransactionsByNote(note),
                        "Chuyển tiền phải tạo 2 giao dịch"
                )
        );
    }

    @Test
    void withdraw() throws Exception {
        insertTransaction(
                senderAccountId,
                "Nạp tiền",
                200_000,
                "JUNIT_WITHDRAW_INITIAL"
        );

        long balanceAfter =
                BankAccountService.withdraw(
                        connection,
                        senderAccountId,
                        50_000,
                        "JUNIT_WITHDRAW"
                );

        assertAll(
                () -> assertEquals(
                        150_000,
                        balanceAfter
                ),

                () -> assertEquals(
                        150_000,
                        BankAccountService.calculateBalance(
                                connection,
                                senderAccountId
                        )
                ),

                () -> assertEquals(
                        1,
                        countTransactions(
                                senderAccountId,
                                "Rút tiền",
                                "JUNIT_WITHDRAW"
                        )
                )
        );
    }

    @Test
    void testWithdraw() throws Exception {
        insertTransaction(
                senderAccountId,
                "Nạp tiền",
                200_000,
                "JUNIT_WITHDRAW_NO_NOTE_INITIAL"
        );

        long balanceAfter =
                BankAccountService.withdraw(
                        connection,
                        senderAccountId,
                        25_000
                );

        assertAll(
                () -> assertEquals(
                        175_000,
                        balanceAfter
                ),

                () -> assertEquals(
                        175_000,
                        BankAccountService.calculateBalance(
                                connection,
                                senderAccountId
                        )
                ),

                () -> assertEquals(
                        1,
                        countTransactionsWithNullNote(
                                senderAccountId,
                                "Rút tiền"
                        )
                )
        );
    }

    @Test
    void toDisplayTransactionType() {
        assertAll(
                () -> assertEquals(
                        "",
                        BankAccountService.toDisplayTransactionType(null)
                ),

                () -> assertEquals(
                        "Nhận tiền từ thẻ 123456789",
                        BankAccountService.toDisplayTransactionType(
                                "Transfer In from card 123456789"
                        )
                ),

                () -> assertEquals(
                        "Chuyển tiền đến thẻ 987654321",
                        BankAccountService.toDisplayTransactionType(
                                "Transfer Out to card 987654321"
                        )
                ),

                () -> assertEquals(
                        "Nạp tiền",
                        BankAccountService.toDisplayTransactionType(
                                "Nạp tiền"
                        )
                )
        );
    }

    @Test
    void toShortDisplayTransactionType() {
        assertAll(
                () -> assertEquals(
                        "Nạp tiền",
                        BankAccountService.toShortDisplayTransactionType(
                                "Nạp tiền"
                        )
                ),

                () -> assertEquals(
                        "Rút tiền",
                        BankAccountService.toShortDisplayTransactionType(
                                "Rút tiền"
                        )
                ),

                () -> assertEquals(
                        "Chuyển tiền",
                        BankAccountService.toShortDisplayTransactionType(
                                "Chuyển tiền đến thẻ 123456789: -50000"
                        )
                ),

                () -> assertEquals(
                        "Nhận tiền",
                        BankAccountService.toShortDisplayTransactionType(
                                "Nhận tiền từ thẻ 123456789: +50000"
                        )
                )
        );
    }

    @Test
    void extractRelatedCardNumber() {
        assertAll(
                () -> assertEquals(
                        "123456789",
                        BankAccountService.extractRelatedCardNumber(
                                "Chuyển tiền đến thẻ 123456789: -50000"
                        )
                ),

                () -> assertEquals(
                        "987654321",
                        BankAccountService.extractRelatedCardNumber(
                                "Nhận tiền từ thẻ 987654321: +50000"
                        )
                ),

                () -> assertEquals(
                        "111222333",
                        BankAccountService.extractRelatedCardNumber(
                                "Transfer Out to card 111222333: -10000"
                        )
                ),

                () -> assertEquals(
                        "444555666",
                        BankAccountService.extractRelatedCardNumber(
                                "Transfer In from card 444555666: +10000"
                        )
                ),

                () -> assertEquals(
                        "-",
                        BankAccountService.extractRelatedCardNumber(
                                "Nạp tiền"
                        )
                ),

                () -> assertEquals(
                        "-",
                        BankAccountService.extractRelatedCardNumber(null)
                )
        );
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    private String createFormId() {
        return "JUNIT_"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 18);
    }

    private String createUniqueCardNumber() throws Exception {
        String cardNumber;

        do {
            cardNumber = String.valueOf(
                    ThreadLocalRandom.current().nextLong(
                            100_000_000_000L,
                            1_000_000_000_000L
                    )
            );
        } while (
                !BankAccountService.isCardNumberUnique(
                        connection,
                        cardNumber
                )
        );

        return cardNumber;
    }

    private int createTestAccount(
            String formId,
            String cardNumber,
            String customerName
    ) throws Exception {

        String insertSignUp = """
                INSERT INTO SignUp(
                    FormID,
                    CustomerName,
                    Email,
                    Phone,
                    Gender,
                    Birthday,
                    Address,
                    City,
                    Pin
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement ps =
                        connection.prepareStatement(insertSignUp)
        ) {
            ps.setString(1, formId);
            ps.setString(2, customerName);
            ps.setString(3, formId + "@test.local");
            ps.setString(4, "0900000000");
            ps.setString(5, "Nam");
            ps.setString(6, "01/01/2000");
            ps.setString(7, "JUnit Test Address");
            ps.setString(8, "Ha Noi");
            ps.setString(9, "123456");

            ps.executeUpdate();
        }

        String insertSignUp3 = """
                INSERT INTO SignUp3(
                    FormID,
                    AccountType,
                    CardNumber,
                    Services
                )
                VALUES (?, ?, ?, ?)
                """;

        try (
                PreparedStatement ps =
                        connection.prepareStatement(insertSignUp3)
        ) {
            ps.setString(1, formId);
            ps.setString(2, "Saving Account");
            ps.setString(3, cardNumber);
            ps.setString(4, "JUnit Test");

            ps.executeUpdate();
        }

        String insertLogin = """
                INSERT INTO Login(
                    FormID,
                    CardNumber,
                    Pin
                )
                VALUES (?, ?, ?)
                RETURNING AccountID
                """;

        try (
                PreparedStatement ps =
                        connection.prepareStatement(insertLogin)
        ) {
            ps.setString(1, formId);
            ps.setString(2, cardNumber);
            ps.setString(3, "123456");

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException(
                            "Không tạo được tài khoản test"
                    );
                }

                return rs.getInt("AccountID");
            }
        }
    }

    private void insertTransaction(
            int accountId,
            String transactionType,
            long amount,
            String note
    ) throws Exception {

        String sql = """
                INSERT INTO Bank(
                    AccountID,
                    TransactionDate,
                    TransactionType,
                    Amount,
                    Note
                )
                VALUES (
                    ?,
                    CURRENT_TIMESTAMP,
                    ?,
                    ?,
                    ?
                )
                """;

        try (
                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {
            ps.setInt(1, accountId);
            ps.setString(2, transactionType);
            ps.setLong(3, amount);
            ps.setString(4, note);

            ps.executeUpdate();
        }
    }

    private String findCardNumberInSignUp3(
            String formId
    ) throws Exception {

        String sql = """
                SELECT CardNumber
                FROM SignUp3
                WHERE FormID = ?
                """;

        try (
                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {
            ps.setString(1, formId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return rs.getString("CardNumber");
            }
        }
    }

    private int countTransactionsByNote(
            String note
    ) throws Exception {

        String sql = """
                SELECT COUNT(*) AS Total
                FROM Bank
                WHERE Note = ?
                """;

        try (
                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {
            ps.setString(1, note);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("Total");
            }
        }
    }

    private int countTransactions(
            int accountId,
            String transactionType,
            String note
    ) throws Exception {

        String sql = """
                SELECT COUNT(*) AS Total
                FROM Bank
                WHERE AccountID = ?
                  AND TransactionType = ?
                  AND Note = ?
                """;

        try (
                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {
            ps.setInt(1, accountId);
            ps.setString(2, transactionType);
            ps.setString(3, note);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("Total");
            }
        }
    }

    private int countTransactionsWithNullNote(
            int accountId,
            String transactionType
    ) throws Exception {

        String sql = """
                SELECT COUNT(*) AS Total
                FROM Bank
                WHERE AccountID = ?
                  AND TransactionType = ?
                  AND Note IS NULL
                """;

        try (
                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {
            ps.setInt(1, accountId);
            ps.setString(2, transactionType);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("Total");
            }
        }
    }

    private void deleteTestData() throws Exception {
        String deleteBank = """
                DELETE FROM Bank
                WHERE AccountID IN (
                    SELECT AccountID
                    FROM Login
                    WHERE FormID IN (?, ?)
                )
                """;

        executeDelete(
                deleteBank,
                senderFormId,
                receiverFormId
        );

        executeDelete(
                "DELETE FROM Login WHERE FormID IN (?, ?)",
                senderFormId,
                receiverFormId
        );

        executeDelete(
                "DELETE FROM SignUp3 WHERE FormID IN (?, ?)",
                senderFormId,
                receiverFormId
        );

        executeDelete(
                "DELETE FROM SignUp2 WHERE FormID IN (?, ?)",
                senderFormId,
                receiverFormId
        );

        executeDelete(
                "DELETE FROM SignUp WHERE FormID IN (?, ?)",
                senderFormId,
                receiverFormId
        );
    }

    private void executeDelete(
            String sql,
            String firstFormId,
            String secondFormId
    ) throws Exception {

        try (
                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {
            ps.setString(1, firstFormId);
            ps.setString(2, secondFormId);

            ps.executeUpdate();
        }
    }
}
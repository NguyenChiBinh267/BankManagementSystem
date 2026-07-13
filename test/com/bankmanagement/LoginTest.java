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

class LoginIntegrationTest {

    private DBConnect db;
    private Connection connection;

    private String testFormId;
    private String testCardNumber;
    private final String correctPin = "123456";

    private int testAccountId;

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

        testFormId = createFormId();
        testCardNumber = createCardNumber();

        createTestCustomer();
        testAccountId = createTestLoginAccount();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection == null || connection.isClosed()) {
            return;
        }

        try {
            deleteTestAccount();
        } finally {
            db.close();
        }
    }

    @Test
    void loginWithCorrectCardAndPin_shouldReturnAccountId()
            throws Exception {

        Integer accountId = authenticate(
                testCardNumber,
                correctPin
        );

        assertNotNull(accountId);
        assertEquals(testAccountId, accountId);
    }

    @Test
    void loginWithWrongPin_shouldReturnNull()
            throws Exception {

        Integer accountId = authenticate(
                testCardNumber,
                "654321"
        );

        assertNull(accountId);
    }

    @Test
    void loginWithUnknownCardNumber_shouldReturnNull()
            throws Exception {

        Integer accountId = authenticate(
                "999999999999",
                correctPin
        );

        assertNull(accountId);
    }

    @Test
    void pinWithLessThanSixDigits_shouldBeInvalid() {
        String pin = "12345";

        assertFalse(pin.matches("[0-9]{6}"));
    }

    @Test
    void pinWithMoreThanSixDigits_shouldBeInvalid() {
        String pin = "1234567";

        assertFalse(pin.matches("[0-9]{6}"));
    }

    @Test
    void pinContainingLetters_shouldBeInvalid() {
        String pin = "12ab56";

        assertFalse(pin.matches("[0-9]{6}"));
    }

    @Test
    void pinWithExactlySixDigits_shouldBeValid() {
        String pin = "123456";

        assertTrue(pin.matches("[0-9]{6}"));
    }

    private Integer authenticate(
            String cardNumber,
            String pin
    ) throws Exception {

        String sql = """
                SELECT AccountID
                FROM Login
                WHERE CardNumber = ?
                  AND Pin = ?
                """;

        try (
                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {
            ps.setString(1, cardNumber);
            ps.setString(2, pin);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return rs.getInt("AccountID");
            }
        }
    }

    private void createTestCustomer() throws Exception {
        String sql = """
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
                        connection.prepareStatement(sql)
        ) {
            ps.setString(1, testFormId);
            ps.setString(2, "JUnit Login Test");
            ps.setString(3, testFormId + "@test.local");
            ps.setString(4, "0900000000");
            ps.setString(5, "Nam");
            ps.setString(6, "01/01/2000");
            ps.setString(7, "JUnit Address");
            ps.setString(8, "Ha Noi");
            ps.setString(9, correctPin);

            ps.executeUpdate();
        }
    }

    private int createTestLoginAccount() throws Exception {
        String sql = """
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
                        connection.prepareStatement(sql)
        ) {
            ps.setString(1, testFormId);
            ps.setString(2, testCardNumber);
            ps.setString(3, correctPin);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException(
                            "Không tạo được tài khoản đăng nhập test"
                    );
                }

                return rs.getInt("AccountID");
            }
        }
    }

    private void deleteTestAccount() throws Exception {
        try (
                PreparedStatement ps = connection.prepareStatement(
                        "DELETE FROM Login WHERE FormID = ?"
                )
        ) {
            ps.setString(1, testFormId);
            ps.executeUpdate();
        }

        try (
                PreparedStatement ps = connection.prepareStatement(
                        "DELETE FROM SignUp WHERE FormID = ?"
                )
        ) {
            ps.setString(1, testFormId);
            ps.executeUpdate();
        }
    }

    private String createFormId() {
        return "JUNIT_LOGIN_"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);
    }

    private String createCardNumber() {
        return String.valueOf(
                ThreadLocalRandom.current().nextLong(
                        100_000_000_000L,
                        1_000_000_000_000L
                )
        );
    }
}
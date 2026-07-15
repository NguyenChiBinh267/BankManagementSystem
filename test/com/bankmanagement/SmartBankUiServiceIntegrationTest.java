package com.bankmanagement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SmartBankUiServiceIntegrationTest {
    private DBConnect db;
    private Connection connection;
    private String formId;
    private String cardNumber;
    private String registeredFormId;
    private int accountId;

    @BeforeEach
    void setUp() throws Exception {
        db = new DBConnect();
        connection = db.connection;
        assertNotNull(connection, "Không kết nối được PostgreSQL");
        formId = compactId("UI-");
        cardNumber = uniqueCard();
        insertPersonal(formId, "123456");
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Login(FormID, CardNumber, Pin) VALUES (?, ?, ?) RETURNING AccountID")) {
            statement.setString(1, formId);
            statement.setString(2, cardNumber);
            statement.setString(3, "123456");
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                accountId = resultSet.getInt(1);
            }
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection == null || connection.isClosed()) return;
        deleteForm(registeredFormId);
        deleteForm(formId);
        db.close();
    }

    @Test
    void authenticationDepositHistoryAndPinChange_workTogether() throws Exception {
        assertEquals(accountId, AuthenticationService.authenticate(connection, cardNumber, "123456"));

        long balanceAfterDeposit = BankAccountService.deposit(connection, accountId, 1_000_000L, "Integration deposit");
        assertEquals(1_000_000L, balanceAfterDeposit);

        var deposits = BankAccountService.loadTransactions(
                connection,
                accountId,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(5),
                BankAccountService.TransactionCategory.DEPOSIT,
                10);
        assertEquals(1, deposits.size());
        assertEquals("Integration deposit", deposits.get(0).note);
        assertTrue(deposits.get(0).moneyIn);

        assertEquals(800_000L, BankAccountService.withdraw(connection, accountId, 200_000L, "Integration withdraw"));
        assertTrue(BankAccountService.changePin(connection, accountId, "123456", "654321"));
        assertEquals(accountId, AuthenticationService.authenticate(connection, cardNumber, "654321"));
        assertEquals(-1, AuthenticationService.authenticate(connection, cardNumber, "123456"));
    }

    @Test
    void registrationService_createsAllLinkedRows() throws Exception {
        registeredFormId = compactId("REG-");
        RegistrationService.PersonalData personal = new RegistrationService.PersonalData(
                registeredFormId, "Integration Customer", "integration@example.com", "0900000000",
                "Nam", "01/01/2000", "Test address", "Test city", "111222");
        RegistrationService.AdditionalData additional = new RegistrationService.AdditionalData(
                "Không", "Cá nhân", "5 - 10 triệu", "Đại học", "Tự do",
                "012345678901", "Không", "Không");
        RegistrationService.AccountData account = new RegistrationService.AccountData(
                "Tài khoản thanh toán", "Thẻ ATM, Ngân hàng trực tuyến");

        String generatedCard = RegistrationService.register(connection, personal, additional, account);
        assertTrue(BankAccountService.isValidCardNumberFormat(generatedCard));
        assertTrue(rowExists("SignUp", registeredFormId));
        assertTrue(rowExists("SignUp2", registeredFormId));
        assertTrue(rowExists("SignUp3", registeredFormId));
        assertTrue(rowExists("Login", registeredFormId));
        assertTrue(AuthenticationService.authenticate(connection, generatedCard, "111222") > 0);
    }

    private void insertPersonal(String id, String pin) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO SignUp(FormID, CustomerName, Email, Phone, Gender, Birthday, Address, City, Pin)
                VALUES (?, 'UI Test', 'ui@example.com', '0900000000', 'Nam', '01/01/2000', 'Test', 'Test', ?)
        """)) {
            statement.setString(1, id);
            statement.setString(2, pin);
            statement.executeUpdate();
        }
    }

    private boolean rowExists(String table, String id) throws Exception {
        String query = "SELECT 1 FROM " + table + " WHERE FormID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void deleteForm(String id) throws Exception {
        if (id == null) return;
        Integer idToDelete = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT AccountID FROM Login WHERE FormID = ?")) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) idToDelete = resultSet.getInt(1);
            }
        }
        if (idToDelete != null) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Bank WHERE AccountID = ?")) {
                statement.setInt(1, idToDelete);
                statement.executeUpdate();
            }
        }
        for (String table : new String[]{"Login", "SignUp3", "SignUp2", "SignUp"}) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + table + " WHERE FormID = ?")) {
                statement.setString(1, id);
                statement.executeUpdate();
            }
        }
    }

    private String uniqueCard() {
        return "98" + Math.abs(UUID.randomUUID().getMostSignificantBits());
    }

    private String compactId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
}

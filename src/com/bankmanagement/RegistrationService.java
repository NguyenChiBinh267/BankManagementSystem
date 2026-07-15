package com.bankmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

final class RegistrationService {
    record PersonalData(String formId, String name, String email, String phone, String gender,
                        String birthday, String address, String city, String pin) {
    }

    record AdditionalData(String religion, String category, String income, String education,
                          String occupation, String citizenId, String seniorCitizen, String existingAccount) {
    }

    record AccountData(String accountType, String services) {
    }

    private RegistrationService() {
    }

    static String register(Connection connection,
                           PersonalData personal,
                           AdditionalData additional,
                           AccountData account) throws SQLException {
        boolean previousAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            insertPersonal(connection, personal);
            insertAdditional(connection, personal.formId(), additional);
            String cardNumber = generateUniqueCardNumber(connection);
            insertAccount(connection, personal.formId(), cardNumber, account);
            insertLogin(connection, personal.formId(), cardNumber, personal.pin());
            connection.commit();
            return cardNumber;
        } catch (SQLException | RuntimeException ex) {
            try {
                connection.rollback();
            } catch (SQLException rollbackError) {
                ex.addSuppressed(rollbackError);
            }
            throw ex;
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    private static void insertPersonal(Connection connection, PersonalData data) throws SQLException {
        String query = """
                INSERT INTO SignUp(FormID, CustomerName, Email, Phone, Gender, Birthday, Address, City, Pin)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, data.formId());
            statement.setString(2, data.name());
            statement.setString(3, data.email());
            statement.setString(4, data.phone());
            statement.setString(5, data.gender());
            statement.setString(6, data.birthday());
            statement.setString(7, data.address());
            statement.setString(8, data.city());
            statement.setString(9, data.pin());
            statement.executeUpdate();
        }
    }

    private static void insertAdditional(Connection connection, String formId, AdditionalData data) throws SQLException {
        String query = """
                INSERT INTO SignUp2(FormID, Religion, Category, Income, Education, Occupation, CCCD, SeniorCitizen, ExistingAccount)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, formId);
            statement.setString(2, data.religion());
            statement.setString(3, data.category());
            statement.setString(4, data.income());
            statement.setString(5, data.education());
            statement.setString(6, data.occupation());
            statement.setString(7, data.citizenId());
            statement.setString(8, data.seniorCitizen());
            statement.setString(9, data.existingAccount());
            statement.executeUpdate();
        }
    }

    private static void insertAccount(Connection connection, String formId, String cardNumber, AccountData data) throws SQLException {
        String query = """
                INSERT INTO SignUp3(FormID, AccountType, CardNumber, Services)
                VALUES (?, ?, ?, ?)
        """;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, formId);
            statement.setString(2, data.accountType());
            statement.setString(3, cardNumber);
            statement.setString(4, data.services());
            statement.executeUpdate();
        }
    }

    private static void insertLogin(Connection connection, String formId, String cardNumber, String pin) throws SQLException {
        String query = "INSERT INTO Login(FormID, CardNumber, Pin) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, formId);
            statement.setString(2, cardNumber);
            statement.setString(3, pin);
            statement.executeUpdate();
        }
    }

    private static String generateUniqueCardNumber(Connection connection) throws SQLException {
        for (int attempt = 0; attempt < 20; attempt++) {
            long suffix = ThreadLocalRandom.current().nextLong(10_000_000L, 100_000_000L);
            String cardNumber = "14099630" + suffix;
            if (BankAccountService.isCardNumberUnique(connection, cardNumber)) {
                return cardNumber;
            }
        }
        throw new SQLException("Không thể tạo số thẻ duy nhất. Vui lòng thử lại.");
    }
}

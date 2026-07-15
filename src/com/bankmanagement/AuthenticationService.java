package com.bankmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class AuthenticationService {
    private AuthenticationService() {
    }

    static int authenticate(Connection connection, String cardNumber, String pin) throws SQLException {
        String query = "SELECT AccountID FROM Login WHERE CardNumber = ? AND Pin = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cardNumber);
            statement.setString(2, pin);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("AccountID") : -1;
            }
        }
    }
}

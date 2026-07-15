package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

final class PersonalRegistrationPanel extends JPanel {
    private final StyledTextField nameField = new StyledTextField(20);
    private final StyledTextField emailField = new StyledTextField(20);
    private final StyledTextField phoneField = new StyledTextField(20);
    private final JComboBox<String> genderField = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
    private final JSpinner birthdayField = new JSpinner(new SpinnerDateModel(defaultBirthday(), null, null, java.util.Calendar.DAY_OF_MONTH));
    private final StyledTextField addressField = new StyledTextField(20);
    private final StyledTextField cityField = new StyledTextField(20);
    private final PasswordField pinField = new PasswordField(20);
    private final PasswordField confirmPinField = new PasswordField(20);

    PersonalRegistrationPanel() {
        super(new GridBagLayout());
        setOpaque(false);
        RoundedPanel surface = new RoundedPanel(UIStyle.RADIUS_CARD, UIStyle.CARD_BACKGROUND, UIStyle.BORDER);
        surface.setLayout(new GridBagLayout());
        surface.setBorder(new EmptyBorder(UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6));
        surface.setPreferredSize(new Dimension(760, 500));
        configureControls();
        addRow(surface, 0, "Họ và tên", nameField, "Email", emailField);
        addRow(surface, 1, "Số điện thoại", phoneField, "Giới tính", genderField);
        addRow(surface, 2, "Ngày sinh", birthdayField, "Thành phố", cityField);
        addWideRow(surface, 3, "Địa chỉ", addressField);
        addRow(surface, 4, "Mã PIN", pinField, "Xác nhận PIN", confirmPinField);
        add(surface);
    }

    RegistrationService.PersonalData read(String formId) {
        clearErrors();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String city = cityField.getText().trim();
        if (name.isEmpty() || address.isEmpty() || city.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ họ tên, địa chỉ và thành phố.");
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            emailField.setError("Email không đúng định dạng.");
            throw new IllegalArgumentException("Email không đúng định dạng.");
        }
        if (!phone.matches("\\d{9,15}")) {
            phoneField.setError("Số điện thoại phải gồm 9 đến 15 chữ số.");
            throw new IllegalArgumentException("Số điện thoại phải gồm 9 đến 15 chữ số.");
        }
        LocalDate birthday = ((Date) birthdayField.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (!birthday.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh phải trước ngày hiện tại.");
        }
        char[] pinChars = pinField.getPassword();
        char[] confirmationChars = confirmPinField.getPassword();
        String pin = new String(pinChars);
        String confirmation = new String(confirmationChars);
        Arrays.fill(pinChars, '\0');
        Arrays.fill(confirmationChars, '\0');
        if (!InputValidators.isSixDigitPin(pin)) {
            pinField.setError("PIN phải gồm đúng 6 chữ số.");
            throw new IllegalArgumentException("PIN phải gồm đúng 6 chữ số.");
        }
        if (!pin.equals(confirmation)) {
            confirmPinField.setError("PIN xác nhận không khớp.");
            throw new IllegalArgumentException("PIN xác nhận không khớp.");
        }
        return new RegistrationService.PersonalData(
                formId, name, email, phone, String.valueOf(genderField.getSelectedItem()),
                birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), address, city, pin);
    }

    private void configureControls() {
        genderField.setFont(UIStyle.FIELD_FONT);
        genderField.setBackground(UIStyle.CARD_BACKGROUND);
        genderField.setPreferredSize(new Dimension(260, UIStyle.CONTROL_HEIGHT));
        birthdayField.setEditor(new JSpinner.DateEditor(birthdayField, "dd/MM/yyyy"));
        birthdayField.setFont(UIStyle.FIELD_FONT);
        birthdayField.setPreferredSize(new Dimension(260, UIStyle.CONTROL_HEIGHT));
    }

    private void clearErrors() {
        nameField.clearError();
        emailField.clearError();
        phoneField.clearError();
        addressField.clearError();
        cityField.clearError();
        pinField.clearError();
        confirmPinField.clearError();
    }

    private static Date defaultBirthday() {
        return Date.from(LocalDate.now().minusYears(18).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    static void addRow(JPanel panel, int row, String leftLabel, JComponent left,
                       String rightLabel, JComponent right) {
        addField(panel, row, 0, leftLabel, left);
        addField(panel, row, 1, rightLabel, right);
    }

    static void addWideRow(JPanel panel, int row, String label, JComponent field) {
        JPanel group = fieldGroup(label, field);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(UIStyle.SPACE_2, UIStyle.SPACE_2, UIStyle.SPACE_2, UIStyle.SPACE_2);
        panel.add(group, gbc);
    }

    private static void addField(JPanel panel, int row, int column, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(UIStyle.SPACE_2, UIStyle.SPACE_2, UIStyle.SPACE_2, UIStyle.SPACE_2);
        panel.add(fieldGroup(label, field), gbc);
    }

    static JPanel fieldGroup(String text, JComponent field) {
        JPanel group = new JPanel(new BorderLayout(0, UIStyle.SPACE_2));
        group.setOpaque(false);
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.LABEL_FONT);
        label.setForeground(UIStyle.TEXT);
        label.setLabelFor(field);
        group.add(label, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        return group;
    }
}

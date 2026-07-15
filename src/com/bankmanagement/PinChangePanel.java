package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

final class PinChangePanel extends BankPanel {
    private final PasswordField currentPinField = new PasswordField(20);
    private final PasswordField newPinField = new PasswordField(20);
    private final PasswordField confirmPinField = new PasswordField(20);
    private final PrimaryButton submitButton = new PrimaryButton("Đổi mã PIN");

    PinChangePanel(int accountId) {
        super(accountId, "Đổi mã PIN", "PIN phải gồm đúng 6 chữ số và không được hiển thị công khai");
        submitButton.setIcon(new SmartBankIcon(SmartBankIcon.Type.LOCK, 16, Color.WHITE));
        submitButton.addActionListener(e -> submit());
        setBody(center(createForm()));
    }

    private JPanel createForm() {
        RoundedPanel surface = createSurface();
        surface.setLayout(new BorderLayout(0, UIStyle.SPACE_6));
        surface.setPreferredSize(new Dimension(620, 430));
        JPanel fields = new JPanel(new GridLayout(3, 1, 0, UIStyle.SPACE_4));
        fields.setOpaque(false);
        fields.add(createFieldGroup("PIN hiện tại", currentPinField));
        fields.add(createFieldGroup("PIN mới", newPinField));
        fields.add(createFieldGroup("Xác nhận PIN mới", confirmPinField));
        surface.add(fields, BorderLayout.CENTER);
        surface.add(createActions(submitButton), BorderLayout.SOUTH);
        return surface;
    }

    private void submit() {
        notification.clear();
        currentPinField.clearError();
        newPinField.clearError();
        confirmPinField.clearError();
        char[] oldChars = currentPinField.getPassword();
        char[] newChars = newPinField.getPassword();
        char[] confirmChars = confirmPinField.getPassword();
        String oldPin = new String(oldChars);
        String newPin = new String(newChars);
        String confirmation = new String(confirmChars);
        Arrays.fill(oldChars, '\0');
        Arrays.fill(newChars, '\0');
        Arrays.fill(confirmChars, '\0');

        if (!InputValidators.isSixDigitPin(oldPin)) {
            currentPinField.setError("PIN hiện tại phải gồm đúng 6 chữ số.");
            notification.showMessage("Kiểm tra lại PIN hiện tại.", NotificationPanel.Type.ERROR);
            return;
        }
        if (!InputValidators.isSixDigitPin(newPin)) {
            newPinField.setError("PIN mới phải gồm đúng 6 chữ số.");
            notification.showMessage("PIN mới phải gồm đúng 6 chữ số.", NotificationPanel.Type.ERROR);
            return;
        }
        if (oldPin.equals(newPin)) {
            newPinField.setError("PIN mới phải khác PIN hiện tại.");
            notification.showMessage("PIN mới phải khác PIN hiện tại.", NotificationPanel.Type.ERROR);
            return;
        }
        if (!newPin.equals(confirmation)) {
            confirmPinField.setError("PIN xác nhận không khớp.");
            notification.showMessage("PIN xác nhận không khớp.", NotificationPanel.Type.ERROR);
            return;
        }
        if (!ConfirmDialog.show(SwingUtilities.getWindowAncestor(this), "Xác nhận đổi PIN",
                "Bạn sẽ sử dụng PIN mới cho lần đăng nhập tiếp theo.", "Đổi PIN")) {
            return;
        }

        notification.showMessage("Đang cập nhật mã PIN...", NotificationPanel.Type.INFO);
        SwingWorkerRunner.run(new JComponent[]{submitButton, currentPinField, newPinField, confirmPinField}, () -> {
            try (DBConnect connection = openConnection()) {
                return BankAccountService.changePin(connection.connection, accountId, oldPin, newPin);
            }
        }, changed -> {
            if (!changed) {
                currentPinField.setError("PIN hiện tại không chính xác.");
                notification.showMessage("PIN hiện tại không chính xác.", NotificationPanel.Type.ERROR);
                return;
            }
            currentPinField.setText("");
            newPinField.setText("");
            confirmPinField.setText("");
            notification.showMessage("Đổi mã PIN thành công.", NotificationPanel.Type.SUCCESS, 5000);
        }, error -> notification.showMessage(failureMessage(error, "đổi mã PIN"), NotificationPanel.Type.ERROR));
    }

    @Override public void refreshData() { notification.clear(); }
}

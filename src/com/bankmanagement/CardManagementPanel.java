package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

final class CardManagementPanel extends BankPanel {
    private final Consumer<BankAccountService.AccountSummary> accountChanged;
    private final StyledTextField currentCardField = new StyledTextField(20);
    private final StyledTextField newCardField = new StyledTextField(20);
    private final PrimaryButton submitButton = new PrimaryButton("Cập nhật số thẻ");

    CardManagementPanel(int accountId, Consumer<BankAccountService.AccountSummary> accountChanged) {
        super(accountId, "Quản lý thẻ ATM", "Xem và cập nhật số thẻ dùng để đăng nhập, chuyển tiền");
        this.accountChanged = accountChanged;
        currentCardField.setEditable(false);
        currentCardField.setBackground(UIStyle.SURFACE_SUBTLE);
        submitButton.setIcon(new SmartBankIcon(SmartBankIcon.Type.CARD, 16, Color.WHITE));
        submitButton.addActionListener(e -> submit());
        setBody(center(createForm()));
    }

    private JPanel createForm() {
        RoundedPanel surface = createSurface();
        surface.setLayout(new BorderLayout(0, UIStyle.SPACE_6));
        surface.setPreferredSize(new Dimension(650, 350));
        JPanel fields = new JPanel(new GridLayout(2, 1, 0, UIStyle.SPACE_4));
        fields.setOpaque(false);
        fields.add(createFieldGroup("Số thẻ hiện tại", currentCardField));
        fields.add(createFieldGroup("Số thẻ mới", newCardField));
        surface.add(fields, BorderLayout.CENTER);
        surface.add(createActions(submitButton), BorderLayout.SOUTH);
        return surface;
    }

    private void submit() {
        notification.clear();
        newCardField.clearError();
        String currentCard = currentCardField.getText().trim();
        String newCard = newCardField.getText().trim();
        if (!BankAccountService.isValidCardNumberFormat(newCard)) {
            newCardField.setError("Số thẻ phải gồm ít nhất 9 chữ số.");
            notification.showMessage("Số thẻ mới phải gồm ít nhất 9 chữ số.", NotificationPanel.Type.ERROR);
            return;
        }
        if (newCard.equals(currentCard)) {
            newCardField.setError("Số thẻ mới phải khác số thẻ hiện tại.");
            notification.showMessage("Số thẻ mới phải khác số thẻ hiện tại.", NotificationPanel.Type.ERROR);
            return;
        }
        if (!ConfirmDialog.show(SwingUtilities.getWindowAncestor(this), "Xác nhận đổi số thẻ",
                "Số thẻ hiện tại: " + currentCard + "\nSố thẻ mới: " + newCard
                        + "\nBạn sẽ dùng số thẻ mới cho lần đăng nhập tiếp theo.", "Cập nhật")) {
            return;
        }

        notification.showMessage("Đang cập nhật số thẻ...", NotificationPanel.Type.INFO);
        SwingWorkerRunner.run(new JComponent[]{submitButton, newCardField}, () -> {
            try (DBConnect connection = openConnection()) {
                BankAccountService.changeCardNumber(connection.connection, accountId, newCard);
                return BankAccountService.findAccountById(connection.connection, accountId);
            }
        }, account -> {
            currentCardField.setText(newCard);
            newCardField.setText("");
            accountChanged.accept(account);
            notification.showMessage("Cập nhật số thẻ thành công.", NotificationPanel.Type.SUCCESS, 5000);
        }, error -> notification.showMessage(failureMessage(error, "cập nhật số thẻ"), NotificationPanel.Type.ERROR));
    }

    @Override
    public void refreshData() {
        currentCardField.setText("Đang tải...");
        SwingWorkerRunner.run(new JComponent[]{submitButton}, () -> {
            try (DBConnect connection = openConnection()) {
                BankAccountService.AccountSummary account = BankAccountService.findAccountById(connection.connection, accountId);
                if (account == null) throw new IllegalStateException("Không tìm thấy tài khoản đang đăng nhập");
                return account;
            }
        }, account -> {
            currentCardField.setText(account.cardNumber == null ? "" : account.cardNumber);
            accountChanged.accept(account);
        }, error -> {
            currentCardField.setText("");
            notification.showMessage(failureMessage(error, "tải thông tin thẻ"), NotificationPanel.Type.ERROR);
        });
    }
}

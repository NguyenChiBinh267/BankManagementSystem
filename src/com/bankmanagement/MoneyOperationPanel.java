package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

final class MoneyOperationPanel extends BankPanel {
    enum Mode { DEPOSIT, WITHDRAW }

    private final Mode mode;
    private final Runnable dataChanged;
    private final StyledTextField amountField = new StyledTextField(20);
    private final StyledTextField noteField = new StyledTextField(20);
    private final JLabel balanceLabel = new JLabel("Số dư hiện tại: -");
    private final PrimaryButton submitButton;

    MoneyOperationPanel(int accountId, Mode mode, Runnable dataChanged) {
        super(accountId,
                mode == Mode.DEPOSIT ? "Nạp tiền" : "Rút tiền",
                mode == Mode.DEPOSIT ? "Ghi nhận tiền vào tài khoản" : "Số dư được kiểm tra lại trước khi giao dịch");
        this.mode = mode;
        this.dataChanged = dataChanged;
        submitButton = new PrimaryButton(mode == Mode.DEPOSIT ? "Xác nhận nạp" : "Xác nhận rút");
        submitButton.setIcon(new SmartBankIcon(mode == Mode.DEPOSIT ? SmartBankIcon.Type.PLUS : SmartBankIcon.Type.MINUS, 16, Color.WHITE));
        submitButton.addActionListener(e -> submit());
        setBody(center(createForm()));
    }

    private JPanel createForm() {
        RoundedPanel surface = createSurface();
        surface.setLayout(new BorderLayout(0, UIStyle.SPACE_6));
        surface.setPreferredSize(new Dimension(620, 390));
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        balanceLabel.setFont(UIStyle.BODY_STRONG_FONT);
        balanceLabel.setForeground(UIStyle.PRIMARY);
        balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel amountGroup = createFieldGroup("Số tiền (VND)", amountField);
        amountGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel noteGroup = createFieldGroup("Nội dung giao dịch", noteField);
        noteGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel helper = new JLabel("Có thể nhập 1000000 hoặc 1.000.000");
        helper.setFont(UIStyle.NOTE_FONT);
        helper.setForeground(UIStyle.MUTED_TEXT);
        helper.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(balanceLabel);
        form.add(Box.createVerticalStrut(UIStyle.SPACE_6));
        form.add(amountGroup);
        form.add(Box.createVerticalStrut(UIStyle.SPACE_2));
        form.add(helper);
        form.add(Box.createVerticalStrut(UIStyle.SPACE_4));
        form.add(noteGroup);
        surface.add(form, BorderLayout.CENTER);
        surface.add(createActions(submitButton), BorderLayout.SOUTH);
        return surface;
    }

    private void submit() {
        notification.clear();
        amountField.clearError();
        long amount;
        try {
            amount = InputValidators.parsePositiveAmount(amountField.getText());
        } catch (IllegalArgumentException ex) {
            amountField.setError(ex.getMessage());
            notification.showMessage(ex.getMessage(), NotificationPanel.Type.ERROR);
            return;
        }
        String note = noteField.getText().trim();
        if (note.length() > 255) {
            noteField.setError("Nội dung không được vượt quá 255 ký tự.");
            notification.showMessage("Nội dung không được vượt quá 255 ký tự.", NotificationPanel.Type.ERROR);
            return;
        }
        String verb = mode == Mode.DEPOSIT ? "nạp" : "rút";
        if (!ConfirmDialog.show(SwingUtilities.getWindowAncestor(this),
                "Xác nhận " + verb + " tiền",
                "Số tiền: " + UIStyle.formatMoney(amount) + (note.isEmpty() ? "" : "\nNội dung: " + note),
                "Xác nhận")) {
            return;
        }

        notification.showMessage("Đang xử lý giao dịch...", NotificationPanel.Type.INFO);
        SwingWorkerRunner.run(new JComponent[]{submitButton, amountField, noteField}, () -> {
            try (DBConnect connection = openConnection()) {
                return mode == Mode.DEPOSIT
                        ? BankAccountService.deposit(connection.connection, accountId, amount, note)
                        : BankAccountService.withdraw(connection.connection, accountId, amount, note);
            }
        }, newBalance -> {
            amountField.setText("");
            noteField.setText("");
            balanceLabel.setText("Số dư hiện tại: " + UIStyle.formatMoney(newBalance));
            notification.showMessage(
                    (mode == Mode.DEPOSIT ? "Nạp tiền" : "Rút tiền") + " thành công. Số dư đã được cập nhật.",
                    NotificationPanel.Type.SUCCESS,
                    5000);
            dataChanged.run();
        }, error -> notification.showMessage(failureMessage(error, verb + " tiền"), NotificationPanel.Type.ERROR));
    }

    @Override
    public void refreshData() {
        balanceLabel.setText("Đang tải số dư...");
        SwingWorkerRunner.run(new JComponent[0], () -> {
            try (DBConnect connection = openConnection()) {
                return BankAccountService.calculateBalance(connection.connection, accountId);
            }
        }, balance -> balanceLabel.setText("Số dư hiện tại: " + UIStyle.formatMoney(balance)),
                error -> {
                    balanceLabel.setText("Số dư hiện tại: -");
                    notification.showMessage(failureMessage(error, "tải số dư"), NotificationPanel.Type.ERROR);
                });
    }
}

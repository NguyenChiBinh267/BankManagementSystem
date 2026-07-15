package com.bankmanagement;

import javax.swing.*;
import java.awt.*;

final class TransferPanel extends BankPanel {
    private final Runnable dataChanged;
    private final StyledTextField receiverCardField = new StyledTextField(20);
    private final StyledTextField amountField = new StyledTextField(20);
    private final StyledTextField noteField = new StyledTextField(20);
    private final StyledTextField receiverInfoField = new StyledTextField(20);
    private final JLabel balanceLabel = new JLabel("Số dư hiện tại: -");
    private final SecondaryButton checkButton = new SecondaryButton("Kiểm tra");
    private final PrimaryButton transferButton = new PrimaryButton("Chuyển tiền");

    TransferPanel(int accountId, Runnable dataChanged) {
        super(accountId, "Chuyển tiền", "Kiểm tra người nhận và xác nhận trước khi giao dịch");
        this.dataChanged = dataChanged;
        receiverInfoField.setEditable(false);
        receiverInfoField.setBackground(UIStyle.SURFACE_SUBTLE);
        receiverInfoField.setText("Chưa kiểm tra người nhận");
        checkButton.setIcon(new SmartBankIcon(SmartBankIcon.Type.USER, 16, UIStyle.PRIMARY));
        transferButton.setIcon(new SmartBankIcon(SmartBankIcon.Type.TRANSFER, 16, Color.WHITE));
        checkButton.addActionListener(e -> previewReceiver());
        transferButton.addActionListener(e -> prepareTransfer());
        setBody(center(createForm()));
    }

    private JPanel createForm() {
        RoundedPanel surface = createSurface();
        surface.setLayout(new BorderLayout(0, UIStyle.SPACE_6));
        surface.setPreferredSize(new Dimension(700, 500));
        JPanel form = new JPanel(new GridLayout(4, 1, 0, UIStyle.SPACE_4));
        form.setOpaque(false);
        form.add(createFieldGroup("Số thẻ người nhận", receiverCardField));
        form.add(createFieldGroup("Người nhận", receiverInfoField));
        form.add(createFieldGroup("Số tiền (VND)", amountField));
        form.add(createFieldGroup("Nội dung", noteField));
        balanceLabel.setFont(UIStyle.BODY_STRONG_FONT);
        balanceLabel.setForeground(UIStyle.PRIMARY);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(balanceLabel, BorderLayout.WEST);
        top.add(checkButton, BorderLayout.EAST);
        surface.add(top, BorderLayout.NORTH);
        surface.add(form, BorderLayout.CENTER);
        surface.add(createActions(transferButton), BorderLayout.SOUTH);
        return surface;
    }

    private String readReceiverCard() {
        receiverCardField.clearError();
        String card = receiverCardField.getText().trim();
        if (!BankAccountService.isValidCardNumberFormat(card)) {
            receiverCardField.setError("Số thẻ phải gồm ít nhất 9 chữ số.");
            throw new IllegalArgumentException("Số thẻ người nhận phải gồm ít nhất 9 chữ số.");
        }
        return card;
    }

    private void previewReceiver() {
        notification.clear();
        String receiverCard;
        try {
            receiverCard = readReceiverCard();
        } catch (IllegalArgumentException ex) {
            notification.showMessage(ex.getMessage(), NotificationPanel.Type.ERROR);
            return;
        }
        receiverInfoField.setText("Đang kiểm tra...");
        SwingWorkerRunner.run(busyComponents(), () -> {
            try (DBConnect connection = openConnection()) {
                return BankAccountService.previewTransfer(connection.connection, accountId, receiverCard);
            }
        }, preview -> {
            receiverInfoField.setText(preview.receiver.displayName() + " - " + preview.receiver.cardNumber);
            balanceLabel.setText("Số dư hiện tại: " + UIStyle.formatMoney(preview.senderBalance));
            notification.showMessage("Đã xác minh người nhận.", NotificationPanel.Type.SUCCESS, 4000);
        }, error -> {
            receiverInfoField.setText("Chưa kiểm tra người nhận");
            notification.showMessage(failureMessage(error, "kiểm tra người nhận"), NotificationPanel.Type.ERROR);
        });
    }

    private void prepareTransfer() {
        notification.clear();
        amountField.clearError();
        noteField.clearError();
        String receiverCard;
        long amount;
        try {
            receiverCard = readReceiverCard();
            amount = InputValidators.parsePositiveAmount(amountField.getText());
        } catch (IllegalArgumentException ex) {
            if (amountField.getText().trim().isEmpty() || !amountField.getText().trim().replace(".", "").matches("\\d+")) {
                amountField.setError(ex.getMessage());
            }
            notification.showMessage(ex.getMessage(), NotificationPanel.Type.ERROR);
            return;
        }
        String note = noteField.getText().trim();
        if (note.length() > 255) {
            noteField.setError("Nội dung không được vượt quá 255 ký tự.");
            notification.showMessage("Nội dung không được vượt quá 255 ký tự.", NotificationPanel.Type.ERROR);
            return;
        }

        notification.showMessage("Đang kiểm tra thông tin giao dịch...", NotificationPanel.Type.INFO);
        SwingWorkerRunner.run(busyComponents(), () -> {
            try (DBConnect connection = openConnection()) {
                return BankAccountService.previewTransfer(connection.connection, accountId, receiverCard);
            }
        }, preview -> {
            receiverInfoField.setText(preview.receiver.displayName() + " - " + preview.receiver.cardNumber);
            balanceLabel.setText("Số dư hiện tại: " + UIStyle.formatMoney(preview.senderBalance));
            if (amount > preview.senderBalance) {
                amountField.setError("Số dư không đủ để chuyển tiền.");
                notification.showMessage("Số dư không đủ để thực hiện giao dịch.", NotificationPanel.Type.ERROR);
                return;
            }
            String message = "Người gửi: " + preview.sender.displayName()
                    + "\nSố thẻ gửi: " + preview.sender.cardNumber
                    + "\nNgười nhận: " + preview.receiver.displayName()
                    + "\nSố thẻ nhận: " + preview.receiver.cardNumber
                    + "\nSố tiền: " + UIStyle.formatMoney(amount)
                    + (note.isEmpty() ? "" : "\nNội dung: " + note);
            if (ConfirmDialog.show(SwingUtilities.getWindowAncestor(this),
                    "Xác nhận chuyển tiền", message, "Chuyển tiền")) {
                SwingUtilities.invokeLater(() -> executeTransfer(receiverCard, amount, note));
            } else {
                notification.clear();
            }
        }, error -> notification.showMessage(failureMessage(error, "kiểm tra giao dịch"), NotificationPanel.Type.ERROR));
    }

    private void executeTransfer(String receiverCard, long amount, String note) {
        notification.showMessage("Đang chuyển tiền. Không đóng ứng dụng...", NotificationPanel.Type.INFO);
        SwingWorkerRunner.run(busyComponents(), () -> {
            try (DBConnect connection = openConnection()) {
                return BankAccountService.transferByCardNumber(connection.connection, accountId, receiverCard, amount, note);
            }
        }, result -> {
            balanceLabel.setText("Số dư hiện tại: " + UIStyle.formatMoney(result.senderBalanceAfter));
            receiverCardField.setText("");
            receiverInfoField.setText("Chưa kiểm tra người nhận");
            amountField.setText("");
            noteField.setText("");
            notification.showMessage(
                    "Chuyển tiền thành công cho " + result.receiver.displayName() + ". Số dư đã được cập nhật.",
                    NotificationPanel.Type.SUCCESS,
                    6000);
            dataChanged.run();
        }, error -> notification.showMessage(failureMessage(error, "chuyển tiền"), NotificationPanel.Type.ERROR));
    }

    private JComponent[] busyComponents() {
        return new JComponent[]{checkButton, transferButton, receiverCardField, amountField, noteField};
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

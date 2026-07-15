package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

final class FastCashPanel extends BankPanel {
    private static final long[] AMOUNTS = {100_000L, 200_000L, 500_000L, 1_000_000L, 2_000_000L, 5_000_000L};
    private final Runnable dataChanged;
    private final JLabel balanceLabel = new JLabel("Số dư hiện tại: -");
    private final List<SecondaryButton> amountButtons = new ArrayList<>();

    FastCashPanel(int accountId, Runnable dataChanged) {
        super(accountId, "Rút tiền nhanh", "Chọn một mệnh giá và xác nhận giao dịch");
        this.dataChanged = dataChanged;
        setBody(center(createContent()));
    }

    private JPanel createContent() {
        RoundedPanel surface = createSurface();
        surface.setLayout(new BorderLayout(0, UIStyle.SPACE_6));
        surface.setPreferredSize(new Dimension(650, 390));
        balanceLabel.setFont(UIStyle.BODY_STRONG_FONT);
        balanceLabel.setForeground(UIStyle.PRIMARY);
        JPanel grid = new JPanel(new GridLayout(3, 2, UIStyle.SPACE_4, UIStyle.SPACE_4));
        grid.setOpaque(false);
        for (long amount : AMOUNTS) {
            SecondaryButton button = new SecondaryButton(UIStyle.formatMoney(amount));
            button.setIcon(new SmartBankIcon(SmartBankIcon.Type.FLASH, 16, UIStyle.PRIMARY));
            button.addActionListener(e -> withdraw(amount, button));
            amountButtons.add(button);
            grid.add(button);
        }
        surface.add(balanceLabel, BorderLayout.NORTH);
        surface.add(grid, BorderLayout.CENTER);
        return surface;
    }

    private void withdraw(long amount, SecondaryButton trigger) {
        if (!ConfirmDialog.show(SwingUtilities.getWindowAncestor(this),
                "Xác nhận rút tiền nhanh",
                "Số tiền: " + UIStyle.formatMoney(amount) + "\nGiao dịch sẽ được ghi vào lịch sử tài khoản.",
                "Rút tiền")) {
            return;
        }
        notification.showMessage("Đang xử lý giao dịch...", NotificationPanel.Type.INFO);
        JComponent[] busy = amountButtons.toArray(JComponent[]::new);
        SwingWorkerRunner.run(busy, () -> {
            try (DBConnect connection = openConnection()) {
                return BankAccountService.withdraw(connection.connection, accountId, amount, "Rút tiền nhanh");
            }
        }, newBalance -> {
            balanceLabel.setText("Số dư hiện tại: " + UIStyle.formatMoney(newBalance));
            notification.showMessage("Rút tiền thành công. Số dư đã được cập nhật.", NotificationPanel.Type.SUCCESS, 5000);
            dataChanged.run();
        }, error -> notification.showMessage(failureMessage(error, "rút tiền"), NotificationPanel.Type.ERROR));
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

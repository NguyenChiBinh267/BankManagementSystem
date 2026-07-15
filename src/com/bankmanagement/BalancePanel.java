package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

final class BalancePanel extends BankPanel {
    private final Consumer<BankAccountService.AccountSummary> accountConsumer;
    private final BalanceCard balanceCard = new BalanceCard("Số dư khả dụng");
    private final PrimaryButton refreshButton = new PrimaryButton("Làm mới");

    BalancePanel(int accountId, Consumer<BankAccountService.AccountSummary> accountConsumer) {
        super(accountId, "Tra cứu số dư", "Số dư được tính từ toàn bộ lịch sử giao dịch");
        this.accountConsumer = accountConsumer;
        refreshButton.setIcon(new SmartBankIcon(SmartBankIcon.Type.REFRESH, 16, Color.WHITE));
        refreshButton.addActionListener(e -> refreshData());
        setBody(center(createContent()));
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, UIStyle.SPACE_4));
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(620, 280));
        content.add(balanceCard, BorderLayout.CENTER);
        content.add(createActions(refreshButton), BorderLayout.SOUTH);
        return content;
    }

    @Override
    public void refreshData() {
        balanceCard.setLoading();
        SwingWorkerRunner.run(new JComponent[]{refreshButton}, () -> {
            try (DBConnect connection = openConnection()) {
                return BankAccountService.loadAccountSnapshot(connection.connection, accountId, 1);
            }
        }, snapshot -> {
            balanceCard.setBalance(snapshot.balance);
            balanceCard.setDetail("Số thẻ: " + snapshot.account.cardNumber);
            accountConsumer.accept(snapshot.account);
            notification.clear();
        }, error -> {
            balanceCard.setDetail("Không thể tải số dư");
            notification.showMessage(failureMessage(error, "tải số dư"), NotificationPanel.Type.ERROR);
        });
    }
}

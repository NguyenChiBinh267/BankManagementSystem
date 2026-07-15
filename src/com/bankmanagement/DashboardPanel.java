package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

final class DashboardPanel extends BankPanel {
    private final Consumer<AppRoute> navigator;
    private final Consumer<BankAccountService.AccountSummary> accountConsumer;
    private final BalanceCard balanceCard = new BalanceCard("Số dư khả dụng");
    private final JLabel cardValue = createMetricValue();
    private final JLabel customerValue = createMetricValue();
    private final JPanel transactionList = new JPanel();
    private boolean loading;

    DashboardPanel(int accountId,
                   Consumer<AppRoute> navigator,
                   Consumer<BankAccountService.AccountSummary> accountConsumer) {
        super(accountId, "Tổng quan tài khoản", "Thông tin quan trọng và thao tác thường dùng");
        this.navigator = navigator;
        this.accountConsumer = accountConsumer;
        setBody(createBody());
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(0, UIStyle.SPACE_4));
        body.setOpaque(false);
        JPanel metrics = new JPanel(new GridLayout(1, 3, UIStyle.SPACE_4, 0));
        metrics.setOpaque(false);
        metrics.add(balanceCard);
        metrics.add(createInfoCard("Số thẻ", cardValue, "Thẻ đang sử dụng", SmartBankIcon.Type.CARD));
        metrics.add(createInfoCard("Chủ tài khoản", customerValue, "Thông tin khách hàng", SmartBankIcon.Type.USER));
        body.add(metrics, BorderLayout.NORTH);

        JPanel lower = new JPanel(new GridLayout(1, 2, UIStyle.SPACE_4, 0));
        lower.setOpaque(false);
        lower.add(createQuickActions());
        lower.add(createRecentTransactions());
        body.add(lower, BorderLayout.CENTER);
        return body;
    }

    private JPanel createQuickActions() {
        RoundedPanel surface = createSurface();
        surface.setLayout(new BorderLayout(0, UIStyle.SPACE_4));
        JLabel title = new JLabel("Thao tác nhanh");
        title.setFont(UIStyle.SUBTITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        JPanel actions = new JPanel(new GridLayout(2, 2, UIStyle.SPACE_3, UIStyle.SPACE_3));
        actions.setOpaque(false);
        actions.add(createAction("Nạp tiền", SmartBankIcon.Type.PLUS, AppRoute.DEPOSIT));
        actions.add(createAction("Rút tiền", SmartBankIcon.Type.MINUS, AppRoute.WITHDRAW));
        actions.add(createAction("Chuyển tiền", SmartBankIcon.Type.TRANSFER, AppRoute.TRANSFER));
        actions.add(createAction("Lịch sử", SmartBankIcon.Type.HISTORY, AppRoute.TRANSACTIONS));
        surface.add(title, BorderLayout.NORTH);
        surface.add(actions, BorderLayout.CENTER);
        return surface;
    }

    private JButton createAction(String text, SmartBankIcon.Type iconType, AppRoute route) {
        SecondaryButton button = new SecondaryButton(text);
        button.setIcon(new SmartBankIcon(iconType, 18, UIStyle.PRIMARY));
        button.addActionListener(e -> navigator.accept(route));
        return button;
    }

    private JPanel createRecentTransactions() {
        RoundedPanel surface = createSurface();
        surface.setLayout(new BorderLayout(0, UIStyle.SPACE_3));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Giao dịch gần đây");
        title.setFont(UIStyle.SUBTITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        JButton viewAll = new JButton("Xem tất cả");
        viewAll.setFont(UIStyle.BUTTON_FONT);
        viewAll.setForeground(UIStyle.PRIMARY);
        viewAll.setContentAreaFilled(false);
        viewAll.setBorder(new EmptyBorder(6, 8, 6, 8));
        viewAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAll.addActionListener(e -> navigator.accept(AppRoute.TRANSACTIONS));
        header.add(title, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);
        transactionList.setOpaque(false);
        transactionList.setLayout(new BoxLayout(transactionList, BoxLayout.Y_AXIS));
        surface.add(header, BorderLayout.NORTH);
        surface.add(transactionList, BorderLayout.CENTER);
        return surface;
    }

    private JPanel createInfoCard(String title, JLabel value, String detail, SmartBankIcon.Type iconType) {
        RoundedPanel card = createSurface();
        card.setLayout(new BorderLayout(0, UIStyle.SPACE_2));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyle.BODY_STRONG_FONT);
        titleLabel.setForeground(UIStyle.MUTED_TEXT);
        titleLabel.setIcon(new SmartBankIcon(iconType, 18, UIStyle.PRIMARY));
        titleLabel.setIconTextGap(UIStyle.SPACE_2);
        JLabel detailLabel = new JLabel(detail);
        detailLabel.setFont(UIStyle.NOTE_FONT);
        detailLabel.setForeground(UIStyle.MUTED_TEXT);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.add(detailLabel, BorderLayout.SOUTH);
        return card;
    }

    private static JLabel createMetricValue() {
        JLabel label = new JLabel("-");
        label.setFont(UIStyle.SUBTITLE_FONT);
        label.setForeground(UIStyle.TEXT);
        return label;
    }

    @Override
    public void refreshData() {
        if (loading) return;
        loading = true;
        balanceCard.setLoading();
        cardValue.setText("Đang tải...");
        customerValue.setText("Đang tải...");
        notification.showMessage("Đang cập nhật dữ liệu tài khoản...", NotificationPanel.Type.INFO);
        SwingWorkerRunner.run(new JComponent[0], () -> {
            try (DBConnect connection = openConnection()) {
                return BankAccountService.loadAccountSnapshot(connection.connection, accountId, 5);
            }
        }, snapshot -> {
            loading = false;
            notification.clear();
            balanceCard.setBalance(snapshot.balance);
            balanceCard.setDetail("Cập nhật từ lịch sử giao dịch");
            cardValue.setText(snapshot.account.cardNumber == null ? "-" : maskCard(snapshot.account.cardNumber));
            customerValue.setText(snapshot.account.displayName());
            accountConsumer.accept(snapshot.account);
            renderTransactions(snapshot.recentTransactions);
        }, error -> {
            loading = false;
            balanceCard.setDetail("Không thể tải số dư");
            cardValue.setText("-");
            customerValue.setText("-");
            renderTransactions(List.of());
            notification.showMessage(failureMessage(error, "tải tổng quan"), NotificationPanel.Type.ERROR);
        });
    }

    private void renderTransactions(List<BankAccountService.TransactionRecord> transactions) {
        transactionList.removeAll();
        if (transactions.isEmpty()) {
            JLabel empty = new JLabel("Chưa có giao dịch.");
            empty.setFont(UIStyle.BODY_FONT);
            empty.setForeground(UIStyle.MUTED_TEXT);
            empty.setBorder(new EmptyBorder(UIStyle.SPACE_6, 0, UIStyle.SPACE_6, 0));
            transactionList.add(empty);
        } else {
            for (int index = 0; index < transactions.size(); index++) {
                BankAccountService.TransactionRecord transaction = transactions.get(index);
                String date = transaction.transactionDate == null ? "-" : UIStyle.DATE_TIME_FORMAT.format(transaction.transactionDate);
                String amount = (transaction.moneyIn ? "+" : "-") + UIStyle.formatMoney(transaction.amount);
                TransactionRow row = new TransactionRow(transaction.displayType, date, amount, transaction.moneyIn);
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                transactionList.add(row);
                if (index < transactions.size() - 1) {
                    JSeparator separator = new JSeparator();
                    separator.setForeground(UIStyle.BORDER);
                    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    separator.setBorder(new MatteBorder(1, 0, 0, 0, UIStyle.BORDER));
                    transactionList.add(separator);
                }
            }
            transactionList.add(Box.createVerticalGlue());
        }
        transactionList.revalidate();
        transactionList.repaint();
    }

    private String maskCard(String value) {
        return value.length() <= 4 ? value : "•••• " + value.substring(value.length() - 4);
    }
}

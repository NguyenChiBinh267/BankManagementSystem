package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Main extends JFrame implements ActionListener {
    private static final Color SIDEBAR = new Color(17, 24, 39);
    private static final Color SIDEBAR_ACTIVE = new Color(37, 99, 235);
    private static final Color SIDEBAR_HOVER = new Color(31, 41, 55);
    private static final Color CONTENT_BACKGROUND = new Color(244, 247, 251);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT = new Color(17, 24, 39);
    private static final Color MUTED_TEXT = new Color(107, 114, 128);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color PRIMARY = new Color(108, 92, 231);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final NumberFormat MONEY_FORMAT = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    JLabel bankIconLabel, chooseServiceLabel, logoutLabel;
    JButton depositBtn, withdrawBtn, fastCashBtn, miniStatementBtn, pinChangeBtn, balanceBtn, changeCardBtn, transferCardBtn, exitBtn, returnBtn;
    int accountId;
    Main(int accountId){
        super("Màn hình chính");
        this.accountId = accountId;

        initializeButtons();

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(CONTENT_BACKGROUND);
        page.add(createSidebar(), BorderLayout.WEST);
        page.add(createDashboard(), BorderLayout.CENTER);

        setContentPane(page);
        UIStyle.showFrame(this, 1200, 760);
    }

    private void initializeButtons() {
        depositBtn = new JButton("Nạp tiền");
        styleSidebarButton(depositBtn, false);
        depositBtn.addActionListener(this);

        withdrawBtn = new JButton("Rút tiền");
        styleSidebarButton(withdrawBtn, false);
        withdrawBtn.addActionListener(this);

        transferCardBtn = new JButton("Chuyển tiền");
        styleSidebarButton(transferCardBtn, false);
        transferCardBtn.addActionListener(this);

        fastCashBtn = new JButton("Rút tiền nhanh");
        styleSecondaryButton(fastCashBtn);
        fastCashBtn.addActionListener(this);

        miniStatementBtn = new JButton("Giao dịch gần đây");
        styleSidebarButton(miniStatementBtn, false);
        miniStatementBtn.addActionListener(this);

        pinChangeBtn = new JButton("Đổi mã PIN");
        styleSidebarButton(pinChangeBtn, false);
        pinChangeBtn.addActionListener(this);

        changeCardBtn = new JButton("Đổi số thẻ");
        styleSidebarButton(changeCardBtn, false);
        changeCardBtn.addActionListener(this);

        balanceBtn = new JButton("Tra cứu số dư");
        styleSidebarButton(balanceBtn, false);
        balanceBtn.addActionListener(this);

        exitBtn = new JButton("Thoát ứng dụng");
        styleSidebarButton(exitBtn, false);
        exitBtn.addActionListener(this);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(24, 18, 24, 18));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new BorderLayout(10, 0));
        brand.setOpaque(false);
        bankIconLabel = UIStyle.createBankIconLabel(42);
        brand.add(bankIconLabel, BorderLayout.WEST);

        JLabel brandName = new JLabel("SmartBank");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandName.setForeground(Color.WHITE);
        brand.add(brandName, BorderLayout.CENTER);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        top.add(brand);
        top.add(Box.createVerticalStrut(28));

        JButton overviewBtn = new JButton("Tổng quan");
        styleSidebarButton(overviewBtn, true);
        overviewBtn.addActionListener(e -> {
            new Main(accountId);
            setVisible(false);
        });
        top.add(overviewBtn);
        top.add(Box.createVerticalStrut(8));
        top.add(depositBtn);
        top.add(Box.createVerticalStrut(8));
        top.add(withdrawBtn);
        top.add(Box.createVerticalStrut(8));
        top.add(transferCardBtn);
        top.add(Box.createVerticalStrut(8));
        top.add(miniStatementBtn);
        top.add(Box.createVerticalStrut(8));
        top.add(pinChangeBtn);
        top.add(Box.createVerticalStrut(8));
        top.add(changeCardBtn);
        top.add(Box.createVerticalStrut(8));
        top.add(balanceBtn);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        logoutLabel = new JLabel("  Đăng xuất");
        logoutLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutLabel.setForeground(new Color(229, 231, 235));
        logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutLabel.setBorder(new EmptyBorder(11, 10, 11, 10));
        logoutLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Login();
                setVisible(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                logoutLabel.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutLabel.setForeground(new Color(229, 231, 235));
            }
        });

        bottom.add(logoutLabel);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(exitBtn);

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel createDashboard() {
        DashboardData data = loadDashboardData();

        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setBackground(CONTENT_BACKGROUND);
        content.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        chooseServiceLabel = new JLabel("Tổng quan tài khoản");
        chooseServiceLabel.setFont(TITLE_FONT);
        chooseServiceLabel.setForeground(TEXT);

        JLabel welcome = new JLabel("Xin chào, " + data.customerName);
        welcome.setFont(BODY_FONT);
        welcome.setForeground(MUTED_TEXT);

        titlePanel.add(chooseServiceLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(welcome);
        header.add(titlePanel, BorderLayout.WEST);

        content.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        center.add(createSummaryCards(data), BorderLayout.NORTH);

        JPanel lower = new JPanel(new BorderLayout(0, 18));
        lower.setOpaque(false);
        lower.add(createQuickActions(), BorderLayout.NORTH);
        lower.add(createRecentTransactionsCard(data.transactionModel), BorderLayout.CENTER);
        center.add(lower, BorderLayout.CENTER);

        content.add(center, BorderLayout.CENTER);
        return content;
    }

    private JPanel createSummaryCards(DashboardData data) {
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        cards.add(createMetricCard("Số dư hiện tại", formatMoney(data.balance) + " đồng", "Cập nhật theo lịch sử giao dịch", PRIMARY));
        cards.add(createMetricCard("Số thẻ", data.cardNumber, "Tài khoản đang đăng nhập", BLUE));
        cards.add(createMetricCard("Chủ tài khoản", data.customerName, "Thông tin khách hàng", new Color(20, 184, 166)));
        return cards;
    }

    private JPanel createMetricCard(String title, String value, String note, Color accent) {
        RoundedPanel card = new RoundedPanel(22, CARD_BACKGROUND, BORDER);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(22, 22, 20, 22));
        card.setPreferredSize(new Dimension(0, 136));

        JPanel accentLine = new JPanel();
        accentLine.setBackground(accent);
        accentLine.setPreferredSize(new Dimension(52, 5));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(MUTED_TEXT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT);

        JLabel noteLabel = new JLabel(note);
        noteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noteLabel.setForeground(MUTED_TEXT);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(8));
        text.add(valueLabel);
        text.add(Box.createVerticalStrut(8));
        text.add(noteLabel);

        card.add(accentLine, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createQuickActions() {
        RoundedPanel card = new RoundedPanel(22, CARD_BACKGROUND, BORDER);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel title = new JLabel("Dịch vụ nhanh");
        title.setFont(SECTION_FONT);
        title.setForeground(TEXT);
        card.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 4, 14, 14));
        grid.setOpaque(false);
        grid.add(createActionCard("Nạp tiền", "Thêm tiền vào tài khoản", PRIMARY, this::openDeposit));
        grid.add(createActionCard("Rút tiền", "Rút tiền theo số nhập", BLUE, this::openWithdraw));
        grid.add(createActionCard("Rút tiền nhanh", "Chọn mệnh giá cố định", new Color(20, 184, 166), this::openFastCash));
        grid.add(createActionCard("Chuyển tiền", "Chuyển bằng số thẻ", new Color(245, 158, 11), this::openTransfer));
        grid.add(createActionCard("Giao dịch", "Xem lịch sử gần đây", new Color(99, 102, 241), this::openMiniStatement));
        grid.add(createActionCard("Đổi mã PIN", "Cập nhật mã bảo mật", new Color(236, 72, 153), this::openPinChange));
        grid.add(createActionCard("Đổi số thẻ", "Cập nhật số thẻ mới", new Color(14, 165, 233), this::openChangeCard));
        grid.add(createActionCard("Tra cứu số dư", "Hiển thị số dư hiện tại", new Color(34, 197, 94), this::showBalanceDialog));

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActionCard(String title, String note, Color accent, Runnable action) {
        RoundedPanel card = new RoundedPanel(18, new Color(249, 250, 251), BORDER);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel marker = new JPanel();
        marker.setBackground(accent);
        marker.setPreferredSize(new Dimension(5, 1));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(TEXT);

        JLabel noteLabel = new JLabel(note);
        noteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noteLabel.setForeground(MUTED_TEXT);

        text.add(titleLabel);
        text.add(Box.createVerticalStrut(6));
        text.add(noteLabel);

        JLabel arrow = new JLabel(">");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 18));
        arrow.setForeground(accent);

        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        };

        card.addMouseListener(listener);
        marker.addMouseListener(listener);
        text.addMouseListener(listener);
        titleLabel.addMouseListener(listener);
        noteLabel.addMouseListener(listener);
        arrow.addMouseListener(listener);

        card.add(marker, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.EAST);
        return card;
    }

    private JPanel createRecentTransactionsCard(DefaultTableModel model) {
        RoundedPanel card = new RoundedPanel(22, CARD_BACKGROUND, BORDER);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("5 giao dịch gần nhất");
        title.setFont(SECTION_FONT);
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);

        JButton viewAllBtn = new JButton("Xem tất cả");
        styleSecondaryButton(viewAllBtn);
        viewAllBtn.setPreferredSize(new Dimension(120, 36));
        viewAllBtn.addActionListener(e -> openMiniStatement());
        header.add(viewAllBtn, BorderLayout.EAST);

        JTable table = new JTable(model);
        styleDashboardTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private DashboardData loadDashboardData() {
        DashboardData data = new DashboardData();
        try (DBConnect c = new DBConnect()) {
            if (c.connection == null) {
                data.transactionModel.addRow(new Object[]{"-", "Không thể kết nối cơ sở dữ liệu", "-", ""});
                return data;
            }

            BankAccountService.AccountSummary account = BankAccountService.findAccountById(c.connection, accountId);
            if (account != null) {
                data.customerName = account.displayName();
                data.cardNumber = account.cardNumber == null || account.cardNumber.trim().isEmpty() ? "-" : account.cardNumber;
            }

            data.balance = BankAccountService.calculateBalance(c.connection, accountId);
            loadRecentTransactions(c.connection, data.transactionModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            data.transactionModel.setRowCount(0);
            data.transactionModel.addRow(new Object[]{"-", "Không thể tải dữ liệu", "-", ""});
        }
        return data;
    }

    private void loadRecentTransactions(Connection connection, DefaultTableModel model) throws Exception {
        String query = """
                SELECT TransactionID, TransactionDate, TransactionType, Amount
                FROM Bank
                WHERE AccountID = ?
                ORDER BY TransactionDate DESC
                LIMIT 5
        """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp transactionDate = rs.getTimestamp("TransactionDate");
                    String transactionType = rs.getString("TransactionType");
                    long amount = rs.getLong("Amount");
                    model.addRow(new Object[]{
                            rs.getInt("TransactionID"),
                            transactionDate == null ? "-" : DATE_FORMAT.format(transactionDate),
                            BankAccountService.toShortDisplayTransactionType(transactionType),
                            formatMoney(amount)
                    });
                }
            }
        }

        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"-", "Chưa có giao dịch", "-", ""});
        }
    }

    private DefaultTableModel createTransactionModel() {
        return new DefaultTableModel(new Object[]{"Mã giao dịch", "Ngày giao dịch", "Loại giao dịch", "Số tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void styleDashboardTable(JTable table) {
        table.setFont(BODY_FONT);
        table.setRowHeight(34);
        table.setSelectionBackground(new Color(230, 237, 255));
        table.setSelectionForeground(TEXT);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(TEXT);
        header.setBackground(new Color(248, 250, 252));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer();
        amountRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(amountRenderer);
    }

    private void styleSidebarButton(JButton button, boolean active) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(active ? SIDEBAR_ACTIVE : SIDEBAR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(11, 14, 11, 14));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        if (!active) {
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(SIDEBAR_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(SIDEBAR);
                }
            });
        }
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(PRIMARY);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 14, 8, 14));
    }

    private String formatMoney(long amount) {
        return MONEY_FORMAT.format(amount);
    }

    private void showBalanceDialog() {
        try (DBConnect c = new DBConnect()) {
            if (c.connection == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }
            long balance = BankAccountService.calculateBalance(c.connection, accountId);
            JOptionPane.showMessageDialog(null, "Số dư: " + formatMoney(balance) + " đồng");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể xem số dư");
        }
    }

    private void openDeposit() {
        new Deposit(accountId);
        setVisible(false);
    }

    private void openWithdraw() {
        new WithDraw(accountId);
        setVisible(false);
    }

    private void openFastCash() {
        new FastCash(accountId);
        setVisible(false);
    }

    private void openTransfer() {
        new TransferByCardNumber(accountId);
        setVisible(false);
    }

    private void openChangeCard() {
        new ChangeCardNumber(accountId);
        setVisible(false);
    }

    private void openPinChange() {
        new PinChange(accountId);
        setVisible(false);
    }

    private void openMiniStatement() {
        new MiniStatement(accountId);
        setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            if(e.getSource()==depositBtn){
                openDeposit();
            }
            else if(e.getSource()==withdrawBtn){
                openWithdraw();
            }
            else if(e.getSource()==fastCashBtn){
                openFastCash();
            }
            else if(e.getSource()==balanceBtn){
                showBalanceDialog();
            }
            else if(e.getSource()==transferCardBtn){
                openTransfer();
            }
            else if(e.getSource()==changeCardBtn){
                openChangeCard();
            }
            else if(e.getSource()==pinChangeBtn){
                openPinChange();
            }
            else if(e.getSource()==miniStatementBtn){
                openMiniStatement();
            }
            else if(e.getSource()==exitBtn){
                System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private class DashboardData {
        String customerName = "Khách hàng";
        String cardNumber = "-";
        long balance = 0;
        DefaultTableModel transactionModel = createTransactionModel();
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fillColor;
        private final Color borderColor;

        RoundedPanel(int radius, Color fillColor, Color borderColor) {
            this.radius = radius;
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        new Main(0);
    }
}

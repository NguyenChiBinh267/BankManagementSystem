package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MiniStatement extends JFrame implements ActionListener {

    private static final Color SIDEBAR = new Color(17, 24, 39);
    private static final Color CONTENT_BACKGROUND = new Color(244, 247, 251);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT = new Color(17, 24, 39);
    private static final Color MUTED_TEXT = new Color(107, 114, 128);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color PRIMARY = new Color(108, 92, 231);
    private static final Color BLUE = new Color(37, 99, 235);

    JLabel bankIconLabel, titleLabel;
    JTable transactionTable;
    JButton returnBtn;
    int accountId;

    public MiniStatement(int accountId) {
        super("Giao dịch gần đây");
        this.accountId = accountId;

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(CONTENT_BACKGROUND);
        page.add(createSidebar(), BorderLayout.WEST);
        page.add(createContent(), BorderLayout.CENTER);

        setContentPane(page);
        UIStyle.showFrame(this, 1200, 760);
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
        top.add(Box.createVerticalStrut(30));
        top.add(createSidebarLabel("Tổng quan", false));
        top.add(Box.createVerticalStrut(8));
        top.add(createSidebarLabel("Nạp tiền", false));
        top.add(Box.createVerticalStrut(8));
        top.add(createSidebarLabel("Rút tiền", false));
        top.add(Box.createVerticalStrut(8));
        top.add(createSidebarLabel("Chuyển tiền", false));
        top.add(Box.createVerticalStrut(8));
        top.add(createSidebarLabel("Giao dịch gần đây", true));
        top.add(Box.createVerticalStrut(8));
        top.add(createSidebarLabel("Đổi mã PIN", false));

        sidebar.add(top, BorderLayout.NORTH);
        return sidebar;
    }

    private JLabel createSidebarLabel(String text, boolean active) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(active ? Color.WHITE : new Color(203, 213, 225));
        label.setOpaque(true);
        label.setBackground(active ? BLUE : SIDEBAR);
        label.setBorder(new EmptyBorder(11, 14, 11, 14));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return label;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setBackground(CONTENT_BACKGROUND);
        content.setBorder(new EmptyBorder(34, 38, 34, 38));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Giao dịch gần đây");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT);

        JLabel subtitle = new JLabel("Theo dõi 10 giao dịch mới nhất của tài khoản");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED_TEXT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitle);

        returnBtn = new JButton("Quay lại");
        styleSecondaryButton(returnBtn);
        returnBtn.addActionListener(this);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(returnBtn, BorderLayout.EAST);

        content.add(header, BorderLayout.NORTH);
        content.add(createTransactionCard(), BorderLayout.CENTER);
        return content;
    }

    private JPanel createTransactionCard() {
        String[] columns = {"Mã GD", "Thời gian", "Loại giao dịch", "Số tiền"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);

        styleTransactionTable();
        configureTransactionTable();

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(new LineBorder(BORDER, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        RoundedPanel card = new RoundedPanel(24, CARD_BACKGROUND, BORDER);
        card.setLayout(new BorderLayout(0, 18));
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel cardTitle = new JLabel("Lịch sử giao dịch");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardTitle.setForeground(TEXT);

        JLabel cardNote = new JLabel("Hiển thị 10 giao dịch mới nhất");
        cardNote.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardNote.setForeground(MUTED_TEXT);

        cardHeader.add(cardTitle, BorderLayout.WEST);
        cardHeader.add(cardNote, BorderLayout.EAST);

        card.add(cardHeader, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        loadTransactions(tableModel);
        return card;
    }

    private void loadTransactions(DefaultTableModel tableModel) {
        try {
            DBConnect c = new DBConnect();
            String q = """
                    SELECT TransactionID, TransactionDate, TransactionType, Amount
                    FROM Bank
                    WHERE AccountID = ?
                    ORDER BY TransactionDate DESC
                    LIMIT 10
            """;
            PreparedStatement ps = c.connection.prepareStatement(q);
            ps.setInt(1, accountId);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                String transactionType = resultSet.getString("TransactionType");
                Object[] row = {
                        resultSet.getInt("TransactionID"),
                        resultSet.getTimestamp("TransactionDate"),
                        BankAccountService.toShortDisplayTransactionType(transactionType),
                        resultSet.getLong("Amount")
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể tải giao dịch gần đây");
        }

    }

    private void styleTransactionTable() {
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transactionTable.setForeground(TEXT);
        transactionTable.setRowHeight(36);
        transactionTable.setSelectionBackground(new Color(230, 237, 255));
        transactionTable.setSelectionForeground(TEXT);
        transactionTable.setGridColor(BORDER);
        transactionTable.setShowVerticalLines(false);
        transactionTable.setFillsViewportHeight(true);

        JTableHeader header = transactionTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(TEXT);
        header.setBackground(new Color(248, 250, 252));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
    }

    private void configureTransactionTable() {
        transactionTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        transactionTable.setPreferredScrollableViewportSize(new Dimension(920, 430));
        transactionTable.getTableHeader().setResizingAllowed(true);

        TableColumnModel columnModel = transactionTable.getColumnModel();
        setColumnWidth(columnModel, 0, 80, 90);
        setColumnWidth(columnModel, 1, 170, 210);
        setColumnWidth(columnModel, 2, 150, 190);
        setColumnWidth(columnModel, 3, 120, 150);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        columnModel.getColumn(3).setCellRenderer(rightRenderer);
    }

    private void setColumnWidth(TableColumnModel columnModel, int columnIndex, int minWidth, int preferredWidth) {
        columnModel.getColumn(columnIndex).setMinWidth(minWidth);
        columnModel.getColumn(columnIndex).setPreferredWidth(preferredWidth);
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(TEXT);
        button.setBackground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 42));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new LineBorder(BORDER, 1, true));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnBtn) {
            new Main(accountId);
            dispose();
        }
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
        new MiniStatement(0);
    }
}

package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Main extends JFrame implements ActionListener {
    private static final Color SIDEBAR = new Color(17, 24, 39);
    private static final Color SIDEBAR_ACTIVE = new Color(37, 99, 235);
    private static final Color SIDEBAR_HOVER = new Color(31, 41, 55);
    private static final Color CONTENT_BACKGROUND = new Color(244, 247, 251);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT = new Color(17, 24, 39);
    private static final Color MUTED_TEXT = new Color(107, 114, 128);
    private static final Color BORDER = new Color(209, 213, 219);
    private static final Color PRIMARY = new Color(108, 92, 231);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final NumberFormat MONEY_FORMAT = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private static final String OVERVIEW = "OVERVIEW";
    private static final String DEPOSIT = "DEPOSIT";
    private static final String WITHDRAW = "WITHDRAW";
    private static final String FAST_CASH = "FAST_CASH";
    private static final String TRANSFER = "TRANSFER";
    private static final String MINI_STATEMENT = "MINI_STATEMENT";
    private static final String PIN_CHANGE = "PIN_CHANGE";
    private static final String CHANGE_CARD = "CHANGE_CARD";
    private static final String BALANCE = "BALANCE";

    JLabel bankIconLabel, chooseServiceLabel, logoutLabel;
    JButton overviewBtn, depositBtn, withdrawBtn, fastCashBtn, miniStatementBtn, pinChangeBtn, balanceBtn, changeCardBtn, transferCardBtn, exitBtn, returnBtn, logoutBtn;
    JPanel sidebarPanel, contentPanel;
    CardLayout cardLayout;
    int accountId;

    private final Map<String, JButton> menuButtons = new LinkedHashMap<>();
    private JLabel overviewBalanceValue, overviewCardValue, overviewNameValue, balancePanelValue, balancePanelCardValue;
    private DefaultTableModel overviewTransactionModel, miniStatementModel;
    private JTextField depositAmountField, withdrawAmountField, transferReceiverCardNumberField, transferAmountField, transferNoteField;
    private JTextField transferReceiverInfoField, currentCardNumberField, newCardNumberField;
    private JTextField pinTextField, pinCheckTextField;

    Main(int accountId){
        super("Màn hình chính");
        this.accountId = accountId;

        initializeButtons();

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(CONTENT_BACKGROUND);

        sidebarPanel = createSidebar();
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(CONTENT_BACKGROUND);
        contentPanel.add(createScrollableContent(createOverviewPanel()), OVERVIEW);
        contentPanel.add(createScrollableContent(createDepositPanel()), DEPOSIT);
        contentPanel.add(createScrollableContent(createWithdrawPanel()), WITHDRAW);
        contentPanel.add(createScrollableContent(createFastCashPanel()), FAST_CASH);
        contentPanel.add(createScrollableContent(createTransferPanel()), TRANSFER);
        contentPanel.add(createScrollableContent(createMiniStatementPanel()), MINI_STATEMENT);
        contentPanel.add(createScrollableContent(createPinChangePanel()), PIN_CHANGE);
        contentPanel.add(createScrollableContent(createChangeCardPanel()), CHANGE_CARD);
        contentPanel.add(createScrollableContent(createBalancePanel()), BALANCE);

        page.add(sidebarPanel, BorderLayout.WEST);
        page.add(contentPanel, BorderLayout.CENTER);

        setContentPane(page);
        showCard(OVERVIEW);
        UIStyle.showFrame(this, 1220, 780);
    }

    private void initializeButtons() {
        overviewBtn = createSidebarButton("Tổng quan");
        overviewBtn.addActionListener(e -> showCard(OVERVIEW));

        depositBtn = createSidebarButton("Nạp tiền");
        depositBtn.addActionListener(e -> showCard(DEPOSIT));

        withdrawBtn = createSidebarButton("Rút tiền");
        withdrawBtn.addActionListener(e -> showCard(WITHDRAW));

        fastCashBtn = createSidebarButton("Rút tiền nhanh");
        fastCashBtn.addActionListener(e -> showCard(FAST_CASH));

        transferCardBtn = createSidebarButton("Chuyển tiền");
        transferCardBtn.addActionListener(e -> showCard(TRANSFER));

        miniStatementBtn = createSidebarButton("Giao dịch gần đây");
        miniStatementBtn.addActionListener(e -> showCard(MINI_STATEMENT));

        pinChangeBtn = createSidebarButton("Đổi mã PIN");
        pinChangeBtn.addActionListener(e -> showCard(PIN_CHANGE));

        changeCardBtn = createSidebarButton("Đổi số thẻ");
        changeCardBtn.addActionListener(e -> showCard(CHANGE_CARD));

        balanceBtn = createSidebarButton("Tra cứu số dư");
        balanceBtn.addActionListener(e -> showCard(BALANCE));

        logoutBtn = createSidebarButton("Đăng xuất");
        logoutBtn.addActionListener(e -> {
            new Login();
            dispose();
        });

        exitBtn = createSidebarButton("Thoát ứng dụng");
        exitBtn.addActionListener(this);

        menuButtons.put(OVERVIEW, overviewBtn);
        menuButtons.put(DEPOSIT, depositBtn);
        menuButtons.put(WITHDRAW, withdrawBtn);
        menuButtons.put(FAST_CASH, fastCashBtn);
        menuButtons.put(TRANSFER, transferCardBtn);
        menuButtons.put(MINI_STATEMENT, miniStatementBtn);
        menuButtons.put(PIN_CHANGE, pinChangeBtn);
        menuButtons.put(CHANGE_CARD, changeCardBtn);
        menuButtons.put(BALANCE, balanceBtn);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 20));
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setMinimumSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(24, 16, 24, 16));

        JPanel brand = new JPanel(new BorderLayout(10, 0));
        brand.setOpaque(false);
        bankIconLabel = UIStyle.createBankIconLabel(42);
        brand.add(bankIconLabel, BorderLayout.WEST);

        JLabel brandName = new JLabel("SmartBank");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandName.setForeground(Color.WHITE);
        brand.add(brandName, BorderLayout.CENTER);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        addSidebarButton(menu, overviewBtn);
        addSidebarButton(menu, depositBtn);
        addSidebarButton(menu, withdrawBtn);
        addSidebarButton(menu, fastCashBtn);
        addSidebarButton(menu, transferCardBtn);
        addSidebarButton(menu, miniStatementBtn);
        addSidebarButton(menu, pinChangeBtn);
        addSidebarButton(menu, changeCardBtn);
        addSidebarButton(menu, balanceBtn);

        JScrollPane menuScroll = new JScrollPane(menu);
        menuScroll.setBorder(null);
        menuScroll.setOpaque(false);
        menuScroll.getViewport().setOpaque(false);
        menuScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        menuScroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        addSidebarButton(bottom, logoutBtn);
        addSidebarButton(bottom, exitBtn);

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(menuScroll, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addSidebarButton(JPanel panel, JButton button) {
        panel.add(button);
        panel.add(Box.createVerticalStrut(8));
    }

    private JPanel createOverviewPanel() {
        JPanel page = createContentPage("Tổng quan tài khoản", "Quản lý nhanh các dịch vụ ngân hàng");

        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        overviewBalanceValue = createMetricValueLabel("-");
        overviewCardValue = createMetricValueLabel("-");
        overviewNameValue = createMetricValueLabel("-");
        cards.add(createMetricCard("Số dư hiện tại", overviewBalanceValue, "Cập nhật theo lịch sử giao dịch", PRIMARY));
        cards.add(createMetricCard("Số thẻ", overviewCardValue, "Tài khoản đang đăng nhập", BLUE));
        cards.add(createMetricCard("Chủ tài khoản", overviewNameValue, "Thông tin khách hàng", new Color(20, 184, 166)));

        JPanel lower = new JPanel(new BorderLayout(0, 18));
        lower.setOpaque(false);
        lower.add(createQuickActions(), BorderLayout.NORTH);

        overviewTransactionModel = createTransactionModel(new Object[]{"Mã giao dịch", "Ngày giao dịch", "Loại giao dịch", "Số tiền"});
        lower.add(createRecentTransactionsCard("5 giao dịch gần nhất", overviewTransactionModel, false), BorderLayout.CENTER);

        body.add(cards, BorderLayout.NORTH);
        body.add(lower, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    private JPanel createDepositPanel() {
        JPanel page = createContentPage("Nạp tiền", "Nhập số tiền muốn nạp và xác nhận giao dịch");
        RoundedPanel card = createFormCard(620, 320);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        depositAmountField = createTextField();
        addFieldRow(form, 0, "Số tiền nạp", createAmountPanel(depositAmountField));
        addNoteRow(form, 2, "Vui lòng nhập số tiền lớn hơn 0");

        JButton submitBtn = createPrimaryButton("Nạp tiền");
        submitBtn.addActionListener(e -> submitDeposit());

        card.add(form, BorderLayout.CENTER);
        card.add(createRightButtonPanel(submitBtn), BorderLayout.SOUTH);
        page.add(center(card), BorderLayout.CENTER);
        return page;
    }

    private JPanel createWithdrawPanel() {
        JPanel page = createContentPage("Rút tiền", "Nhập số tiền muốn rút từ tài khoản");
        RoundedPanel card = createFormCard(620, 320);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        withdrawAmountField = createTextField();
        addFieldRow(form, 0, "Số tiền rút", createAmountPanel(withdrawAmountField));
        addNoteRow(form, 2, "Vui lòng nhập số tiền lớn hơn 0");

        JButton submitBtn = createPrimaryButton("Rút tiền");
        submitBtn.addActionListener(e -> submitWithdraw());

        card.add(form, BorderLayout.CENTER);
        card.add(createRightButtonPanel(submitBtn), BorderLayout.SOUTH);
        page.add(center(card), BorderLayout.CENTER);
        return page;
    }

    private JPanel createFastCashPanel() {
        JPanel page = createContentPage("Rút tiền nhanh", "Chọn nhanh mệnh giá muốn rút");
        RoundedPanel card = createFormCard(660, 380);

        JPanel amountGrid = new JPanel(new GridLayout(3, 2, 16, 16));
        amountGrid.setOpaque(false);
        long[] amounts = {100000L, 200000L, 500000L, 1000000L, 2000000L, 5000000L};
        for (long amount : amounts) {
            JButton amountButton = createSecondaryButton(formatMoney(amount) + " đồng");
            amountButton.setPreferredSize(new Dimension(220, 46));
            amountButton.addActionListener(e -> withdrawFastCash(amount));
            amountGrid.add(amountButton);
        }

        card.add(amountGrid, BorderLayout.CENTER);
        page.add(center(card), BorderLayout.CENTER);
        return page;
    }

    private JPanel createTransferPanel() {
        JPanel page = createContentPage("Chuyển tiền", "Chuyển tiền đến tài khoản khác bằng số thẻ");
        RoundedPanel card = createFormCard(720, 440);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        transferReceiverCardNumberField = createTextField();
        transferAmountField = createTextField();
        transferNoteField = createTextField();
        transferReceiverInfoField = createTextField();
        transferReceiverInfoField.setEditable(false);
        transferReceiverInfoField.setText("Chưa kiểm tra");

        addFieldRow(form, 0, "Số thẻ người nhận", transferReceiverCardNumberField);
        addFieldRow(form, 2, "Số tiền", createAmountPanel(transferAmountField));
        addFieldRow(form, 4, "Ghi chú", transferNoteField);
        addFieldRow(form, 6, "Người nhận", transferReceiverInfoField);

        JButton checkBtn = createSecondaryButton("Kiểm tra");
        checkBtn.addActionListener(e -> previewReceiver());
        JButton transferBtn = createPrimaryButton("Chuyển tiền");
        transferBtn.setPreferredSize(new Dimension(150, 42));
        transferBtn.addActionListener(e -> transferMoney());

        card.add(form, BorderLayout.CENTER);
        card.add(createRightButtonPanel(checkBtn, transferBtn), BorderLayout.SOUTH);
        page.add(center(card), BorderLayout.CENTER);
        return page;
    }

    private JPanel createMiniStatementPanel() {
        JPanel page = createContentPage("Giao dịch gần đây", "Theo dõi 10 giao dịch mới nhất của tài khoản");
        miniStatementModel = createTransactionModel(new Object[]{"Mã GD", "Thời gian", "Loại giao dịch", "Số tiền"});
        page.add(createRecentTransactionsCard("Lịch sử giao dịch", miniStatementModel, true), BorderLayout.CENTER);
        return page;
    }

    private JPanel createPinChangePanel() {
        JPanel page = createContentPage("Đổi mã PIN", "Cập nhật mã PIN bảo mật gồm đúng 6 chữ số");
        RoundedPanel card = createFormCard(620, 380);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        pinCheckTextField = createTextField();
        pinTextField = createTextField();
        addFieldRow(form, 0, "Nhập mã PIN hiện tại", pinCheckTextField);
        addFieldRow(form, 2, "Nhập mã PIN muốn đổi", pinTextField);
        addNoteRow(form, 4, "PIN phải gồm đúng 6 chữ số");

        JButton submitBtn = createPrimaryButton("Xác nhận");
        submitBtn.addActionListener(e -> submitPinChange());

        card.add(form, BorderLayout.CENTER);
        card.add(createRightButtonPanel(submitBtn), BorderLayout.SOUTH);
        page.add(center(card), BorderLayout.CENTER);
        return page;
    }

    private JPanel createChangeCardPanel() {
        JPanel page = createContentPage("Đổi số thẻ", "Cập nhật số thẻ đăng nhập cho tài khoản hiện tại");
        RoundedPanel card = createFormCard(720, 380);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        currentCardNumberField = createTextField();
        currentCardNumberField.setEditable(false);
        newCardNumberField = createTextField();
        addFieldRow(form, 0, "Số thẻ hiện tại", currentCardNumberField);
        addFieldRow(form, 2, "Số thẻ mới", newCardNumberField);
        addNoteRow(form, 4, "Số thẻ mới phải gồm ít nhất 9 chữ số và không trùng số thẻ đã có.");

        JButton submitBtn = createPrimaryButton("Xác nhận đổi");
        submitBtn.setPreferredSize(new Dimension(160, 42));
        submitBtn.addActionListener(e -> confirmCardNumberChange());

        card.add(form, BorderLayout.CENTER);
        card.add(createRightButtonPanel(submitBtn), BorderLayout.SOUTH);
        page.add(center(card), BorderLayout.CENTER);
        return page;
    }

    private JPanel createBalancePanel() {
        JPanel page = createContentPage("Tra cứu số dư", "Xem số dư hiện tại của tài khoản");
        RoundedPanel card = createFormCard(620, 300);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Số dư hiện tại");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(MUTED_TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        balancePanelValue = new JLabel("-");
        balancePanelValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        balancePanelValue.setForeground(TEXT);
        balancePanelValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        balancePanelCardValue = new JLabel("-");
        balancePanelCardValue.setFont(BODY_FONT);
        balancePanelCardValue.setForeground(MUTED_TEXT);
        balancePanelCardValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(12));
        content.add(balancePanelValue);
        content.add(Box.createVerticalStrut(10));
        content.add(balancePanelCardValue);

        JButton refreshBtn = createPrimaryButton("Làm mới");
        refreshBtn.addActionListener(e -> refreshBalancePanel());

        card.add(content, BorderLayout.CENTER);
        card.add(createRightButtonPanel(refreshBtn), BorderLayout.SOUTH);
        page.add(center(card), BorderLayout.CENTER);
        return page;
    }

    private JPanel createContentPage(String title, String subtitle) {
        JPanel page = new JPanel(new BorderLayout(0, 22));
        page.setBackground(CONTENT_BACKGROUND);
        page.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(BODY_FONT);
        subtitleLabel.setForeground(MUTED_TEXT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitleLabel);
        page.add(header, BorderLayout.NORTH);
        return page;
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
        grid.add(createActionCard("Nạp tiền", "Thêm tiền vào tài khoản", PRIMARY, () -> showCard(DEPOSIT)));
        grid.add(createActionCard("Rút tiền", "Rút tiền theo số nhập", BLUE, () -> showCard(WITHDRAW)));
        grid.add(createActionCard("Rút tiền nhanh", "Chọn mệnh giá cố định", new Color(20, 184, 166), () -> showCard(FAST_CASH)));
        grid.add(createActionCard("Chuyển tiền", "Chuyển bằng số thẻ", new Color(245, 158, 11), () -> showCard(TRANSFER)));
        grid.add(createActionCard("Giao dịch", "Xem lịch sử gần đây", new Color(99, 102, 241), () -> showCard(MINI_STATEMENT)));
        grid.add(createActionCard("Đổi mã PIN", "Cập nhật mã bảo mật", new Color(236, 72, 153), () -> showCard(PIN_CHANGE)));
        grid.add(createActionCard("Đổi số thẻ", "Cập nhật số thẻ mới", new Color(14, 165, 233), () -> showCard(CHANGE_CARD)));
        grid.add(createActionCard("Tra cứu số dư", "Hiển thị số dư hiện tại", new Color(34, 197, 94), () -> showCard(BALANCE)));

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

    private JPanel createRecentTransactionsCard(String title, DefaultTableModel model, boolean large) {
        RoundedPanel card = new RoundedPanel(22, CARD_BACKGROUND, BORDER);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SECTION_FONT);
        titleLabel.setForeground(TEXT);
        header.add(titleLabel, BorderLayout.WEST);

        if (!large) {
            JButton viewAllBtn = createSecondaryButton("Xem tất cả");
            viewAllBtn.setPreferredSize(new Dimension(126, 38));
            viewAllBtn.addActionListener(e -> showCard(MINI_STATEMENT));
            header.add(viewAllBtn, BorderLayout.EAST);
        }

        JTable table = new JTable(model);
        styleDashboardTable(table);
        if (large) {
            table.setRowHeight(36);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(0, large ? 420 : 210));

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAmountPanel(JTextField field) {
        JLabel unitLabel = new JLabel("đồng");
        unitLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        unitLabel.setForeground(MUTED_TEXT);
        unitLabel.setPreferredSize(new Dimension(56, 44));

        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.add(field, BorderLayout.CENTER);
        panel.add(unitLabel, BorderLayout.EAST);
        return panel;
    }

    private void addFieldRow(JPanel panel, int row, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);

        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.gridx = 0;
        labelGbc.gridy = row;
        labelGbc.anchor = GridBagConstraints.WEST;
        labelGbc.insets = new Insets(0, 0, 8, 0);
        panel.add(label, labelGbc);

        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.gridx = 0;
        fieldGbc.gridy = row + 1;
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.weightx = 1;
        fieldGbc.insets = new Insets(0, 0, 14, 0);
        panel.add(field, fieldGbc);
    }

    private void addNoteRow(JPanel panel, int row, String note) {
        JLabel noteLabel = new JLabel(note);
        noteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noteLabel.setForeground(MUTED_TEXT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(2, 0, 0, 0);
        panel.add(noteLabel, gbc);
    }

    private JPanel createRightButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);
        for (JButton button : buttons) {
            panel.add(button);
        }
        return panel;
    }

    private RoundedPanel createFormCard(int width, int height) {
        RoundedPanel card = new RoundedPanel(24, CARD_BACKGROUND, BORDER);
        card.setLayout(new BorderLayout(0, 24));
        card.setBorder(new EmptyBorder(30, 34, 30, 34));
        card.setPreferredSize(new Dimension(width, height));
        return card;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, String note, Color accent) {
        RoundedPanel card = new RoundedPanel(22, CARD_BACKGROUND, BORDER);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(22, 22, 20, 22));
        card.setPreferredSize(new Dimension(0, 166));

        JPanel accentLine = new JPanel();
        accentLine.setBackground(accent);
        accentLine.setPreferredSize(new Dimension(52, 5));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(MUTED_TEXT);

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

    private JLabel createMetricValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(TEXT);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(16);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(TEXT);
        field.setPreferredSize(new Dimension(420, 44));
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setPreferredSize(new Dimension(140, 42));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(9, 16, 9, 16));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(PRIMARY);
        button.setBackground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 42));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        return button;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(SIDEBAR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(11, 14, 11, 14));
        button.setPreferredSize(new Dimension(228, 44));
        button.setMinimumSize(new Dimension(0, 44));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.putClientProperty("active", Boolean.FALSE);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!Boolean.TRUE.equals(button.getClientProperty("active"))) {
                    button.setBackground(SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!Boolean.TRUE.equals(button.getClientProperty("active"))) {
                    button.setBackground(SIDEBAR);
                }
            }
        });
        return button;
    }

    private JPanel center(Component component) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(component);
        return wrapper;
    }

    private JScrollPane createScrollableContent(JPanel page) {
        JScrollPane scrollPane = new JScrollPane(page);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(CONTENT_BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void showCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
        updateActiveMenu(cardName);
        if (OVERVIEW.equals(cardName)) {
            refreshOverviewData();
        } else if (MINI_STATEMENT.equals(cardName)) {
            refreshMiniStatementTable();
        } else if (BALANCE.equals(cardName)) {
            refreshBalancePanel();
        } else if (CHANGE_CARD.equals(cardName)) {
            refreshChangeCardPanel();
        }
    }

    private void updateActiveMenu(String activeCard) {
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            boolean active = entry.getKey().equals(activeCard);
            JButton button = entry.getValue();
            button.putClientProperty("active", active);
            button.setBackground(active ? SIDEBAR_ACTIVE : SIDEBAR);
        }
    }

    private void refreshAllData() {
        refreshOverviewData();
        refreshMiniStatementTable();
        refreshBalancePanel();
        refreshChangeCardPanel();
    }

    private void refreshOverviewData() {
        try (DBConnect c = new DBConnect()) {
            if (c.connection == null) {
                setOverviewFallback("Không thể kết nối cơ sở dữ liệu");
                return;
            }

            BankAccountService.AccountSummary account = BankAccountService.findAccountById(c.connection, accountId);
            long balance = BankAccountService.calculateBalance(c.connection, accountId);

            overviewBalanceValue.setText(formatMoney(balance) + " đồng");
            overviewCardValue.setText(account == null || account.cardNumber == null ? "-" : account.cardNumber);
            overviewNameValue.setText(account == null ? "Khách hàng" : account.displayName());
            loadRecentTransactions(c.connection, overviewTransactionModel, 5);
        } catch (Exception ex) {
            ex.printStackTrace();
            setOverviewFallback("Không thể tải dữ liệu");
        }
    }

    private void setOverviewFallback(String message) {
        overviewBalanceValue.setText("-");
        overviewCardValue.setText("-");
        overviewNameValue.setText("Khách hàng");
        overviewTransactionModel.setRowCount(0);
        overviewTransactionModel.addRow(new Object[]{"-", message, "-", ""});
    }

    private void refreshMiniStatementTable() {
        try (DBConnect c = new DBConnect()) {
            if (c.connection == null) {
                miniStatementModel.setRowCount(0);
                miniStatementModel.addRow(new Object[]{"-", "Không thể kết nối cơ sở dữ liệu", "-", ""});
                return;
            }
            loadRecentTransactions(c.connection, miniStatementModel, 10);
        } catch (Exception ex) {
            ex.printStackTrace();
            miniStatementModel.setRowCount(0);
            miniStatementModel.addRow(new Object[]{"-", "Không thể tải giao dịch", "-", ""});
        }
    }

    private void refreshBalancePanel() {
        try (DBConnect c = new DBConnect()) {
            if (c.connection == null) {
                balancePanelValue.setText("-");
                balancePanelCardValue.setText("Không thể kết nối cơ sở dữ liệu");
                return;
            }

            BankAccountService.AccountSummary account = BankAccountService.findAccountById(c.connection, accountId);
            long balance = BankAccountService.calculateBalance(c.connection, accountId);
            balancePanelValue.setText(formatMoney(balance) + " đồng");
            balancePanelCardValue.setText("Số thẻ: " + (account == null || account.cardNumber == null ? "-" : account.cardNumber));
        } catch (Exception ex) {
            ex.printStackTrace();
            balancePanelValue.setText("-");
            balancePanelCardValue.setText("Không thể xem số dư");
        }
    }

    private void refreshChangeCardPanel() {
        try (DBConnect conn = new DBConnect()) {
            if (conn.connection == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }

            BankAccountService.AccountSummary account = BankAccountService.findAccountById(conn.connection, accountId);
            if (account == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy tài khoản đang đăng nhập");
                return;
            }
            currentCardNumberField.setText(account.cardNumber);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể tải số thẻ hiện tại");
        }
    }

    private void loadRecentTransactions(Connection connection, DefaultTableModel model, int limit) throws SQLException {
        model.setRowCount(0);
        String query = limit == 10 ? """
                SELECT TransactionID, TransactionDate, TransactionType, Amount
                FROM Bank
                WHERE AccountID = ?
                ORDER BY TransactionDate DESC
                LIMIT 10
        """ : """
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

    private DefaultTableModel createTransactionModel(Object[] columns) {
        return new DefaultTableModel(columns, 0) {
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
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer();
        amountRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(amountRenderer);
    }

    private void submitDeposit() {
        String amountText = depositAmountField.getText().trim();

        if (amountText.equals("")) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số tiền muốn nạp");
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Số tiền phải là số");
            return;
        }

        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "Số tiền phải lớn hơn 0");
            return;
        }

        try {
            DBConnect conn = new DBConnect();

            String q = "INSERT INTO Bank(AccountID, TransactionDate, TransactionType, Amount) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = conn.connection.prepareStatement(q);
            ps.setInt(1, accountId);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setString(3, "Nạp tiền");
            ps.setInt(4, amount);

            ps.executeUpdate();

            depositAmountField.setText("");
            JOptionPane.showMessageDialog(null, "Nạp tiền thành công\nSố tiền: " + amount);
            refreshAllData();
            showCard(OVERVIEW);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void submitWithdraw() {
        try {
            String amountText = withdrawAmountField.getText().trim();
            if (amountText.equals("")) {
                JOptionPane.showMessageDialog(null, "Số tiền không được để trống");
                return;
            }
            long amount;
            try {
                amount = Long.parseLong(amountText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Số tiền phải là số");
                return;
            }
            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "Số tiền phải lớn hơn 0");
                return;
            }

            try{
                try (DBConnect c = new DBConnect()) {
                    if (c.connection == null) {
                        JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                        return;
                    }

                    long balance = BankAccountService.calculateBalance(c.connection, accountId);
                    if (amount > balance) {
                        JOptionPane.showMessageDialog(null, "Số dư không đủ để rút");
                        return;
                    }
                    String query = """
                            INSERT INTO Bank(AccountID, TransactionDate, TransactionType, Amount)
                            VALUES (?, ?, ?, ?)
                    """;
                    PreparedStatement p = c.connection.prepareStatement(query);
                    p.setInt(1, accountId);
                    p.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    p.setString(3, "Rút tiền");
                    p.setLong(4, amount);
                    p.executeUpdate();
                }
                withdrawAmountField.setText("");
                JOptionPane.showMessageDialog(null, "Rút tiền thành công");
                refreshAllData();
                showCard(OVERVIEW);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void withdrawFastCash(long amount) {
        try (DBConnect conn = new DBConnect()) {
            if (conn.connection == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }

            long currentBalance = BankAccountService.calculateBalance(conn.connection, accountId);
            if (amount > currentBalance) {
                JOptionPane.showMessageDialog(null, "Số dư không đủ để rút tiền");
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Xác nhận rút " + amount + " đồng?",
                    "Xác nhận rút tiền",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            long remainingBalance = BankAccountService.withdraw(conn.connection, accountId, amount, "Rút tiền nhanh");
            JOptionPane.showMessageDialog(
                    null,
                    "Rút tiền thành công\nSố tiền: " + amount + "\nSố dư còn lại: " + remainingBalance
            );
            refreshAllData();
            showCard(OVERVIEW);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể rút tiền. Vui lòng thử lại.");
        }
    }

    private void previewReceiver() {
        String receiverCardNumber = readReceiverCardNumber();
        if (receiverCardNumber == null) {
            return;
        }

        try (DBConnect conn = new DBConnect()) {
            if (conn.connection == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }

            BankAccountService.AccountSummary receiver = loadValidReceiver(conn, receiverCardNumber);
            transferReceiverInfoField.setText(receiver.displayName() + " - " + receiver.cardNumber);
            JOptionPane.showMessageDialog(null, "Người nhận: " + receiver.displayName() + "\nSố thẻ: " + receiver.cardNumber);
        } catch (IllegalStateException ex) {
            transferReceiverInfoField.setText("Chưa kiểm tra");
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (SQLException ex) {
            transferReceiverInfoField.setText("Chưa kiểm tra");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể kiểm tra số thẻ người nhận");
        }
    }

    private void transferMoney() {
        String receiverCardNumber = readReceiverCardNumber();
        if (receiverCardNumber == null) {
            return;
        }

        Long amount = readTransferAmount();
        if (amount == null) {
            return;
        }

        String note = transferNoteField.getText().trim();
        if (note.length() > 255) {
            JOptionPane.showMessageDialog(null, "Ghi chú không được vượt quá 255 ký tự");
            return;
        }

        BankAccountService.AccountSummary receiver;
        long currentBalance;

        try (DBConnect conn = new DBConnect()) {
            if (conn.connection == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }

            receiver = loadValidReceiver(conn, receiverCardNumber);
            transferReceiverInfoField.setText(receiver.displayName() + " - " + receiver.cardNumber);
            currentBalance = BankAccountService.calculateBalance(conn.connection, accountId);
            if (amount > currentBalance) {
                JOptionPane.showMessageDialog(null, "Số dư không đủ để chuyển tiền");
                return;
            }
        } catch (IllegalStateException ex) {
            transferReceiverInfoField.setText("Chưa kiểm tra");
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể kiểm tra thông tin chuyển tiền");
            return;
        }

        String confirmMessage = "Xác nhận chuyển tiền?\n\n"
                + "Người nhận: " + receiver.displayName() + "\n"
                + "Số thẻ người nhận: " + receiver.cardNumber + "\n"
                + "Số tiền: " + amount + "\n"
                + "Số dư hiện tại: " + currentBalance;
        if (!note.isEmpty()) {
            confirmMessage += "\nGhi chú: " + note;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                confirmMessage,
                "Xác nhận chuyển tiền",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try (DBConnect conn = new DBConnect()) {
            if (conn.connection == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }

            BankAccountService.TransferResult result = BankAccountService.transferByCardNumber(
                    conn.connection,
                    accountId,
                    receiverCardNumber,
                    amount,
                    note
            );

            JOptionPane.showMessageDialog(
                    null,
                    "Chuyển tiền thành công\n"
                            + "Người nhận: " + result.receiver.displayName() + "\n"
                            + "Số tiền: " + result.amount + "\n"
                            + "Số dư còn lại: " + result.senderBalanceAfter
            );
            transferReceiverCardNumberField.setText("");
            transferAmountField.setText("");
            transferNoteField.setText("");
            transferReceiverInfoField.setText("Chưa kiểm tra");
            refreshAllData();
            showCard(OVERVIEW);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Chuyển tiền thất bại. Giao dịch đã được hủy.");
        }
    }

    private String readReceiverCardNumber() {
        String receiverCardNumber = transferReceiverCardNumberField.getText().trim();
        if (receiverCardNumber.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số thẻ người nhận");
            return null;
        }
        if (!BankAccountService.isValidCardNumberFormat(receiverCardNumber)) {
            JOptionPane.showMessageDialog(null, "Số thẻ phải gồm ít nhất 9 chữ số.");
            return null;
        }
        return receiverCardNumber;
    }

    private Long readTransferAmount() {
        String amountText = transferAmountField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số tiền");
            return null;
        }

        long amount;
        try {
            amount = Long.parseLong(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Số tiền phải là số");
            return null;
        }

        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "Số tiền phải lớn hơn 0");
            return null;
        }

        return amount;
    }

    private BankAccountService.AccountSummary loadValidReceiver(DBConnect conn, String receiverCardNumber) throws SQLException {
        BankAccountService.AccountSummary sender = BankAccountService.findAccountById(conn.connection, accountId);
        if (sender == null) {
            throw new IllegalStateException("Không tìm thấy tài khoản đang đăng nhập");
        }

        BankAccountService.AccountSummary receiver = BankAccountService.findAccountByCardNumber(conn.connection, receiverCardNumber);
        if (receiver == null) {
            throw new IllegalStateException("Số thẻ người nhận không tồn tại");
        }
        if (sender.accountId == receiver.accountId) {
            throw new IllegalStateException("Không thể chuyển tiền cho chính số thẻ của bạn");
        }

        return receiver;
    }

    private void submitPinChange() {
        try{
            String oldPin = pinCheckTextField.getText();
            String newPin = pinTextField.getText();
            if(oldPin.equals("") || newPin.equals("")){
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin");
                return;
            }
            if (!oldPin.matches("[0-9]{6}") || !newPin.matches("[0-9]{6}")) {
                JOptionPane.showMessageDialog(null, "PIN phải gồm đúng 6 chữ số");
                return;
            }
            DBConnect c = new DBConnect();
            String q = """
                    UPDATE Login
                    SET pin = ?
                    WHERE AccountID = ? AND pin = ?
            """;
            PreparedStatement ps = c.connection.prepareStatement(q);
            ps.setString(1, newPin);
            ps.setInt(2, accountId);
            ps.setString(3, oldPin);
            int updatedRows = ps.executeUpdate();

            if (updatedRows == 0) {
                JOptionPane.showMessageDialog(null, "Sai mã PIN");
                return;
            }

            pinCheckTextField.setText("");
            pinTextField.setText("");
            JOptionPane.showMessageDialog(null, "Đổi mã PIN thành công");
            refreshAllData();
            showCard(OVERVIEW);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void confirmCardNumberChange() {
        String currentCardNumber = currentCardNumberField.getText().trim();
        String newCardNumber = newCardNumberField.getText().trim();

        if (currentCardNumber.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy số thẻ hiện tại");
            return;
        }
        if (!BankAccountService.isValidCardNumberFormat(newCardNumber)) {
            JOptionPane.showMessageDialog(null, "Số thẻ phải gồm ít nhất 9 chữ số.");
            return;
        }
        if (newCardNumber.equals(currentCardNumber)) {
            JOptionPane.showMessageDialog(null, "Số thẻ mới phải khác số thẻ hiện tại");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn đổi số thẻ?\n\nSố thẻ hiện tại: " + currentCardNumber + "\nSố thẻ mới: " + newCardNumber,
                "Xác nhận đổi số thẻ",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try (DBConnect conn = new DBConnect()) {
            if (conn.connection == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }

            BankAccountService.changeCardNumber(conn.connection, accountId, newCardNumber);
            currentCardNumberField.setText(newCardNumber);
            newCardNumberField.setText("");
            JOptionPane.showMessageDialog(null, "Đổi số thẻ thành công\nSố thẻ mới: " + newCardNumber);
            refreshAllData();
            showCard(OVERVIEW);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể đổi số thẻ. Vui lòng thử lại.");
        }
    }

    private String formatMoney(long amount) {
        return MONEY_FORMAT.format(amount);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==exitBtn){
            System.exit(0);
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
        new Main(0);
    }
}

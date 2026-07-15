package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

public class Main extends JFrame {
    private static final int INACTIVITY_TIMEOUT_MILLIS = 5 * 60 * 1000;

    private final int accountId;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<AppRoute, SidebarItem> menuItems = new EnumMap<>(AppRoute.class);
    private final Map<AppRoute, RefreshablePanel> routePanels = new EnumMap<>(AppRoute.class);
    private final MainHeader header;
    private InactivityMonitor inactivityMonitor;

    public Main(int accountId) {
        this(accountId, AppRoute.OVERVIEW);
    }

    Main(int accountId, AppRoute initialRoute) {
        super("SmartBank");
        this.accountId = accountId;
        UIStyle.installGlobalDefaults();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 650));

        header = new MainHeader(this::confirmLogout);
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(UIStyle.BACKGROUND);
        page.add(createSidebar(), BorderLayout.WEST);

        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(UIStyle.BACKGROUND);
        workspace.add(header, BorderLayout.NORTH);
        contentPanel.setBackground(UIStyle.BACKGROUND);
        workspace.add(contentPanel, BorderLayout.CENTER);
        page.add(workspace, BorderLayout.CENTER);

        registerPanels();
        setContentPane(page);
        UIStyle.showFrame(this, 1240, 700);
        installInactivityMonitor();
        refreshHeader();
        showRoute(initialRoute == null ? AppRoute.OVERVIEW : initialRoute);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, UIStyle.SPACE_4));
        sidebar.setBackground(UIStyle.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(UIStyle.SIDEBAR_WIDTH, 0));
        sidebar.setBorder(new EmptyBorder(UIStyle.SPACE_6, UIStyle.SPACE_4, UIStyle.SPACE_4, UIStyle.SPACE_4));

        JPanel brand = new JPanel(new BorderLayout(UIStyle.SPACE_3, 0));
        brand.setOpaque(false);
        brand.add(UIStyle.createBankIconLabel(38), BorderLayout.WEST);
        JLabel name = new JLabel("SmartBank");
        name.setFont(UIStyle.SUBTITLE_FONT);
        name.setForeground(Color.WHITE);
        brand.add(name, BorderLayout.CENTER);
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        for (AppRoute route : AppRoute.values()) {
            SidebarItem item = new SidebarItem(route.title, route.icon);
            item.addActionListener(e -> showRoute(route));
            menuItems.put(route, item);
            menu.add(item);
            menu.add(Box.createVerticalStrut(UIStyle.SPACE_2));
        }
        JScrollPane menuScroll = new JScrollPane(menu);
        menuScroll.setBorder(null);
        menuScroll.setOpaque(false);
        menuScroll.getViewport().setOpaque(false);
        menuScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        menuScroll.getVerticalScrollBar().setUnitIncrement(12);
        sidebar.add(menuScroll, BorderLayout.CENTER);

        SidebarItem logout = new SidebarItem("Đăng xuất", SmartBankIcon.Type.LOGOUT);
        logout.addActionListener(e -> confirmLogout());
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(logout, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private void registerPanels() {
        register(AppRoute.OVERVIEW, new DashboardPanel(accountId, this::showRoute, header::setAccount));
        register(AppRoute.DEPOSIT, new MoneyOperationPanel(accountId, MoneyOperationPanel.Mode.DEPOSIT, this::onAccountDataChanged));
        register(AppRoute.WITHDRAW, new MoneyOperationPanel(accountId, MoneyOperationPanel.Mode.WITHDRAW, this::onAccountDataChanged));
        register(AppRoute.FAST_CASH, new FastCashPanel(accountId, this::onAccountDataChanged));
        register(AppRoute.TRANSFER, new TransferPanel(accountId, this::onAccountDataChanged));
        register(AppRoute.TRANSACTIONS, new TransactionHistoryPanel(accountId));
        register(AppRoute.PIN_CHANGE, new PinChangePanel(accountId));
        register(AppRoute.CARD_MANAGEMENT, new CardManagementPanel(accountId, this::onAccountChanged));
        register(AppRoute.BALANCE, new BalancePanel(accountId, header::setAccount));
    }

    private void register(AppRoute route, BankPanel panel) {
        routePanels.put(route, panel);
        contentPanel.add(panel, route.name());
    }

    void showRoute(AppRoute route) {
        cardLayout.show(contentPanel, route.name());
        header.setSection(route.title);
        for (Map.Entry<AppRoute, SidebarItem> entry : menuItems.entrySet()) {
            entry.getValue().setActive(entry.getKey() == route);
        }
        RefreshablePanel panel = routePanels.get(route);
        if (panel != null) panel.refreshData();
    }

    private void onAccountDataChanged() {
        refreshHeader();
    }

    private void onAccountChanged(BankAccountService.AccountSummary account) {
        header.setAccount(account);
    }

    private void refreshHeader() {
        SwingWorkerRunner.run(new JComponent[0], () -> {
            try (DBConnect connection = new DBConnect()) {
                if (connection.connection == null) {
                    throw new SQLException("Không thể kết nối PostgreSQL.");
                }
                return BankAccountService.findAccountById(connection.connection, accountId);
            }
        }, header::setAccount, error -> header.setAccount(null));
    }

    private void confirmLogout() {
        if (!ConfirmDialog.show(this, "Đăng xuất", "Bạn có chắc muốn kết thúc phiên SmartBank hiện tại?", "Đăng xuất")) {
            return;
        }
        dispose();
        new Login();
    }

    private void installInactivityMonitor() {
        inactivityMonitor = new InactivityMonitor(this, INACTIVITY_TIMEOUT_MILLIS, () -> {
            dispose();
            Login login = new Login();
            login.showSessionExpired();
        });
        inactivityMonitor.start();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                closeInactivityMonitor();
            }
        });
    }

    private void closeInactivityMonitor() {
        if (inactivityMonitor != null) {
            inactivityMonitor.close();
            inactivityMonitor = null;
        }
    }

    @Override
    public void dispose() {
        closeInactivityMonitor();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main(0));
    }
}

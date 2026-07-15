package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

final class TransactionHistoryPanel extends BankPanel {
    private final JComboBox<BankAccountService.TransactionCategory> typeFilter =
            new JComboBox<>(BankAccountService.TransactionCategory.values());
    private final JCheckBox dateFilterEnabled = new JCheckBox("Khoảng thời gian");
    private final JSpinner fromDate = createDateSpinner(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000));
    private final JSpinner toDate = createDateSpinner(new Date());
    private final PrimaryButton filterButton = new PrimaryButton("Áp dụng");
    private final SecondaryButton resetButton = new SecondaryButton("Đặt lại");
    private final DefaultTableModel model = createModel();
    private final JTable table = new JTable(model);
    private boolean loading;

    TransactionHistoryPanel(int accountId) {
        super(accountId, "Lịch sử giao dịch", "Lọc theo loại và khoảng thời gian, tối đa 500 giao dịch");
        configureFilterControls();
        configureTable();
        setBody(createContent());
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, UIStyle.SPACE_4));
        content.setOpaque(false);
        content.add(createFilters(), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UIStyle.BORDER, 1, true));
        scroll.getViewport().setBackground(UIStyle.CARD_BACKGROUND);
        content.add(scroll, BorderLayout.CENTER);
        return content;
    }

    private JPanel createFilters() {
        RoundedPanel filters = createSurface();
        filters.setLayout(new BorderLayout(0, UIStyle.SPACE_3));
        JLabel typeLabel = new JLabel("Loại giao dịch");
        typeLabel.setFont(UIStyle.LABEL_FONT);
        typeLabel.setLabelFor(typeFilter);
        typeFilter.setPreferredSize(new Dimension(170, UIStyle.CONTROL_HEIGHT));
        fromDate.setPreferredSize(new Dimension(150, UIStyle.CONTROL_HEIGHT));
        toDate.setPreferredSize(new Dimension(150, UIStyle.CONTROL_HEIGHT));
        JPanel criteria = new JPanel(new FlowLayout(FlowLayout.LEFT, UIStyle.SPACE_3, 0));
        criteria.setOpaque(false);
        criteria.add(typeLabel);
        criteria.add(typeFilter);
        criteria.add(dateFilterEnabled);
        criteria.add(new JLabel("Từ"));
        criteria.add(fromDate);
        criteria.add(new JLabel("Đến"));
        criteria.add(toDate);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIStyle.SPACE_3, 0));
        actions.setOpaque(false);
        actions.add(resetButton);
        actions.add(filterButton);
        filters.add(criteria, BorderLayout.CENTER);
        filters.add(actions, BorderLayout.SOUTH);
        return filters;
    }

    private void configureFilterControls() {
        typeFilter.setFont(UIStyle.FIELD_FONT);
        typeFilter.setBackground(UIStyle.CARD_BACKGROUND);
        dateFilterEnabled.setFont(UIStyle.BODY_FONT);
        dateFilterEnabled.setOpaque(false);
        fromDate.setEnabled(false);
        toDate.setEnabled(false);
        dateFilterEnabled.addActionListener(e -> {
            fromDate.setEnabled(dateFilterEnabled.isSelected());
            toDate.setEnabled(dateFilterEnabled.isSelected());
        });
        filterButton.setIcon(new SmartBankIcon(SmartBankIcon.Type.REFRESH, 16, Color.WHITE));
        filterButton.addActionListener(e -> refreshData());
        resetButton.addActionListener(e -> {
            typeFilter.setSelectedItem(BankAccountService.TransactionCategory.ALL);
            dateFilterEnabled.setSelected(false);
            fromDate.setEnabled(false);
            toDate.setEnabled(false);
            refreshData();
        });
    }

    private void configureTable() {
        UIStyle.styleTable(table);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(145);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(220);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        PaddedRenderer idRenderer = new PaddedRenderer();
        idRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(idRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(new DateRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new AmountRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());
    }

    @Override
    public void refreshData() {
        if (loading) return;
        LocalDateTime from = null;
        LocalDateTime to = null;
        if (dateFilterEnabled.isSelected()) {
            Date fromValue = (Date) fromDate.getValue();
            Date toValue = (Date) toDate.getValue();
            from = LocalDateTime.ofInstant(fromValue.toInstant(), ZoneId.systemDefault()).toLocalDate().atStartOfDay();
            to = LocalDateTime.ofInstant(toValue.toInstant(), ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59);
            if (from.isAfter(to)) {
                notification.showMessage("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.", NotificationPanel.Type.ERROR);
                return;
            }
        }
        BankAccountService.TransactionCategory category =
                (BankAccountService.TransactionCategory) typeFilter.getSelectedItem();
        LocalDateTime finalFrom = from;
        LocalDateTime finalTo = to;
        loading = true;
        notification.showMessage("Đang tải lịch sử giao dịch...", NotificationPanel.Type.INFO);
        SwingWorkerRunner.run(new JComponent[]{filterButton, resetButton, typeFilter, dateFilterEnabled, fromDate, toDate}, () -> {
            try (DBConnect connection = openConnection()) {
                return BankAccountService.loadTransactions(connection.connection, accountId, finalFrom, finalTo, category, 500);
            }
        }, transactions -> {
            loading = false;
            render(transactions);
            if (transactions.isEmpty()) {
                notification.showMessage("Không có giao dịch phù hợp với bộ lọc.", NotificationPanel.Type.INFO);
            } else {
                notification.showMessage("Đã tải " + transactions.size() + " giao dịch.", NotificationPanel.Type.SUCCESS, 3000);
            }
        }, error -> {
            loading = false;
            model.setRowCount(0);
            notification.showMessage(failureMessage(error, "tải lịch sử giao dịch"), NotificationPanel.Type.ERROR);
        });
    }

    private void render(List<BankAccountService.TransactionRecord> transactions) {
        model.setRowCount(0);
        for (BankAccountService.TransactionRecord transaction : transactions) {
            model.addRow(new Object[]{
                    transaction.transactionId,
                    transaction.transactionDate,
                    transaction.displayType,
                    transaction.relatedCardNumber,
                    transaction.moneyIn ? transaction.amount : -transaction.amount,
                    transaction.note == null || transaction.note.isBlank() ? "-" : transaction.note,
                    "Thành công"
            });
        }
    }

    private static DefaultTableModel createModel() {
        return new DefaultTableModel(new Object[]{"Mã GD", "Thời gian", "Loại", "Thẻ liên quan", "Số tiền", "Nội dung", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0 -> Integer.class;
                    case 1 -> LocalDateTime.class;
                    case 4 -> Long.class;
                    default -> String.class;
                };
            }
        };
    }

    private static JSpinner createDateSpinner(Date value) {
        JSpinner spinner = new JSpinner(new SpinnerDateModel(value, null, null, java.util.Calendar.DAY_OF_MONTH));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        spinner.setFont(UIStyle.FIELD_FONT);
        return spinner;
    }

    private static class PaddedRenderer extends DefaultTableCellRenderer {
        PaddedRenderer() {
            setBorder(BorderFactory.createEmptyBorder(0, UIStyle.SPACE_3, 0, UIStyle.SPACE_3));
        }
    }

    private static class DateRenderer extends PaddedRenderer {
        @Override protected void setValue(Object value) {
            setText(value instanceof LocalDateTime date ? UIStyle.DATE_TIME_FORMAT.format(date) : "-");
        }
    }

    private static class AmountRenderer extends PaddedRenderer {
        AmountRenderer() { setHorizontalAlignment(SwingConstants.RIGHT); }
        @Override protected void setValue(Object value) {
            long amount = value instanceof Number number ? number.longValue() : 0L;
            setForeground(amount >= 0 ? UIStyle.SUCCESS : UIStyle.TEXT);
            setFont(UIStyle.BODY_STRONG_FONT);
            setText((amount >= 0 ? "+" : "-") + UIStyle.formatMoney(Math.abs(amount)));
        }
    }

    private static class StatusRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            StatusBadge badge = new StatusBadge(String.valueOf(value), StatusBadge.Status.SUCCESS);
            if (selected) badge.setBackground(UIStyle.TABLE_SELECTION);
            return badge;
        }
    }
}

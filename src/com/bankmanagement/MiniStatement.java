package com.bankmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MiniStatement extends JFrame implements ActionListener {

    JLabel bankIconLabel, titleLabel;
    JTable transactionTable;
    JButton returnBtn;
    int accountId;

    public MiniStatement(int accountId) {
        super("Giao dịch gần đây");
        this.accountId = accountId;

        JPanel page = UIStyle.createPage();

        bankIconLabel = UIStyle.createBankIconLabel(88);
        titleLabel = new JLabel("GIAO DỊCH GẦN ĐÂY");
        page.add(UIStyle.createHeader(bankIconLabel, titleLabel, null), BorderLayout.NORTH);

        String[] columns = {"TransactionID", "TransactionDate", "TransactionType", "Amount"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        UIStyle.styleTable(transactionTable);

        JScrollPane scrollPane = new JScrollPane(transactionTable);

        returnBtn = new JButton("Quay lại");
        UIStyle.styleButton(returnBtn);
        returnBtn.addActionListener(this);

        JPanel card = UIStyle.createCard();
        card.setLayout(new BorderLayout(0, 18));
        card.setPreferredSize(new Dimension(730, 520));
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(UIStyle.createButtonPanel(returnBtn), BorderLayout.SOUTH);

        loadTransactions(tableModel);

        page.add(UIStyle.center(card), BorderLayout.CENTER);
        setContentPane(page);
        UIStyle.showFrame(this, 850, 800);
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
                Object[] row = {
                        resultSet.getInt("TransactionID"),
                        resultSet.getTimestamp("TransactionDate"),
                        resultSet.getString("TransactionType"),
                        resultSet.getLong("Amount")
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể tải giao dịch gần đây");
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnBtn) {
            new Main(accountId);
            dispose();
        }
    }

    public static void main(String[] args) {
        new MiniStatement(0);
    }
}

package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class FastCash extends JFrame implements ActionListener {
    JLabel bankIconLabel, titleLabel, noteLabel;
    JButton returnBtn;
    int accountId;
    private static final long[] FAST_CASH_AMOUNTS = {100000L, 200000L, 500000L, 1000000L, 2000000L, 5000000L};

    public FastCash(int accountId) {
        super("Rút tiền nhanh");
        this.accountId = accountId;

        JPanel page = UIStyle.createPage();

        bankIconLabel = UIStyle.createBankIconLabel(88);
        titleLabel = new JLabel("RÚT TIỀN NHANH");
        page.add(UIStyle.createHeader(bankIconLabel, titleLabel, null), BorderLayout.NORTH);

        noteLabel = new JLabel("Chọn số tiền muốn rút");
        UIStyle.styleNoteLabel(noteLabel);
        noteLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel amountGrid = new JPanel(new GridLayout(3, 2, 18, 18));
        amountGrid.setOpaque(false);
        for (long amount : FAST_CASH_AMOUNTS) {
            JButton amountButton = new JButton(String.valueOf(amount));
            UIStyle.styleButton(amountButton);
            amountButton.setPreferredSize(new Dimension(180, 44));
            amountButton.setActionCommand(String.valueOf(amount));
            amountButton.addActionListener(this);
            amountGrid.add(amountButton);
        }

        returnBtn = new JButton("Quay lại");
        UIStyle.styleButton(returnBtn);
        returnBtn.addActionListener(this);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 18));
        contentPanel.setOpaque(false);
        contentPanel.add(noteLabel, BorderLayout.NORTH);
        contentPanel.add(amountGrid, BorderLayout.CENTER);

        JPanel card = UIStyle.createCard();
        card.setLayout(new BorderLayout(0, 22));
        card.setPreferredSize(new Dimension(650, 360));
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(UIStyle.createButtonPanel(returnBtn), BorderLayout.SOUTH);

        page.add(UIStyle.center(card), BorderLayout.CENTER);
        setContentPane(page);
        UIStyle.showFrame(this, 850, 800);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnBtn) {
            new Main(accountId);
            setVisible(false);
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(e.getActionCommand());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Số tiền rút không hợp lệ");
            return;
        }

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
            new Main(accountId);
            setVisible(false);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể rút tiền. Vui lòng thử lại.");
        }
    }

    public static void main(String[] args) {
        new FastCash(0);
    }
}

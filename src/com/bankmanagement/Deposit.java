package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class Deposit extends JFrame implements ActionListener {

    JLabel bankIconLabel, titleLabel, unitLabel;
    JLabel amountLabel, noteLabel;
    JTextField amountField;
    JButton depositBtn, returnBtn;

    int accountId;

    public Deposit(int accountId) {
        super("Nạp tiền");
        this.accountId = accountId;

        JPanel page = UIStyle.createPage();

        bankIconLabel = UIStyle.createBankIconLabel(88);
        titleLabel = new JLabel("NẠP TIỀN VÀO TÀI KHOẢN");
        page.add(UIStyle.createHeader(bankIconLabel, titleLabel, null), BorderLayout.NORTH);

        amountLabel = new JLabel("Số tiền nạp :");

        amountField = new JTextField(15);
        UIStyle.styleTextField(amountField);

        unitLabel = new JLabel("đồng");
        UIStyle.styleFieldLabel(unitLabel);
        unitLabel.setPreferredSize(new Dimension(60, 38));
        unitLabel.setHorizontalAlignment(SwingConstants.LEFT);

        noteLabel = new JLabel("Vui lòng nhập số tiền lớn hơn 0");
        UIStyle.styleNoteLabel(noteLabel);

        returnBtn = new JButton("Quay lại");
        UIStyle.styleButton(returnBtn);
        returnBtn.addActionListener(this);

        depositBtn = new JButton("Nạp tiền");
        UIStyle.styleButton(depositBtn);
        depositBtn.addActionListener(this);

        JPanel amountPanel = new JPanel(new BorderLayout(10, 0));
        amountPanel.setOpaque(false);
        amountPanel.add(amountField, BorderLayout.CENTER);
        amountPanel.add(unitLabel, BorderLayout.EAST);

        JPanel card = UIStyle.createCard();
        card.setPreferredSize(new Dimension(650, 230));
        UIStyle.addFormRow(card, 0, amountLabel, amountPanel);
        UIStyle.addFullWidthRow(card, 1, noteLabel);
        UIStyle.addFullWidthRow(card, 2, UIStyle.createButtonPanel(returnBtn, depositBtn));

        page.add(UIStyle.center(card), BorderLayout.CENTER);
        setContentPane(page);
        getRootPane().setDefaultButton(depositBtn);
        UIStyle.showFrame(this, 850, 800);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == depositBtn) {
            String amountText = amountField.getText().trim();

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

                JOptionPane.showMessageDialog(null, "Nạp tiền thành công\nSố tiền: " + amount);
                new Main(accountId);
                setVisible(false);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (e.getSource() == returnBtn) {
            new Main(accountId);
            setVisible(false);
        }
    }

    public static void main(String[] args) {

        new Deposit(0);
    }
}

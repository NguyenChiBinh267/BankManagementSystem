package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class Deposit extends JFrame implements ActionListener {

    JLabel bankIconLabel, titleLabel, pageLabel, detailLabel;
    JLabel amountLabel, noteLabel;
    JTextField amountField;
    JButton depositBtn, returnBtn;

    String pin;

    public Deposit(String pin) {
        super("Deposit");
        this.pin = pin;

        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank_icon.png"));
        Image scaledBankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        bankIconLabel = new JLabel(new ImageIcon(scaledBankImage));
        bankIconLabel.setBounds(25, 10, 100, 100);
        add(bankIconLabel);

        titleLabel = new JLabel("BANK MANAGEMENT SYSTEM");
        titleLabel.setBounds(220, 30, 550, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel);

        pageLabel = new JLabel("Giao dịch");
        pageLabel.setBounds(380, 60, 500, 30);
        pageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(pageLabel);

        detailLabel = new JLabel("Nạp tiền vào tài khoản");
        detailLabel.setBounds(310, 90, 500, 30);
        detailLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(detailLabel);

        amountLabel = new JLabel("Số tiền nạp :");
        amountLabel.setBounds(75, 250, 375, 30);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(amountLabel);

        amountField = new JTextField(15);
        amountField.setBounds(275, 250, 350, 30);
        amountField.setFont(new Font("Arial", Font.BOLD, 18));
        add(amountField);

        noteLabel = new JLabel("Vui lòng nhập số tiền lớn hơn 0");
        noteLabel.setBounds(275, 285, 350, 25);
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(noteLabel);

        returnBtn = new JButton("Quay lại");
        returnBtn.setBounds(500, 700, 120, 35);
        returnBtn.setFont(new Font("Arial", Font.BOLD, 16));
        returnBtn.setBackground(Color.BLACK);
        returnBtn.setForeground(Color.WHITE);
        returnBtn.addActionListener(this);
        add(returnBtn);

        depositBtn = new JButton("Nạp tiền");
        depositBtn.setBounds(650, 700, 120, 35);
        depositBtn.setFont(new Font("Arial", Font.BOLD, 16));
        depositBtn.setBackground(Color.BLACK);
        depositBtn.setForeground(Color.WHITE);
        depositBtn.addActionListener(this);
        add(depositBtn);

        getContentPane().setBackground(new Color(222, 255, 228));
        setLayout(null);
        setSize(850, 800);
        setLocation(360, 40);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == depositBtn) {
            String amountText = amountField.getText().trim();

            if (amountText.equals("")) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập số tiền muốn nạp");
                return;
            }

            double amount;

            try {
                amount = Double.parseDouble(amountText);
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

                String q = "INSERT INTO Bank(Pin, TransactionDate, TransactionType, Amount) VALUES (?, ?, ?, ?)";

                PreparedStatement ps = conn.connection.prepareStatement(q);
                ps.setString(1, pin);
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setString(3, "Nạp tiền");
                ps.setDouble(4, amount);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "Nạp tiền thành công\nSố tiền: " + amount);

                setVisible(false);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (e.getSource() == returnBtn) {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new Deposit("");
    }
}
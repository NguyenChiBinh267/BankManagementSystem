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

    String pin;

    public Deposit(String pin) {
        super("Nạp tiền");
        this.pin = pin;

        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank_icon.png"));
        Image scaledBankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        bankIconLabel = new JLabel(new ImageIcon(scaledBankImage));
        bankIconLabel.setBounds(25, 10, 100, 100);
        add(bankIconLabel);

        titleLabel = new JLabel("NẠP TIỀN VÀO TÀI KHOẢN");
        titleLabel.setBounds(250, 30, 550, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel);

        amountLabel = new JLabel("Số tiền nạp :");
        amountLabel.setBounds(75, 250, 375, 30);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(amountLabel);

        amountField = new JTextField(15);
        amountField.setBounds(275, 250, 350, 30);
        amountField.setFont(new Font("Arial", Font.BOLD, 18));
        add(amountField);

        unitLabel = new JLabel("đồng");
        unitLabel.setBounds(650, 250, 350, 30);
        unitLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(unitLabel);

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
        returnBtn.setFocusPainted(false);
        add(returnBtn);

        depositBtn = new JButton("Nạp tiền");
        depositBtn.setBounds(650, 700, 120, 35);
        depositBtn.setFont(new Font("Arial", Font.BOLD, 16));
        depositBtn.setBackground(Color.BLACK);
        depositBtn.setForeground(Color.WHITE);
        depositBtn.addActionListener(this);
        depositBtn.setFocusPainted(false);
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

                String q = "INSERT INTO Bank(Pin, TransactionDate, TransactionType, Amount) VALUES (?, ?, ?, ?)";

                PreparedStatement ps = conn.connection.prepareStatement(q);
                ps.setString(1, pin);
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setString(3, "Nạp tiền");
                ps.setInt(4, amount);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "Nạp tiền thành công\nSố tiền: " + amount);
                new Main(pin);
                setVisible(false);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (e.getSource() == returnBtn) {
            new Main(pin);
            setVisible(false);
        }
    }

    public static void main(String[] args) {

        new Deposit("");
    }
}
package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class WithDraw extends JFrame implements ActionListener {
    JLabel bankIconLabel, titleLabel, amountLabel, noteLabel, unitLabel;
    JTextField amountField;
    JButton returnBtn, withDrawBtn;
    String pin;
    public WithDraw(String pin) {
        super("Rút tiền");
        this.pin = pin;

        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank_icon.png"));
        Image scaledBankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        bankIconLabel = new JLabel(new ImageIcon(scaledBankImage));
        bankIconLabel.setBounds(25, 10, 100, 100);
        add(bankIconLabel);

        titleLabel = new JLabel("RÚT TIỀN");
        titleLabel.setBounds(350, 30, 550, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel);

        amountLabel = new JLabel("Số tiền rút :");
        amountLabel.setBounds(75, 250, 375, 30);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(amountLabel);

        amountField = new JTextField(15);
        amountField.setBounds(275, 250, 350, 30);
        amountField.setFont(new Font("Arial", Font.BOLD, 20));
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

        withDrawBtn = new JButton("Rút tiền");
        withDrawBtn.setBounds(650, 700, 120, 35);
        withDrawBtn.setFont(new Font("Arial", Font.BOLD, 16));
        withDrawBtn.setBackground(Color.BLACK);
        withDrawBtn.setForeground(Color.WHITE);
        withDrawBtn.addActionListener(this);
        withDrawBtn.setFocusPainted(false);
        add(withDrawBtn);

        getContentPane().setBackground(new Color(222, 255, 228));
        setLayout(null);
        setSize(850, 800);
        setLocation(360, 40);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == withDrawBtn) {
                String amountText = amountField.getText();
                if (amountText.equals("")) {
                    JOptionPane.showMessageDialog(null, "Số tiền không được để trống");
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

                try{
                    DBConnect c = new DBConnect();
                    String q = """
                            SELECT *
                            FROM bank
                            WHERE pin = ?
                    """;
                    PreparedStatement ps = c.connection.prepareStatement(q);
                    ps.setString(1, pin);
                    ResultSet resultSet = ps.executeQuery();
                    int balance = 0;
                    while (resultSet.next()) {
                        if(resultSet.getString("transactiontype").equals("Nạp tiền")) {
                            balance += Integer.parseInt(resultSet.getString("amount"));
                        }
                        else if(resultSet.getString("transactiontype").equals("Rút tiền")){
                            balance -= Integer.parseInt(resultSet.getString("amount"));
                        }
                    }
                    if (amount > balance) {
                        JOptionPane.showMessageDialog(null, "Số dư không đủ để rút");
                        return;
                    }
                    String query = """
                            INSERT INTO Bank(Pin, TransactionDate, TransactionType, Amount)
                            VALUES (?, ?, ?, ?)
                    """;
                    PreparedStatement p = c.connection.prepareStatement(query);
                    p.setString(1, pin);
                    p.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    p.setString(3, "Rút tiền");
                    p.setInt(4, amount);
                    p.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Rút tiền thành công");
                    new Main(pin);
                    setVisible(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if(e.getSource() == returnBtn){
                new Main(pin);
                setVisible(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new WithDraw("");
    }
}
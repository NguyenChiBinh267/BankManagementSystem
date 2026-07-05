package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;

public class PinChange extends JFrame implements ActionListener {
    JLabel bankIconLabel, titleLabel, pinChangeLabel, pinCheckLabel;
    JTextField pinTextField, pinCheckTextField;
    JButton returnBtn, conFirmBtn;
    int accountId;
    PinChange(int accountId) {
        super("Đổi mã pin");
        this.accountId = accountId;

        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank_icon.png"));
        Image scaledBankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        bankIconLabel = new JLabel(new ImageIcon(scaledBankImage));
        bankIconLabel.setBounds(25, 10, 100, 100);
        add(bankIconLabel);

        titleLabel = new JLabel("ĐỔI MÃ PIN");
        titleLabel.setBounds(350, 30, 550, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel);

        pinCheckLabel = new JLabel("Nhập mã PIN hiện tại:");
        pinCheckLabel.setBounds(75, 250, 375, 30);
        pinCheckLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(pinCheckLabel);

        pinCheckTextField = new JTextField(15);
        pinCheckTextField.setBounds(325, 250, 350, 30);
        pinCheckTextField.setFont(new Font("Arial", Font.BOLD, 20));
        add(pinCheckTextField);

        pinChangeLabel = new JLabel("Nhập mã PIN muốn đổi:");
        pinChangeLabel.setBounds(75, 350, 375, 30);
        pinChangeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(pinChangeLabel);

        pinTextField = new JTextField(15);
        pinTextField.setBounds(325, 350, 350, 30);
        pinTextField.setFont(new Font("Arial", Font.BOLD, 20));
        add(pinTextField);

        returnBtn = new JButton("Quay lại");
        returnBtn.setBounds(500, 700, 120, 35);
        returnBtn.setFont(new Font("Arial", Font.BOLD, 16));
        returnBtn.setBackground(Color.BLACK);
        returnBtn.setForeground(Color.WHITE);
        returnBtn.addActionListener(this);
        returnBtn.setFocusPainted(false);
        add(returnBtn);

        conFirmBtn = new JButton("Xác nhận");
        conFirmBtn.setBounds(650, 700, 120, 35);
        conFirmBtn.setFont(new Font("Arial", Font.BOLD, 16));
        conFirmBtn.setBackground(Color.BLACK);
        conFirmBtn.setForeground(Color.WHITE);
        conFirmBtn.addActionListener(this);
        conFirmBtn.setFocusPainted(false);
        add(conFirmBtn);


        getContentPane().setBackground(new Color(222, 255, 228));
        setLayout(null);
        setSize(850, 800);
        setLocation(360, 40);
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            if (e.getSource() == conFirmBtn) {
                String oldPin = pinCheckTextField.getText();
                String newPin = pinTextField.getText();
                if(oldPin.equals("") || newPin.equals("")){
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin");
                    return;
                }
                if (!oldPin.matches("[0-9]{6}") || !newPin.matches("[0-9]{6}")) {
                    JOptionPane.showMessageDialog(null, "PIN must contain exactly 6 digits");
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

                JOptionPane.showMessageDialog(null, "Đổi mã PIN thành công");
                new Main(accountId);
                setVisible(false);

            }
            else if (e.getSource() == returnBtn) {
                new Main(accountId);
                setVisible(false);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    static void main(String[] args) {
        new PinChange(0);
    }

}

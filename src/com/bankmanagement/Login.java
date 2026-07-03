package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class Login extends JFrame implements ActionListener{
    JLabel bankIconLabel, bankBackgroundLabel, welcomeLabel, cardNumberLabel, PINNumberLabel;
    JTextField cardNumberInputField;
    JPasswordField passwordInputField;
    JButton signInBtn, signUpBtn;
    Login(){
        super("Bank Management System");
        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank_icon.png"));
        Image scaledBankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon scaledBankIcon = new ImageIcon(scaledBankImage);
        bankIconLabel = new JLabel(scaledBankIcon);
        bankIconLabel.setBounds(350, 10, 100, 100);
        add(bankIconLabel);

        welcomeLabel = new JLabel("Chào mừng quý khách");
        welcomeLabel.setFont(new Font("Railway", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(200, 130, 575, 40);
        add(welcomeLabel);

        cardNumberLabel = new JLabel("Số thẻ");
        cardNumberLabel.setFont(new Font("Railway", Font.BOLD, 28));
        cardNumberLabel.setForeground(Color.WHITE);
        cardNumberLabel.setBounds(150, 190, 375, 30);
        add(cardNumberLabel);

        PINNumberLabel = new JLabel("PIN");
        PINNumberLabel.setFont(new Font("Railway", Font.BOLD, 28));
        PINNumberLabel.setForeground(Color.WHITE);
        PINNumberLabel.setBounds(150, 270, 375, 30);
        add(PINNumberLabel);

        cardNumberInputField = new JTextField(15);
        cardNumberInputField.setBounds(305, 200, 230, 30);
        cardNumberInputField.setFont(new Font("Arial", Font.BOLD, 14));
        add(cardNumberInputField);

        passwordInputField = new JPasswordField(15);
        passwordInputField.setBounds(305, 280, 230, 30);
        passwordInputField.setFont(new Font("Arial", Font.BOLD, 14));
        add(passwordInputField);

        signInBtn = new JButton("Đăng nhập");
        signInBtn.setBounds(345, 325, 150,30);
        signInBtn.setFont(new Font("Arial", Font.BOLD, 14));
        signInBtn.addActionListener(this);
        add(signInBtn);

        signUpBtn = new JButton("Đăng ký");
        signUpBtn.setBounds(345, 375, 150,30);
        signUpBtn.setFont(new Font("Arial", Font.BOLD, 14));
        signUpBtn.addActionListener(this);
        add(signUpBtn);

        ImageIcon bankBackground = new ImageIcon(ClassLoader.getSystemResource("icon/bank_background.png"));
        Image scaledBankBackgroundImage = bankBackground.getImage().getScaledInstance(850, 480, Image.SCALE_DEFAULT);
        ImageIcon scaledBankBackground = new ImageIcon(scaledBankBackgroundImage);
        bankBackgroundLabel = new JLabel(scaledBankBackground);
        bankBackgroundLabel.setBounds(0, 0, 850, 480);
        add(bankBackgroundLabel);

        setLayout(null);
        setSize(850, 480);
        setLocation(350, 150);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        try{
            if(e.getSource()==signInBtn){
                DBConnect c = new DBConnect();
                String cardno = cardNumberInputField.getText();
                String pin = passwordInputField.getText();
                String q = "select * from login where card_number = '"+cardno+"' and  pin = '"+pin+"'";
                ResultSet resultSet = c.statement.executeQuery(q);
                if (resultSet.next()){
                    setVisible(false);
                }else {
                    JOptionPane.showMessageDialog(null,"Incorrect Card Number or PIN");
                }
            }
            else if(e.getSource()==signUpBtn){
                new SignUp();
                setVisible(false);
            }
        } catch (Exception E){
            E.printStackTrace();
        }
    }

    static void main(String[] args) {
        new Login();
    }
}

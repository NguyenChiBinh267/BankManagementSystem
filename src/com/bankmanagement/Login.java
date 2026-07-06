package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame implements ActionListener{
    JLabel bankIconLabel, exitIconLabel, bankBackgroundLabel, welcomeLabel, cardNumberLabel, PINNumberLabel;
    JTextField cardNumberInputField;
    JPasswordField passwordInputField;
    JButton signInBtn, signUpBtn, exitBtn;
    Login(){
        super("Bank Management System");

        JPanel page = UIStyle.createImagePage("bank_background.png");

        exitIconLabel = new JLabel(UIStyle.createImageIcon("exit_icon.png", 36, 36));
        exitIconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(exitIconLabel, BorderLayout.EAST);
        page.add(topBar, BorderLayout.NORTH);

        welcomeLabel = new JLabel("Chào mừng quý khách");
        bankIconLabel = UIStyle.createBankIconLabel(88);

        cardNumberLabel = new JLabel("Số thẻ");
        PINNumberLabel = new JLabel("PIN");

        cardNumberInputField = new JTextField(15);
        UIStyle.styleTextField(cardNumberInputField);

        passwordInputField = new JPasswordField(15);
        UIStyle.styleTextField(passwordInputField);

        signInBtn = new JButton("Đăng nhập");
        UIStyle.styleButton(signInBtn);
        signInBtn.addActionListener(this);

        signUpBtn = new JButton("Đăng ký");
        UIStyle.styleButton(signUpBtn);
        signUpBtn.addActionListener(this);

        JPanel card = UIStyle.createCard();
        card.setLayout(new BorderLayout(0, 22));
        card.setPreferredSize(new Dimension(560, 420));

        card.add(UIStyle.createHeader(bankIconLabel, welcomeLabel, null), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        UIStyle.addFormRow(form, 0, cardNumberLabel, cardNumberInputField);
        UIStyle.addFormRow(form, 1, PINNumberLabel, passwordInputField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(signInBtn);
        buttonPanel.add(signUpBtn);
        UIStyle.addFullWidthRow(form, 2, buttonPanel);

        card.add(form, BorderLayout.CENTER);
        page.add(UIStyle.center(card), BorderLayout.CENTER);

        setContentPane(page);
        getRootPane().setDefaultButton(signInBtn);
        UIStyle.showFrame(this, 850, 620);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        try{
            if(e.getSource()==signInBtn){
                String cardno = cardNumberInputField.getText();
                String pin = new String(passwordInputField.getPassword());

                if (!pin.matches("[0-9]{6}")) {
                    JOptionPane.showMessageDialog(null, "PIN must contain exactly 6 digits");
                    return;
                }

                DBConnect c = new DBConnect();
                String q = "SELECT AccountID FROM Login WHERE cardNumber = ? AND pin = ?";
                PreparedStatement ps = c.connection.prepareStatement(q);
                ps.setString(1, cardno);
                ps.setString(2, pin);
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()){
                    int accountId = resultSet.getInt("AccountID");
                    setVisible(false);
                    new Main(accountId);
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

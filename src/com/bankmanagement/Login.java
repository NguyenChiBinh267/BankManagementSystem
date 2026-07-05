package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame implements ActionListener{
    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(path));
        Image img = icon.getImage();

        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(img, 0, 0, width, height, null);
        g2.dispose();

        return new ImageIcon(resized);
    }
    JLabel bankIconLabel, exitIconLabel, bankBackgroundLabel, welcomeLabel, cardNumberLabel, PINNumberLabel;
    JTextField cardNumberInputField;
    JPasswordField passwordInputField;
    JButton signInBtn, signUpBtn, exitBtn;
    Login(){
        super("Bank Management System");
        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank_icon.png"));
        Image scaledBankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon scaledBankIcon = new ImageIcon(scaledBankImage);
        bankIconLabel = new JLabel(scaledBankIcon);
        bankIconLabel.setBounds(350, 10, 100, 100);
        add(bankIconLabel);

        ImageIcon scaledExitIcon = resizeIcon("icon/exit_icon.png", 50, 50);

        exitIconLabel = new JLabel(scaledExitIcon);
        exitIconLabel.setBounds(750, 10, 50, 50);
        exitIconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(exitIconLabel);

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
        signInBtn.setFocusPainted(false);
        signInBtn.addActionListener(this);
        add(signInBtn);

        signUpBtn = new JButton("Đăng ký");
        signUpBtn.setBounds(345, 375, 150,30);
        signUpBtn.setFont(new Font("Arial", Font.BOLD, 14));
        signUpBtn.setFocusPainted(false);
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

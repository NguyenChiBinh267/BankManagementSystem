package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.util.Random;

public class SignUp3 extends JFrame implements ActionListener {

    JLabel bankIconLabel, applicationFormNo, signUpPageNo, signUpPageDetail;
    JLabel accountTypeLabel, cardNumberLabel, cardNumberNoteLabel;
    JLabel pinLabel, pinNoteLabel, servicesLabel;

    JRadioButton savingAccountRadioBtn, fixedDepositRadioBtn, currentAccountRadioBtn, recurringDepositRadioBtn;
    JCheckBox atmCardCheckBox, internetBankingCheckBox, mobileBankingCheckBox;
    JCheckBox emailAlertCheckBox, chequeBookCheckBox, eStatementCheckBox, confirmCheckBox;

    JButton submitBtn, returnBtn;

    String formNo;
    String pin;

    public SignUp3(String formNo, String pin) {
        super("Application Form");
        this.formNo = formNo;
        this.pin = pin;

        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank_icon.png"));
        Image scaledBankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon scaledBankIcon = new ImageIcon(scaledBankImage);

        bankIconLabel = new JLabel(scaledBankIcon);
        bankIconLabel.setBounds(25, 10, 100, 100);
        add(bankIconLabel);

        applicationFormNo = new JLabel("APPLICATION FORM NO." + formNo);
        applicationFormNo.setBounds(250, 30, 500, 30);
        applicationFormNo.setFont(new Font("Arial", Font.BOLD, 32));
        add(applicationFormNo);

        signUpPageNo = new JLabel("Page 3");
        signUpPageNo.setBounds(400, 60, 500, 30);
        signUpPageNo.setFont(new Font("Arial", Font.BOLD, 20));
        add(signUpPageNo);

        signUpPageDetail = new JLabel("Thông tin tài khoản");
        signUpPageDetail.setBounds(330, 90, 500, 30);
        signUpPageDetail.setFont(new Font("Arial", Font.BOLD, 24));
        add(signUpPageDetail);

        accountTypeLabel = new JLabel("Loại tài khoản :");
        accountTypeLabel.setBounds(75, 170, 375, 30);
        accountTypeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(accountTypeLabel);

        savingAccountRadioBtn = new JRadioButton("Tài khoản tiết kiệm");
        savingAccountRadioBtn.setBounds(275, 170, 220, 30);
        savingAccountRadioBtn.setFont(new Font("Arial", Font.BOLD, 16));
        savingAccountRadioBtn.setBackground(new Color(222, 255, 228));
        add(savingAccountRadioBtn);

        fixedDepositRadioBtn = new JRadioButton("Tiền gửi cố định");
        fixedDepositRadioBtn.setBounds(510, 170, 220, 30);
        fixedDepositRadioBtn.setFont(new Font("Arial", Font.BOLD, 16));
        fixedDepositRadioBtn.setBackground(new Color(222, 255, 228));
        add(fixedDepositRadioBtn);

        currentAccountRadioBtn = new JRadioButton("Tài khoản thanh toán");
        currentAccountRadioBtn.setBounds(275, 210, 220, 30);
        currentAccountRadioBtn.setFont(new Font("Arial", Font.BOLD, 16));
        currentAccountRadioBtn.setBackground(new Color(222, 255, 228));
        add(currentAccountRadioBtn);

        recurringDepositRadioBtn = new JRadioButton("Tiền gửi định kỳ");
        recurringDepositRadioBtn.setBounds(510, 210, 220, 30);
        recurringDepositRadioBtn.setFont(new Font("Arial", Font.BOLD, 16));
        recurringDepositRadioBtn.setBackground(new Color(222, 255, 228));
        add(recurringDepositRadioBtn);

        ButtonGroup accountTypeGroup = new ButtonGroup();
        accountTypeGroup.add(savingAccountRadioBtn);
        accountTypeGroup.add(fixedDepositRadioBtn);
        accountTypeGroup.add(currentAccountRadioBtn);
        accountTypeGroup.add(recurringDepositRadioBtn);

        cardNumberLabel = new JLabel("Số thẻ :");
        cardNumberLabel.setBounds(75, 280, 375, 30);
        cardNumberLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(cardNumberLabel);

        JLabel cardNumberValueLabel = new JLabel("XXXX-XXXX-XXXX-XXXX");
        cardNumberValueLabel.setBounds(275, 280, 350, 30);
        cardNumberValueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(cardNumberValueLabel);

        cardNumberNoteLabel = new JLabel("Số thẻ sẽ được hệ thống tự động tạo sau khi đăng ký.");
        cardNumberNoteLabel.setBounds(275, 310, 500, 20);
        cardNumberNoteLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        add(cardNumberNoteLabel);

        pinLabel = new JLabel("Mã PIN :");
        pinLabel.setBounds(75, 350, 375, 30);
        pinLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(pinLabel);

        JLabel pinValueLabel = new JLabel(pin);
        pinValueLabel.setBounds(275, 350, 350, 30);
        pinValueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(pinValueLabel);

        pinNoteLabel = new JLabel("Mã PIN 6 chữ số đã nhập ở trang 1.");
        pinNoteLabel.setBounds(275, 380, 500, 20);
        pinNoteLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        add(pinNoteLabel);

        servicesLabel = new JLabel("Dịch vụ đăng ký :");
        servicesLabel.setBounds(75, 430, 375, 30);
        servicesLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(servicesLabel);

        atmCardCheckBox = new JCheckBox("Thẻ ATM");
        atmCardCheckBox.setBounds(275, 430, 200, 30);
        atmCardCheckBox.setFont(new Font("Arial", Font.BOLD, 16));
        atmCardCheckBox.setBackground(new Color(222, 255, 228));
        add(atmCardCheckBox);

        internetBankingCheckBox = new JCheckBox("Internet Banking");
        internetBankingCheckBox.setBounds(510, 430, 220, 30);
        internetBankingCheckBox.setFont(new Font("Arial", Font.BOLD, 16));
        internetBankingCheckBox.setBackground(new Color(222, 255, 228));
        add(internetBankingCheckBox);

        mobileBankingCheckBox = new JCheckBox("Mobile Banking");
        mobileBankingCheckBox.setBounds(275, 480, 200, 30);
        mobileBankingCheckBox.setFont(new Font("Arial", Font.BOLD, 16));
        mobileBankingCheckBox.setBackground(new Color(222, 255, 228));
        add(mobileBankingCheckBox);

        emailAlertCheckBox = new JCheckBox("Thông báo Email");
        emailAlertCheckBox.setBounds(510, 480, 220, 30);
        emailAlertCheckBox.setFont(new Font("Arial", Font.BOLD, 16));
        emailAlertCheckBox.setBackground(new Color(222, 255, 228));
        add(emailAlertCheckBox);

        chequeBookCheckBox = new JCheckBox("Sổ séc");
        chequeBookCheckBox.setBounds(275, 530, 200, 30);
        chequeBookCheckBox.setFont(new Font("Arial", Font.BOLD, 16));
        chequeBookCheckBox.setBackground(new Color(222, 255, 228));
        add(chequeBookCheckBox);

        eStatementCheckBox = new JCheckBox("Sao kê điện tử");
        eStatementCheckBox.setBounds(510, 530, 220, 30);
        eStatementCheckBox.setFont(new Font("Arial", Font.BOLD, 16));
        eStatementCheckBox.setBackground(new Color(222, 255, 228));
        add(eStatementCheckBox);

        confirmCheckBox = new JCheckBox("Tôi xác nhận các thông tin đã nhập là chính xác.");
        confirmCheckBox.setBounds(75, 620, 650, 30);
        confirmCheckBox.setFont(new Font("Arial", Font.BOLD, 15));
        confirmCheckBox.setBackground(new Color(222, 255, 228));
        add(confirmCheckBox);

        returnBtn = new JButton("Quay lại");
        returnBtn.setBounds(500, 700, 120, 35);
        returnBtn.setFont(new Font("Arial", Font.BOLD, 16));
        returnBtn.setBackground(Color.BLACK);
        returnBtn.setForeground(Color.WHITE);
        returnBtn.addActionListener(this);
        returnBtn.setFocusPainted(false);
        add(returnBtn);

        submitBtn = new JButton("Hoàn tất");
        submitBtn.setBounds(650, 700, 120, 35);
        submitBtn.setFont(new Font("Arial", Font.BOLD, 16));
        submitBtn.setBackground(Color.BLACK);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(this);
        add(submitBtn);

        getContentPane().setBackground(new Color(222, 255, 228));
        setLayout(null);
        setSize(850, 800);
        setLocation(360, 40);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitBtn) {
            String DBAccountType = null;

            if (savingAccountRadioBtn.isSelected()) {
                DBAccountType = "Tài khoản tiết kiệm";
            } else if (fixedDepositRadioBtn.isSelected()) {
                DBAccountType = "Tiền gửi cố định";
            } else if (currentAccountRadioBtn.isSelected()) {
                DBAccountType = "Tài khoản thanh toán";
            } else if (recurringDepositRadioBtn.isSelected()) {
                DBAccountType = "Tiền gửi định kỳ";
            }

            String DBServices = "";

            if (atmCardCheckBox.isSelected()) {
                DBServices += "Thẻ ATM, ";
            }
            if (internetBankingCheckBox.isSelected()) {
                DBServices += "Internet Banking, ";
            }
            if (mobileBankingCheckBox.isSelected()) {
                DBServices += "Mobile Banking, ";
            }
            if (emailAlertCheckBox.isSelected()) {
                DBServices += "Thông báo Email, ";
            }
            if (chequeBookCheckBox.isSelected()) {
                DBServices += "Sổ séc, ";
            }
            if (eStatementCheckBox.isSelected()) {
                DBServices += "Sao kê điện tử, ";
            }

            if (DBServices.endsWith(", ")) {
                DBServices = DBServices.substring(0, DBServices.length() - 2);
            }

            Random random = new Random();

            long cardNumberValue = Math.abs(random.nextLong() % 90000000L) + 1409963000000000L;
            String DBCardNumber = String.valueOf(cardNumberValue);

            String DBPin = pin;

            try {
                if (DBAccountType == null || DBServices.equals("") || DBPin.equals("") || !confirmCheckBox.isSelected()) {
                    JOptionPane.showMessageDialog(null, "Fill all the fields");
                } else {
                    DBConnect conn = new DBConnect();

                    String q1 = "INSERT INTO SignUp3(FormID, AccountType, CardNumber, Services) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps1 = conn.connection.prepareStatement(q1);

                    ps1.setString(1, formNo);
                    ps1.setString(2, DBAccountType);
                    ps1.setString(3, DBCardNumber);
                    ps1.setString(4, DBServices);

                    ps1.executeUpdate();

                    String q2 = "INSERT INTO Login(formID, cardNumber, pin) VALUES (?, ?, ?)";
                    PreparedStatement ps2 = conn.connection.prepareStatement(q2);

                    ps2.setString(1, formNo);
                    ps2.setString(2, DBCardNumber);
                    ps2.setString(3, DBPin);

                    ps2.executeUpdate();

                    JOptionPane.showMessageDialog(
                            null,
                            "Đăng ký tài khoản thành công\n\nSố thẻ: " + DBCardNumber + "\nMã PIN: " + DBPin
                    );
                    new Login();
                    setVisible(false);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == returnBtn) {
            new SignUp2(formNo, pin);
            setVisible(false);
        }
    }

    public static void main(String[] args) {

        new SignUp3("", "");
    }
}

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
        super("Biểu mẫu đăng ký");
        this.formNo = formNo;
        this.pin = pin;

        JPanel page = UIStyle.createPage();

        bankIconLabel = UIStyle.createBankIconLabel(82);
        applicationFormNo = new JLabel("MÃ HỒ SƠ ĐĂNG KÝ" + formNo);
        signUpPageNo = new JLabel("Trang 3");
        signUpPageDetail = new JLabel("Thông tin tài khoản");

        JPanel header = UIStyle.createHeader(bankIconLabel, applicationFormNo, signUpPageNo);
        UIStyle.styleSubtitle(signUpPageDetail);
        signUpPageDetail.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(Box.createVerticalStrut(2));
        header.add(signUpPageDetail);
        page.add(header, BorderLayout.NORTH);

        accountTypeLabel = new JLabel("Loại tài khoản :");

        savingAccountRadioBtn = new JRadioButton("Tài khoản tiết kiệm");
        fixedDepositRadioBtn = new JRadioButton("Tiền gửi cố định");
        currentAccountRadioBtn = new JRadioButton("Tài khoản thanh toán");
        recurringDepositRadioBtn = new JRadioButton("Tiền gửi định kỳ");

        ButtonGroup accountTypeGroup = new ButtonGroup();
        accountTypeGroup.add(savingAccountRadioBtn);
        accountTypeGroup.add(fixedDepositRadioBtn);
        accountTypeGroup.add(currentAccountRadioBtn);
        accountTypeGroup.add(recurringDepositRadioBtn);

        cardNumberLabel = new JLabel("Số thẻ :");

        JLabel cardNumberValueLabel = new JLabel("XXXX-XXXX-XXXX-XXXX");
        cardNumberValueLabel.setFont(UIStyle.FIELD_FONT);
        cardNumberValueLabel.setForeground(UIStyle.TEXT);

        cardNumberNoteLabel = new JLabel("Số thẻ sẽ được hệ thống tự động tạo sau khi đăng ký.");
        UIStyle.styleNoteLabel(cardNumberNoteLabel);

        pinLabel = new JLabel("Mã PIN :");

        JLabel pinValueLabel = new JLabel(pin);
        pinValueLabel.setFont(UIStyle.FIELD_FONT);
        pinValueLabel.setForeground(UIStyle.TEXT);

        pinNoteLabel = new JLabel("Mã PIN 6 chữ số đã nhập ở trang 1.");
        UIStyle.styleNoteLabel(pinNoteLabel);

        servicesLabel = new JLabel("Dịch vụ đăng ký :");

        atmCardCheckBox = new JCheckBox("Thẻ ATM");
        internetBankingCheckBox = new JCheckBox("Ngân hàng trực tuyến");
        mobileBankingCheckBox = new JCheckBox("Ngân hàng di động");
        emailAlertCheckBox = new JCheckBox("Thông báo Email");
        chequeBookCheckBox = new JCheckBox("Sổ séc");
        eStatementCheckBox = new JCheckBox("Sao kê điện tử");

        confirmCheckBox = new JCheckBox("Tôi xác nhận các thông tin đã nhập là chính xác.");
        UIStyle.styleOption(confirmCheckBox);

        returnBtn = new JButton("Quay lại");
        UIStyle.styleButton(returnBtn);
        returnBtn.addActionListener(this);

        submitBtn = new JButton("Hoàn tất");
        UIStyle.styleButton(submitBtn);
        submitBtn.addActionListener(this);

        JPanel accountTypePanel = new JPanel(new GridLayout(2, 2, 10, 6));
        accountTypePanel.setOpaque(false);
        UIStyle.styleOption(savingAccountRadioBtn);
        UIStyle.styleOption(fixedDepositRadioBtn);
        UIStyle.styleOption(currentAccountRadioBtn);
        UIStyle.styleOption(recurringDepositRadioBtn);
        accountTypePanel.add(savingAccountRadioBtn);
        accountTypePanel.add(fixedDepositRadioBtn);
        accountTypePanel.add(currentAccountRadioBtn);
        accountTypePanel.add(recurringDepositRadioBtn);

        JPanel cardNumberPanel = new JPanel();
        cardNumberPanel.setOpaque(false);
        cardNumberPanel.setLayout(new BoxLayout(cardNumberPanel, BoxLayout.Y_AXIS));
        cardNumberPanel.add(cardNumberValueLabel);
        cardNumberPanel.add(Box.createVerticalStrut(4));
        cardNumberPanel.add(cardNumberNoteLabel);

        JPanel pinPanel = new JPanel();
        pinPanel.setOpaque(false);
        pinPanel.setLayout(new BoxLayout(pinPanel, BoxLayout.Y_AXIS));
        pinPanel.add(pinValueLabel);
        pinPanel.add(Box.createVerticalStrut(4));
        pinPanel.add(pinNoteLabel);

        JPanel servicesPanel = new JPanel(new GridLayout(3, 2, 10, 6));
        servicesPanel.setOpaque(false);
        UIStyle.styleOption(atmCardCheckBox);
        UIStyle.styleOption(internetBankingCheckBox);
        UIStyle.styleOption(mobileBankingCheckBox);
        UIStyle.styleOption(emailAlertCheckBox);
        UIStyle.styleOption(chequeBookCheckBox);
        UIStyle.styleOption(eStatementCheckBox);
        servicesPanel.add(atmCardCheckBox);
        servicesPanel.add(internetBankingCheckBox);
        servicesPanel.add(mobileBankingCheckBox);
        servicesPanel.add(emailAlertCheckBox);
        servicesPanel.add(chequeBookCheckBox);
        servicesPanel.add(eStatementCheckBox);

        JPanel card = UIStyle.createCard();
        card.setPreferredSize(new Dimension(730, 540));
        UIStyle.addFormRow(card, 0, accountTypeLabel, accountTypePanel);
        UIStyle.addFormRow(card, 1, cardNumberLabel, cardNumberPanel);
        UIStyle.addFormRow(card, 2, pinLabel, pinPanel);
        UIStyle.addFormRow(card, 3, servicesLabel, servicesPanel);
        UIStyle.addFullWidthRow(card, 4, confirmCheckBox);
        UIStyle.addFullWidthRow(card, 5, UIStyle.createButtonPanel(returnBtn, submitBtn));

        page.add(UIStyle.center(card), BorderLayout.CENTER);
        setContentPane(page);
        getRootPane().setDefaultButton(submitBtn);
        UIStyle.showFrame(this, 900, 850);
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
                DBServices += "Ngân hàng trực tuyến, ";
            }
            if (mobileBankingCheckBox.isSelected()) {
                DBServices += "Ngân hàng di động, ";
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
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin");
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

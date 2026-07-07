package com.bankmanagement;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.util.Random;

public class SignUp extends JFrame implements ActionListener {
    JLabel bankIconLabel, applicationFormNo, signUpPageNo, signUpPageDetail, customerName, customerEmail, gender, birthday, address, city, pin, phone;
    JTextField customerNameField, customerEmailField, addressField, cityField, pinField, phoneField;
    JRadioButton maleGenderRadioBtn, femaleGenderRadioBtn;
    JDateChooser birthdayDC;
    JButton nextBtn, returnBtn;
    Random ran = new Random();
    long appFormNumber = (ran.nextLong()%9000L) + 1000L;
    String appFormNo = " " + Math.abs(appFormNumber);
    SignUp(){
        super("Biểu mẫu đăng ký");

        JPanel page = UIStyle.createPage();

        bankIconLabel = UIStyle.createBankIconLabel(82);
        applicationFormNo = new JLabel("MÃ HỒ SƠ ĐĂNG KÝ" + appFormNo);
        signUpPageNo = new JLabel("Trang 1");
        signUpPageDetail = new JLabel("Thông tin cá nhân");

        JPanel header = UIStyle.createHeader(bankIconLabel, applicationFormNo, signUpPageNo);
        UIStyle.styleSubtitle(signUpPageDetail);
        signUpPageDetail.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(Box.createVerticalStrut(2));
        header.add(signUpPageDetail);
        page.add(header, BorderLayout.NORTH);

        customerName = new JLabel("Tên khách hàng :");
        customerEmail = new JLabel("Email :");
        phone = new JLabel("Số điện thoại :");
        gender = new JLabel("Giới tính :");
        birthday = new JLabel("Ngày sinh :");
        address = new JLabel("Địa chỉ :");
        city = new JLabel("Thành phố :");
        pin = new JLabel("Mã PIN :");

        customerNameField = new JTextField(15);
        UIStyle.styleTextField(customerNameField);

        customerEmailField = new JTextField(15);
        UIStyle.styleTextField(customerEmailField);

        phoneField = new JTextField(15);
        UIStyle.styleTextField(phoneField);

        maleGenderRadioBtn = new JRadioButton("Nam");
        femaleGenderRadioBtn = new JRadioButton("Nữ");

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleGenderRadioBtn);
        genderGroup.add(femaleGenderRadioBtn);

        birthdayDC = new JDateChooser();
        birthdayDC.setForeground(new Color(105, 105, 105));
        birthdayDC.setFont(UIStyle.FIELD_FONT);
        birthdayDC.setPreferredSize(new Dimension(360, 38));

        addressField = new JTextField(15);
        UIStyle.styleTextField(addressField);

        cityField = new JTextField(15);
        UIStyle.styleTextField(cityField);

        pinField = new JTextField(15);
        UIStyle.styleTextField(pinField);

        nextBtn = new JButton("Tiếp tục");
        UIStyle.styleButton(nextBtn);
        nextBtn.addActionListener(this);

        returnBtn = new JButton("Quay lại");
        UIStyle.styleButton(returnBtn);
        returnBtn.addActionListener(this);

        JPanel card = UIStyle.createCard();
        card.setPreferredSize(new Dimension(700, 510));
        UIStyle.addFormRow(card, 0, customerName, customerNameField);
        UIStyle.addFormRow(card, 1, customerEmail, customerEmailField);
        UIStyle.addFormRow(card, 2, phone, phoneField);
        UIStyle.addFormRow(card, 3, gender, UIStyle.createOptionPanel(maleGenderRadioBtn, femaleGenderRadioBtn));
        UIStyle.addFormRow(card, 4, birthday, birthdayDC);
        UIStyle.addFormRow(card, 5, address, addressField);
        UIStyle.addFormRow(card, 6, city, cityField);
        UIStyle.addFormRow(card, 7, pin, pinField);
        UIStyle.addFullWidthRow(card, 8, UIStyle.createButtonPanel(returnBtn, nextBtn));

        page.add(UIStyle.center(card), BorderLayout.CENTER);
        setContentPane(page);
        getRootPane().setDefaultButton(nextBtn);
        UIStyle.showFrame(this, 900, 850);
    }

    public void actionPerformed(ActionEvent e){
        try{
            if(e.getSource()==nextBtn){
                String DBFormNo = appFormNo;
                String DBCustomerName = customerNameField.getText();
                String DBEmail = customerEmailField.getText();
                String DBPhone = phoneField.getText();
                String DBGender = null;
                if(maleGenderRadioBtn.isSelected()) DBGender = "Nam";
                else if (femaleGenderRadioBtn.isSelected()) DBGender = "Nữ";
                String DBCustomerBirth = ((JTextField) birthdayDC.getDateEditor().getUiComponent()).getText();
                String DBAddress = addressField.getText();
                String DBCity = cityField.getText();
                String DBPin = pinField.getText();
                try{
                    if (DBCustomerName.equals("") || DBEmail.equals("") || DBGender == null ||
                            DBCustomerBirth.equals("") || DBAddress.equals("") ||
                            DBCity.equals("") || DBPin.equals("") || DBPhone.equals("")) {
                        JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin");
                    } else if (!DBPin.matches("[0-9]{6}")) {
                        JOptionPane.showMessageDialog(null, "PIN phải gồm đúng 6 chữ số");
                    }else {
                        DBConnect conn = new DBConnect();
                        String q = "INSERT INTO SignUp VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                        PreparedStatement ps = conn.connection.prepareStatement(q);

                        ps.setString(1, DBFormNo);
                        ps.setString(2, DBCustomerName);
                        ps.setString(3, DBEmail);
                        ps.setString(4, DBPhone);
                        ps.setString(5, DBGender);
                        ps.setString(6, DBCustomerBirth);
                        ps.setString(7, DBAddress);
                        ps.setString(8, DBCity);
                        ps.setString(9, DBPin);

                        ps.executeUpdate();
                        new SignUp2(appFormNo, DBPin);
                        setVisible(false);
                    }
                } catch (Exception E){
                    E.printStackTrace();
                }
            }
            else if(e.getSource()==returnBtn) {
                new Login();
                setVisible(false);
            }
        } catch (Exception E){
            E.printStackTrace();
        }
    }

    static void main(String[] args) {
        new SignUp();
    }
}

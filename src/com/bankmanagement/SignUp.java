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
        super("Application Form");

        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank_icon.png"));
        Image scaledBankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon scaledBankIcon = new ImageIcon(scaledBankImage);
        bankIconLabel = new JLabel(scaledBankIcon);
        bankIconLabel.setBounds(25, 10, 100, 100);
        add(bankIconLabel);

        applicationFormNo = new JLabel("APPLICATION FORM NO." + appFormNo);
        applicationFormNo.setBounds(250, 30, 500, 30);
        applicationFormNo.setFont(new Font("Arial", Font.BOLD, 32));
        add(applicationFormNo);

        signUpPageNo = new JLabel("Page 1");
        signUpPageNo.setBounds(400, 60, 500, 30);
        signUpPageNo.setFont(new Font("Arial", Font.BOLD, 20));
        add(signUpPageNo);

        signUpPageDetail = new JLabel("Personal Detail");
        signUpPageDetail.setBounds(350, 90, 500, 30);
        signUpPageDetail.setFont(new Font("Arial", Font.BOLD, 24));
        add(signUpPageDetail);

        customerName = new JLabel("Tên khách hàng :");
        customerName.setBounds(75, 200, 375, 30);
        customerName.setFont(new Font("Arial", Font.BOLD, 20));
        add(customerName);

        customerEmail = new JLabel("Email :");
        customerEmail.setBounds(75, 250, 375, 30);
        customerEmail.setFont(new Font("Arial", Font.BOLD, 20));
        add(customerEmail);

        phone = new JLabel("Số điện thoại :");
        phone.setBounds(75, 550, 375, 30);
        phone.setFont(new Font("Arial", Font.BOLD, 20));
        add(phone);

        gender = new JLabel("Giới tính :");
        gender.setBounds(75, 300, 375, 30);
        gender.setFont(new Font("Arial", Font.BOLD, 20));
        add(gender);

        birthday = new JLabel("Ngày sinh :");
        birthday.setBounds(75, 350, 375, 30);
        birthday.setFont(new Font("Arial", Font.BOLD, 20));
        add(birthday);

        address = new JLabel("Địa chỉ :");
        address.setBounds(75, 400, 375, 30);
        address.setFont(new Font("Arial", Font.BOLD, 20));
        add(address);

        city = new JLabel("Thành phố :");
        city.setBounds(75, 450, 375, 30);
        city.setFont(new Font("Arial", Font.BOLD, 20));
        add(city);

        pin = new JLabel("Mã PIN :");
        pin.setBounds(75, 500, 375, 30);
        pin.setFont(new Font("Arial", Font.BOLD, 20));
        add(pin);

        customerNameField = new JTextField(15);
        customerNameField.setBounds(275, 200, 350, 30);
        customerNameField.setFont(new Font("Arial", Font.BOLD, 18));
        add(customerNameField);

        customerEmailField = new JTextField(15);
        customerEmailField.setBounds(275, 250, 350, 30);
        customerEmailField.setFont(new Font("Arial", Font.BOLD, 18));
        add(customerEmailField);

        phoneField = new JTextField(15);
        phoneField.setBounds(275, 550, 350, 30);
        phoneField.setFont(new Font("Arial", Font.BOLD, 18));
        add(phoneField);

        maleGenderRadioBtn = new JRadioButton("Nam");
        maleGenderRadioBtn.setFont(new Font("Arial", Font.BOLD, 18));
        maleGenderRadioBtn.setBackground(new Color(222, 255, 228));
        maleGenderRadioBtn.setBounds(275, 300, 100, 30);
        add(maleGenderRadioBtn);

        femaleGenderRadioBtn = new JRadioButton("Nữ");
        femaleGenderRadioBtn.setFont(new Font("Arial", Font.BOLD, 18));
        femaleGenderRadioBtn.setBackground(new Color(222, 255, 228));
        femaleGenderRadioBtn.setBounds(375, 300, 100, 30);
        add(femaleGenderRadioBtn);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleGenderRadioBtn);
        genderGroup.add(femaleGenderRadioBtn);

        birthdayDC = new JDateChooser();
        birthdayDC.setForeground(new Color(105, 105, 105));
        birthdayDC.setFont(new Font("Arial", Font.BOLD, 18));
        birthdayDC.setBounds(275, 350, 375, 30);
        add(birthdayDC);

        addressField = new JTextField(15);
        addressField.setBounds(275, 400, 350, 30);
        addressField.setFont(new Font("Arial", Font.BOLD, 18));
        add(addressField);

        cityField = new JTextField(15);
        cityField.setBounds(275, 450, 350, 30);
        cityField.setFont(new Font("Arial", Font.BOLD, 18));
        add(cityField);

        pinField = new JTextField(15);
        pinField.setBounds(275, 500, 350, 30);
        pinField.setFont(new Font("Arial", Font.BOLD, 18));
        add(pinField);

        nextBtn = new JButton("Tiếp tục");
        nextBtn.setBounds(650, 700, 120, 35);
        nextBtn.setFont(new Font("Arial", Font.BOLD, 16));
        nextBtn.setBackground(Color.BLACK);
        nextBtn.setForeground(Color.WHITE);
        nextBtn.addActionListener(this);
        add(nextBtn);

        returnBtn = new JButton("Quay lại");
        returnBtn.setBounds(500, 700, 120, 35);
        returnBtn.setFont(new Font("Arial", Font.BOLD, 16));
        returnBtn.setBackground(Color.BLACK);
        returnBtn.setForeground(Color.WHITE);
        returnBtn.addActionListener(this);
        add(returnBtn);

        getContentPane().setBackground(new Color(222,255, 228));
        setLayout(null);
        setSize(850, 800);
        setLocation(360, 40);
        setVisible(true);
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
                        JOptionPane.showMessageDialog(null, "Fill all the fields");
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
                        new SignUp2(appFormNo);
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

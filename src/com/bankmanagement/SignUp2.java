package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;

public class SignUp2 extends JFrame implements ActionListener {

    JLabel bankIconLabel, applicationFormNo, signUpPageNo, signUpPageDetail;
    JLabel religionLabel, categoryLabel, incomeLabel, educationLabel, occupationLabel;
    JLabel citizenIDLabel, seniorCitizenLabel, existingAccountLabel;

    JComboBox<String> religionComboBox, categoryComboBox, incomeComboBox, educationComboBox, occupationComboBox;
    JTextField citizenIDField;
    JRadioButton seniorYesRadioBtn, seniorNoRadioBtn, accountYesRadioBtn, accountNoRadioBtn;
    JButton nextBtn, returnBtn;

    String formNo;
    String pin;

    public SignUp2(String formNo, String pin) {
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

        signUpPageNo = new JLabel("Page 2");
        signUpPageNo.setBounds(400, 60, 500, 30);
        signUpPageNo.setFont(new Font("Arial", Font.BOLD, 20));
        add(signUpPageNo);

        signUpPageDetail = new JLabel("Thông tin bổ sung");
        signUpPageDetail.setBounds(335, 90, 500, 30);
        signUpPageDetail.setFont(new Font("Arial", Font.BOLD, 24));
        add(signUpPageDetail);

        religionLabel = new JLabel("Tôn giáo :");
        religionLabel.setBounds(75, 200, 375, 30);
        religionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(religionLabel);

        String[] religions = {"Không", "Phật giáo", "Thiên Chúa giáo", "Hồi giáo", "Khác"};
        religionComboBox = new JComboBox<>(religions);
        religionComboBox.setBounds(275, 200, 350, 30);
        religionComboBox.setFont(new Font("Arial", Font.BOLD, 18));
        add(religionComboBox);

        categoryLabel = new JLabel("Loại khách hàng :");
        categoryLabel.setBounds(75, 250, 375, 30);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(categoryLabel);

        String[] categories = {"Cá nhân", "Doanh nghiệp", "Sinh viên", "Khác"};
        categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setBounds(275, 250, 350, 30);
        categoryComboBox.setFont(new Font("Arial", Font.BOLD, 18));
        add(categoryComboBox);

        incomeLabel = new JLabel("Thu nhập :");
        incomeLabel.setBounds(75, 300, 375, 30);
        incomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(incomeLabel);

        String[] incomes = {"Không có", "Dưới 5 triệu", "5 - 10 triệu", "10 - 20 triệu", "Trên 20 triệu"};
        incomeComboBox = new JComboBox<>(incomes);
        incomeComboBox.setBounds(275, 300, 350, 30);
        incomeComboBox.setFont(new Font("Arial", Font.BOLD, 18));
        add(incomeComboBox);

        educationLabel = new JLabel("Học vấn :");
        educationLabel.setBounds(75, 350, 375, 30);
        educationLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(educationLabel);

        String[] educations = {"THPT", "Cao đẳng", "Đại học", "Sau đại học", "Khác"};
        educationComboBox = new JComboBox<>(educations);
        educationComboBox.setBounds(275, 350, 350, 30);
        educationComboBox.setFont(new Font("Arial", Font.BOLD, 18));
        add(educationComboBox);

        occupationLabel = new JLabel("Nghề nghiệp :");
        occupationLabel.setBounds(75, 400, 375, 30);
        occupationLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(occupationLabel);

        String[] occupations = {"Sinh viên", "Nhân viên văn phòng", "Kinh doanh", "Tự do", "Khác"};
        occupationComboBox = new JComboBox<>(occupations);
        occupationComboBox.setBounds(275, 400, 350, 30);
        occupationComboBox.setFont(new Font("Arial", Font.BOLD, 18));
        add(occupationComboBox);

        citizenIDLabel = new JLabel("Số CCCD :");
        citizenIDLabel.setBounds(75, 450, 375, 30);
        citizenIDLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(citizenIDLabel);

        citizenIDField = new JTextField(15);
        citizenIDField.setBounds(275, 450, 350, 30);
        citizenIDField.setFont(new Font("Arial", Font.BOLD, 18));
        add(citizenIDField);

        seniorCitizenLabel = new JLabel("Người cao tuổi :");
        seniorCitizenLabel.setBounds(75, 500, 375, 30);
        seniorCitizenLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(seniorCitizenLabel);

        seniorYesRadioBtn = new JRadioButton("Có");
        seniorYesRadioBtn.setBounds(275, 500, 100, 30);
        seniorYesRadioBtn.setFont(new Font("Arial", Font.BOLD, 18));
        seniorYesRadioBtn.setBackground(new Color(222, 255, 228));
        add(seniorYesRadioBtn);

        seniorNoRadioBtn = new JRadioButton("Không");
        seniorNoRadioBtn.setBounds(375, 500, 120, 30);
        seniorNoRadioBtn.setFont(new Font("Arial", Font.BOLD, 18));
        seniorNoRadioBtn.setBackground(new Color(222, 255, 228));
        add(seniorNoRadioBtn);

        ButtonGroup seniorGroup = new ButtonGroup();
        seniorGroup.add(seniorYesRadioBtn);
        seniorGroup.add(seniorNoRadioBtn);

        existingAccountLabel = new JLabel("Đã có tài khoản :");
        existingAccountLabel.setBounds(75, 550, 375, 30);
        existingAccountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(existingAccountLabel);

        accountYesRadioBtn = new JRadioButton("Có");
        accountYesRadioBtn.setBounds(275, 550, 100, 30);
        accountYesRadioBtn.setFont(new Font("Arial", Font.BOLD, 18));
        accountYesRadioBtn.setBackground(new Color(222, 255, 228));
        add(accountYesRadioBtn);

        accountNoRadioBtn = new JRadioButton("Không");
        accountNoRadioBtn.setBounds(375, 550, 120, 30);
        accountNoRadioBtn.setFont(new Font("Arial", Font.BOLD, 18));
        accountNoRadioBtn.setBackground(new Color(222, 255, 228));
        add(accountNoRadioBtn);

        ButtonGroup accountGroup = new ButtonGroup();
        accountGroup.add(accountYesRadioBtn);
        accountGroup.add(accountNoRadioBtn);

        returnBtn = new JButton("Quay lại");
        returnBtn.setBounds(500, 700, 120, 35);
        returnBtn.setFont(new Font("Arial", Font.BOLD, 16));
        returnBtn.setBackground(Color.BLACK);
        returnBtn.setForeground(Color.WHITE);
        returnBtn.addActionListener(this);
        add(returnBtn);

        nextBtn = new JButton("Tiếp tục");
        nextBtn.setBounds(650, 700, 120, 35);
        nextBtn.setFont(new Font("Arial", Font.BOLD, 16));
        nextBtn.setBackground(Color.BLACK);
        nextBtn.setForeground(Color.WHITE);
        nextBtn.addActionListener(this);
        add(nextBtn);

        getContentPane().setBackground(new Color(222, 255, 228));
        setLayout(null);
        setSize(850, 800);
        setLocation(360, 40);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextBtn) {
            String DBReligion = (String) religionComboBox.getSelectedItem();
            String DBCategory = (String) categoryComboBox.getSelectedItem();
            String DBIncome = (String) incomeComboBox.getSelectedItem();
            String DBEducation = (String) educationComboBox.getSelectedItem();
            String DBOccupation = (String) occupationComboBox.getSelectedItem();
            String DBCCCD = citizenIDField.getText();

            String DBSeniorCitizen = null;
            if (seniorYesRadioBtn.isSelected()) {
                DBSeniorCitizen = "Có";
            } else if (seniorNoRadioBtn.isSelected()) {
                DBSeniorCitizen = "Không";
            }

            String DBExistingAccount = null;
            if (accountYesRadioBtn.isSelected()) {
                DBExistingAccount = "Có";
            } else if (accountNoRadioBtn.isSelected()) {
                DBExistingAccount = "Không";
            }

            try {
                if (DBCCCD.equals("") || DBSeniorCitizen == null || DBExistingAccount == null) {
                    JOptionPane.showMessageDialog(null, "Fill all the fields");
                } else {
                    DBConnect conn = new DBConnect();

                    String q = "INSERT INTO SignUp2 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement ps = conn.connection.prepareStatement(q);

                    ps.setString(1, formNo);
                    ps.setString(2, DBReligion);
                    ps.setString(3, DBCategory);
                    ps.setString(4, DBIncome);
                    ps.setString(5, DBEducation);
                    ps.setString(6, DBOccupation);
                    ps.setString(7, DBCCCD);
                    ps.setString(8, DBSeniorCitizen);
                    ps.setString(9, DBExistingAccount);

                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Lưu thông tin thành công");
                    setVisible(false);

                    new SignUp3(formNo, pin);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == returnBtn) {
            new SignUp();
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new SignUp2("", "");
    }
}

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

        JPanel page = UIStyle.createPage();

        bankIconLabel = UIStyle.createBankIconLabel(82);
        applicationFormNo = new JLabel("APPLICATION FORM NO." + formNo);
        signUpPageNo = new JLabel("Page 2");
        signUpPageDetail = new JLabel("Thông tin bổ sung");

        JPanel header = UIStyle.createHeader(bankIconLabel, applicationFormNo, signUpPageNo);
        UIStyle.styleSubtitle(signUpPageDetail);
        signUpPageDetail.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(Box.createVerticalStrut(2));
        header.add(signUpPageDetail);
        page.add(header, BorderLayout.NORTH);

        religionLabel = new JLabel("Tôn giáo :");

        String[] religions = {"Không", "Phật giáo", "Thiên Chúa giáo", "Hồi giáo", "Khác"};
        religionComboBox = new JComboBox<>(religions);
        UIStyle.styleComboBox(religionComboBox);

        categoryLabel = new JLabel("Loại khách hàng :");

        String[] categories = {"Cá nhân", "Doanh nghiệp", "Sinh viên", "Khác"};
        categoryComboBox = new JComboBox<>(categories);
        UIStyle.styleComboBox(categoryComboBox);

        incomeLabel = new JLabel("Thu nhập :");

        String[] incomes = {"Không có", "Dưới 5 triệu", "5 - 10 triệu", "10 - 20 triệu", "Trên 20 triệu"};
        incomeComboBox = new JComboBox<>(incomes);
        UIStyle.styleComboBox(incomeComboBox);

        educationLabel = new JLabel("Học vấn :");

        String[] educations = {"THPT", "Cao đẳng", "Đại học", "Sau đại học", "Khác"};
        educationComboBox = new JComboBox<>(educations);
        UIStyle.styleComboBox(educationComboBox);

        occupationLabel = new JLabel("Nghề nghiệp :");

        String[] occupations = {"Sinh viên", "Nhân viên văn phòng", "Kinh doanh", "Tự do", "Khác"};
        occupationComboBox = new JComboBox<>(occupations);
        UIStyle.styleComboBox(occupationComboBox);

        citizenIDLabel = new JLabel("Số CCCD :");

        citizenIDField = new JTextField(15);
        UIStyle.styleTextField(citizenIDField);

        seniorCitizenLabel = new JLabel("Người cao tuổi :");

        seniorYesRadioBtn = new JRadioButton("Có");
        seniorNoRadioBtn = new JRadioButton("Không");

        ButtonGroup seniorGroup = new ButtonGroup();
        seniorGroup.add(seniorYesRadioBtn);
        seniorGroup.add(seniorNoRadioBtn);

        existingAccountLabel = new JLabel("Đã có tài khoản :");

        accountYesRadioBtn = new JRadioButton("Có");
        accountNoRadioBtn = new JRadioButton("Không");

        ButtonGroup accountGroup = new ButtonGroup();
        accountGroup.add(accountYesRadioBtn);
        accountGroup.add(accountNoRadioBtn);

        returnBtn = new JButton("Quay lại");
        UIStyle.styleButton(returnBtn);
        returnBtn.addActionListener(this);

        nextBtn = new JButton("Tiếp tục");
        UIStyle.styleButton(nextBtn);
        nextBtn.addActionListener(this);

        JPanel card = UIStyle.createCard();
        card.setPreferredSize(new Dimension(700, 510));
        UIStyle.addFormRow(card, 0, religionLabel, religionComboBox);
        UIStyle.addFormRow(card, 1, categoryLabel, categoryComboBox);
        UIStyle.addFormRow(card, 2, incomeLabel, incomeComboBox);
        UIStyle.addFormRow(card, 3, educationLabel, educationComboBox);
        UIStyle.addFormRow(card, 4, occupationLabel, occupationComboBox);
        UIStyle.addFormRow(card, 5, citizenIDLabel, citizenIDField);
        UIStyle.addFormRow(card, 6, seniorCitizenLabel, UIStyle.createOptionPanel(seniorYesRadioBtn, seniorNoRadioBtn));
        UIStyle.addFormRow(card, 7, existingAccountLabel, UIStyle.createOptionPanel(accountYesRadioBtn, accountNoRadioBtn));
        UIStyle.addFullWidthRow(card, 8, UIStyle.createButtonPanel(returnBtn, nextBtn));

        page.add(UIStyle.center(card), BorderLayout.CENTER);
        setContentPane(page);
        getRootPane().setDefaultButton(nextBtn);
        UIStyle.showFrame(this, 900, 850);
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

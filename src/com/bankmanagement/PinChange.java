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

        JPanel page = UIStyle.createPage();

        bankIconLabel = UIStyle.createBankIconLabel(88);
        titleLabel = new JLabel("ĐỔI MÃ PIN");
        page.add(UIStyle.createHeader(bankIconLabel, titleLabel, null), BorderLayout.NORTH);

        pinCheckLabel = new JLabel("Nhập mã PIN hiện tại:");

        pinCheckTextField = new JTextField(15);
        UIStyle.styleTextField(pinCheckTextField);

        pinChangeLabel = new JLabel("Nhập mã PIN muốn đổi:");

        pinTextField = new JTextField(15);
        UIStyle.styleTextField(pinTextField);

        returnBtn = new JButton("Quay lại");
        UIStyle.styleButton(returnBtn);
        returnBtn.addActionListener(this);

        conFirmBtn = new JButton("Xác nhận");
        UIStyle.styleButton(conFirmBtn);
        conFirmBtn.addActionListener(this);

        JPanel card = UIStyle.createCard();
        card.setPreferredSize(new Dimension(650, 250));
        UIStyle.addFormRow(card, 0, pinCheckLabel, pinCheckTextField);
        UIStyle.addFormRow(card, 1, pinChangeLabel, pinTextField);
        UIStyle.addFullWidthRow(card, 2, UIStyle.createButtonPanel(returnBtn, conFirmBtn));

        page.add(UIStyle.center(card), BorderLayout.CENTER);
        setContentPane(page);
        getRootPane().setDefaultButton(conFirmBtn);
        UIStyle.showFrame(this, 850, 800);
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

package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ChangeCardNumber extends JFrame implements ActionListener {
    JLabel bankIconLabel, titleLabel, currentCardNumberLabel, newCardNumberLabel, noteLabel;
    JTextField currentCardNumberField, newCardNumberField;
    JButton confirmBtn, returnBtn;
    int accountId;

    public ChangeCardNumber(int accountId) {
        super("Đổi số thẻ");
        this.accountId = accountId;

        JPanel page = UIStyle.createPage();

        bankIconLabel = UIStyle.createBankIconLabel(88);
        titleLabel = new JLabel("ĐỔI SỐ THẺ");
        page.add(UIStyle.createHeader(bankIconLabel, titleLabel, null), BorderLayout.NORTH);

        currentCardNumberLabel = new JLabel("Số thẻ hiện tại:");
        newCardNumberLabel = new JLabel("Số thẻ mới:");

        currentCardNumberField = new JTextField(16);
        currentCardNumberField.setEditable(false);
        UIStyle.styleTextField(currentCardNumberField);

        newCardNumberField = new JTextField(16);
        UIStyle.styleTextField(newCardNumberField);

        noteLabel = new JLabel("Nhập số thẻ mới gồm 16 chữ số, không bắt đầu bằng 0 và không trùng với số thẻ đã có.");
        UIStyle.styleNoteLabel(noteLabel);

        returnBtn = new JButton("Quay lại");
        UIStyle.styleButton(returnBtn);
        returnBtn.addActionListener(this);

        confirmBtn = new JButton("Xác nhận đổi");
        UIStyle.styleButton(confirmBtn);
        confirmBtn.setPreferredSize(new Dimension(160, 40));
        confirmBtn.addActionListener(this);

        JPanel card = UIStyle.createCard();
        card.setPreferredSize(new Dimension(720, 300));
        UIStyle.addFormRow(card, 0, currentCardNumberLabel, currentCardNumberField);
        UIStyle.addFormRow(card, 1, newCardNumberLabel, newCardNumberField);
        UIStyle.addFullWidthRow(card, 2, noteLabel);
        UIStyle.addFullWidthRow(card, 3, createButtonPanel());

        page.add(UIStyle.center(card), BorderLayout.CENTER);
        setContentPane(page);
        getRootPane().setDefaultButton(confirmBtn);

        loadCurrentCardNumber();

        UIStyle.showFrame(this, 850, 800);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmBtn) {
            confirmCardNumberChange();
        } else if (e.getSource() == returnBtn) {
            new Main(accountId);
            setVisible(false);
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);
        panel.add(returnBtn);
        panel.add(confirmBtn);
        return panel;
    }

    private void loadCurrentCardNumber() {
        try (DBConnect conn = new DBConnect()) {
            if (conn.connection == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }

            BankAccountService.AccountSummary account = BankAccountService.findAccountById(conn.connection, accountId);
            if (account == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy tài khoản đang đăng nhập");
                return;
            }
            currentCardNumberField.setText(account.cardNumber);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể tải số thẻ hiện tại");
        }
    }

    private void confirmCardNumberChange() {
        String currentCardNumber = currentCardNumberField.getText().trim();
        String newCardNumber = newCardNumberField.getText().trim();

        if (currentCardNumber.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy số thẻ hiện tại");
            return;
        }
        if (!BankAccountService.isValidCardNumberFormat(newCardNumber)) {
            JOptionPane.showMessageDialog(null, "Số thẻ mới phải gồm 16 chữ số và không bắt đầu bằng 0");
            return;
        }
        if (newCardNumber.equals(currentCardNumber)) {
            JOptionPane.showMessageDialog(null, "Số thẻ mới phải khác số thẻ hiện tại");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn đổi số thẻ?\n\nSố thẻ hiện tại: " + currentCardNumber + "\nSố thẻ mới: " + newCardNumber,
                "Xác nhận đổi số thẻ",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try (DBConnect conn = new DBConnect()) {
            if (conn.connection == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }

            BankAccountService.changeCardNumber(conn.connection, accountId, newCardNumber);
            currentCardNumberField.setText(newCardNumber);
            newCardNumberField.setText("");
            JOptionPane.showMessageDialog(null, "Đổi số thẻ thành công\nSố thẻ mới: " + newCardNumber);
            new Main(accountId);
            setVisible(false);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể đổi số thẻ. Vui lòng thử lại.");
        }
    }

    public static void main(String[] args) {
        new ChangeCardNumber(0);
    }
}

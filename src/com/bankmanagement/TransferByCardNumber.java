package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class TransferByCardNumber extends JFrame implements ActionListener {
    JLabel bankIconLabel, titleLabel, receiverCardNumberLabel, amountLabel, unitLabel, noteLabel, receiverInfoLabel;
    JTextField receiverCardNumberField, amountField, noteField, receiverInfoField;
    JButton checkReceiverBtn, transferBtn, returnBtn;
    int accountId;

    public TransferByCardNumber(int accountId) {
        super("Chuyển tiền theo số thẻ");
        this.accountId = accountId;

        JPanel page = UIStyle.createPage();

        bankIconLabel = UIStyle.createBankIconLabel(88);
        titleLabel = new JLabel("CHUYỂN TIỀN THEO SỐ THẺ");
        page.add(UIStyle.createHeader(bankIconLabel, titleLabel, null), BorderLayout.NORTH);

        receiverCardNumberLabel = new JLabel("Số thẻ người nhận:");
        amountLabel = new JLabel("Số tiền:");
        noteLabel = new JLabel("Ghi chú:");
        receiverInfoLabel = new JLabel("Người nhận:");

        receiverCardNumberField = new JTextField(16);
        UIStyle.styleTextField(receiverCardNumberField);

        amountField = new JTextField(15);
        UIStyle.styleTextField(amountField);

        noteField = new JTextField(15);
        UIStyle.styleTextField(noteField);

        receiverInfoField = new JTextField("Chưa kiểm tra");
        receiverInfoField.setEditable(false);
        UIStyle.styleTextField(receiverInfoField);

        unitLabel = new JLabel("đồng");
        UIStyle.styleFieldLabel(unitLabel);
        unitLabel.setPreferredSize(new Dimension(60, 38));
        unitLabel.setHorizontalAlignment(SwingConstants.LEFT);

        returnBtn = new JButton("Quay lại");
        UIStyle.styleButton(returnBtn);
        returnBtn.addActionListener(this);

        checkReceiverBtn = new JButton("Kiểm tra");
        UIStyle.styleButton(checkReceiverBtn);
        checkReceiverBtn.addActionListener(this);

        transferBtn = new JButton("Chuyển tiền");
        UIStyle.styleButton(transferBtn);
        transferBtn.setPreferredSize(new Dimension(150, 40));
        transferBtn.addActionListener(this);

        JPanel amountPanel = new JPanel(new BorderLayout(10, 0));
        amountPanel.setOpaque(false);
        amountPanel.add(amountField, BorderLayout.CENTER);
        amountPanel.add(unitLabel, BorderLayout.EAST);

        JPanel card = UIStyle.createCard();
        card.setPreferredSize(new Dimension(740, 390));
        UIStyle.addFormRow(card, 0, receiverCardNumberLabel, receiverCardNumberField);
        UIStyle.addFormRow(card, 1, amountLabel, amountPanel);
        UIStyle.addFormRow(card, 2, noteLabel, noteField);
        UIStyle.addFormRow(card, 3, receiverInfoLabel, receiverInfoField);
        UIStyle.addFullWidthRow(card, 4, createButtonPanel());

        page.add(UIStyle.center(card), BorderLayout.CENTER);
        setContentPane(page);
        getRootPane().setDefaultButton(transferBtn);
        UIStyle.showFrame(this, 850, 800);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkReceiverBtn) {
            previewReceiver();
        } else if (e.getSource() == transferBtn) {
            transferMoney();
        } else if (e.getSource() == returnBtn) {
            new Main(accountId);
            setVisible(false);
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);
        panel.add(returnBtn);
        panel.add(checkReceiverBtn);
        panel.add(transferBtn);
        return panel;
    }

    private void previewReceiver() {
        String receiverCardNumber = readReceiverCardNumber();
        if (receiverCardNumber == null) {
            return;
        }

        try (DBConnect conn = new DBConnect()) {
            if (conn.connection == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }

            BankAccountService.AccountSummary receiver = loadValidReceiver(conn, receiverCardNumber);
            receiverInfoField.setText(receiver.displayName() + " - " + receiver.cardNumber);
            JOptionPane.showMessageDialog(null, "Người nhận: " + receiver.displayName() + "\nSố thẻ: " + receiver.cardNumber);
        } catch (IllegalStateException ex) {
            receiverInfoField.setText("Chưa kiểm tra");
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (SQLException ex) {
            receiverInfoField.setText("Chưa kiểm tra");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể kiểm tra số thẻ người nhận");
        }
    }

    private void transferMoney() {
        String receiverCardNumber = readReceiverCardNumber();
        if (receiverCardNumber == null) {
            return;
        }

        Long amount = readAmount();
        if (amount == null) {
            return;
        }

        String note = noteField.getText().trim();
        if (note.length() > 255) {
            JOptionPane.showMessageDialog(null, "Ghi chú không được vượt quá 255 ký tự");
            return;
        }

        BankAccountService.AccountSummary receiver;
        long currentBalance;

        try (DBConnect conn = new DBConnect()) {
            if (conn.connection == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }

            receiver = loadValidReceiver(conn, receiverCardNumber);
            receiverInfoField.setText(receiver.displayName() + " - " + receiver.cardNumber);
            currentBalance = BankAccountService.calculateBalance(conn.connection, accountId);
            if (amount > currentBalance) {
                JOptionPane.showMessageDialog(null, "Số dư không đủ để chuyển tiền");
                return;
            }
        } catch (IllegalStateException ex) {
            receiverInfoField.setText("Chưa kiểm tra");
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể kiểm tra thông tin chuyển tiền");
            return;
        }

        String confirmMessage = "Xác nhận chuyển tiền?\n\n"
                + "Người nhận: " + receiver.displayName() + "\n"
                + "Số thẻ người nhận: " + receiver.cardNumber + "\n"
                + "Số tiền: " + amount + "\n"
                + "Số dư hiện tại: " + currentBalance;
        if (!note.isEmpty()) {
            confirmMessage += "\nGhi chú: " + note;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                confirmMessage,
                "Xác nhận chuyển tiền",
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

            BankAccountService.TransferResult result = BankAccountService.transferByCardNumber(
                    conn.connection,
                    accountId,
                    receiverCardNumber,
                    amount,
                    note
            );

            JOptionPane.showMessageDialog(
                    null,
                    "Chuyển tiền thành công\n"
                            + "Người nhận: " + result.receiver.displayName() + "\n"
                            + "Số tiền: " + result.amount + "\n"
                            + "Số dư còn lại: " + result.senderBalanceAfter
            );
            new Main(accountId);
            setVisible(false);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Chuyển tiền thất bại. Giao dịch đã được hủy.");
        }
    }

    private String readReceiverCardNumber() {
        String receiverCardNumber = receiverCardNumberField.getText().trim();
        if (receiverCardNumber.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số thẻ người nhận");
            return null;
        }
        if (!BankAccountService.isValidCardNumberFormat(receiverCardNumber)) {
            JOptionPane.showMessageDialog(null, "Số thẻ người nhận phải gồm 16 chữ số và không bắt đầu bằng 0");
            return null;
        }
        return receiverCardNumber;
    }

    private Long readAmount() {
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số tiền");
            return null;
        }

        long amount;
        try {
            amount = Long.parseLong(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Số tiền phải là số");
            return null;
        }

        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "Số tiền phải lớn hơn 0");
            return null;
        }

        return amount;
    }

    private BankAccountService.AccountSummary loadValidReceiver(DBConnect conn, String receiverCardNumber) throws SQLException {
        BankAccountService.AccountSummary sender = BankAccountService.findAccountById(conn.connection, accountId);
        if (sender == null) {
            throw new IllegalStateException("Không tìm thấy tài khoản đang đăng nhập");
        }

        BankAccountService.AccountSummary receiver = BankAccountService.findAccountByCardNumber(conn.connection, receiverCardNumber);
        if (receiver == null) {
            throw new IllegalStateException("Số thẻ người nhận không tồn tại");
        }
        if (sender.accountId == receiver.accountId) {
            throw new IllegalStateException("Không thể chuyển tiền cho chính số thẻ của bạn");
        }

        return receiver;
    }

    public static void main(String[] args) {
        new TransferByCardNumber(0);
    }
}

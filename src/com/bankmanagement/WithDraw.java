package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class WithDraw extends JFrame implements ActionListener {
    JLabel bankIconLabel, titleLabel, amountLabel, noteLabel, unitLabel;
    JTextField amountField;
    JButton returnBtn, withDrawBtn;
    int accountId;
    public WithDraw(int accountId) {
        super("Rút tiền");
        this.accountId = accountId;

        JPanel page = UIStyle.createPage();

        bankIconLabel = UIStyle.createBankIconLabel(88);
        titleLabel = new JLabel("RÚT TIỀN");
        page.add(UIStyle.createHeader(bankIconLabel, titleLabel, null), BorderLayout.NORTH);

        amountLabel = new JLabel("Số tiền rút :");

        amountField = new JTextField(15);
        UIStyle.styleTextField(amountField);

        unitLabel = new JLabel("đồng");
        UIStyle.styleFieldLabel(unitLabel);
        unitLabel.setPreferredSize(new Dimension(60, 38));
        unitLabel.setHorizontalAlignment(SwingConstants.LEFT);

        noteLabel = new JLabel("Vui lòng nhập số tiền lớn hơn 0");
        UIStyle.styleNoteLabel(noteLabel);

        returnBtn = new JButton("Quay lại");
        UIStyle.styleButton(returnBtn);
        returnBtn.addActionListener(this);

        withDrawBtn = new JButton("Rút tiền");
        UIStyle.styleButton(withDrawBtn);
        withDrawBtn.addActionListener(this);

        JPanel amountPanel = new JPanel(new BorderLayout(10, 0));
        amountPanel.setOpaque(false);
        amountPanel.add(amountField, BorderLayout.CENTER);
        amountPanel.add(unitLabel, BorderLayout.EAST);

        JPanel card = UIStyle.createCard();
        card.setPreferredSize(new Dimension(650, 230));
        UIStyle.addFormRow(card, 0, amountLabel, amountPanel);
        UIStyle.addFullWidthRow(card, 1, noteLabel);
        UIStyle.addFullWidthRow(card, 2, UIStyle.createButtonPanel(returnBtn, withDrawBtn));

        page.add(UIStyle.center(card), BorderLayout.CENTER);
        setContentPane(page);
        getRootPane().setDefaultButton(withDrawBtn);
        UIStyle.showFrame(this, 850, 800);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == withDrawBtn) {
                String amountText = amountField.getText();
                if (amountText.equals("")) {
                    JOptionPane.showMessageDialog(null, "Số tiền không được để trống");
                    return;
                }
                int amount;
                try {
                    amount = Integer.parseInt(amountText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Số tiền phải là số");
                    return;
                }
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(null, "Số tiền phải lớn hơn 0");
                    return;
                }

                try{
                    DBConnect c = new DBConnect();
                    String q = """
                            SELECT *
                            FROM bank
                            WHERE AccountID = ?
                    """;
                    PreparedStatement ps = c.connection.prepareStatement(q);
                    ps.setInt(1, accountId);
                    ResultSet resultSet = ps.executeQuery();
                    int balance = 0;
                    while (resultSet.next()) {
                        if(resultSet.getString("transactiontype").equals("Nạp tiền")) {
                            balance += Integer.parseInt(resultSet.getString("amount"));
                        }
                        else if(resultSet.getString("transactiontype").equals("Rút tiền")){
                            balance -= Integer.parseInt(resultSet.getString("amount"));
                        }
                    }
                    if (amount > balance) {
                        JOptionPane.showMessageDialog(null, "Số dư không đủ để rút");
                        return;
                    }
                    String query = """
                            INSERT INTO Bank(AccountID, TransactionDate, TransactionType, Amount)
                            VALUES (?, ?, ?, ?)
                    """;
                    PreparedStatement p = c.connection.prepareStatement(query);
                    p.setInt(1, accountId);
                    p.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    p.setString(3, "Rút tiền");
                    p.setInt(4, amount);
                    p.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Rút tiền thành công");
                    new Main(accountId);
                    setVisible(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if(e.getSource() == returnBtn){
                new Main(accountId);
                setVisible(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new WithDraw(0);
    }
}

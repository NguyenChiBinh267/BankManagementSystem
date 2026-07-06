package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Main extends JFrame implements ActionListener {
    JLabel bankIconLabel, chooseServiceLabel, logoutLabel;
    JButton depositBtn, withdrawBtn, fastCashBtn, miniStatementBtn, pinChangeBtn, balanceBtn, exitBtn, returnBtn;
    int accountId;
    Main(int accountId){
        super("Màn hình chính");
        this.accountId = accountId;

        depositBtn = new JButton("Nạp tiền");
        UIStyle.styleButton(depositBtn);
        depositBtn.addActionListener(this);

        withdrawBtn = new JButton("Rút tiền");
        UIStyle.styleButton(withdrawBtn);
        withdrawBtn.addActionListener(this);

        fastCashBtn = new JButton("Rút tiền nhanh");
        UIStyle.styleButton(fastCashBtn);
        fastCashBtn.addActionListener(this);

        miniStatementBtn = new JButton("Giao dịch gần đây");
        UIStyle.styleButton(miniStatementBtn);
        miniStatementBtn.addActionListener(this);

        pinChangeBtn = new JButton("Đổi mã PIN");
        UIStyle.styleButton(pinChangeBtn);
        pinChangeBtn.addActionListener(this);

        exitBtn = new JButton("Thoát ứng dụng");
        UIStyle.styleButton(exitBtn);
        exitBtn.addActionListener(this);

        logoutLabel = new JLabel("Đăng xuất");
        UIStyle.styleLinkLabel(logoutLabel);

        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Login();
                setVisible(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                logoutLabel.setText("<html><u>Đăng xuất</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutLabel.setForeground(Color.BLACK);
                logoutLabel.setText("Đăng xuất");
            }
        });

        balanceBtn = new JButton("Xem số dư");
        UIStyle.styleButton(balanceBtn);
        balanceBtn.addActionListener(this);

        JPanel page = UIStyle.createPage();

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(logoutLabel, BorderLayout.EAST);
        page.add(topBar, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        bankIconLabel = UIStyle.createBankIconLabel(88);
        chooseServiceLabel = new JLabel("Chọn dịch vụ");
        content.add(UIStyle.createHeader(bankIconLabel, chooseServiceLabel, null));
        content.add(Box.createVerticalStrut(24));

        JPanel card = UIStyle.createCard();
        card.setLayout(new BorderLayout(0, 22));
        card.setPreferredSize(new Dimension(650, 360));
        card.setMaximumSize(new Dimension(650, 360));

        JPanel buttonGrid = new JPanel(new GridLayout(3, 2, 18, 18));
        buttonGrid.setOpaque(false);
        buttonGrid.add(depositBtn);
        buttonGrid.add(withdrawBtn);
        buttonGrid.add(fastCashBtn);
        buttonGrid.add(miniStatementBtn);
        buttonGrid.add(pinChangeBtn);
        buttonGrid.add(balanceBtn);
        card.add(buttonGrid, BorderLayout.CENTER);

        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        exitPanel.setOpaque(false);
        exitPanel.add(exitBtn);
        card.add(exitPanel, BorderLayout.SOUTH);

        content.add(card);
        page.add(UIStyle.center(content), BorderLayout.CENTER);

        setContentPane(page);
        UIStyle.showFrame(this, 850, 800);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            if(e.getSource()==depositBtn){
                new Deposit(accountId);
                setVisible(false);
            }
            else if(e.getSource()==withdrawBtn){
                new WithDraw(accountId);
                setVisible(false);
            }
            else if(e.getSource()==fastCashBtn){

            }
            else if(e.getSource()==balanceBtn){
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
                    JOptionPane.showMessageDialog(null, "Số dư: " + balance);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if(e.getSource()==pinChangeBtn){
                new PinChange(accountId);
                setVisible(false);
            }
            else if(e.getSource()==miniStatementBtn){
                new MiniStatement(accountId);
                setVisible(false);
            }
            else if(e.getSource()==exitBtn){
                System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    public static void main(String[] args) {
        new Main(0);
    }
}

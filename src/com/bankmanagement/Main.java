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
        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank_icon.png"));
        Image scaledBankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon scaledBankIcon = new ImageIcon(scaledBankImage);
        bankIconLabel = new JLabel(scaledBankIcon);
        bankIconLabel.setBounds(350, 10, 100, 100);
        add(bankIconLabel);

        chooseServiceLabel = new JLabel("Chọn dịch vụ");
        chooseServiceLabel.setBounds(320, 100, 450, 75);
        chooseServiceLabel.setFont(new Font("Arial", Font.BOLD, 25));
        add(chooseServiceLabel);

        depositBtn = new JButton("Nạp tiền");
        depositBtn.setBounds(100, 200, 250, 50);
        depositBtn.setFont(new Font("Arial", Font.BOLD, 18));
        depositBtn.setFocusPainted(false);
        depositBtn.addActionListener(this);
        add(depositBtn);

        withdrawBtn = new JButton("Rút tiền");
        withdrawBtn.setBounds(500, 200, 250, 50);
        withdrawBtn.setFont(new Font("Arial", Font.BOLD, 18));
        withdrawBtn.setFocusPainted(false);
        withdrawBtn.addActionListener(this);
        add(withdrawBtn);

        fastCashBtn = new JButton("Rút tiền nhanh");
        fastCashBtn.setBounds(100, 300, 250, 50);
        fastCashBtn.setFont(new Font("Arial", Font.BOLD, 18));
        fastCashBtn.setFocusPainted(false);
        fastCashBtn.addActionListener(this);
        add(fastCashBtn);

        miniStatementBtn = new JButton("Giao dịch gần đây");
        miniStatementBtn.setBounds(500, 300, 250, 50);
        miniStatementBtn.setFont(new Font("Arial", Font.BOLD, 18));
        miniStatementBtn.setFocusPainted(false);
        miniStatementBtn.addActionListener(this);
        add(miniStatementBtn);

        pinChangeBtn = new JButton("Đổi mã PIN");
        pinChangeBtn.setBounds(100, 400, 250, 50);
        pinChangeBtn.setFont(new Font("Arial", Font.BOLD, 18));
        pinChangeBtn.setFocusPainted(false);
        pinChangeBtn.addActionListener(this);
        add(pinChangeBtn);

        exitBtn = new JButton("Thoát ứng dụng");
        exitBtn.setBounds(600, 650, 200, 50);
        exitBtn.setFont(new Font("Arial", Font.BOLD, 18));
        exitBtn.setBackground(Color.BLACK);
        exitBtn.setForeground(Color.WHITE);
        exitBtn.addActionListener(this);
        exitBtn.setFocusPainted(false);
        add(exitBtn);

        logoutLabel = new JLabel("Đăng xuất");
        logoutLabel.setBounds(700, 20, 100, 30);
        logoutLabel.setFont(new Font("Arial", Font.BOLD, 18));
        logoutLabel.setForeground(Color.BLACK);
        logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(logoutLabel);

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
        balanceBtn.setBounds(500, 400, 250, 50);
        balanceBtn.setFont(new Font("Arial", Font.BOLD, 18));
        balanceBtn.setFocusPainted(false);
        balanceBtn.addActionListener(this);
        add(balanceBtn);

        getContentPane().setBackground(new Color(222, 255, 228));
        setLayout(null);
        setSize(850, 800);
        setLocation(360, 40);
        setVisible(true);
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

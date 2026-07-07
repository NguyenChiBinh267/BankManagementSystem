package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame implements ActionListener{
    private static final Color PAGE_BACKGROUND = new Color(244, 247, 251);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT = new Color(17, 24, 39);
    private static final Color MUTED_TEXT = new Color(107, 114, 128);
    private static final Color BORDER = new Color(209, 213, 219);
    private static final Color PRIMARY = new Color(37, 99, 235);

    JLabel bankIconLabel, exitIconLabel, bankBackgroundLabel, welcomeLabel, cardNumberLabel, PINNumberLabel;
    JTextField cardNumberInputField;
    JPasswordField passwordInputField;
    JButton signInBtn, signUpBtn, exitBtn;
    Login(){
        super("Hệ thống quản lý ngân hàng");

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(PAGE_BACKGROUND);
        page.setBorder(new EmptyBorder(22, 30, 28, 30));

        exitIconLabel = new JLabel(UIStyle.createImageIcon("exit_icon.png", 38, 38));
        exitIconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitIconLabel.setToolTipText("Thoát ứng dụng");
        exitIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(exitIconLabel, BorderLayout.EAST);
        page.add(topBar, BorderLayout.NORTH);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(createLoginCard());
        page.add(centerWrap, BorderLayout.CENTER);

        setContentPane(page);
        getRootPane().setDefaultButton(signInBtn);
        UIStyle.showFrame(this, 950, 650);
    }

    private JPanel createLoginCard() {
        RoundedPanel card = new RoundedPanel(24, CARD_BACKGROUND, BORDER);
        card.setLayout(new BorderLayout(0, 24));
        card.setBorder(new EmptyBorder(34, 40, 34, 40));
        card.setPreferredSize(new Dimension(460, 480));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        bankIconLabel = UIStyle.createBankIconLabel(72);
        bankIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(bankIconLabel);
        header.add(Box.createVerticalStrut(16));

        welcomeLabel = new JLabel("Chào mừng quý khách");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(welcomeLabel);

        JLabel subTitle = new JLabel("Đăng nhập để tiếp tục sử dụng dịch vụ");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(MUTED_TEXT);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(Box.createVerticalStrut(8));
        header.add(subTitle);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        cardNumberLabel = new JLabel("Số thẻ");
        PINNumberLabel = new JLabel("PIN");

        cardNumberInputField = new JTextField(15);
        styleModernTextField(cardNumberInputField);

        passwordInputField = new JPasswordField(15);
        styleModernTextField(passwordInputField);

        addInputRow(form, 0, cardNumberLabel, cardNumberInputField);
        addInputRow(form, 1, PINNumberLabel, passwordInputField);

        signInBtn = new JButton("Đăng nhập");
        stylePrimaryButton(signInBtn);
        signInBtn.addActionListener(this);

        signUpBtn = new JButton("Đăng ký");
        styleSecondaryButton(signUpBtn);
        signUpBtn.addActionListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(signInBtn);
        buttonPanel.add(signUpBtn);

        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.gridx = 0;
        buttonGbc.gridy = 2;
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;
        buttonGbc.insets = new Insets(20, 0, 0, 0);
        form.add(buttonPanel, buttonGbc);

        card.add(header, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private void addInputRow(JPanel panel, int row, JLabel label, JComponent field) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT);

        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(row == 0 ? 0 : 14, 0, 0, 0);
        panel.add(wrapper, gbc);
    }

    private void styleModernTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(TEXT);
        field.setPreferredSize(new Dimension(360, 44));
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setPreferredSize(new Dimension(150, 44));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(PRIMARY);
        button.setBackground(Color.WHITE);
        button.setPreferredSize(new Dimension(150, 44));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(9, 16, 9, 16)
        ));
    }

    @Override
    public void actionPerformed(ActionEvent e){
        try{
            if(e.getSource()==signInBtn){
                String cardno = cardNumberInputField.getText();
                String pin = new String(passwordInputField.getPassword());

                if (!pin.matches("[0-9]{6}")) {
                    JOptionPane.showMessageDialog(null, "PIN phải gồm đúng 6 chữ số");
                    return;
                }

                DBConnect c = new DBConnect();
                String q = "SELECT AccountID FROM Login WHERE cardNumber = ? AND pin = ?";
                PreparedStatement ps = c.connection.prepareStatement(q);
                ps.setString(1, cardno);
                ps.setString(2, pin);
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()){
                    int accountId = resultSet.getInt("AccountID");
                    setVisible(false);
                    new Main(accountId);
                }else {
                    JOptionPane.showMessageDialog(null,"Số thẻ hoặc mã PIN không đúng");
                }
            }
            else if(e.getSource()==signUpBtn){
                new SignUp();
                setVisible(false);
            }
        } catch (Exception E){
            E.printStackTrace();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fillColor;
        private final Color borderColor;

        RoundedPanel(int radius, Color fillColor, Color borderColor) {
            this.radius = radius;
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static void main(String[] args) {
        new Login();
    }
}

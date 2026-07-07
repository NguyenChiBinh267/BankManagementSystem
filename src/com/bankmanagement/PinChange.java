package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;

public class PinChange extends JFrame implements ActionListener {
    private static final Color SIDEBAR = new Color(17, 24, 39);
    private static final Color CONTENT_BACKGROUND = new Color(244, 247, 251);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT = new Color(17, 24, 39);
    private static final Color MUTED_TEXT = new Color(107, 114, 128);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color PRIMARY = new Color(108, 92, 231);
    private static final Color BLUE = new Color(37, 99, 235);

    JLabel bankIconLabel, titleLabel, pinChangeLabel, pinCheckLabel;
    JTextField pinTextField, pinCheckTextField;
    JButton returnBtn, conFirmBtn;
    int accountId;
    PinChange(int accountId) {
        super("Đổi mã pin");
        this.accountId = accountId;

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(CONTENT_BACKGROUND);
        page.add(createSidebar(), BorderLayout.WEST);
        page.add(createContent(), BorderLayout.CENTER);

        setContentPane(page);
        getRootPane().setDefaultButton(conFirmBtn);
        UIStyle.showFrame(this, 1200, 760);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(24, 18, 24, 18));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new BorderLayout(10, 0));
        brand.setOpaque(false);
        bankIconLabel = UIStyle.createBankIconLabel(42);
        brand.add(bankIconLabel, BorderLayout.WEST);

        JLabel brandName = new JLabel("SmartBank");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandName.setForeground(Color.WHITE);
        brand.add(brandName, BorderLayout.CENTER);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        top.add(brand);
        top.add(Box.createVerticalStrut(30));
        top.add(createSidebarLabel("Tổng quan", false));
        top.add(Box.createVerticalStrut(8));
        top.add(createSidebarLabel("Nạp tiền", false));
        top.add(Box.createVerticalStrut(8));
        top.add(createSidebarLabel("Rút tiền", false));
        top.add(Box.createVerticalStrut(8));
        top.add(createSidebarLabel("Chuyển tiền", false));
        top.add(Box.createVerticalStrut(8));
        top.add(createSidebarLabel("Giao dịch gần đây", false));
        top.add(Box.createVerticalStrut(8));
        top.add(createSidebarLabel("Đổi mã PIN", true));

        sidebar.add(top, BorderLayout.NORTH);
        return sidebar;
    }

    private JLabel createSidebarLabel(String text, boolean active) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(active ? Color.WHITE : new Color(203, 213, 225));
        label.setOpaque(true);
        label.setBackground(active ? BLUE : SIDEBAR);
        label.setBorder(new EmptyBorder(11, 14, 11, 14));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return label;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setBackground(CONTENT_BACKGROUND);
        content.setBorder(new EmptyBorder(34, 38, 34, 38));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Đổi mã PIN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT);

        JLabel subtitle = new JLabel("Cập nhật mã PIN bảo mật gồm đúng 6 chữ số");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED_TEXT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        content.add(header, BorderLayout.NORTH);
        content.add(createPinCard(), BorderLayout.CENTER);
        return content;
    }

    private JPanel createPinCard() {
        RoundedPanel card = new RoundedPanel(24, CARD_BACKGROUND, BORDER);
        card.setLayout(new BorderLayout(0, 24));
        card.setBorder(new EmptyBorder(30, 34, 30, 34));
        card.setPreferredSize(new Dimension(620, 390));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        pinCheckLabel = new JLabel("Nhập mã PIN hiện tại");
        pinCheckTextField = new JTextField(15);
        styleTextField(pinCheckTextField);

        pinChangeLabel = new JLabel("Nhập mã PIN muốn đổi");
        pinTextField = new JTextField(15);
        styleTextField(pinTextField);

        addFieldRow(form, 0, pinCheckLabel, pinCheckTextField);
        addFieldRow(form, 2, pinChangeLabel, pinTextField);

        JLabel noteLabel = new JLabel("PIN phải gồm đúng 6 chữ số");
        noteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noteLabel.setForeground(MUTED_TEXT);
        addFullRow(form, 4, noteLabel);

        returnBtn = new JButton("Quay lại");
        styleSecondaryButton(returnBtn);
        returnBtn.addActionListener(this);

        conFirmBtn = new JButton("Xác nhận");
        stylePrimaryButton(conFirmBtn);
        conFirmBtn.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(returnBtn);
        buttonPanel.add(conFirmBtn);

        card.add(form, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);
        return wrapper;
    }

    private void addFieldRow(JPanel panel, int row, JLabel label, JComponent field) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);

        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.gridx = 0;
        labelGbc.gridy = row;
        labelGbc.anchor = GridBagConstraints.WEST;
        labelGbc.insets = new Insets(0, 0, 8, 0);
        panel.add(label, labelGbc);

        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.gridx = 0;
        fieldGbc.gridy = row + 1;
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.weightx = 1;
        fieldGbc.insets = new Insets(0, 0, 14, 0);
        panel.add(field, fieldGbc);
    }

    private void addFullRow(JPanel panel, int row, JComponent component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(2, 0, 0, 0);
        panel.add(component, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(TEXT);
        field.setPreferredSize(new Dimension(420, 44));
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setPreferredSize(new Dimension(140, 42));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(9, 16, 9, 16));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(TEXT);
        button.setBackground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 42));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
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
                    JOptionPane.showMessageDialog(null, "PIN phải gồm đúng 6 chữ số");
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
        new PinChange(0);
    }

}

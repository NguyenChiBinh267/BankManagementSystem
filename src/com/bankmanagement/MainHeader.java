package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

final class MainHeader extends JPanel {
    private final JLabel sectionLabel = new JLabel("Tổng quan");
    private final JLabel customerLabel = new JLabel("Đang tải...");
    private final JLabel cardLabel = new JLabel("Số thẻ: -");
    private final SecondaryButton logoutButton = new SecondaryButton("Đăng xuất");

    MainHeader(Runnable logoutAction) {
        super(new BorderLayout(UIStyle.SPACE_4, 0));
        setBackground(UIStyle.CARD_BACKGROUND);
        setBorder(new EmptyBorder(UIStyle.SPACE_3, UIStyle.SPACE_6, UIStyle.SPACE_3, UIStyle.SPACE_6));
        setPreferredSize(new Dimension(0, UIStyle.HEADER_HEIGHT));
        sectionLabel.setFont(UIStyle.SUBTITLE_FONT);
        sectionLabel.setForeground(UIStyle.TEXT);
        add(sectionLabel, BorderLayout.WEST);

        customerLabel.setFont(UIStyle.BODY_STRONG_FONT);
        customerLabel.setForeground(UIStyle.TEXT);
        customerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        cardLabel.setFont(UIStyle.NOTE_FONT);
        cardLabel.setForeground(UIStyle.MUTED_TEXT);
        cardLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel identity = new JPanel();
        identity.setOpaque(false);
        identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
        identity.add(customerLabel);
        identity.add(Box.createVerticalStrut(2));
        identity.add(cardLabel);

        logoutButton.setIcon(new SmartBankIcon(SmartBankIcon.Type.LOGOUT, 16, UIStyle.PRIMARY));
        logoutButton.setPreferredSize(new Dimension(128, UIStyle.CONTROL_HEIGHT));
        logoutButton.addActionListener(e -> logoutAction.run());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIStyle.SPACE_4, 0));
        right.setOpaque(false);
        right.add(identity);
        right.add(logoutButton);
        add(right, BorderLayout.EAST);
    }

    void setSection(String section) {
        sectionLabel.setText(section);
    }

    void setAccount(BankAccountService.AccountSummary account) {
        customerLabel.setText(account == null ? "Khách hàng" : account.displayName());
        cardLabel.setText("Số thẻ: " + (account == null || account.cardNumber == null ? "-" : maskCard(account.cardNumber)));
    }

    private String maskCard(String cardNumber) {
        if (cardNumber.length() <= 4) return cardNumber;
        return "•••• " + cardNumber.substring(cardNumber.length() - 4);
    }
}

package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TransactionRow extends JPanel {
    public TransactionRow(String title, String detail, String amount, boolean moneyIn) {
        super(new BorderLayout(UIStyle.SPACE_3, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(UIStyle.SPACE_3, 0, UIStyle.SPACE_3, 0));
        setPreferredSize(new Dimension(0, 64));
        setMinimumSize(new Dimension(0, 64));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        StatusBadge badge = new StatusBadge(moneyIn ? "Vào" : "Ra", moneyIn ? StatusBadge.Status.SUCCESS : StatusBadge.Status.INFO);
        JPanel badgeWrapper = new JPanel(new GridBagLayout());
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(badge);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyle.BODY_STRONG_FONT);
        titleLabel.setForeground(UIStyle.TEXT);
        JLabel detailLabel = new JLabel(detail);
        detailLabel.setFont(UIStyle.NOTE_FONT);
        detailLabel.setForeground(UIStyle.MUTED_TEXT);
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(UIStyle.SPACE_1));
        text.add(detailLabel);
        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(UIStyle.BODY_STRONG_FONT);
        amountLabel.setForeground(moneyIn ? UIStyle.SUCCESS : UIStyle.TEXT);
        add(badgeWrapper, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);
        add(amountLabel, BorderLayout.EAST);
    }
}

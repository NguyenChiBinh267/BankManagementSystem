package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BalanceCard extends RoundedPanel {
    private final JLabel valueLabel = new JLabel("-");
    private final JLabel detailLabel = new JLabel("-");

    public BalanceCard(String title) {
        super(UIStyle.RADIUS_CARD, UIStyle.PRIMARY, UIStyle.PRIMARY);
        setLayout(new BorderLayout(0, UIStyle.SPACE_3));
        setBorder(new EmptyBorder(UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyle.BODY_STRONG_FONT);
        titleLabel.setForeground(UIStyle.ON_PRIMARY_MUTED);
        valueLabel.setFont(UIStyle.MONEY_FONT);
        valueLabel.setForeground(Color.WHITE);
        detailLabel.setFont(UIStyle.NOTE_FONT);
        detailLabel.setForeground(UIStyle.ON_PRIMARY_MUTED);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(UIStyle.SPACE_3));
        text.add(valueLabel);
        text.add(Box.createVerticalStrut(UIStyle.SPACE_2));
        text.add(detailLabel);
        add(text, BorderLayout.CENTER);
    }

    public void setBalance(long balance) {
        valueLabel.setText(UIStyle.formatMoney(balance));
    }

    public void setDetail(String detail) {
        detailLabel.setText(detail == null || detail.isBlank() ? "-" : detail);
    }

    public void setLoading() {
        valueLabel.setText("Đang tải...");
        detailLabel.setText("Vui lòng chờ");
    }
}

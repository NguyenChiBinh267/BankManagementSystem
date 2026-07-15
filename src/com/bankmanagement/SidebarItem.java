package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarItem extends JButton {
    private final SmartBankIcon.Type iconType;
    private boolean active;

    public SidebarItem(String text, SmartBankIcon.Type iconType) {
        super(text);
        this.iconType = iconType;
        setFont(UIStyle.BUTTON_FONT);
        setForeground(UIStyle.SIDEBAR_TEXT);
        setBackground(UIStyle.SIDEBAR);
        setHorizontalAlignment(SwingConstants.LEFT);
        setIconTextGap(UIStyle.SPACE_3);
        setIcon(new SmartBankIcon(iconType, 18, getForeground()));
        setFocusPainted(true);
        setOpaque(true);
        setContentAreaFilled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new javax.swing.border.EmptyBorder(10, 12, 10, 12));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, UIStyle.CONTROL_HEIGHT));
        setPreferredSize(new Dimension(UIStyle.SIDEBAR_WIDTH - 32, UIStyle.CONTROL_HEIGHT));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (!active) setBackground(UIStyle.SIDEBAR_HOVER); }
            @Override public void mouseExited(MouseEvent e) { if (!active) setBackground(UIStyle.SIDEBAR); }
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        setBackground(active ? UIStyle.SIDEBAR_ACTIVE : UIStyle.SIDEBAR);
        setForeground(Color.WHITE);
        setIcon(new SmartBankIcon(iconType, 18, getForeground()));
        getAccessibleContext().setAccessibleDescription(active ? "Mục đang được chọn" : null);
    }
}

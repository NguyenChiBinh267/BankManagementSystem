package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

abstract class StyledButton extends JButton {
    private final Color normalBackground;
    private final Color hoverBackground;
    private final Color foregroundColor;
    private Icon normalIcon;
    private Timer loadingTimer;
    private int loadingAngle;

    StyledButton(String text, Color normalBackground, Color hoverBackground, Color foregroundColor, Color borderColor) {
        super(text);
        this.normalBackground = normalBackground;
        this.hoverBackground = hoverBackground;
        this.foregroundColor = foregroundColor;
        setFont(UIStyle.BUTTON_FONT);
        setForeground(foregroundColor);
        setBackground(normalBackground);
        setPreferredSize(new Dimension(140, UIStyle.CONTROL_HEIGHT));
        setMinimumSize(new Dimension(96, UIStyle.CONTROL_HEIGHT));
        setFocusPainted(true);
        setOpaque(true);
        setContentAreaFilled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(UIStyle.buttonBorder(borderColor));
        setIconTextGap(UIStyle.SPACE_2);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) setBackground(hoverBackground);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) setBackground(normalBackground);
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(new javax.swing.border.CompoundBorder(
                        new javax.swing.border.LineBorder(UIStyle.PRIMARY, 2, true),
                        new javax.swing.border.EmptyBorder(8, 15, 8, 15)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(UIStyle.buttonBorder(borderColor));
            }
        });
    }

    public final void setLoading(boolean loading) {
        if (loading) {
            if (loadingTimer != null) return;
            normalIcon = getIcon();
            setEnabled(false);
            loadingTimer = new Timer(80, e -> {
                loadingAngle = (loadingAngle + 30) % 360;
                setIcon(new SpinnerIcon(16, foregroundColor, loadingAngle));
            });
            loadingTimer.start();
        } else {
            if (loadingTimer != null) loadingTimer.stop();
            loadingTimer = null;
            setIcon(normalIcon);
            setEnabled(true);
            setBackground(normalBackground);
        }
    }

    private record SpinnerIcon(int size, Color color, int angle) implements Icon {
        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(x + 2, y + 2, size - 4, size - 4, angle, 240);
            g2.dispose();
        }
    }
}

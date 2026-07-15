package com.bankmanagement;

import javax.swing.*;
import java.awt.*;

/** Lightweight rounded surface used by cards and dialogs. */
public class RoundedPanel extends JPanel {
    private final int radius;
    private Color fillColor;
    private Color borderColor;

    public RoundedPanel() {
        this(UIStyle.RADIUS_CARD, UIStyle.CARD_BACKGROUND, UIStyle.BORDER);
    }

    public RoundedPanel(int radius, Color fillColor, Color borderColor) {
        this.radius = radius;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        repaint();
    }

    public void setPanelBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
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

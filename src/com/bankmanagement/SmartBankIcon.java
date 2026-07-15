package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Small dependency-free vector icon set with a consistent 2px stroke. */
public final class SmartBankIcon implements Icon {
    public enum Type { HOME, PLUS, MINUS, ARROW_RIGHT, FLASH, TRANSFER, HISTORY, LOCK, CARD, WALLET, USER, LOGOUT, POWER, REFRESH, INFO, CHECK, WARNING, ERROR }

    private final Type type;
    private final int size;
    private final Color color;

    public SmartBankIcon(Type type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(Math.max(1.6f, size / 10f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int p = Math.max(3, size / 5);
        int mid = size / 2;
        int end = size - p;

        switch (type) {
            case PLUS -> { g2.drawLine(mid, p, mid, end); g2.drawLine(p, mid, end, mid); }
            case MINUS -> g2.drawLine(p, mid, end, mid);
            case ARROW_RIGHT -> { g2.drawLine(p, mid, end, mid); g2.drawLine(end, mid, end - 5, mid - 5); g2.drawLine(end, mid, end - 5, mid + 5); }
            case TRANSFER -> { g2.drawLine(p, size / 3, end, size / 3); g2.drawLine(end, size / 3, end - 4, size / 3 - 4); g2.drawLine(p, 2 * size / 3, end, 2 * size / 3); g2.drawLine(p, 2 * size / 3, p + 4, 2 * size / 3 + 4); }
            case HISTORY -> { g2.drawOval(p, p, size - 2 * p, size - 2 * p); g2.drawLine(mid, mid, mid, p + 3); g2.drawLine(mid, mid, end - 2, mid); }
            case LOCK -> { g2.draw(new RoundRectangle2D.Float(p, mid - 1, size - 2f * p, end - mid + 3, 3, 3)); g2.drawArc(p + 3, p, size - 2 * p - 6, mid, 0, 180); }
            case CARD -> { g2.draw(new RoundRectangle2D.Float(p, p + 2, size - 2f * p, size - 2f * p - 4, 3, 3)); g2.drawLine(p, mid - 2, end, mid - 2); }
            case USER -> { g2.drawOval(mid - 4, p, 8, 8); g2.drawArc(p, mid, size - 2 * p, end - mid + 5, 0, 180); }
            case POWER, LOGOUT -> { g2.drawArc(p, p, size - 2 * p, size - 2 * p, 45, 270); g2.drawLine(mid, 1, mid, mid); }
            case REFRESH -> { g2.drawArc(p, p, size - 2 * p, size - 2 * p, 30, 285); g2.drawLine(end, p, end, p + 5); g2.drawLine(end, p, end - 5, p); }
            case CHECK -> { g2.drawLine(p, mid, mid - 1, end); g2.drawLine(mid - 1, end, end, p); }
            case WARNING, ERROR -> { g2.drawOval(p, p, size - 2 * p, size - 2 * p); g2.drawLine(mid, p + 4, mid, mid + 2); g2.fillOval(mid - 1, end - 3, 3, 3); }
            case FLASH -> { Polygon bolt = new Polygon(new int[]{mid + 1, p, mid - 1, end, mid + 3, mid + 1, end}, new int[]{p, mid, mid, end, mid + 2, mid + 2, p}, 7); g2.drawPolygon(bolt); }
            case WALLET -> { g2.draw(new RoundRectangle2D.Float(p, p + 2, size - 2f * p, size - 2f * p - 4, 3, 3)); g2.drawLine(mid, mid, end, mid); }
            case INFO -> { g2.drawOval(p, p, size - 2 * p, size - 2 * p); g2.drawLine(mid, mid - 1, mid, end - 3); g2.fillOval(mid - 1, p + 3, 3, 3); }
            case HOME -> { Polygon house = new Polygon(new int[]{p, mid, end}, new int[]{mid, p, mid}, 3); g2.drawPolygon(house); g2.drawRect(p + 3, mid, size - 2 * p - 6, end - mid); }
        }
        g2.dispose();
    }
}

package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class NotificationPanel extends JPanel {
    public enum Type { INFO, SUCCESS, WARNING, ERROR }

    private final JLabel messageLabel = new JLabel();
    private Timer dismissTimer;

    public NotificationPanel() {
        super(new BorderLayout(UIStyle.SPACE_2, 0));
        setVisible(false);
        messageLabel.setFont(UIStyle.BODY_FONT);
        add(messageLabel, BorderLayout.CENTER);
    }

    public void showMessage(String message, Type type) {
        showMessage(message, type, 0);
    }

    public void showMessage(String message, Type type, int autoDismissMillis) {
        if (dismissTimer != null) dismissTimer.stop();
        Color foreground;
        Color background;
        SmartBankIcon.Type iconType;
        switch (type) {
            case SUCCESS -> { foreground = UIStyle.SUCCESS; background = UIStyle.SUCCESS_BACKGROUND; iconType = SmartBankIcon.Type.CHECK; }
            case WARNING -> { foreground = UIStyle.WARNING; background = UIStyle.WARNING_BACKGROUND; iconType = SmartBankIcon.Type.WARNING; }
            case ERROR -> { foreground = UIStyle.ERROR; background = UIStyle.ERROR_BACKGROUND; iconType = SmartBankIcon.Type.ERROR; }
            default -> { foreground = UIStyle.PRIMARY; background = UIStyle.INFO_BACKGROUND; iconType = SmartBankIcon.Type.INFO; }
        }
        setBackground(background);
        setBorder(new CompoundBorder(new LineBorder(foreground, 1, true), new EmptyBorder(10, 12, 10, 12)));
        messageLabel.setForeground(foreground);
        messageLabel.setText(message);
        messageLabel.setIcon(new SmartBankIcon(iconType, 16, foreground));
        messageLabel.setIconTextGap(UIStyle.SPACE_2);
        setVisible(true);
        revalidate();
        if (autoDismissMillis > 0) {
            dismissTimer = new Timer(autoDismissMillis, e -> clear());
            dismissTimer.setRepeats(false);
            dismissTimer.start();
        }
    }

    public void clear() {
        setVisible(false);
        messageLabel.setText("");
        revalidate();
    }
}

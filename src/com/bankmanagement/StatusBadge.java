package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class StatusBadge extends JLabel {
    public enum Status { SUCCESS, WARNING, ERROR, INFO, NEUTRAL }

    public StatusBadge(String text, Status status) {
        super(text);
        setFont(UIStyle.NOTE_FONT.deriveFont(Font.BOLD));
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
        setStatus(status);
    }

    public final void setStatus(Status status) {
        Color foreground;
        Color background;
        switch (status) {
            case SUCCESS -> { foreground = UIStyle.SUCCESS; background = UIStyle.SUCCESS_BACKGROUND; }
            case WARNING -> { foreground = UIStyle.WARNING; background = UIStyle.WARNING_BACKGROUND; }
            case ERROR -> { foreground = UIStyle.ERROR; background = UIStyle.ERROR_BACKGROUND; }
            case INFO -> { foreground = UIStyle.PRIMARY; background = UIStyle.INFO_BACKGROUND; }
            default -> { foreground = UIStyle.MUTED_TEXT; background = UIStyle.SURFACE_SUBTLE; }
        }
        setForeground(foreground);
        setBackground(background);
        setBorder(new CompoundBorder(new LineBorder(foreground, 1, true), new EmptyBorder(3, 8, 3, 8)));
    }
}

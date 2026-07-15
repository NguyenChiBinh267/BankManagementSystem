package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class StyledTextField extends JTextField {
    private boolean error;

    public StyledTextField() {
        this(18);
    }

    public StyledTextField(int columns) {
        super(columns);
        setFont(UIStyle.FIELD_FONT);
        setForeground(UIStyle.TEXT);
        setCaretColor(UIStyle.PRIMARY);
        setBackground(UIStyle.CARD_BACKGROUND);
        setPreferredSize(new Dimension(360, UIStyle.CONTROL_HEIGHT));
        setBorder(UIStyle.inputBorder(UIStyle.BORDER));
        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { updateBorder(); }
            @Override public void focusLost(FocusEvent e) { updateBorder(); }
        });
    }

    public void setError(String message) {
        error = message != null && !message.isBlank();
        setToolTipText(error ? message : null);
        updateBorder();
    }

    public void clearError() {
        setError(null);
    }

    private void updateBorder() {
        setBorder(UIStyle.inputBorder(error ? UIStyle.ERROR : isFocusOwner() ? UIStyle.PRIMARY : UIStyle.BORDER));
    }
}

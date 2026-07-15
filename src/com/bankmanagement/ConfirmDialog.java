package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class ConfirmDialog extends JDialog {
    private boolean confirmed;

    private ConfirmDialog(Window owner, String title, String message, String confirmText, boolean destructive, boolean showCancel) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel page = new JPanel(new BorderLayout(0, UIStyle.SPACE_6));
        page.setBackground(UIStyle.CARD_BACKGROUND);
        page.setBorder(new EmptyBorder(UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyle.SUBTITLE_FONT);
        titleLabel.setForeground(UIStyle.TEXT);
        JLabel messageLabel = new JLabel("<html><body style='width:360px'>" + escape(message).replace("\n", "<br>") + "</body></html>");
        messageLabel.setFont(UIStyle.BODY_FONT);
        messageLabel.setForeground(UIStyle.MUTED_TEXT);
        JPanel content = new JPanel(new BorderLayout(0, UIStyle.SPACE_3));
        content.setOpaque(false);
        content.add(titleLabel, BorderLayout.NORTH);
        content.add(messageLabel, BorderLayout.CENTER);

        SecondaryButton cancel = new SecondaryButton("Hủy");
        PrimaryButton confirm = new PrimaryButton(confirmText);
        if (destructive) {
            confirm.setBackground(UIStyle.ERROR);
            confirm.setBorder(UIStyle.buttonBorder(UIStyle.ERROR));
        }
        cancel.addActionListener(e -> dispose());
        confirm.addActionListener(e -> { confirmed = true; dispose(); });
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIStyle.SPACE_3, 0));
        actions.setOpaque(false);
        if (showCancel) {
            actions.add(cancel);
        }
        actions.add(confirm);

        page.add(content, BorderLayout.CENTER);
        page.add(actions, BorderLayout.SOUTH);
        setContentPane(page);
        getRootPane().setDefaultButton(confirm);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public static boolean show(Window owner, String title, String message, String confirmText) {
        ConfirmDialog dialog = new ConfirmDialog(owner, title, message, confirmText, false, true);
        dialog.setVisible(true);
        return dialog.confirmed;
    }

    public static boolean showDestructive(Window owner, String title, String message, String confirmText) {
        ConfirmDialog dialog = new ConfirmDialog(owner, title, message, confirmText, true, true);
        dialog.setVisible(true);
        return dialog.confirmed;
    }

    public static void showInformation(Window owner, String title, String message, String buttonText) {
        ConfirmDialog dialog = new ConfirmDialog(owner, title, message, buttonText, false, false);
        dialog.setVisible(true);
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

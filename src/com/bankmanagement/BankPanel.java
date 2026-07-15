package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

abstract class BankPanel extends JPanel implements RefreshablePanel {
    protected final int accountId;
    protected final NotificationPanel notification = new NotificationPanel();
    private final JPanel content = new JPanel(new BorderLayout(0, UIStyle.SPACE_4));

    BankPanel(int accountId, String title, String subtitle) {
        super(new BorderLayout(0, UIStyle.SPACE_4));
        this.accountId = accountId;
        setBackground(UIStyle.BACKGROUND);
        setBorder(new EmptyBorder(UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6));
        add(createHeading(title, subtitle), BorderLayout.NORTH);
        content.setOpaque(false);
        content.add(notification, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    protected final void setBody(Component body) {
        content.add(body, BorderLayout.CENTER);
    }

    protected RoundedPanel createSurface() {
        RoundedPanel surface = new RoundedPanel(UIStyle.RADIUS_CARD, UIStyle.CARD_BACKGROUND, UIStyle.BORDER);
        surface.setBorder(new EmptyBorder(UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6));
        return surface;
    }

    protected JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel group = new JPanel(new BorderLayout(0, UIStyle.SPACE_2));
        group.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(UIStyle.LABEL_FONT);
        label.setForeground(UIStyle.TEXT);
        label.setLabelFor(field);
        group.add(label, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        return group;
    }

    protected JPanel createActions(JButton... buttons) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIStyle.SPACE_3, 0));
        actions.setOpaque(false);
        for (JButton button : buttons) actions.add(button);
        return actions;
    }

    protected JPanel center(Component component) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(component);
        return wrapper;
    }

    protected String failureMessage(Throwable error, String action) {
        if (error instanceof IllegalArgumentException || error instanceof IllegalStateException) {
            return error.getMessage();
        }
        if (error instanceof SQLException) {
            return "Không thể " + action + " do kết nối cơ sở dữ liệu. Vui lòng thử lại.";
        }
        return "Không thể " + action + " lúc này. Vui lòng thử lại.";
    }

    protected DBConnect openConnection() throws SQLException {
        DBConnect connection = new DBConnect();
        if (connection.connection == null) {
            connection.close();
            throw new SQLException("Không thể kết nối PostgreSQL.");
        }
        return connection;
    }

    private JPanel createHeading(String title, String subtitle) {
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyle.TITLE_FONT);
        titleLabel.setForeground(UIStyle.TEXT);
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(UIStyle.BODY_FONT);
        subtitleLabel.setForeground(UIStyle.MUTED_TEXT);
        heading.add(titleLabel);
        heading.add(Box.createVerticalStrut(UIStyle.SPACE_1));
        heading.add(subtitleLabel);
        return heading;
    }
}

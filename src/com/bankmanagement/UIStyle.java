package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

final class UIStyle {
    // Put image resources in src/images:
    // bank_icon.png (logo/window icon), exit_icon.png (Login exit button), bank_background.png (Login background).
    // Optional illustrations: smartbank_cards.jpg, smartbank_documents.jpg, smartbank_finance.jpg.
    private static final String IMAGE_RESOURCE_ROOT = "/images/";
    static final Color BACKGROUND = new Color(222, 255, 228);
    static final Color CARD_BACKGROUND = Color.WHITE;
    static final Color TEXT = new Color(26, 36, 31);
    static final Color MUTED_TEXT = new Color(92, 105, 96);
    static final Color BORDER = new Color(205, 225, 212);
    static final Color TABLE_SELECTION = new Color(184, 232, 192);

    static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 16);
    static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15);
    static final Font NOTE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private static final Dimension FIELD_SIZE = new Dimension(360, 38);
    private static final Dimension BUTTON_SIZE = new Dimension(140, 40);

    private UIStyle() {
    }

    static JPanel createPage() {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setBackground(BACKGROUND);
        page.setBorder(new EmptyBorder(24, 40, 28, 40));
        return page;
    }

    static JPanel createImagePage(String backgroundFileName) {
        ImageIcon backgroundIcon = loadImageIcon(backgroundFileName);
        Image backgroundImage = backgroundIcon.getIconWidth() > 1 ? backgroundIcon.getImage() : null;

        JPanel page = new JPanel(new BorderLayout(0, 18)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage == null) {
                    return;
                }

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2.setColor(new Color(222, 255, 228, 205));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        page.setBackground(BACKGROUND);
        page.setBorder(new EmptyBorder(24, 40, 28, 40));
        return page;
    }

    static JPanel createCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(24, 28, 24, 28)
        ));
        return card;
    }

    static JPanel center(Component component) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(component);
        return wrapper;
    }

    static JPanel createHeader(JLabel iconLabel, JLabel titleLabel, JLabel detailLabel) {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        if (iconLabel != null) {
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            header.add(iconLabel);
            header.add(Box.createVerticalStrut(8));
        }

        styleTitle(titleLabel);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(titleLabel);

        if (detailLabel != null) {
            styleSubtitle(detailLabel);
            detailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            header.add(Box.createVerticalStrut(4));
            header.add(detailLabel);
        }

        return header;
    }

    static JLabel createBankIconLabel(int size) {
        return new JLabel(createImageIcon("bank_icon.png", size, size));
    }

    static ImageIcon loadImageIcon(String fileName) {
        String normalizedFileName = normalizeImageFileName(fileName);
        if (normalizedFileName.isEmpty()) {
            System.err.println("Missing image resource: src/images/" + fileName);
            return createMissingImageIcon();
        }

        URL resource = UIStyle.class.getResource(IMAGE_RESOURCE_ROOT + normalizedFileName);

        if (resource == null) {
            System.err.println("Missing image resource: src/images/" + normalizedFileName);
            return createMissingImageIcon();
        }

        return new ImageIcon(resource);
    }

    static ImageIcon createImageIcon(String fileName, int width, int height) {
        ImageIcon icon = loadImageIcon(fileName);
        Image image = icon.getImage();
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();
        return new ImageIcon(resized);
    }

    private static String normalizeImageFileName(String fileName) {
        String normalized = fileName == null ? "" : fileName.trim().replace("\\", "/");

        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        if (normalized.startsWith("images/")) {
            normalized = normalized.substring("images/".length());
        }

        if (normalized.contains("/") || normalized.contains("..")) {
            return "";
        }

        return normalized;
    }

    private static ImageIcon createMissingImageIcon() {
        BufferedImage missing = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return new ImageIcon(missing);
    }

    static void styleTitle(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    static void styleSubtitle(JLabel label) {
        label.setFont(SUBTITLE_FONT);
        label.setForeground(MUTED_TEXT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    static void styleFieldLabel(JLabel label) {
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setPreferredSize(new Dimension(190, 38));
    }

    static void styleNoteLabel(JLabel label) {
        label.setFont(NOTE_FONT);
        label.setForeground(MUTED_TEXT);
    }

    static void styleTextField(JTextField field) {
        field.setFont(FIELD_FONT);
        field.setPreferredSize(FIELD_SIZE);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FIELD_FONT);
        comboBox.setPreferredSize(FIELD_SIZE);
        comboBox.setBackground(Color.WHITE);
    }

    static void styleOption(AbstractButton button) {
        button.setFont(FIELD_FONT);
        button.setForeground(TEXT);
        button.setBackground(CARD_BACKGROUND);
        button.setOpaque(true);
        button.setFocusPainted(false);
    }

    static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setPreferredSize(BUTTON_SIZE);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        button.putClientProperty("JButton.buttonType", "roundRect");
    }

    static void styleLinkLabel(JLabel label) {
        label.setFont(BUTTON_FONT);
        label.setForeground(TEXT);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    static JPanel createOptionPanel(AbstractButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        panel.setOpaque(false);
        for (AbstractButton button : buttons) {
            styleOption(button);
            panel.add(button);
        }
        return panel;
    }

    static JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);
        for (JButton button : buttons) {
            styleButton(button);
            panel.add(button);
        }
        return panel;
    }

    static void addFormRow(JPanel panel, int row, JLabel label, JComponent field) {
        styleFieldLabel(label);

        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.gridx = 0;
        labelGbc.gridy = row;
        labelGbc.anchor = GridBagConstraints.EAST;
        labelGbc.insets = new Insets(7, 0, 7, 18);
        panel.add(label, labelGbc);

        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.gridx = 1;
        fieldGbc.gridy = row;
        fieldGbc.weightx = 1;
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.anchor = GridBagConstraints.WEST;
        fieldGbc.insets = new Insets(7, 0, 7, 0);
        panel.add(field, fieldGbc);
    }

    static void addFullWidthRow(JPanel panel, int row, JComponent component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 0, 0, 0);
        panel.add(component, gbc);
    }

    static void styleTable(JTable table) {
        table.setFont(FIELD_FONT);
        table.setRowHeight(32);
        table.setSelectionBackground(TABLE_SELECTION);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(LABEL_FONT);
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
    }

    static void showFrame(JFrame frame, int width, int height) {
        ImageIcon windowIcon = loadImageIcon("bank_icon.png");
        if (windowIcon.getIconWidth() > 1) {
            frame.setIconImage(windowIcon.getImage());
        }
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

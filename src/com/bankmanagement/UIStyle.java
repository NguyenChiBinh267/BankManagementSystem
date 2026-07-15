package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Shared SmartBank design tokens and Swing styling helpers. */
public final class UIStyle {
    private static final String IMAGE_RESOURCE_ROOT = "/images/";

    public static final Color PRIMARY = new Color(30, 64, 175);
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color PRIMARY_PRESSED = new Color(30, 58, 138);
    public static final Color SECONDARY = new Color(59, 130, 246);
    public static final Color ACCENT = new Color(180, 83, 9);

    public static final Color BACKGROUND = new Color(248, 250, 252);
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color SURFACE_SUBTLE = new Color(241, 245, 249);
    public static final Color TEXT = new Color(15, 23, 42);
    public static final Color MUTED_TEXT = new Color(71, 85, 105);
    public static final Color BORDER = new Color(203, 213, 225);
    public static final Color BORDER_STRONG = new Color(148, 163, 184);
    public static final Color TABLE_SELECTION = new Color(219, 234, 254);

    public static final Color SIDEBAR = new Color(15, 23, 42);
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    public static final Color SIDEBAR_ACTIVE = PRIMARY;
    public static final Color SIDEBAR_TEXT = new Color(226, 232, 240);
    public static final Color ON_PRIMARY_MUTED = new Color(219, 234, 254);

    public static final Color SUCCESS = new Color(21, 128, 61);
    public static final Color SUCCESS_BACKGROUND = new Color(240, 253, 244);
    public static final Color WARNING = new Color(180, 83, 9);
    public static final Color WARNING_BACKGROUND = new Color(255, 251, 235);
    public static final Color ERROR = new Color(185, 28, 28);
    public static final Color ERROR_BACKGROUND = new Color(254, 242, 242);
    public static final Color INFO_BACKGROUND = new Color(239, 246, 255);
    public static final Color DISABLED_BACKGROUND = new Color(226, 232, 240);
    public static final Color DISABLED_TEXT = new Color(100, 116, 139);

    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font DISPLAY_FONT = new Font(FONT_FAMILY, Font.BOLD, 28);
    public static final Font TITLE_FONT = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font BODY_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font BODY_STRONG_FONT = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font LABEL_FONT = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FIELD_FONT = new Font(FONT_FAMILY, Font.PLAIN, 15);
    public static final Font BUTTON_FONT = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font NOTE_FONT = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font MONEY_FONT = new Font(FONT_FAMILY, Font.BOLD, 26);

    public static final int SPACE_1 = 4;
    public static final int SPACE_2 = 8;
    public static final int SPACE_3 = 12;
    public static final int SPACE_4 = 16;
    public static final int SPACE_6 = 24;
    public static final int SPACE_8 = 32;
    public static final int RADIUS_CONTROL = 6;
    public static final int RADIUS_CARD = 8;
    public static final int CONTROL_HEIGHT = 42;
    public static final int SIDEBAR_WIDTH = 232;
    public static final int HEADER_HEIGHT = 72;

    public static final NumberFormat MONEY_FORMAT = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Dimension FIELD_SIZE = new Dimension(360, CONTROL_HEIGHT);
    private static final Dimension BUTTON_SIZE = new Dimension(140, CONTROL_HEIGHT);

    private UIStyle() {
    }

    public static void installGlobalDefaults() {
        UIManager.put("Label.font", BODY_FONT);
        UIManager.put("Button.font", BUTTON_FONT);
        UIManager.put("TextField.font", FIELD_FONT);
        UIManager.put("PasswordField.font", FIELD_FONT);
        UIManager.put("ComboBox.font", FIELD_FONT);
        UIManager.put("Table.font", BODY_FONT);
        UIManager.put("TableHeader.font", LABEL_FONT);
        UIManager.put("OptionPane.messageFont", BODY_FONT);
        UIManager.put("OptionPane.buttonFont", BUTTON_FONT);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("ToolTip.font", NOTE_FONT);
    }

    static JPanel createPage() {
        JPanel page = new JPanel(new BorderLayout(0, SPACE_4));
        page.setBackground(BACKGROUND);
        page.setBorder(new EmptyBorder(SPACE_6, SPACE_8, SPACE_6, SPACE_8));
        return page;
    }

    static JPanel createImagePage(String backgroundFileName) {
        ImageIcon backgroundIcon = loadImageIcon(backgroundFileName);
        Image backgroundImage = backgroundIcon.getIconWidth() > 1 ? backgroundIcon.getImage() : null;
        JPanel page = new JPanel(new BorderLayout(0, SPACE_4)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage == null) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2.setColor(new Color(248, 250, 252, 224));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        page.setBackground(BACKGROUND);
        page.setBorder(new EmptyBorder(SPACE_6, SPACE_8, SPACE_6, SPACE_8));
        return page;
    }

    static JPanel createCard() {
        RoundedPanel card = new RoundedPanel(RADIUS_CARD, CARD_BACKGROUND, BORDER);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(SPACE_6, SPACE_6, SPACE_6, SPACE_6));
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
            header.add(Box.createVerticalStrut(SPACE_2));
        }
        styleTitle(titleLabel);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(titleLabel);
        if (detailLabel != null) {
            styleSubtitle(detailLabel);
            detailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            header.add(Box.createVerticalStrut(SPACE_1));
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
            return createMissingImageIcon();
        }
        URL resource = UIStyle.class.getResource(IMAGE_RESOURCE_ROOT + normalizedFileName);
        if (resource != null) {
            return new ImageIcon(resource);
        }
        File sourceResource = new File("src/images", normalizedFileName);
        return sourceResource.isFile() ? new ImageIcon(sourceResource.getAbsolutePath()) : createMissingImageIcon();
    }

    static ImageIcon createImageIcon(String fileName, int width, int height) {
        ImageIcon icon = loadImageIcon(fileName);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(icon.getImage(), 0, 0, width, height, null);
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
        return normalized.contains("/") || normalized.contains("..") ? "" : normalized;
    }

    private static ImageIcon createMissingImageIcon() {
        return new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    }

    static void styleTitle(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    static void styleSubtitle(JLabel label) {
        label.setFont(BODY_FONT);
        label.setForeground(MUTED_TEXT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    static void styleFieldLabel(JLabel label) {
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setPreferredSize(new Dimension(190, CONTROL_HEIGHT));
    }

    static void styleNoteLabel(JLabel label) {
        label.setFont(NOTE_FONT);
        label.setForeground(MUTED_TEXT);
    }

    static void styleTextField(JTextField field) {
        field.setFont(FIELD_FONT);
        field.setForeground(TEXT);
        field.setCaretColor(PRIMARY);
        field.setPreferredSize(FIELD_SIZE);
        field.setBackground(CARD_BACKGROUND);
        field.setBorder(inputBorder(BORDER));
        installFocusBorder(field);
    }

    static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FIELD_FONT);
        comboBox.setPreferredSize(FIELD_SIZE);
        comboBox.setBackground(CARD_BACKGROUND);
        comboBox.setForeground(TEXT);
    }

    static void styleOption(AbstractButton button) {
        button.setFont(BODY_FONT);
        button.setForeground(TEXT);
        button.setBackground(CARD_BACKGROUND);
        button.setOpaque(true);
        button.setFocusPainted(true);
    }

    static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setPreferredSize(BUTTON_SIZE);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(buttonBorder(PRIMARY));
    }

    static void styleLinkLabel(JLabel label) {
        label.setFont(BUTTON_FONT);
        label.setForeground(PRIMARY);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    static JPanel createOptionPanel(AbstractButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACE_3, 0));
        panel.setOpaque(false);
        for (AbstractButton button : buttons) {
            styleOption(button);
            panel.add(button);
        }
        return panel;
    }

    static JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACE_3, 0));
        panel.setOpaque(false);
        for (JButton button : buttons) {
            if (!(button instanceof StyledButton)) {
                styleButton(button);
            }
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
        labelGbc.insets = new Insets(SPACE_2, 0, SPACE_2, SPACE_4);
        panel.add(label, labelGbc);

        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.gridx = 1;
        fieldGbc.gridy = row;
        fieldGbc.weightx = 1;
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.anchor = GridBagConstraints.WEST;
        fieldGbc.insets = new Insets(SPACE_2, 0, SPACE_2, 0);
        panel.add(field, fieldGbc);
    }

    static void addFullWidthRow(JPanel panel, int row, JComponent component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(SPACE_3, 0, 0, 0);
        panel.add(component, gbc);
    }

    static void styleTable(JTable table) {
        table.setFont(BODY_FONT);
        table.setForeground(TEXT);
        table.setRowHeight(38);
        table.setSelectionBackground(TABLE_SELECTION);
        table.setSelectionForeground(TEXT);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        DefaultTableCellRenderer paddedRenderer = new DefaultTableCellRenderer();
        paddedRenderer.setBorder(new EmptyBorder(0, SPACE_3, 0, SPACE_3));
        table.setDefaultRenderer(Object.class, paddedRenderer);

        JTableHeader header = table.getTableHeader();
        header.setFont(LABEL_FONT);
        header.setBackground(SURFACE_SUBTLE);
        header.setForeground(TEXT);
        header.setBorder(new LineBorder(BORDER, 1));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer(right);
        }
    }

    static Border inputBorder(Color color) {
        return new CompoundBorder(new LineBorder(color, 1, true), new EmptyBorder(8, 12, 8, 12));
    }

    static Border buttonBorder(Color color) {
        return new CompoundBorder(new LineBorder(color, 1, true), new EmptyBorder(9, 16, 9, 16));
    }

    static void installFocusBorder(JComponent component) {
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                component.setBorder(inputBorder(PRIMARY));
            }

            @Override
            public void focusLost(FocusEvent e) {
                component.setBorder(inputBorder(BORDER));
            }
        });
    }

    static String formatMoney(long amount) {
        return MONEY_FORMAT.format(amount) + " đ";
    }

    static void showFrame(JFrame frame, int width, int height) {
        installGlobalDefaults();
        ImageIcon windowIcon = loadImageIcon("bank_icon.png");
        if (windowIcon.getIconWidth() > 1) {
            frame.setIconImage(windowIcon.getImage());
        }
        Rectangle usableBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int safeWidth = Math.min(width, Math.max(960, usableBounds.width));
        int safeHeight = Math.min(height, Math.max(640, usableBounds.height));
        frame.setSize(safeWidth, safeHeight);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

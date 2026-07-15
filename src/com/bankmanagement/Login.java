package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Arrays;

public class Login extends JFrame implements ActionListener {
    private final StyledTextField cardNumberInputField = new StyledTextField(20);
    private final PasswordField pinInputField = new PasswordField(20);
    private final PrimaryButton signInButton = new PrimaryButton("Đăng nhập");
    private final SecondaryButton signUpButton = new SecondaryButton("Đăng ký");
    private final NotificationPanel notificationPanel = new NotificationPanel();

    public Login() {
        super("SmartBank - Đăng nhập");
        UIStyle.installGlobalDefaults();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 560));

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(UIStyle.BACKGROUND);
        page.setBorder(new EmptyBorder(UIStyle.SPACE_4, UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6));
        page.add(createTopBar(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(createLoginCard());
        page.add(center, BorderLayout.CENTER);

        setContentPane(page);
        getRootPane().setDefaultButton(signInButton);
        signInButton.addActionListener(this);
        signUpButton.addActionListener(this);
        UIStyle.showFrame(this, 920, 640);
        SwingUtilities.invokeLater(cardNumberInputField::requestFocusInWindow);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel productName = new JLabel("SmartBank");
        productName.setFont(UIStyle.SUBTITLE_FONT);
        productName.setForeground(UIStyle.TEXT);
        topBar.add(productName, BorderLayout.WEST);

        JButton closeButton = new JButton(new SmartBankIcon(SmartBankIcon.Type.POWER, 18, UIStyle.MUTED_TEXT));
        closeButton.setPreferredSize(new Dimension(UIStyle.CONTROL_HEIGHT, UIStyle.CONTROL_HEIGHT));
        closeButton.setToolTipText("Thoát ứng dụng");
        closeButton.getAccessibleContext().setAccessibleName("Thoát ứng dụng");
        closeButton.setBackground(UIStyle.CARD_BACKGROUND);
        closeButton.setBorder(UIStyle.buttonBorder(UIStyle.BORDER));
        closeButton.setFocusPainted(true);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        topBar.add(closeButton, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createLoginCard() {
        RoundedPanel card = new RoundedPanel(UIStyle.RADIUS_CARD, UIStyle.CARD_BACKGROUND, UIStyle.BORDER);
        card.setLayout(new BorderLayout(0, UIStyle.SPACE_6));
        card.setBorder(new EmptyBorder(UIStyle.SPACE_8, UIStyle.SPACE_8, UIStyle.SPACE_8, UIStyle.SPACE_8));
        card.setPreferredSize(new Dimension(460, 500));
        card.add(createHeader(), BorderLayout.NORTH);
        card.add(createForm(), BorderLayout.CENTER);
        return card;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel icon = UIStyle.createBankIconLabel(56);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = new JLabel("Đăng nhập an toàn");
        title.setFont(UIStyle.TITLE_FONT);
        title.setForeground(UIStyle.TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("Truy cập tài khoản SmartBank của bạn");
        subtitle.setFont(UIStyle.BODY_FONT);
        subtitle.setForeground(UIStyle.MUTED_TEXT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(icon);
        header.add(Box.createVerticalStrut(UIStyle.SPACE_4));
        header.add(title);
        header.add(Box.createVerticalStrut(UIStyle.SPACE_2));
        header.add(subtitle);
        return header;
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.gridy = 0;
        form.add(createFieldGroup("Số thẻ", cardNumberInputField), gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(UIStyle.SPACE_4, 0, 0, 0);
        form.add(createFieldGroup("Mã PIN", pinInputField), gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(UIStyle.SPACE_4, 0, 0, 0);
        form.add(notificationPanel, gbc);

        JPanel actions = new JPanel(new GridLayout(1, 2, UIStyle.SPACE_3, 0));
        actions.setOpaque(false);
        actions.add(signInButton);
        actions.add(signUpButton);
        gbc.gridy = 3;
        gbc.insets = new Insets(UIStyle.SPACE_4, 0, 0, 0);
        form.add(actions, gbc);

        JLabel securityNote = new JLabel("SmartBank không bao giờ yêu cầu bạn chia sẻ mã PIN.");
        securityNote.setFont(UIStyle.NOTE_FONT);
        securityNote.setForeground(UIStyle.MUTED_TEXT);
        securityNote.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        gbc.insets = new Insets(UIStyle.SPACE_4, 0, 0, 0);
        form.add(securityNote, gbc);
        return form;
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
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

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == signUpButton) {
            new SignUp();
            dispose();
            return;
        }
        if (event.getSource() == signInButton) {
            authenticate();
        }
    }

    private void authenticate() {
        notificationPanel.clear();
        cardNumberInputField.clearError();
        pinInputField.clearError();
        String cardNumber = cardNumberInputField.getText().trim();
        char[] pinCharacters = pinInputField.getPassword();
        String pin = new String(pinCharacters);
        Arrays.fill(pinCharacters, '\0');

        boolean valid = true;
        if (!BankAccountService.isValidCardNumberFormat(cardNumber)) {
            cardNumberInputField.setError("Số thẻ phải gồm ít nhất 9 chữ số.");
            valid = false;
        }
        if (!pin.matches("\\d{6}")) {
            pinInputField.setError("PIN phải gồm đúng 6 chữ số.");
            valid = false;
        }
        if (!valid) {
            notificationPanel.showMessage("Kiểm tra lại các trường được đánh dấu.", NotificationPanel.Type.ERROR);
            return;
        }

        notificationPanel.showMessage("Đang xác thực thông tin đăng nhập...", NotificationPanel.Type.INFO);
        SwingWorkerRunner.run(
                new JComponent[]{signInButton, signUpButton, cardNumberInputField, pinInputField},
                () -> {
                    try (DBConnect connection = new DBConnect()) {
                        if (connection.connection == null) {
                            throw new SQLException("Không thể kết nối PostgreSQL.");
                        }
                        return AuthenticationService.authenticate(connection.connection, cardNumber, pin);
                    }
                },
                accountId -> {
                    if (accountId < 0) {
                        pinInputField.setText("");
                        pinInputField.setError("Số thẻ hoặc mã PIN không chính xác.");
                        notificationPanel.showMessage("Số thẻ hoặc mã PIN không chính xác.", NotificationPanel.Type.ERROR);
                        pinInputField.requestFocusInWindow();
                        return;
                    }
                    notificationPanel.showMessage("Đăng nhập thành công.", NotificationPanel.Type.SUCCESS);
                    dispose();
                    new Main(accountId);
                },
                error -> notificationPanel.showMessage(
                        error instanceof SQLException
                                ? "Không thể kết nối dịch vụ ngân hàng. Kiểm tra PostgreSQL và thử lại."
                                : "Không thể đăng nhập lúc này. Vui lòng thử lại.",
                        NotificationPanel.Type.ERROR)
        );
    }

    void showSessionExpired() {
        notificationPanel.showMessage(
                "Phiên đăng nhập đã hết hạn do không hoạt động. Vui lòng đăng nhập lại.",
                NotificationPanel.Type.WARNING);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}

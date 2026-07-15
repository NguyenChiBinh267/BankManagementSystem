package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class SignUp extends JFrame {
    private static final String[] STEP_TITLES = {
            "Thông tin cá nhân", "Thông tin bổ sung", "Tài khoản và dịch vụ"};

    private final String formId = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10_000));
    private final CardLayout stepLayout = new CardLayout();
    private final JPanel stepContainer = new JPanel(stepLayout);
    private final PersonalRegistrationPanel personalPanel = new PersonalRegistrationPanel();
    private final AdditionalRegistrationPanel additionalPanel = new AdditionalRegistrationPanel();
    private final AccountRegistrationPanel accountPanel = new AccountRegistrationPanel();
    private final NotificationPanel notification = new NotificationPanel();
    private final JLabel stepLabel = new JLabel();
    private final JLabel titleLabel = new JLabel();
    private final JProgressBar progress = new JProgressBar(1, 3);
    private final SecondaryButton backButton = new SecondaryButton("Quay lại");
    private final PrimaryButton nextButton = new PrimaryButton("Tiếp tục");

    private int currentStep;
    private RegistrationService.PersonalData personalData;
    private RegistrationService.AdditionalData additionalData;

    public SignUp() {
        super("SmartBank - Đăng ký tài khoản");
        UIStyle.installGlobalDefaults();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { returnToLogin(); }
        });

        JPanel page = new JPanel(new BorderLayout(0, UIStyle.SPACE_4));
        page.setBackground(UIStyle.BACKGROUND);
        page.setBorder(new EmptyBorder(UIStyle.SPACE_4, UIStyle.SPACE_6, UIStyle.SPACE_4, UIStyle.SPACE_6));
        page.add(createHeader(), BorderLayout.NORTH);

        stepContainer.setOpaque(false);
        stepContainer.add(personalPanel, "0");
        stepContainer.add(additionalPanel, "1");
        stepContainer.add(accountPanel, "2");
        page.add(stepContainer, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(0, UIStyle.SPACE_3));
        footer.setOpaque(false);
        footer.add(notification, BorderLayout.NORTH);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIStyle.SPACE_3, 0));
        actions.setOpaque(false);
        actions.add(backButton);
        actions.add(nextButton);
        footer.add(actions, BorderLayout.SOUTH);
        page.add(footer, BorderLayout.SOUTH);

        backButton.addActionListener(e -> previousStep());
        nextButton.addActionListener(e -> nextStep());
        setContentPane(page);
        getRootPane().setDefaultButton(nextButton);
        updateStep();
        UIStyle.showFrame(this, 980, 720);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(UIStyle.SPACE_4, 0));
        header.setOpaque(false);
        JLabel icon = UIStyle.createBankIconLabel(48);
        header.add(icon, BorderLayout.WEST);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        titleLabel.setFont(UIStyle.TITLE_FONT);
        titleLabel.setForeground(UIStyle.TEXT);
        stepLabel.setFont(UIStyle.BODY_FONT);
        stepLabel.setForeground(UIStyle.MUTED_TEXT);
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(UIStyle.SPACE_1));
        text.add(stepLabel);
        header.add(text, BorderLayout.CENTER);
        progress.setPreferredSize(new Dimension(180, 8));
        progress.setForeground(UIStyle.PRIMARY);
        progress.setBackground(UIStyle.SURFACE_SUBTLE);
        progress.setBorderPainted(false);
        JPanel progressWrapper = new JPanel(new GridBagLayout());
        progressWrapper.setOpaque(false);
        progressWrapper.add(progress);
        header.add(progressWrapper, BorderLayout.EAST);
        return header;
    }

    private void previousStep() {
        notification.clear();
        if (currentStep == 0) {
            returnToLogin();
            return;
        }
        currentStep--;
        updateStep();
    }

    private void nextStep() {
        notification.clear();
        try {
            if (currentStep == 0) {
                personalData = personalPanel.read(formId);
                currentStep = 1;
                updateStep();
            } else if (currentStep == 1) {
                additionalData = additionalPanel.read();
                currentStep = 2;
                updateStep();
            } else {
                completeRegistration(accountPanel.read());
            }
        } catch (IllegalArgumentException ex) {
            notification.showMessage(ex.getMessage(), NotificationPanel.Type.ERROR);
        }
    }

    private void completeRegistration(RegistrationService.AccountData accountData) {
        if (personalData == null || additionalData == null) {
            notification.showMessage("Thông tin đăng ký chưa đầy đủ. Vui lòng kiểm tra lại các bước trước.", NotificationPanel.Type.ERROR);
            return;
        }
        if (!ConfirmDialog.show(this, "Xác nhận đăng ký",
                "Họ tên: " + personalData.name()
                        + "\nEmail: " + personalData.email()
                        + "\nLoại tài khoản: " + accountData.accountType()
                        + "\nMã hồ sơ: " + formId,
                "Tạo tài khoản")) {
            return;
        }

        notification.showMessage("Đang tạo tài khoản. Vui lòng không đóng ứng dụng...", NotificationPanel.Type.INFO);
        SwingWorkerRunner.run(new JComponent[]{nextButton, backButton}, () -> {
            try (DBConnect connection = new DBConnect()) {
                if (connection.connection == null) throw new SQLException("Không thể kết nối PostgreSQL.");
                return RegistrationService.register(connection.connection, personalData, additionalData, accountData);
            }
        }, cardNumber -> {
            ConfirmDialog.showInformation(
                    this,
                    "Đăng ký thành công",
                    "Đăng ký tài khoản thành công\n\nSố thẻ: " + cardNumber
                            + "\nMã PIN được giữ bí mật và không hiển thị lại.",
                    "Đến đăng nhập");
            dispose();
            new Login();
        }, error -> notification.showMessage(
                error instanceof SQLException
                        ? "Không thể tạo tài khoản. Không có dữ liệu dở dang được lưu; vui lòng kiểm tra kết nối và thử lại."
                        : "Không thể hoàn tất đăng ký lúc này. Vui lòng thử lại.",
                NotificationPanel.Type.ERROR));
    }

    private void updateStep() {
        stepLayout.show(stepContainer, String.valueOf(currentStep));
        titleLabel.setText(STEP_TITLES[currentStep]);
        stepLabel.setText("Bước " + (currentStep + 1) + "/3 · Mã hồ sơ " + formId);
        progress.setValue(currentStep + 1);
        nextButton.setText(currentStep == 2 ? "Hoàn tất" : "Tiếp tục");
        nextButton.setIcon(new SmartBankIcon(
                currentStep == 2 ? SmartBankIcon.Type.CHECK : SmartBankIcon.Type.ARROW_RIGHT,
                16,
                Color.WHITE));
        backButton.setText(currentStep == 0 ? "Đăng nhập" : "Quay lại");
    }

    private void returnToLogin() {
        dispose();
        new Login();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SignUp::new);
    }
}

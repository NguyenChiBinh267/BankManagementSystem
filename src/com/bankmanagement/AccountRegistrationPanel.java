package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

final class AccountRegistrationPanel extends JPanel {
    private final JComboBox<String> accountType = new JComboBox<>(new String[]{
            "Tài khoản thanh toán", "Tài khoản tiết kiệm", "Tiền gửi cố định", "Tiền gửi định kỳ"});
    private final JCheckBox atm = option("Thẻ ATM");
    private final JCheckBox internet = option("Ngân hàng trực tuyến");
    private final JCheckBox mobile = option("Ngân hàng di động");
    private final JCheckBox email = option("Thông báo Email");
    private final JCheckBox cheque = option("Sổ séc");
    private final JCheckBox statement = option("Sao kê điện tử");
    private final JCheckBox confirmation = option("Tôi xác nhận thông tin đăng ký là chính xác.");

    AccountRegistrationPanel() {
        super(new GridBagLayout());
        setOpaque(false);
        RoundedPanel surface = new RoundedPanel(UIStyle.RADIUS_CARD, UIStyle.CARD_BACKGROUND, UIStyle.BORDER);
        surface.setLayout(new BorderLayout(0, UIStyle.SPACE_6));
        surface.setBorder(new EmptyBorder(UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6));
        surface.setPreferredSize(new Dimension(760, 420));
        accountType.setFont(UIStyle.FIELD_FONT);
        accountType.setBackground(UIStyle.CARD_BACKGROUND);
        JPanel services = new JPanel(new GridLayout(3, 2, UIStyle.SPACE_4, UIStyle.SPACE_3));
        services.setOpaque(false);
        services.add(atm);
        services.add(internet);
        services.add(mobile);
        services.add(email);
        services.add(cheque);
        services.add(statement);
        JPanel content = new JPanel(new BorderLayout(0, UIStyle.SPACE_6));
        content.setOpaque(false);
        content.add(PersonalRegistrationPanel.fieldGroup("Loại tài khoản", accountType), BorderLayout.NORTH);
        content.add(PersonalRegistrationPanel.fieldGroup("Dịch vụ đăng ký", services), BorderLayout.CENTER);
        JLabel security = new JLabel("Mã PIN được bảo mật và sẽ không hiển thị lại trên màn hình này.");
        security.setFont(UIStyle.NOTE_FONT);
        security.setForeground(UIStyle.MUTED_TEXT);
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.add(security);
        footer.add(Box.createVerticalStrut(UIStyle.SPACE_4));
        footer.add(confirmation);
        surface.add(content, BorderLayout.CENTER);
        surface.add(footer, BorderLayout.SOUTH);
        add(surface);
    }

    RegistrationService.AccountData read() {
        List<String> services = new ArrayList<>();
        for (JCheckBox option : new JCheckBox[]{atm, internet, mobile, email, cheque, statement}) {
            if (option.isSelected()) services.add(option.getText());
        }
        if (services.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một dịch vụ.");
        }
        if (!confirmation.isSelected()) {
            throw new IllegalArgumentException("Bạn cần xác nhận thông tin trước khi hoàn tất.");
        }
        return new RegistrationService.AccountData(String.valueOf(accountType.getSelectedItem()), String.join(", ", services));
    }

    private static JCheckBox option(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        UIStyle.styleOption(checkBox);
        return checkBox;
    }
}

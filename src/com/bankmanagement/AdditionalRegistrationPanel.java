package com.bankmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

final class AdditionalRegistrationPanel extends JPanel {
    private final JComboBox<String> religion = combo("Không", "Phật giáo", "Thiên Chúa giáo", "Hồi giáo", "Khác");
    private final JComboBox<String> category = combo("Cá nhân", "Sinh viên", "Doanh nghiệp", "Khác");
    private final JComboBox<String> income = combo("Không có", "Dưới 5 triệu", "5 - 10 triệu", "10 - 20 triệu", "Trên 20 triệu");
    private final JComboBox<String> education = combo("THPT", "Cao đẳng", "Đại học", "Sau đại học", "Khác");
    private final JComboBox<String> occupation = combo("Sinh viên", "Nhân viên văn phòng", "Kinh doanh", "Tự do", "Khác");
    private final StyledTextField citizenId = new StyledTextField(20);
    private final JComboBox<String> seniorCitizen = combo("Không", "Có");
    private final JComboBox<String> existingAccount = combo("Không", "Có");

    AdditionalRegistrationPanel() {
        super(new GridBagLayout());
        setOpaque(false);
        RoundedPanel surface = new RoundedPanel(UIStyle.RADIUS_CARD, UIStyle.CARD_BACKGROUND, UIStyle.BORDER);
        surface.setLayout(new GridBagLayout());
        surface.setBorder(new EmptyBorder(UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6, UIStyle.SPACE_6));
        surface.setPreferredSize(new Dimension(760, 450));
        PersonalRegistrationPanel.addRow(surface, 0, "Tôn giáo", religion, "Loại khách hàng", category);
        PersonalRegistrationPanel.addRow(surface, 1, "Thu nhập", income, "Học vấn", education);
        PersonalRegistrationPanel.addRow(surface, 2, "Nghề nghiệp", occupation, "Số CCCD", citizenId);
        PersonalRegistrationPanel.addRow(surface, 3, "Người cao tuổi", seniorCitizen, "Đã có tài khoản", existingAccount);
        add(surface);
    }

    RegistrationService.AdditionalData read() {
        citizenId.clearError();
        String id = citizenId.getText().trim();
        if (!id.matches("\\d{9,12}")) {
            citizenId.setError("CCCD phải gồm 9 đến 12 chữ số.");
            throw new IllegalArgumentException("CCCD phải gồm 9 đến 12 chữ số.");
        }
        return new RegistrationService.AdditionalData(
                selected(religion), selected(category), selected(income), selected(education),
                selected(occupation), id, selected(seniorCitizen), selected(existingAccount));
    }

    private static JComboBox<String> combo(String... values) {
        JComboBox<String> combo = new JComboBox<>(values);
        combo.setFont(UIStyle.FIELD_FONT);
        combo.setBackground(UIStyle.CARD_BACKGROUND);
        combo.setPreferredSize(new Dimension(260, UIStyle.CONTROL_HEIGHT));
        return combo;
    }

    private static String selected(JComboBox<String> combo) {
        return String.valueOf(combo.getSelectedItem());
    }
}

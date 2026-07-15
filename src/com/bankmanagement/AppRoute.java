package com.bankmanagement;

enum AppRoute {
    OVERVIEW("Tổng quan", "Tình hình tài khoản và giao dịch gần đây", SmartBankIcon.Type.HOME),
    DEPOSIT("Nạp tiền", "Ghi nhận tiền vào tài khoản", SmartBankIcon.Type.PLUS),
    WITHDRAW("Rút tiền", "Rút tiền với kiểm tra số dư", SmartBankIcon.Type.MINUS),
    FAST_CASH("Rút tiền nhanh", "Chọn mệnh giá thường dùng", SmartBankIcon.Type.FLASH),
    TRANSFER("Chuyển tiền", "Chuyển tiền an toàn bằng số thẻ", SmartBankIcon.Type.TRANSFER),
    TRANSACTIONS("Lịch sử giao dịch", "Tra cứu và lọc giao dịch", SmartBankIcon.Type.HISTORY),
    PIN_CHANGE("Đổi mã PIN", "Cập nhật mã bảo mật tài khoản", SmartBankIcon.Type.LOCK),
    CARD_MANAGEMENT("Quản lý thẻ", "Xem và cập nhật số thẻ", SmartBankIcon.Type.CARD),
    BALANCE("Tra cứu số dư", "Xem số dư khả dụng hiện tại", SmartBankIcon.Type.WALLET);

    final String title;
    final String subtitle;
    final SmartBankIcon.Type icon;

    AppRoute(String title, String subtitle, SmartBankIcon.Type icon) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
    }
}

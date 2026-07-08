# SmartBank - Bank Management System

## 1. Giới thiệu

SmartBank là ứng dụng ngân hàng desktop viết bằng Java Swing. Project sử dụng Supabase PostgreSQL thông qua JDBC để lưu trữ thông tin tài khoản và lịch sử giao dịch.

Ứng dụng được xây dựng phục vụ mục đích học tập và mô phỏng các thao tác ngân hàng cơ bản, không phải hệ thống ngân hàng thực tế.

## 2. Chức năng chính

- Đăng ký tài khoản qua 3 bước.
- Đăng nhập bằng số thẻ và mã PIN 6 chữ số.
- Xem tổng quan tài khoản.
- Nạp tiền.
- Rút tiền có kiểm tra số dư.
- Rút tiền nhanh.
- Chuyển tiền theo số thẻ.
- Xem giao dịch gần đây / Mini Statement.
- Tra cứu số dư.
- Đổi mã PIN.
- Đổi số thẻ.
- Đăng xuất / thoát ứng dụng.
- Truy vấn cơ sở dữ liệu bằng JDBC `PreparedStatement`.
- Giao diện dùng chung định dạng qua `UIStyle`.

## 3. Công nghệ sử dụng

- Java
- Java Swing
- JDBC
- Supabase PostgreSQL
- PostgreSQL JDBC Driver
- JCalendar
- IntelliJ IDEA

PostgreSQL được triển khai trên Supabase. Không bắt buộc cài PostgreSQL local nếu chạy project với Supabase.

## 4. Yêu cầu

- JDK 17 trở lên.
- IntelliJ IDEA.
- Tài khoản/project Supabase.
- Kết nối Internet để ứng dụng kết nối tới Supabase.
- PostgreSQL JDBC Driver: `lib/postgresql-42.7.12.jar`.
- JCalendar: `jcalendar-1.4.jar`.

## 5. Cấu trúc dự án

```text
Bank/
|-- database/
|   `-- bankmanagement.sql
|-- lib/
|   `-- postgresql-42.7.12.jar
|-- src/
|   |-- META-INF/
|   |   `-- MANIFEST.MF
|   |-- com/bankmanagement/
|   |   |-- BankAccountService.java
|   |   |-- ChangeCardNumber.java
|   |   |-- DBConnect.java
|   |   |-- Deposit.java
|   |   |-- FastCash.java
|   |   |-- Login.java
|   |   |-- Main.java
|   |   |-- MiniStatement.java
|   |   |-- PinChange.java
|   |   |-- SignUp.java
|   |   |-- SignUp2.java
|   |   |-- SignUp3.java
|   |   |-- TransferByCardNumber.java
|   |   |-- UIStyle.java
|   |   `-- WithDraw.java
|   `-- images/
|       |-- bank_background.png
|       |-- bank_icon.png
|       |-- exit_icon.png
|       |-- smartbank_cards.jpg
|       |-- smartbank_documents.jpg
|       `-- smartbank_finance.jpg
|-- Bank.iml
|-- jcalendar-1.4.jar
`-- README.md
```

## 6. Thiết lập cơ sở dữ liệu Supabase

1. Tạo project trên Supabase.
2. Vào Supabase Dashboard của project.
3. Mở `SQL Editor`.
4. Chạy nội dung file `database/bankmanagement.sql` để tạo các bảng.
5. Không cần chạy `CREATE DATABASE bankmanagement` trên Supabase vì Supabase đã cung cấp database mặc định.
6. Kiểm tra các bảng `SignUp`, `SignUp2`, `SignUp3`, `Login`, `Bank` đã được tạo.

Khuyến nghị tạo thêm index để tối ưu truy vấn giao dịch gần đây:

```sql
CREATE INDEX IF NOT EXISTS idx_bank_account_date
ON Bank (AccountID, TransactionDate DESC);
```

## 7. Cấu hình kết nối Supabase

Cấu hình kết nối nằm trong `src/com/bankmanagement/DBConnect.java`. Khi chạy project, thay thông tin kết nối bằng thông tin Supabase của bạn, nhưng không đưa thông tin thật lên GitHub nếu repository public.

Ví dụ dùng host database Supabase:

```java
connection = DriverManager.getConnection(
    "jdbc:postgresql://<SUPABASE_HOST>:5432/postgres?sslmode=require",
    "<SUPABASE_USER>",
    "<SUPABASE_PASSWORD>"
);
```

Ví dụ dùng Supabase pooler:

```java
connection = DriverManager.getConnection(
    "jdbc:postgresql://<SUPABASE_POOLER_HOST>:5432/postgres?sslmode=require",
    "<SUPABASE_USER>",
    "<SUPABASE_PASSWORD>"
);
```

Trong đó:

- `<SUPABASE_HOST>` lấy trong Supabase Dashboard, phần Database Connection.
- `<SUPABASE_USER>` là user database do Supabase cung cấp.
- `<SUPABASE_PASSWORD>` là database password của project.
- Luôn thêm `sslmode=require` khi kết nối Supabase.

Nên dùng biến môi trường hoặc file cấu hình không commit nếu tiếp tục phát triển project. Nếu lỡ public mật khẩu Supabase, cần reset password trong Supabase Dashboard.

## 8. Ghi chú database

- `Login.CardNumber` dùng để đăng nhập và tìm người nhận khi chuyển tiền.
- `SignUp3.CardNumber` lưu số thẻ được tạo khi đăng ký.
- `Bank` lưu lịch sử giao dịch theo `AccountID`.
- `Bank.AccountID` liên kết với `Login.AccountID`.
- Các giao dịch không dùng PIN để truy vấn bảng `Bank`.
- Số dư được tính từ lịch sử giao dịch trong bảng `Bank`, không lưu trong một cột số dư riêng.

Ứng dụng tính số dư bằng cách cộng/trừ các giao dịch:

- `Nạp tiền` làm tăng số dư.
- `Rút tiền` làm giảm số dư.
- `Nhận tiền từ thẻ ...` làm tăng số dư.
- `Chuyển tiền đến thẻ ...` làm giảm số dư.

Lưu ý kiểu dữ liệu:

- `TransactionType` nên dùng `text` hoặc `varchar` đủ dài để lưu mô tả giao dịch.
- `Note` là ghi chú tùy chọn nếu schema hiện có cột này.

## 9. Chạy bằng IntelliJ IDEA

1. Clone repository.
2. Mở project bằng IntelliJ IDEA.
3. Cấu hình Project SDK là JDK 17 trở lên.
4. Thêm các thư viện vào module nếu chưa có:
   - `lib/postgresql-42.7.12.jar`
   - `jcalendar-1.4.jar`
5. Tạo bảng trên Supabase bằng file `database/bankmanagement.sql`.
6. Cập nhật `DBConnect.java` bằng thông tin Supabase của bạn.
7. Chạy file `src/com/bankmanagement/Login.java`.

Ứng dụng bắt đầu từ màn hình đăng nhập. Nếu chưa có tài khoản, chọn đăng ký để tạo tài khoản mới.

## 10. Build file JAR

Trong IntelliJ IDEA:

1. Vào `File` > `Project Structure` > `Artifacts`.
2. Chọn `JAR` > `From modules with dependencies`.
3. Chọn Main Class là `com.bankmanagement.Login`.
4. Vào `Build` > `Build Artifacts`.

Chạy file JAR:

```bash
java -jar SmartBank.jar
```

## 11. Lỗi thường gặp

### `password authentication failed`

- Kiểm tra user/password Supabase.
- Reset database password trong Supabase Dashboard nếu cần.

### `Connection refused` hoặc timeout

- Kiểm tra kết nối Internet.
- Kiểm tra host/port Supabase.
- Kiểm tra URL đã có `sslmode=require`.

### `No suitable driver found`

- Kiểm tra PostgreSQL JDBC Driver đã được thêm vào classpath/module.
- Đảm bảo file `lib/postgresql-42.7.12.jar` tồn tại trong project.

### `relation does not exist`

- Chưa chạy file `database/bankmanagement.sql` trên Supabase.
- Kiểm tra đúng project/database Supabase đang được kết nối.

### `value too long for type character varying`

- Cột `TransactionType` có thể đang quá ngắn.
- Đổi kiểu dữ liệu sang `text` hoặc `varchar` đủ dài.

### Không tìm thấy JCalendar

- Thêm `jcalendar-1.4.jar` vào module trong IntelliJ IDEA.
- `SignUp.java` sử dụng `com.toedter.calendar.JDateChooser`.

### Shortcut/JAR chưa cập nhật UI

- Build lại Artifact.
- Chạy đúng file JAR mới được build.

## 12. Lưu ý bảo mật

- Không commit mật khẩu Supabase.
- Không public connection string thật.
- Không đưa API key/password vào README.
- Nên dùng biến môi trường hoặc file cấu hình không commit nếu tiếp tục phát triển.
- Nếu lỡ đưa mật khẩu lên GitHub, cần đổi/reset password ngay.
- PIN hiện đang lưu dạng văn bản thuần để phục vụ học tập, không phù hợp hệ thống thực tế.

## 13. Tác giả / Nhóm thực hiện

- Điền tên thành viên nhóm tại đây.

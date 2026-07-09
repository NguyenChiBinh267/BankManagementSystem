# SmartBank - Bank Management System

## 1. Giới thiệu

SmartBank - Bank Management System là ứng dụng ngân hàng desktop viết bằng Java Swing. Ứng dụng sử dụng PostgreSQL local thông qua JDBC để lưu trữ thông tin tài khoản và lịch sử giao dịch.

Project được xây dựng phục vụ mục đích học tập và mô phỏng các thao tác ngân hàng cơ bản, không phải hệ thống ngân hàng thực tế.

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
- Giao diện dùng chung định dạng qua `UIStyle`.
- Truy vấn cơ sở dữ liệu bằng JDBC `PreparedStatement`.

## 3. Công nghệ sử dụng

- Java
- Java Swing
- JDBC
- PostgreSQL local
- PostgreSQL JDBC Driver
- JCalendar
- IntelliJ IDEA

## 4. Yêu cầu cài đặt

- JDK 17 trở lên.
- IntelliJ IDEA.
- PostgreSQL local đã được cài đặt và đang chạy.
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

## 6. Thiết lập database local

### Bước 1: Tạo database

```sql
CREATE DATABASE bankmanagement;
```

### Bước 2: Import schema

Chạy lệnh sau trong terminal:

```bash
psql -U postgres -d bankmanagement -f database/bankmanagement.sql
```

Hoặc mở pgAdmin, chọn database `bankmanagement`, mở `Query Tool` và chạy trực tiếp nội dung file `database/bankmanagement.sql`.

### Bước 3: Kiểm tra các bảng đã tạo

- `SignUp`
- `SignUp2`
- `SignUp3`
- `Login`
- `Bank`

Khuyến nghị tạo thêm index để tối ưu truy vấn giao dịch gần đây:

```sql
CREATE INDEX IF NOT EXISTS idx_bank_account_date
ON Bank (AccountID, TransactionDate DESC);
```

## 7. Cấu hình kết nối database

Cấu hình kết nối nằm trong `src/com/bankmanagement/DBConnect.java`:

```java
connection = DriverManager.getConnection(
    "jdbc:postgresql://localhost:5432/bankmanagement",
    "postgres",
    "123456"
);
```

Trong đó:

- `localhost` là máy đang chạy PostgreSQL.
- `5432` là port PostgreSQL mặc định.
- `bankmanagement` là tên database.
- `postgres` là username mặc định.
- `123456` là mật khẩu mẫu, cần đổi theo mật khẩu PostgreSQL local của từng máy.

Nếu PostgreSQL chạy port khác, ví dụ `5433`, sửa URL thành:

```text
jdbc:postgresql://localhost:5433/bankmanagement
```

## 8. Ghi chú database

- `Login.CardNumber` dùng để đăng nhập và tìm người nhận khi chuyển tiền.
- `SignUp3.CardNumber` lưu số thẻ được tạo khi đăng ký.
- `Bank` lưu lịch sử giao dịch theo `AccountID`.
- `Bank.AccountID` liên kết với `Login.AccountID`.
- Không dùng PIN để truy vấn giao dịch trong bảng `Bank`.
- Số dư được tính từ lịch sử giao dịch trong bảng `Bank`, không lưu riêng trong một cột `balance`.

Ứng dụng tính số dư bằng cách cộng/trừ các giao dịch:

- `Nạp tiền` làm tăng số dư.
- `Rút tiền` làm giảm số dư.
- `Nhận tiền từ thẻ ...` làm tăng số dư.
- `Chuyển tiền đến thẻ ...` làm giảm số dư.

Lưu ý kiểu dữ liệu:

- `TransactionType` nên dùng `text` hoặc `varchar` đủ dài để lưu mô tả giao dịch.
- `Note` là ghi chú tùy chọn trong bảng `Bank`.

## 9. Chạy bằng IntelliJ IDEA

1. Clone repository.
2. Mở project bằng IntelliJ IDEA.
3. Cấu hình Project SDK là JDK 17 trở lên.
4. Thêm các thư viện vào module:
   - `lib/postgresql-42.7.12.jar`
   - `jcalendar-1.4.jar`
5. Cài và chạy PostgreSQL local.
6. Tạo database `bankmanagement`.
7. Import `database/bankmanagement.sql`.
8. Kiểm tra `DBConnect.java` đúng thông tin local.
9. Chạy `src/com/bankmanagement/Login.java`.

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

### Không kết nối được PostgreSQL

- Kiểm tra PostgreSQL service đã chạy chưa.
- Kiểm tra port PostgreSQL đang dùng là `5432` hay `5433`.
- Kiểm tra database `bankmanagement` đã tồn tại chưa.
- Kiểm tra username/password trong `DBConnect.java`.

### `password authentication failed`

- Mật khẩu user `postgres` không đúng.
- Sửa lại password trong `DBConnect.java` theo mật khẩu PostgreSQL local.

### `database "bankmanagement" does not exist`

- Chưa tạo database `bankmanagement`.
- Tạo database trước khi import schema và chạy ứng dụng.

### `relation does not exist`

- Chưa import file `database/bankmanagement.sql`.
- Kiểm tra đã import đúng vào database `bankmanagement`.

### `No suitable driver found`

- Chưa thêm PostgreSQL JDBC Driver vào classpath/module.
- Đảm bảo file `lib/postgresql-42.7.12.jar` tồn tại trong project.

### Không tìm thấy JCalendar

- Chưa thêm `jcalendar-1.4.jar` vào module trong IntelliJ IDEA.
- `SignUp.java` sử dụng `com.toedter.calendar.JDateChooser`.

### `value too long for type character varying`

- Cột `TransactionType` có thể đang quá ngắn.
- Đổi kiểu dữ liệu sang `text` hoặc `varchar` đủ dài.

### Shortcut/JAR chưa cập nhật UI

- Build lại Artifact.
- Chạy đúng file JAR mới được build.

## 12. Lưu ý bảo mật

- Mã PIN hiện đang lưu dạng văn bản thuần để phục vụ học tập.
- Cách lưu PIN hiện tại không phù hợp cho hệ thống ngân hàng thực tế.
- Không nên public mật khẩu database thật nếu repository public.
- Nếu triển khai thật, cần mã hóa mật khẩu/PIN và dùng biến môi trường hoặc file cấu hình không commit.

## 13. Tác giả / Nhóm thực hiện

- Điền tên thành viên nhóm tại đây.

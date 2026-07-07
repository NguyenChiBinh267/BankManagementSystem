# Hệ thống quản lý ngân hàng

Đây là ứng dụng ngân hàng máy tính để bàn viết bằng Java Swing, sử dụng PostgreSQL qua JDBC. Dự án mô phỏng các thao tác ngân hàng cơ bản như đăng ký tài khoản, đăng nhập, nạp tiền, rút tiền, xem số dư, đổi số thẻ và chuyển tiền theo số thẻ.

Ứng dụng lưu lịch sử giao dịch trong bảng `Bank`. Số dư không được lưu trong một cột riêng mà được tính lại từ các bản ghi giao dịch.

## Chức năng chính

- Đăng nhập bằng số thẻ và mã PIN 6 chữ số.
- Đăng ký tài khoản qua 3 bước:
  - thông tin cá nhân
  - thông tin bổ sung
  - loại tài khoản, dịch vụ đăng ký, số thẻ được tạo tự động và thông tin đăng nhập
- Nạp tiền.
- Rút tiền có kiểm tra số dư.
- Xem số dư được tính từ lịch sử giao dịch.
- Đổi số thẻ bằng cách nhập số thẻ mới hợp lệ và chưa tồn tại.
- Chuyển tiền đến người dùng khác bằng số thẻ.
- Xem các giao dịch gần đây trong bảng lịch sử.
- Giao diện dùng chung định dạng qua `UIStyle`.
- Truy vấn cơ sở dữ liệu bằng JDBC `PreparedStatement`.

Khi chuyển tiền theo số thẻ, ứng dụng tạo 2 bản ghi giao dịch:

- Người gửi: `Chuyển tiền đến thẻ <số_thẻ_người_nhận>: -<số_tiền>`
- Người nhận: `Nhận tiền từ thẻ <số_thẻ_người_gửi>: +<số_tiền>`

## Công nghệ sử dụng

- Java
- Java Swing
- PostgreSQL
- JDBC
- PostgreSQL JDBC Driver: `lib/postgresql-42.7.12.jar`
- JCalendar: `jcalendar-1.4.jar`
- IntelliJ IDEA

## Yêu cầu

- Khuyến nghị dùng JDK 17 trở lên.
  - Mã nguồn có dùng cú pháp chuỗi nhiều dòng của Java, vì vậy cần tối thiểu Java 15.
- PostgreSQL đã được cài đặt và đang chạy.
- Các file JAR cần có trong classpath:
  - `lib/postgresql-42.7.12.jar`
  - `jcalendar-1.4.jar`

## Cấu trúc dự án

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

## Thiết lập cơ sở dữ liệu

1. Tạo database PostgreSQL:

```sql
CREATE DATABASE bankmanagement;
```

2. Nhập schema:

```bash
psql -U postgres -d bankmanagement -f database/bankmanagement.sql
```

3. Kiểm tra cấu hình kết nối trong `src/com/bankmanagement/DBConnect.java`:

```java
connection = DriverManager.getConnection(
    "jdbc:postgresql://localhost:5432/bankmanagement",
    "postgres",
    "123456"
);
```

Nếu máy của bạn dùng tên database, tên đăng nhập hoặc mật khẩu khác, hãy cập nhật lại trong `DBConnect.java`.

## Ghi chú về database

- `Login.CardNumber` được dùng để đăng nhập và tìm người nhận khi chuyển tiền.
- `SignUp3.CardNumber` lưu số thẻ được tạo khi đăng ký tài khoản.
- `Bank` lưu lịch sử giao dịch. Ứng dụng tính số dư bằng cách cộng/trừ các giao dịch:
  - `Nạp tiền` làm tăng số dư.
  - `Rút tiền` làm giảm số dư.
  - `Nhận tiền từ thẻ ...` làm tăng số dư.
  - `Chuyển tiền đến thẻ ...` làm giảm số dư.
- Ứng dụng vẫn nhận diện các bản ghi chuyển tiền cũ đã lưu bằng mô tả tiếng Anh để tính số dư đúng.
- `Bank.TransactionType` phải đủ dài cho mô tả giao dịch chuyển tiền. Nên dùng `text` hoặc tối thiểu `varchar(100)`.
- `Bank.Note` là ghi chú tùy chọn, nên dùng `text` hoặc `varchar(255)`.

Schema hiện tại nằm trong `database/bankmanagement.sql`. Các bảng chính:

```sql
CREATE TABLE SignUp (...);
CREATE TABLE SignUp2 (...);
CREATE TABLE SignUp3 (
    FormID varchar(30) PRIMARY KEY,
    AccountType varchar(50),
    CardNumber varchar(30) UNIQUE,
    Services varchar(255)
);
CREATE TABLE Login (
    AccountID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    FormID varchar(30),
    CardNumber varchar(30) UNIQUE,
    Pin varchar(30)
);
CREATE TABLE Bank (
    TransactionID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    AccountID INTEGER NOT NULL,
    TransactionDate timestamp DEFAULT CURRENT_TIMESTAMP,
    TransactionType text,
    Amount BIGINT,
    Note text
);
```

## Chạy bằng IntelliJ IDEA

1. Mở thư mục dự án bằng IntelliJ IDEA.
2. Cấu hình Project SDK là JDK 17 trở lên.
3. Kiểm tra thư viện đã được thêm vào module:
   - `jcalendar-1.4.jar`
   - `lib/postgresql-42.7.12.jar`
4. Khởi động PostgreSQL và chắc chắn database `bankmanagement` đã tồn tại.
5. Cập nhật `DBConnect.java` nếu thông tin đăng nhập PostgreSQL khác với máy của bạn.
6. Chạy file `src/com/bankmanagement/Login.java`.

Ứng dụng bắt đầu từ màn hình đăng nhập.

## Các file nguồn chính

- `Login.java` - màn hình đăng nhập.
- `SignUp.java`, `SignUp2.java`, `SignUp3.java` - luồng đăng ký tài khoản.
- `Main.java` - màn hình chính và điều hướng chức năng.
- `Deposit.java` - nạp tiền.
- `WithDraw.java` - rút tiền và kiểm tra số dư.
- `TransferByCardNumber.java` - chuyển tiền theo số thẻ.
- `ChangeCardNumber.java` - đổi số thẻ thủ công.
- `MiniStatement.java` - xem các giao dịch gần đây.
- `PinChange.java` - đổi mã PIN.
- `BankAccountService.java` - xử lý tra cứu tài khoản, đổi số thẻ, chuyển tiền và tính số dư.
- `DBConnect.java` - kết nối PostgreSQL.
- `UIStyle.java` - định dạng giao diện và tải tài nguyên ảnh.

## Lỗi thường gặp

### Không kết nối được PostgreSQL

- Kiểm tra PostgreSQL đã chạy chưa.
- Kiểm tra database `bankmanagement` đã tồn tại chưa.
- Kiểm tra tên đăng nhập và mật khẩu trong `DBConnect.java`.
- Kiểm tra PostgreSQL có nhận kết nối ở `localhost:5432` không.

### Không tìm thấy PostgreSQL JDBC Driver

- Thêm `lib/postgresql-42.7.12.jar` vào phần phụ thuộc của module trong IntelliJ IDEA.
- Build lại dự án sau khi thêm thư viện.

### Không tìm thấy JCalendar

- Thêm `jcalendar-1.4.jar` vào phần phụ thuộc của module.
- `SignUp.java` dùng `com.toedter.calendar.JDateChooser`, nên phần đăng ký sẽ không biên dịch nếu thiếu JCalendar.

### Lỗi `value too long for type character varying(30)`

- Lỗi này xảy ra khi database cũ vẫn để `Bank.TransactionType varchar(30)`.
- Chạy:

```sql
ALTER TABLE Bank
    ALTER COLUMN TransactionType TYPE text;
```

### Sai cấu hình JDK/JRE

- Dùng JDK đầy đủ, không chỉ dùng JRE.
- Khuyến nghị dùng JDK 17 trở lên.
- Kiểm tra trong IntelliJ IDEA: `File > Project Structure > Project SDK`.

## Ghi chú

- Mã PIN hiện đang được lưu dạng văn bản thuần để phục vụ mục đích học tập.
- Đây là dự án máy tính để bàn phục vụ học tập, không phải hệ thống ngân hàng dùng trong thực tế.
- Bảng `Bank` là nguồn dữ liệu cho lịch sử giao dịch và tính số dư.

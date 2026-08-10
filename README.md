# SmartBank - Bank Management System

## Giới thiệu

SmartBank là ứng dụng desktop mô phỏng quản lý tài khoản ngân hàng, xây dựng bằng Java Swing và PostgreSQL. Ứng dụng cho phép người dùng đăng ký, đăng nhập bằng số thẻ/PIN và thực hiện các thao tác tài khoản cơ bản trên dữ liệu PostgreSQL local.

Đây là project học tập/mô phỏng; không phải hệ thống ngân hàng production.

## Tính năng

- Đăng ký tài khoản theo 3 bước: thông tin cá nhân, thông tin bổ sung, loại tài khoản/dịch vụ.
- Tạo số thẻ tự động, kiểm tra tính duy nhất trước khi lưu.
- Đăng nhập bằng số thẻ (ít nhất 9 chữ số) và PIN 6 chữ số.
- Dashboard hiển thị số dư, chủ tài khoản, số thẻ đã che một phần và 5 giao dịch gần nhất.
- Nạp tiền và rút tiền với số tiền dương, cho phép nhập dấu `.`/`,` để phân tách hàng nghìn và ghi chú tối đa 255 ký tự.
- Rút tiền nhanh với các mức 100.000, 200.000, 500.000, 1.000.000, 2.000.000 và 5.000.000 VND.
- Chuyển tiền theo số thẻ: kiểm tra người nhận, không cho chuyển cho chính mình, kiểm tra số dư và yêu cầu xác nhận trước khi thực hiện.
- Tra cứu số dư được tính từ lịch sử giao dịch.
- Xem lịch sử giao dịch, sắp xếp trên bảng, lọc theo loại giao dịch và khoảng ngày; mỗi lần tải tối đa 500 bản ghi.
- Đổi PIN sau khi xác thực PIN hiện tại.
- Quản lý số thẻ: cập nhật đồng thời số thẻ trong dữ liệu đăng nhập và hồ sơ đăng ký.
- Tự đăng xuất sau 5 phút không có thao tác chuột hoặc bàn phím trong cửa sổ ứng dụng.

## Giao diện và luồng sử dụng

Entry point thực tế là `com.bankmanagement.Login`. Từ màn hình đăng nhập, người dùng có thể đi tới đăng ký hoặc vào màn hình chính sau khi xác thực thành công.

Màn hình chính `Main` dùng `CardLayout`, sidebar và header để điều hướng giữa 9 route:

1. Tổng quan
2. Nạp tiền
3. Rút tiền
4. Rút tiền nhanh
5. Chuyển tiền
6. Lịch sử giao dịch
7. Đổi mã PIN
8. Quản lý thẻ
9. Tra cứu số dư

Các thao tác truy vấn/ghi dữ liệu từ UI chạy qua `SwingWorkerRunner`, giúp tránh chặn Swing event-dispatch thread. Những action quan trọng như đăng ký, nạp/rút/chuyển tiền, đổi PIN và đổi số thẻ đều có validation hoặc hộp thoại xác nhận tương ứng.

## Công nghệ sử dụng

- Java Swing/AWT
- JDBC với PostgreSQL JDBC Driver `42.7.12`
- PostgreSQL
- IntelliJ IDEA module/artifact configuration
- JUnit Jupiter `5.14.3` (thư viện test có trong `lib/`)

Repository không có Maven hoặc Gradle wrapper/build file. `jcalendar-1.4.jar` được khai báo trong cấu hình IntelliJ nhưng UI hiện tại dùng `JSpinner` cho ngày tháng, không import hay sử dụng JCalendar trong source hiện tại.

## Kiến trúc và cấu trúc project

```text
Bank/
├── database/
│   └── bankmanagement.sql             # Schema PostgreSQL
├── lib/
│   ├── postgresql-42.7.12.jar          # JDBC driver runtime
│   ├── jcalendar-1.4.jar               # Có trong cấu hình IDE, hiện không dùng bởi source
│   └── junit-*.jar                     # Dependency cho test
├── src/
│   ├── META-INF/MANIFEST.MF            # Main-Class: com.bankmanagement.Login
│   ├── com/bankmanagement/
│   │   ├── Login.java, SignUp.java, Main.java
│   │   ├── *RegistrationPanel.java     # Đăng ký ba bước
│   │   ├── DashboardPanel.java, BalancePanel.java
│   │   ├── MoneyOperationPanel.java, FastCashPanel.java, TransferPanel.java
│   │   ├── TransactionHistoryPanel.java, PinChangePanel.java, CardManagementPanel.java
│   │   ├── AuthenticationService.java, RegistrationService.java, BankAccountService.java, DBConnect.java
│   │   └── UIStyle.java và các component Swing dùng lại
│   └── images/                         # Tài nguyên hình ảnh UI (bank_icon.png được dùng làm icon ứng dụng)
├── test/com/bankmanagement/            # Unit/integration test hiện có
├── Bank.iml                            # Cấu hình module IntelliJ
└── README.md
```

Các lớp có tên theo luồng cũ như `Deposit`, `WithDraw`, `FastCash`, `MiniStatement`, `PinChange`, `ChangeCardNumber` và `TransferByCardNumber` vẫn tồn tại trong source, nhưng navigation hiện tại đăng ký các panel mới nêu trên.

## Database

Database mặc định có tên `bankmanagement`; schema ở [database/bankmanagement.sql](database/bankmanagement.sql) tạo 5 bảng:

| Bảng | Vai trò |
| --- | --- |
| `SignUp` | Hồ sơ cá nhân; `FormID` là khóa chính, bao gồm cả PIN đăng ký. |
| `SignUp2` | Thông tin bổ sung, liên kết `SignUp(FormID)`. |
| `SignUp3` | Loại tài khoản, dịch vụ và số thẻ; liên kết `SignUp(FormID)`. |
| `Login` | `AccountID` identity, `FormID`, số thẻ và PIN dùng cho xác thực. |
| `Bank` | Lịch sử giao dịch theo `AccountID`, gồm thời gian, loại, số tiền và `Note`. |

`Login.AccountID` là khóa ngoại được `Bank.AccountID` tham chiếu. Số thẻ có ràng buộc `UNIQUE` trong `SignUp3` và `Login`; khi đổi thẻ, service cập nhật cả hai bảng trong một database transaction.

Số dư **không có cột lưu riêng**. `BankAccountService.calculateBalance()` đọc toàn bộ giao dịch của tài khoản: cộng các giao dịch nạp/nhận tiền và trừ các giao dịch rút/chuyển tiền. Mỗi lần chuyển tiền tạo hai bản ghi `Bank` cùng thời điểm: một giao dịch ra cho người gửi và một giao dịch vào cho người nhận. `TransactionType` lưu nhãn nghiệp vụ (với chuyển tiền có kèm số thẻ liên quan); `Amount` là `BIGINT`, còn `Note` có thể rỗng và giới hạn 255 ký tự theo schema/UI.

Đăng ký cũng được thực hiện trong một transaction: ghi `SignUp`, `SignUp2`, `SignUp3` và `Login`; nếu lỗi thì rollback.

## Yêu cầu môi trường

- PostgreSQL đang chạy và có quyền tạo/import database.
- IntelliJ IDEA (khuyến nghị để dùng trực tiếp cấu hình module có sẵn).
- JDK 26 nếu mở đúng cấu hình IntelliJ hiện tại (`.idea/misc.xml` dùng `openjdk-26`). Source sử dụng các tính năng Java hiện đại như text blocks, `record` và switch expression, nên cần tối thiểu JDK 17 nếu tự cấu hình SDK khác.
- PostgreSQL JDBC driver `lib/postgresql-42.7.12.jar` trên runtime classpath.

## Cài đặt

```bash
git clone https://github.com/NguyenChiBinh267/BankManagementSystem.git
cd BankManagementSystem
```

Sau đó mở thư mục project bằng IntelliJ IDEA. Nếu clone vào thư mục có tên khác, điều này không ảnh hưởng đến source; các đường dẫn dependency trong cấu hình IDE có thể cần được sửa lại như mô tả bên dưới.

## Cấu hình PostgreSQL

1. Tạo database:

   ```sql
   CREATE DATABASE bankmanagement;
   ```

2. Import schema:

   ```bash
   psql -U postgres -d bankmanagement -f database/bankmanagement.sql
   ```

   Hoặc mở Query Tool trong pgAdmin, chọn database `bankmanagement` rồi chạy nội dung file SQL.

3. Cấu hình kết nối trong `DBConnect.java`. Code ưu tiên các biến môi trường sau:

   ```text
   SMARTBANK_DB_URL=jdbc:postgresql://localhost:5432/bankmanagement
   SMARTBANK_DB_USER=<postgres-user-local>
   SMARTBANK_DB_PASSWORD=<postgres-password-local>
   ```

   Nếu không có biến môi trường, code fallback về URL local `jdbc:postgresql://localhost:5432/bankmanagement`, user `postgres` và một mật khẩu mẫu hard-code. Không dùng giá trị fallback đó làm credential production. Bạn có thể đặt biến môi trường cho Run Configuration hoặc chỉnh fallback trong `DBConnect.java` cho môi trường local của mình.

## Chạy ứng dụng

1. Trong IntelliJ, chọn Project SDK phù hợp (JDK 26 theo cấu hình hiện có, hoặc JDK 17+ nếu tự thiết lập).
2. Kiểm tra module `Bank` có PostgreSQL driver trên classpath. Repository chứa [lib/postgresql-42.7.12.jar](lib/postgresql-42.7.12.jar), nhưng file `.idea/libraries/postgresql_42_7_12.xml` hiện tham chiếu một đường dẫn `D:/Downloads/...`; sau khi clone, hãy đổi library này sang JAR trong `lib/` nếu IntelliJ báo thiếu dependency.
3. Tạo/import database và cấu hình kết nối như phần trên.
4. Chạy class `com.bankmanagement.Login`.

Sau khi đăng ký thành công, số thẻ được hiển thị một lần trong hộp thoại thành công để dùng cho đăng nhập.

## Build JAR

Repository có IntelliJ artifact `Bank:jar` và `src/META-INF/MANIFEST.MF` khai báo `Main-Class: com.bankmanagement.Login`. Tuy nhiên artifact hiện tham chiếu PostgreSQL driver bằng đường dẫn máy cục bộ `D:/Downloads/postgresql-42.7.12.jar`, nên không thể xem là cấu hình đóng gói portable ngay sau khi clone.

Để build trong IntelliJ, trước hết sửa dependency/artifact để dùng `lib/postgresql-42.7.12.jar`, rồi chọn **Build → Build Artifacts → Bank:jar → Build**. Output được cấu hình tại `out/artifacts/Bank_jar/Bank.jar`. Khi chạy JAR, PostgreSQL vẫn phải đang hoạt động và cấu hình database vẫn phải hợp lệ.

## Lưu ý về dữ liệu và transaction

- Nạp tiền ghi một bản ghi giao dịch; rút tiền khóa bản ghi tài khoản, kiểm tra số dư rồi mới ghi giao dịch.
- Chuyển tiền khóa người gửi và người nhận theo thứ tự `AccountID`, kiểm tra số dư trong cùng transaction, ghi đủ hai bản ghi rồi commit; lỗi sẽ rollback.
- Đổi số thẻ cập nhật `Login` và `SignUp3` trong cùng transaction; số thẻ phải có ít nhất 9 chữ số và chưa tồn tại.
- Lịch sử chỉ hiển thị các giao dịch đã lưu trong `Bank`; cột trạng thái ở UI hiện hiển thị nhãn `Thành công` cho các bản ghi được tải.

## Security notes và giới hạn

- PIN được lưu và so khớp dạng plaintext trong `SignUp`/`Login`; không có hashing, encryption hoặc cơ chế quản lý PIN production.
- Fallback database credential có trong source. Dùng environment variables hoặc secret/configuration management khi phát triển/triển khai thực tế; không commit credential thật.
- Kết nối hướng tới PostgreSQL local; không có cấu hình server, HTTPS, phân quyền, audit trail hay triển khai production.
- Mô hình số dư phụ thuộc vào chuỗi `TransactionType`, nên đây là mô phỏng nghiệp vụ đơn giản, không thay thế ledger/accounting banking thực tế.
- Timeout 5 phút chỉ là timeout UI/session local, không phải cơ chế bảo mật cấp server.

## Troubleshooting

### Không kết nối được PostgreSQL

- Kiểm tra PostgreSQL service đang chạy, host/port trong `SMARTBANK_DB_URL` (hoặc fallback) đúng, và database `bankmanagement` đã được tạo.
- Kiểm tra `SMARTBANK_DB_USER`/`SMARTBANK_DB_PASSWORD` hoặc giá trị local trong `DBConnect.java`.
- Import lại `database/bankmanagement.sql` nếu báo thiếu bảng như `Login` hoặc `Bank`.

### `No suitable driver` hoặc `ClassNotFoundException: org.postgresql.Driver`

Thêm `lib/postgresql-42.7.12.jar` vào module/runtime classpath. Đặc biệt kiểm tra và thay đường dẫn `D:/Downloads/...` trong IntelliJ library/artifact sau khi clone.

### Không thể đăng nhập hoặc chuyển tiền

- Số thẻ phải chỉ gồm chữ số và dài ít nhất 9 ký tự; PIN phải đúng 6 chữ số.
- Tài khoản người nhận phải tồn tại trong `Login`; không thể chuyển cho số thẻ của chính tài khoản đang đăng nhập.
- Rút/chuyển yêu cầu số dư tính từ lịch sử giao dịch phải đủ.

### Lỗi resource/icon khi chạy từ cấu hình tự tạo hoặc JAR

Đảm bảo thư mục `src/images/` được đưa vào classpath/output resources. `UIStyle` tải ảnh từ `/images/`, trong đó `bank_icon.png` là icon cửa sổ ứng dụng.

## Project status

Project phục vụ mục đích học tập về Java Swing, JDBC, validation giao diện và database transaction. Không có build automation Maven/Gradle hoặc tài liệu triển khai production trong repository hiện tại.

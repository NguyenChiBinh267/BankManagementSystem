```markdown
# Bank Management System

## Project Description

**Bank Management System** is a Java desktop application that simulates basic banking operations.  
The application is built with **Java Swing** and provides a graphical interface for account registration, login, deposits, withdrawals, PIN changes, and viewing recent transactions.

This project is designed for learning and demonstration purposes, especially for understanding Java Swing GUI development, JDBC database connectivity, and basic banking workflow implementation.

---

## Features

- User login with card number and PIN
- Multi-step user registration flow:
  - `SignUp`
  - `SignUp2`
  - `SignUp3`
- Main banking dashboard
- Deposit money
- Withdraw money
- Change account PIN
- View mini statement / recent transactions
- Basic input validation
- Image-based UI resources loaded from the project resources folder

---

## Technologies Used

- **Java**
- **Java Swing**
- **JDBC**
- **IntelliJ IDEA**
- **PostgreSQL / MySQL**  
  Update this according to your actual database configuration.
- **JCalendar**  
  Used for date selection in the registration form.

---

## Project Structure
```text
Bank/
├── database/
│   └── bankmanagement.sql
│
├── lib/
│   └── postgresql-42.7.12.jar
│
├── src/
│   ├── com/
│   │   └── bankmanagement/
│   │       ├── DBConnect.java
│   │       ├── Login.java
│   │       ├── SignUp.java
│   │       ├── SignUp2.java
│   │       ├── SignUp3.java
│   │       ├── Main.java
│   │       ├── Deposit.java
│   │       ├── WithDraw.java
│   │       ├── PinChange.java
│   │       ├── MiniStatement.java
│   │       └── UIStyle.java
│   │
│   └── images/
│       ├── bank_background.png
│       ├── bank_icon.png
│       ├── exit_icon.png
│       ├── smartbank_cards.jpg
│       ├── smartbank_documents.jpg
│       └── smartbank_finance.jpg
│
├── Bank.iml
└── README.md
```
---

## Database Setup

This project uses JDBC to connect to a relational database.

You can use **PostgreSQL** or **MySQL**, depending on your local setup.  
Update the connection settings in `DBConnect.java` according to your configuration.

Example configuration values to check:
```java
String url = "jdbc:postgresql://localhost:5432/bankmanagement";
String username = "postgres";
String password = "your_password";
```
If you are using MySQL, update the JDBC URL and driver dependency accordingly:
```java
String url = "jdbc:mysql://localhost:3306/bankmanagement";
String username = "root";
String password = "your_password";
```
### Steps

1. Create a database named:
```sql
bankmanagement
```
2. Import the SQL file if available:
```text
database/bankmanagement.sql
```
3. Update `DBConnect.java` with your database URL, username, and password.

4. Make sure the required JDBC driver is added to the project classpath.

---

## How to Run

1. Clone the repository:
```bash
git clone <your-repository-url>
```
2. Open the project in **IntelliJ IDEA**.

3. Make sure the project SDK is configured.

4. Add required libraries if they are not already configured:
    - PostgreSQL JDBC Driver or MySQL JDBC Driver
    - JCalendar library

5. Confirm that image resources are available in:
```text
src/images/
```
6. Configure the database connection in:
```text
src/com/bankmanagement/DBConnect.java
```
7. Run the application from:
```text
src/com/bankmanagement/Login.java
```
---

## Screenshots

Add screenshots to an `images` folder or update these paths according to your repository structure.

![Login Screen](images/login.png)

![Sign Up Screen](images/signup.png)

![Main Dashboard](images/main-dashboard.png)

![Mini Statement](images/mini-statement.png)

---

## Future Improvements

- Add password/PIN hashing instead of storing plain text values
- Improve validation for registration and transaction forms
- Add transaction history filtering and search
- Add account balance summary on the dashboard
- Improve database schema and relationships
- Add role-based access for users and administrators
- Add unit tests for business logic
- Package the application as an executable `.jar`
- Improve UI responsiveness and accessibility

---
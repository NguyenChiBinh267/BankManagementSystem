CREATE TABLE SignUp
(
    FormID varchar(30) PRIMARY KEY,
    CustomerName varchar(50) NOT NULL,
    Email varchar(50) NOT NULL,
    Phone varchar(20) NOT NULL,
    Gender varchar(10) NOT NULL,
    Birthday varchar(30) NOT NULL,
    Address varchar(70) NOT NULL,
    City varchar(50) NOT NULL,
    Pin varchar(30) NOT NULL
);

CREATE TABLE SignUp2
(
    FormID varchar(30) PRIMARY KEY,
    Religion varchar(30),
    Category varchar(30),
    Income varchar(30),
    Education varchar(50),
    Occupation varchar(50),
    CCCD varchar(20),
    SeniorCitizen varchar(10),
    ExistingAccount varchar(10)
);

CREATE TABLE SignUp3
(
    FormID varchar(30) PRIMARY KEY,
    AccountType varchar(50),
    CardNumber varchar(30),
    Pin varchar(30),
    Services varchar(255)
);

CREATE TABLE Login
(
    FormID varchar(30) PRIMARY KEY,
    CardNumber varchar(30),
    Pin varchar(30)
);

CREATE TABLE Bank
(
    TransactionID SERIAL PRIMARY KEY,
    Pin varchar(30),
    TransactionDate timestamp,
    TransactionType varchar(30),
    Amount numeric(15,2)
);

SELECT * FROM SignUp;
SELECT * FROM SignUp2;
SELECT * FROM SignUp3;
SELECT * FROM Login;
SELECT * FROM Bank;

SELECT *
FROM SignUp s1
JOIN SignUp2 s2 ON s1.FormID = s2.FormID
JOIN SignUp3 s3 ON s1.FormID = s3.FormID
JOIN Login l ON s1.FormID = l.FormID;
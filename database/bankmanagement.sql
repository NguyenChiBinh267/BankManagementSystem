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
    FormID varchar(30),
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

CREATE TABLE Login(
	AccountID int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	formID varchar(30),
	cardNumber varchar(30),
	pin varchar(50)
)

CREATE TABLE Bank
(
    TransactionID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Pin varchar(30),
    TransactionDate timestamp,
    TransactionType varchar(30),
    Amount BIGINT
);





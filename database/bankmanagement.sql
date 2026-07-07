CREATE TABLE SignUp
(
    FormID varchar(30) PRIMARY KEY,
    CustomerName varchar(50) NOT NULL,
    Email varchar(50) NOT NULL,
    Phone varchar(20) NOT NULL,
    Gender varchar(10) NOT NULL,
    Birthday varchar(30) NOT NULL,
    Address varchar(255) NOT NULL,
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
    ExistingAccount varchar(10),

    FOREIGN KEY (FormID) REFERENCES SignUp(FormID)
);

CREATE TABLE SignUp3
(
    FormID varchar(30) PRIMARY KEY,
    AccountType varchar(50),
    CardNumber varchar(30) UNIQUE,
    Services varchar(255),

    FOREIGN KEY (FormID) REFERENCES SignUp(FormID)
);

CREATE TABLE Login
(
    AccountID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    FormID varchar(30),
    CardNumber varchar(30) UNIQUE,
    Pin varchar(30),

    FOREIGN KEY (FormID) REFERENCES SignUp(FormID)
);

CREATE TABLE Bank
(
    TransactionID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    AccountID INTEGER NOT NULL,
    TransactionDate timestamp DEFAULT CURRENT_TIMESTAMP,
    TransactionType text,
    Amount BIGINT,
	Note varchar(255);

    FOREIGN KEY (AccountID) REFERENCES Login(AccountID)
);


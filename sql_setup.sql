CREATE DATABASE IF NOT EXISTS BankEnterpriseDB;
USE BankEnterpriseDB;

-- =====================================================
-- 1. INDEPENDENT TABLES (Lookup & Base Entities)
-- =====================================================

CREATE TABLE branches (
    branch_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_name VARCHAR(100) NOT NULL,
    branch_code VARCHAR(20) UNIQUE NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(50),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20),
    nationality VARCHAR(50),
    national_id VARCHAR(50) UNIQUE NOT NULL,
    occupation VARCHAR(50),
    marital_status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE account_types (
    type_id INT AUTO_INCREMENT PRIMARY KEY,
    account_type_name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO account_types (account_type_name) VALUES 
('Saving Account'), ('Current Account'), ('Fixed Deposit'), ('Business Account'), ('Loan Account');

CREATE TABLE transaction_types (
    type_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_type_name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO transaction_types (transaction_type_name) VALUES 
('Deposit'), ('Withdrawal'), ('Transfer'), ('Loan Payment'), ('Interest Credit'), ('Service Fee');

-- =====================================================
-- 2. STAFF & INTERNAL OPERATIONS
-- =====================================================

CREATE TABLE staff_information (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_id INT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    staff_role VARCHAR(50) NOT NULL,
    department VARCHAR(50) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    account_status VARCHAR(20) DEFAULT 'ACTIVE',
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_branch FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
);

-- =====================================================
-- 3. CUSTOMER COMPLIANCE & SECURITY
-- =====================================================

CREATE TABLE contacts (
    contact_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT UNIQUE NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    residential_address VARCHAR(255) NOT NULL,
    city VARCHAR(50) NOT NULL,
    country VARCHAR(50) NOT NULL,
    postal_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contact_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE credentials (
    credential_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(100) NOT NULL,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(100),
    last_login DATETIME,
    failed_login_attempts INT DEFAULT 0,
    account_locked BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_credential_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE kyc (
    kyc_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT UNIQUE NOT NULL,
    id_document_type VARCHAR(50) NOT NULL,
    id_number VARCHAR(50) NOT NULL,
    id_expiry_date DATE,
    proof_of_address VARCHAR(100),
    kyc_verification_status VARCHAR(20) DEFAULT 'PENDING',
    kyc_approval_date DATETIME,
    CONSTRAINT fk_kyc_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE document_storage (
    doc_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT UNIQUE NOT NULL,
    passport_url VARCHAR(255),
    national_id_scan_url VARCHAR(255),
    selfie_verification_url VARCHAR(255),
    utility_bill_url VARCHAR(255),
    CONSTRAINT fk_document_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- =====================================================
-- 4. CORE BANKING (Accounts & Ownership)
-- =====================================================

CREATE TABLE accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_id INT NOT NULL,
    account_type_id INT NOT NULL,
    account_number VARCHAR(25) UNIQUE NOT NULL,
    account_status VARCHAR(20) DEFAULT 'ACTIVE',
    currency VARCHAR(10) DEFAULT 'USD',
    current_balance DECIMAL(18,2) DEFAULT 0.00,
    available_balance DECIMAL(18,2) DEFAULT 0.00,
    overdraft_limit DECIMAL(18,2) DEFAULT 0.00,
    opening_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_branch FOREIGN KEY (branch_id) REFERENCES branches(branch_id),
    CONSTRAINT fk_account_type FOREIGN KEY (account_type_id) REFERENCES account_types(type_id)
);

CREATE TABLE account_owners (
    mapping_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    customer_id INT NOT NULL,
    ownership_type VARCHAR(20) DEFAULT 'PRIMARY', 
    CONSTRAINT fk_mapping_account FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
    CONSTRAINT fk_mapping_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- =====================================================
-- 5. TRANSACTIONS & SCHEDULING
-- =====================================================

CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    destination_account_id INT NULL, 
    transaction_type_id INT NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    description VARCHAR(255),
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    balance_before DECIMAL(18,2) NOT NULL,
    balance_after DECIMAL(18,2) NOT NULL,
    transaction_status VARCHAR(20) DEFAULT 'COMPLETED',
    reference_number VARCHAR(50) UNIQUE,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_transaction_dest FOREIGN KEY (destination_account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_transaction_type FOREIGN KEY (transaction_type_id) REFERENCES transaction_types(type_id)
);

CREATE TABLE scheduled_transactions (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    destination_account_id INT NULL,
    transaction_type_id INT NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    frequency VARCHAR(20) NOT NULL, 
    next_execution_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    CONSTRAINT fk_scheduled_account FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_scheduled_type FOREIGN KEY (transaction_type_id) REFERENCES transaction_types(type_id)
);

-- =====================================================
-- 6. FINANCIAL PRODUCTS (Cards, Loans, Beneficiaries)
-- =====================================================

CREATE TABLE beneficiaries (
    beneficiary_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    beneficiary_name VARCHAR(100) NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    swift_code VARCHAR(20),
    country VARCHAR(50),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_beneficiary_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE loan_information (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    loan_type VARCHAR(50) NOT NULL,
    principal_amount DECIMAL(18,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    loan_term VARCHAR(50) NOT NULL,
    monthly_payment DECIMAL(18,2) NOT NULL,
    outstanding_balance DECIMAL(18,2) NOT NULL,
    loan_status VARCHAR(20) DEFAULT 'ACTIVE',
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loan_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE debit_credit_cards (
    card_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    account_id INT NULL, 
    last_4_digits CHAR(4) NOT NULL,
    card_type VARCHAR(20),
    expiry_date DATE NOT NULL,
    card_status VARCHAR(20) DEFAULT 'ACTIVE',
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_card_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CONSTRAINT fk_card_account FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

-- =====================================================
-- 7. AUDIT & SYSTEM LOGS
-- =====================================================

CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    staff_id INT NULL,
    customer_id INT NULL,
    action VARCHAR(255) NOT NULL,
    ip_address VARCHAR(50),
    device_info VARCHAR(100),
    log_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    log_status VARCHAR(20) DEFAULT 'SUCCESS',
    CONSTRAINT fk_audit_staff FOREIGN KEY (staff_id) REFERENCES staff_information(staff_id),
    CONSTRAINT fk_audit_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);
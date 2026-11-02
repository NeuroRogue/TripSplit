-- ==========================================================
-- TripSplit - Database Schema (MySQL 8.x)
-- Database Name: tripsplit_db
-- ==========================================================

CREATE DATABASE IF NOT EXISTS tripsplit_db;
USE tripsplit_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Trips Table
CREATE TABLE IF NOT EXISTS trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trips_user FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Trip Members Table (Many-to-Many mapping with role)
CREATE TABLE IF NOT EXISTS trip_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(50) DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_members_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    CONSTRAINT fk_members_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_trip_user UNIQUE (trip_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Activities Table (Categories: HOTEL, FOOD, SIGHTSEEING, TRAVEL, SHOPPING, OTHER)
CREATE TABLE IF NOT EXISTS activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    estimated_cost DECIMAL(12, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activities_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Expenses Table
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    activity_id BIGINT NULL,
    title VARCHAR(150) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payer_id BIGINT NOT NULL,
    expense_date DATE NOT NULL,
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_expenses_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    CONSTRAINT fk_expenses_activity FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE SET NULL,
    CONSTRAINT fk_expenses_payer FOREIGN KEY (payer_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Expense Shares Table (Tracks exact share per participant)
CREATE TABLE IF NOT EXISTS expense_shares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    share_amount DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_shares_expense FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_shares_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT uk_expense_user UNIQUE (expense_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Settlements Table
CREATE TABLE IF NOT EXISTS settlements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    payer_id BIGINT NOT NULL,
    payee_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    settlement_date DATE NOT NULL,
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_settlements_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    CONSTRAINT fk_settlements_payer FOREIGN KEY (payer_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_settlements_payee FOREIGN KEY (payee_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

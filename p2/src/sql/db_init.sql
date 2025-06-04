CREATE DATABASE IF NOT EXISTS asset_db;

USE asset_db;

-- 资产信息表
CREATE TABLE IF NOT EXISTS asset_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS assets (
    asset_id VARCHAR(20) PRIMARY KEY,
    asset_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    model VARCHAR(30),
    price DECIMAL(10,2),
    purchase_date DATE,
    status INT DEFAULT 0,
    remark TEXT
);

-- 人员信息表
CREATE TABLE staff (
    staff_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    gender ENUM('男','女'),
    department VARCHAR(30),
    position VARCHAR(30),
    other_info TEXT
);

-- 资产操作流水表
CREATE TABLE IF NOT EXISTS asset_operations (
    operation_id INT PRIMARY KEY AUTO_INCREMENT,
    operation_type ENUM('BORROW', 'RETURN', 'SCRAP') NOT NULL,
    asset_id VARCHAR(20) NOT NULL,
    operator VARCHAR(20),
    operate_time DATETIME NOT NULL,
    purpose VARCHAR(255),
    remark TEXT,
    FOREIGN KEY (asset_id) REFERENCES assets(asset_id)
);

-- 补充人员表性别字段
ALTER TABLE person ADD COLUMN gender VARCHAR(2);

-- 补充操作流水表字段
ALTER TABLE asset_operations 
ADD COLUMN purpose VARCHAR(100),
ADD COLUMN remark VARCHAR(200);
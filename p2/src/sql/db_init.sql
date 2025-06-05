-- 移除CREATE DATABASE语句
-- 创建资产类别表
CREATE TABLE IF NOT EXISTS asset_categories (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name TEXT NOT NULL UNIQUE
);

-- 创建资产表
CREATE TABLE IF NOT EXISTS assets (
    asset_id TEXT PRIMARY KEY,
    asset_name TEXT NOT NULL,
    category TEXT NOT NULL,
    model TEXT,
    price REAL,
    purchase_date TEXT,  -- SQLite使用TEXT存储日期
    status INTEGER DEFAULT 0,
    remark TEXT
);

-- 创建人员表
CREATE TABLE IF NOT EXISTS staff (
    staff_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    gender TEXT,
    department TEXT,
    position TEXT,
    other_info TEXT
);

-- 创建资产操作表
CREATE TABLE IF NOT EXISTS asset_operations (
    operation_id INTEGER PRIMARY KEY AUTOINCREMENT,
    operation_type TEXT NOT NULL,
    asset_id TEXT NOT NULL,
    operator TEXT,
    operate_time TEXT NOT NULL,
    purpose TEXT,
    remark TEXT
);

-- 补充人员表性别字段
ALTER TABLE person ADD COLUMN gender VARCHAR(2);

-- 补充操作流水表字段
ALTER TABLE asset_operations 
ADD COLUMN purpose VARCHAR(100),
ADD COLUMN remark VARCHAR(200);
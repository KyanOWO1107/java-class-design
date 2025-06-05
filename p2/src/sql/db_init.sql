-- 合并表结构定义，移除后续ALTER操作
CREATE TABLE IF NOT EXISTS asset_categories (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name TEXT NOT NULL UNIQUE
);

-- 优化字段类型并添加注释说明
CREATE TABLE IF NOT EXISTS assets (
    asset_id TEXT PRIMARY KEY,
    asset_name TEXT NOT NULL CHECK(LENGTH(asset_name) > 0),
    category TEXT NOT NULL REFERENCES asset_categories(category_name),
    model TEXT,
    price REAL CHECK(price >= 0),
    purchase_date TEXT NOT NULL,
    status INTEGER DEFAULT 0 CHECK(status IN (0,1,2)), -- 0:库存 1:在用 2:报废
    remark TEXT,
    created_time TEXT DEFAULT CURRENT_TIMESTAMP
);

-- 统一人员表结构命名规范
CREATE TABLE IF NOT EXISTS staff (
    staff_id TEXT PRIMARY KEY CHECK(LENGTH(staff_id) = 8),
    name TEXT NOT NULL,
    gender VARCHAR(2) CHECK(gender IN ('男','女')),
    department TEXT NOT NULL,
    position TEXT,
    phone VARCHAR(20) UNIQUE CHECK(LENGTH(phone) >= 11),
    entry_date TEXT NOT NULL
);

-- 增强操作流水表约束
CREATE TABLE IF NOT EXISTS asset_operations (
    operation_id INTEGER PRIMARY KEY AUTOINCREMENT,
    operation_type VARCHAR(10) NOT NULL CHECK(operation_type IN ('入库','领用','维保','报废')),
    asset_id TEXT NOT NULL REFERENCES assets(asset_id),
    operator TEXT REFERENCES staff(staff_id),
    operate_time TEXT DEFAULT CURRENT_TIMESTAMP,
    purpose VARCHAR(100),
    remark VARCHAR(200),
    -- 添加组合索引
    FOREIGN KEY(asset_id) REFERENCES assets(asset_id) ON DELETE CASCADE
);

-- 创建索引提升查询性能
CREATE INDEX idx_asset_operations_asset ON asset_operations(asset_id);
CREATE INDEX idx_asset_operations_operator ON asset_operations(operator);
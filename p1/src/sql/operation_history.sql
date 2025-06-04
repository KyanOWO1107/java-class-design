-- 历史操作记录表需要新增字段
CREATE TABLE operation_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    operation_type VARCHAR(50) NOT NULL,
    operator VARCHAR(50) NOT NULL,
    operation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    emp_id VARCHAR(20) NOT NULL,
    detail TEXT
);
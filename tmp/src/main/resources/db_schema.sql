-- 部门表
CREATE TABLE department (
    id INT PRIMARY KEY AUTO_INCREMENT,
    parent_id INT DEFAULT 0,
    name VARCHAR(50) NOT NULL,
    level INT NOT NULL -- 1:一级部门 2:二级部门
);

-- 员工信息表
CREATE TABLE employee (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    gender ENUM('男','女'),
    birthdate DATE,
    department_id INT,
    salary DECIMAL(10,2),
    address VARCHAR(100),
    FOREIGN KEY (department_id) REFERENCES department(id)
);

-- 操作记录表
CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20),
    operation_type VARCHAR(50),
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    old_data TEXT,
    new_data TEXT,
    FOREIGN KEY (employee_id) REFERENCES employee(id)
);
CREATE TABLE system_user (
    user_id VARCHAR(20) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    emp_id VARCHAR(20),
    role VARCHAR(20),
    last_login DATETIME,
    FOREIGN KEY (emp_id) REFERENCES employee_info(emp_id)
);

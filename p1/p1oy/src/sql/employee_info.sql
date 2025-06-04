CREATE TABLE employee_info (
    emp_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10),
    birth_date DATE,
    nationality VARCHAR(50),
    address VARCHAR(200),
    department_id VARCHAR(20),
    salary DECIMAL(10,2),
    performance VARCHAR(50),
    position VARCHAR(50),
    education VARCHAR(50),
    hire_date DATE,
    phone VARCHAR(20),
    email VARCHAR(100),
    FOREIGN KEY (department_id) REFERENCES department(dept_id)
);

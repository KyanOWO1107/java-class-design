CREATE TABLE department (
    dept_id VARCHAR(20) PRIMARY KEY,
    dept_name VARCHAR(50) NOT NULL,
    parent_dept_id VARCHAR(20),
    FOREIGN KEY (parent_dept_id) REFERENCES department(dept_id)
);

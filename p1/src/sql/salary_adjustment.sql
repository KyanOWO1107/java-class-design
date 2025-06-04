CREATE TABLE salary_adjustment (
    adjustment_id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id VARCHAR(20),
    old_salary DECIMAL(10,2),
    new_salary DECIMAL(10,2),
    adjustment_date DATE,
    reason VARCHAR(200),
    approver VARCHAR(50),
    FOREIGN KEY (emp_id) REFERENCES employee_info(emp_id)
);

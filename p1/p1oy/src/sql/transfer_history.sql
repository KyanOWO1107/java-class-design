CREATE TABLE transfer_history (
    transfer_id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id VARCHAR(20),
    from_dept VARCHAR(20),
    to_dept VARCHAR(20),
    transfer_date DATE,
    reason VARCHAR(200),
    approver VARCHAR(50),
    FOREIGN KEY (emp_id) REFERENCES employee_info(emp_id),
    FOREIGN KEY (from_dept) REFERENCES department(dept_id),
    FOREIGN KEY (to_dept) REFERENCES department(dept_id)
);

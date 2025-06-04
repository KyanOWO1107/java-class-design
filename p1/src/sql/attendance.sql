CREATE TABLE attendance (
    ...
    FOREIGN KEY (emp_id) REFERENCES employee_info(emp_id) ON DELETE CASCADE
);
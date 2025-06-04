CREATE TABLE performance_review (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id VARCHAR(20),
    review_year INT,
    review_result VARCHAR(50),
    reviewer VARCHAR(50),
    review_date DATE,
    comments VARCHAR(500),
    FOREIGN KEY (emp_id) REFERENCES employee_info(emp_id)
);

CREATE TRIGGER log_salary_change 
AFTER UPDATE ON employee_info 
FOR EACH ROW 
BEGIN
    IF OLD.salary <> NEW.salary THEN
        INSERT INTO salary_history (emp_id, old_salary, new_salary, change_date)
        VALUES (NEW.emp_id, OLD.salary, NEW.salary, CURDATE());
    END IF;
END;
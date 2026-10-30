package com.employee.employee_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface EmployeeRepository extends
        JpaRepository<Employee, Integer>,
        JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmail(String email);
}
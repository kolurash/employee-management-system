
package com.employee.employee_management;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepartmentTransferRepository
        extends JpaRepository<DepartmentTransfer, Integer> {

    List<DepartmentTransfer> findByEmployeeId(Integer employeeId);
}

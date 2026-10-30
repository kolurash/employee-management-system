
package com.employee.employee_management;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "department_transfer_history")
public class DepartmentTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transferId;

    private Integer employeeId;

    private Integer previousDepartmentId;

    private Integer newDepartmentId;

    private LocalDate transferDate;

    public DepartmentTransfer() {
    }

    public Integer getTransferId() {
        return transferId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getPreviousDepartmentId() {
        return previousDepartmentId;
    }

    public void setPreviousDepartmentId(Integer previousDepartmentId) {
        this.previousDepartmentId = previousDepartmentId;
    }

    public Integer getNewDepartmentId() {
        return newDepartmentId;
    }

    public void setNewDepartmentId(Integer newDepartmentId) {
        this.newDepartmentId = newDepartmentId;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }
}

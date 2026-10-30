
package com.employee.employee_management;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "employee_code", unique = true)
    private String employeeCode;

    // Full Name
    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100,
          message = "Name must be 3 to 100 characters")
    @Column(name = "full_name")
    private String fullName;

    // Email
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    // Mobile
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$",
            message = "Enter a valid 10-digit Indian mobile number")
    private String mobile;

    // Aadhaar Number
    @NotBlank(message = "Aadhaar number is required")
    @Pattern(regexp = "^[0-9]{12}$",
            message = "Aadhaar must contain exactly 12 digits")
    @Column(name = "aadhaar_number")
    private String aadhaarNumber;

    // PAN Number
    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$",
            message = "Enter a valid PAN number (e.g., ABCDE1234F)")
    @Column(name = "pan_number")
    private String panNumber;

    // Date of Birth
    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Gender
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "MALE|FEMALE|OTHER",
            message = "Gender must be MALE, FEMALE, or OTHER")
    @Column(name = "gender")
    private String gender;

    // Address
    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 250,
          message = "Address must be between 5 and 250 characters")
    @Column(name = "address")
    private String address;

    // Department
    @NotNull(message = "Department is required")
    @Column(name = "department_id")
    private Integer departmentId;

    // Role
    @NotNull(message = "Role is required")
    @Column(name = "role_id")
    private Integer roleId;

    // Joining Date
    @NotNull(message = "Joining date is required")
    @PastOrPresent(message = "Joining date cannot be in the future")
    @Column(name = "joining_date")
    private LocalDate joiningDate;

    // Annual Salary
    @NotNull(message = "Annual salary is required")
    @Positive(message = "Salary must be greater than zero")
    @Column(name = "annual_salary")
    private Double annualSalary;

    // Employee Status
    @NotBlank(message = "Employee status is required")
    @Pattern(regexp = "ACTIVE|INACTIVE",
            message = "Status must be ACTIVE or INACTIVE")
    @Column(name = "employee_status")
    private String employeeStatus;

    // Constructor
    public Employee() {
    }

    // Employee ID
    public Integer getEmployeeId() {
        return employeeId;
    }

    // Employee Code
    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    // Full Name
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Mobile
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    // Aadhaar
    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    // PAN
    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    // Date of Birth
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // Gender
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Address
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Department
    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    // Role
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    // Joining Date
    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    // Annual Salary
    public Double getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(Double annualSalary) {
        this.annualSalary = annualSalary;
    }

    // Employee Status
    public String getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(String employeeStatus) {
        this.employeeStatus = employeeStatus;
    }
}

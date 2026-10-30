
package com.employee.employee_management;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeRepository repository;
    private final EmployeeCodeService codeService;
    private final DepartmentTransferRepository transferRepository;

    public EmployeeController(
            EmployeeRepository repository,
            EmployeeCodeService codeService,
            DepartmentTransferRepository transferRepository) {

        this.repository = repository;
        this.codeService = codeService;
        this.transferRepository = transferRepository;
    }

    // GET ALL + SEARCH + FILTER + SORT + PAGINATION
    // ADMIN, MANAGER, EMPLOYEE
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<?> getAllEmployees(

            @RequestParam(defaultValue = "") String search,

            @RequestParam(required = false)
            Integer departmentId,

            @RequestParam(required = false)
            String employeeStatus,

            // NEW FILTERS
            @RequestParam(required = false)
            Integer roleId,

            @RequestParam(required = false)
            String gender,

            @RequestParam(required = false)
            LocalDate joiningDateFrom,

            @RequestParam(required = false)
            LocalDate joiningDateTo,

            @RequestParam(required = false)
            Double minSalary,

            @RequestParam(required = false)
            Double maxSalary,

            // PAGINATION
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            // SORTING
            @RequestParam(defaultValue = "employeeId")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String sortDir) {

        // VALIDATE DATE RANGE
        if (joiningDateFrom != null
                && joiningDateTo != null
                && joiningDateFrom.isAfter(joiningDateTo)) {

            return ResponseEntity.badRequest()
                    .body("Joining Date From cannot be after Joining Date To");
        }

        // VALIDATE SALARY RANGE
        if (minSalary != null
                && maxSalary != null
                && minSalary > maxSalary) {

            return ResponseEntity.badRequest()
                    .body("Minimum salary cannot be greater than maximum salary");
        }

        // ALLOWED SORT FIELDS
        List<String> allowedSortFields = List.of(
                "employeeId",
                "fullName",
                "email",
                "joiningDate",
                "annualSalary",
                "employeeStatus"
        );

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "employeeId";
        }

        // SORT DIRECTION
        Sort.Direction direction =
                sortDir.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        // PAGINATION
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(1, Math.min(size, 100)),
                Sort.by(direction, sortBy)
        );

        // BASE SPECIFICATION
        Specification<Employee> specification =
                (root, query, cb) -> cb.conjunction();

        // SEARCH FILTER
        if (search != null && !search.isBlank()) {

            String keyword = "%" + search.toLowerCase() + "%";

            Specification<Employee> searchSpec =
                    (root, query, cb) -> cb.or(

                            cb.like(
                                    cb.lower(root.get("fullName")),
                                    keyword
                            ),

                            cb.like(
                                    cb.lower(root.get("email")),
                                    keyword
                            ),

                            cb.like(
                                    cb.lower(root.get("employeeCode")),
                                    keyword
                            ),

                            cb.like(
                                    root.get("mobile"),
                                    "%" + search + "%"
                            )
                    );

            specification = specification.and(searchSpec);
        }

        // DEPARTMENT FILTER
        if (departmentId != null) {

            Specification<Employee> departmentSpec =
                    (root, query, cb) ->
                            cb.equal(
                                    root.get("departmentId"),
                                    departmentId
                            );

            specification = specification.and(departmentSpec);
        }

        // EMPLOYEE STATUS FILTER
        if (employeeStatus != null
                && !employeeStatus.isBlank()) {

            Specification<Employee> statusSpec =
                    (root, query, cb) ->
                            cb.equal(
                                    root.get("employeeStatus"),
                                    employeeStatus
                            );

            specification = specification.and(statusSpec);
        }

        // ROLE FILTER
        if (roleId != null) {

            Specification<Employee> roleSpec =
                    (root, query, cb) ->
                            cb.equal(
                                    root.get("roleId"),
                                    roleId
                            );

            specification = specification.and(roleSpec);
        }

        // GENDER FILTER
        if (gender != null && !gender.isBlank()) {

            Specification<Employee> genderSpec =
                    (root, query, cb) ->
                            cb.equal(
                                    cb.upper(root.get("gender")),
                                    gender.toUpperCase()
                            );

            specification = specification.and(genderSpec);
        }

        // JOINING DATE FROM
        if (joiningDateFrom != null) {

            Specification<Employee> joiningFromSpec =
                    (root, query, cb) ->
                            cb.greaterThanOrEqualTo(
                                    root.get("joiningDate"),
                                    joiningDateFrom
                            );

            specification = specification.and(joiningFromSpec);
        }

        // JOINING DATE TO
        if (joiningDateTo != null) {

            Specification<Employee> joiningToSpec =
                    (root, query, cb) ->
                            cb.lessThanOrEqualTo(
                                    root.get("joiningDate"),
                                    joiningDateTo
                            );

            specification = specification.and(joiningToSpec);
        }

        // MINIMUM SALARY
        if (minSalary != null) {

            Specification<Employee> minSalarySpec =
                    (root, query, cb) ->
                            cb.greaterThanOrEqualTo(
                                    root.<Double>get("annualSalary"),
                                    minSalary
                            );

            specification = specification.and(minSalarySpec);
        }

        // MAXIMUM SALARY
        if (maxSalary != null) {

            Specification<Employee> maxSalarySpec =
                    (root, query, cb) ->
                            cb.lessThanOrEqualTo(
                                    root.<Double>get("annualSalary"),
                                    maxSalary
                            );

            specification = specification.and(maxSalarySpec);
        }

        // RETURN FILTERED EMPLOYEES
        Page<Employee> employees =
                repository.findAll(specification, pageable);

        return ResponseEntity.ok(employees);
    }

    // GET EMPLOYEE BY ID
    // ADMIN, MANAGER, EMPLOYEE
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<Employee> getEmployee(
            @PathVariable Integer id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE EMPLOYEE
    // ADMIN, MANAGER
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Employee createEmployee(
            @RequestBody Employee employee) {

        employee.setEmployeeCode(
                codeService.generateEmployeeCode(
                        employee.getDepartmentId()
                )
        );

        return repository.save(employee);
    }

    // UPDATE EMPLOYEE
    // ADMIN, MANAGER
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Employee updateEmployee(
            @PathVariable Integer id,
            @RequestBody Employee updatedEmployee) {

        Employee employee = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        employee.setFullName(updatedEmployee.getFullName());
        employee.setEmail(updatedEmployee.getEmail());
        employee.setMobile(updatedEmployee.getMobile());

        employee.setDepartmentId(
                updatedEmployee.getDepartmentId()
        );

        employee.setRoleId(
                updatedEmployee.getRoleId()
        );

        employee.setJoiningDate(
                updatedEmployee.getJoiningDate()
        );

        employee.setAnnualSalary(
                updatedEmployee.getAnnualSalary()
        );

        employee.setEmployeeStatus(
                updatedEmployee.getEmployeeStatus()
        );

        // NEW EMPLOYEE DETAILS
        employee.setAadhaarNumber(
                updatedEmployee.getAadhaarNumber()
        );

        employee.setPanNumber(
                updatedEmployee.getPanNumber()
        );

        employee.setDateOfBirth(
                updatedEmployee.getDateOfBirth()
        );

        employee.setGender(
                updatedEmployee.getGender()
        );

        employee.setAddress(
                updatedEmployee.getAddress()
        );

        return repository.save(employee);
    }

    // DELETE EMPLOYEE
    // ADMIN ONLY
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Integer id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.ok(
                "Employee deleted successfully"
        );
    }

    // TRANSFER EMPLOYEE
    // ADMIN, MANAGER
    @PutMapping("/{id}/transfer")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<String> transferEmployee(
            @PathVariable Integer id,
            @RequestParam Integer newDepartmentId) {

        Employee employee = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        Integer previousDepartmentId =
                employee.getDepartmentId();

        if (previousDepartmentId.equals(newDepartmentId)) {

            return ResponseEntity.badRequest()
                    .body("Employee is already in this department");
        }

        DepartmentTransfer transfer = new DepartmentTransfer();

        transfer.setEmployeeId(id);

        transfer.setPreviousDepartmentId(
                previousDepartmentId
        );

        transfer.setNewDepartmentId(
                newDepartmentId
        );

        transfer.setTransferDate(
                LocalDate.now()
        );

        transferRepository.save(transfer);

        employee.setDepartmentId(newDepartmentId);

        repository.save(employee);

        return ResponseEntity.ok(
                "Employee transferred successfully"
        );
    }

    // GET EMPLOYEE TRANSFER HISTORY
    // ADMIN, MANAGER
    @GetMapping("/{id}/transfer-history")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<DepartmentTransfer> getTransferHistory(
            @PathVariable Integer id) {

        return transferRepository.findByEmployeeId(id);
    }
}

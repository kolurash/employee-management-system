
package com.employee.employee_management;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            AppUserRepository userRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create login users
            createUser(userRepository, passwordEncoder,
                    "admin@example.com", "Admin@123", "ADMIN");

            createUser(userRepository, passwordEncoder,
                    "manager@example.com", "Manager@123", "MANAGER");

            createUser(userRepository, passwordEncoder,
                    "employee@example.com", "Employee@123", "EMPLOYEE");

            // Create 20 employees
            createEmployee(employeeRepository,
                    "EMP001", "Rahul Sharma",
                    "rahul@example.com", "9876543210",
                    1, 3, LocalDate.of(2024, 1, 10), 450000);

            createEmployee(employeeRepository,
                    "EMP002", "Priya Reddy",
                    "priya.reddy@example.com", "9876543211",
                    1, 3, LocalDate.of(2024, 2, 15), 500000);

            createEmployee(employeeRepository,
                    "EMP003", "Arjun Kumar",
                    "arjun@example.com", "9876543212",
                    1, 3, LocalDate.of(2024, 3, 20), 550000);

            createEmployee(employeeRepository,
                    "EMP004", "Sneha Rao",
                    "sneha@example.com", "9876543213",
                    1, 3, LocalDate.of(2024, 4, 5), 480000);

            createEmployee(employeeRepository,
                    "EMP005", "Kiran Reddy",
                    "kiran@example.com", "9876543214",
                    2, 3, LocalDate.of(2024, 1, 12), 400000);

            createEmployee(employeeRepository,
                    "EMP006", "Ananya Sharma",
                    "ananya@example.com", "9876543215",
                    2, 3, LocalDate.of(2024, 2, 18), 420000);

            createEmployee(employeeRepository,
                    "EMP007", "Vikram Rao",
                    "vikram@example.com", "9876543216",
                    2, 3, LocalDate.of(2024, 3, 22), 460000);

            createEmployee(employeeRepository,
                    "EMP008", "Meghana Kumar",
                    "meghana@example.com", "9876543217",
                    2, 3, LocalDate.of(2024, 4, 8), 430000);

            createEmployee(employeeRepository,
                    "EMP009", "Sandeep Reddy",
                    "sandeep@example.com", "9876543218",
                    3, 3, LocalDate.of(2024, 1, 14), 600000);

            createEmployee(employeeRepository,
                    "EMP010", "Divya Sharma",
                    "divya@example.com", "9876543219",
                    3, 3, LocalDate.of(2024, 2, 20), 620000);

            createEmployee(employeeRepository,
                    "EMP011", "Naveen Kumar",
                    "naveen@example.com", "9876543220",
                    3, 3, LocalDate.of(2024, 3, 24), 580000);

            createEmployee(employeeRepository,
                    "EMP012", "Pooja Reddy",
                    "pooja@example.com", "9876543221",
                    3, 3, LocalDate.of(2024, 4, 10), 610000);

            createEmployee(employeeRepository,
                    "EMP013", "Ramesh Kumar",
                    "ramesh@example.com", "9876543222",
                    4, 3, LocalDate.of(2024, 1, 16), 450000);

            createEmployee(employeeRepository,
                    "EMP014", "Swathi Rao",
                    "swathi@example.com", "9876543223",
                    4, 3, LocalDate.of(2024, 2, 22), 470000);

            createEmployee(employeeRepository,
                    "EMP015", "Abhinav Sharma",
                    "abhinav@example.com", "9876543224",
                    4, 3, LocalDate.of(2024, 3, 26), 490000);

            createEmployee(employeeRepository,
                    "EMP016", "Keerthi Reddy",
                    "keerthi@example.com", "9876543225",
                    4, 3, LocalDate.of(2024, 4, 12), 460000);

            createEmployee(employeeRepository,
                    "EMP017", "Mahesh Kumar",
                    "mahesh@example.com", "9876543226",
                    5, 3, LocalDate.of(2024, 1, 18), 500000);

            createEmployee(employeeRepository,
                    "EMP018", "Lakshmi Rao",
                    "lakshmi@example.com", "9876543227",
                    5, 3, LocalDate.of(2024, 2, 24), 520000);

            createEmployee(employeeRepository,
                    "EMP019", "Tarun Reddy",
                    "tarun@example.com", "9876543228",
                    5, 3, LocalDate.of(2024, 3, 28), 530000);

            createEmployee(employeeRepository,
                    "EMP020", "Harika Sharma",
                    "harika@example.com", "9876543229",
                    5, 3, LocalDate.of(2024, 4, 14), 510000);
        };
    }

    
     private void createUser(AppUserRepository userRepository, PasswordEncoder passwordEncoder,
                            String email, String password, String role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            AppUser user = new AppUser();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            userRepository.save(user);
        }
    }

    private void createEmployee(
        EmployeeRepository repository,
        String employeeCode,
        String fullName,
        String email,
        String mobile,
        Integer departmentId,
        Integer roleId,
        LocalDate joiningDate,
        double annualSalary) {

    if (repository.findByEmail(email).isEmpty()) {

        Employee employee = new Employee();

        employee.setEmployeeCode(employeeCode);
        employee.setFullName(fullName);
        employee.setEmail(email);
        employee.setMobile(mobile);
        employee.setDepartmentId(departmentId);
        employee.setRoleId(roleId);
        employee.setJoiningDate(joiningDate);
        employee.setAnnualSalary(annualSalary);
        employee.setEmployeeStatus("ACTIVE");

        employee.setAadhaarNumber("123456789012");
        employee.setPanNumber("ABCDE1234F");
        employee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        employee.setGender("OTHER");
        employee.setAddress("Hyderabad, Telangana");

        repository.save(employee);
    }
}
}

const API = "http://localhost:8080/api";

let token = "";
let currentRole = "";
let currentEmail = "";

let currentPage = 0;
let totalPages = 0;

let sortBy = "employeeId";
let sortDir = "asc";

const loginPage = document.getElementById("loginPage");
const dashboard = document.getElementById("dashboard");
const employeeTable = document.getElementById("employeeTable");

const today = new Date().toISOString().split("T")[0];


// DATE VALIDATION
document.getElementById("dateOfBirth").max = today;
document.getElementById("joiningDate").max = today;


// AUTH HEADERS
function authHeaders() {
    return {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token
    };
}


// LOGIN
document.getElementById("loginForm").addEventListener("submit", async (e) => {

    e.preventDefault();

    const loginMessage = document.getElementById("loginMessage");

    const loginButton = document.querySelector(
        "#loginForm button[type='submit']"
    );

    loginMessage.textContent = "Logging in...";

    if (loginButton) {
        loginButton.disabled = true;
    }

    try {

        const response = await fetch(`${API}/auth/login`, {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({

                email: document.getElementById("email").value.trim(),

                password: document.getElementById("password").value

            })

        });

        const text = await response.text();

        let data = {};

        try {

            data = text ? JSON.parse(text) : {};

        } catch {

            throw new Error("Invalid response from backend");

        }

        if (!response.ok) {

            throw new Error(
                data.message || "Invalid email or password"
            );

        }

        if (!data.token) {

            throw new Error(
                "Login successful, but token is missing"
            );

        }

        token = data.token;

        currentRole = (data.role || "").toUpperCase();

        currentEmail = data.email ||
            document.getElementById("email").value.trim();

        if (!currentRole) {

            throw new Error("Role missing in login response");

        }

        loginPage.hidden = true;

        dashboard.hidden = false;

        document.getElementById("userInfo").textContent =
            `${currentEmail} (${currentRole})`;

        document.getElementById("addEmployeeBtn").hidden =
            !["ADMIN", "MANAGER"].includes(currentRole);

        loginMessage.textContent = "";

        currentPage = 0;

        await loadEmployees();

    } catch (error) {

        loginMessage.textContent = error.message;

        console.error("Login error:", error);

    } finally {

        if (loginButton) {

            loginButton.disabled = false;

        }

    }

});


// LOAD EMPLOYEES
async function loadEmployees() {

    try {

        const search =
            document.getElementById("search").value.trim();

        const department =
            document.getElementById("departmentFilter").value;

        const status =
            document.getElementById("statusFilter").value;

        const role =
            document.getElementById("roleFilter").value;

        const gender =
            document.getElementById("genderFilter").value;

        const joiningDateFrom =
            document.getElementById("joiningDateFrom").value;

        const joiningDateTo =
            document.getElementById("joiningDateTo").value;

        const minSalary =
            document.getElementById("minSalary").value;

        const maxSalary =
            document.getElementById("maxSalary").value;


        // VALIDATE FILTER RANGES

        if (joiningDateFrom && joiningDateTo &&
            joiningDateFrom > joiningDateTo) {

            document.getElementById("message").textContent =
                "Joining From date cannot be after Joining To date";

            return;

        }

        if (minSalary !== "" && maxSalary !== "" &&
            Number(minSalary) > Number(maxSalary)) {

            document.getElementById("message").textContent =
                "Minimum salary cannot be greater than maximum salary";

            return;

        }


        // QUERY PARAMETERS

        const params = new URLSearchParams({

            search: search,

            page: currentPage,

            size: 10,

            sortBy: sortBy,

            sortDir: sortDir

        });


        // DEPARTMENT FILTER

        if (department) {

            params.append("departmentId", department);

        }


        // STATUS FILTER

        if (status) {

            params.append("employeeStatus", status);

        }


        // ROLE FILTER

        if (role) {

            params.append("roleId", role);

        }


        // GENDER FILTER

        if (gender) {

            params.append("gender", gender);

        }


        // JOINING DATE FROM

        if (joiningDateFrom) {

            params.append("joiningDateFrom", joiningDateFrom);

        }


        // JOINING DATE TO

        if (joiningDateTo) {

            params.append("joiningDateTo", joiningDateTo);

        }


        // MINIMUM SALARY

        if (minSalary !== "") {

            params.append("minSalary", minSalary);

        }


        // MAXIMUM SALARY

        if (maxSalary !== "") {

            params.append("maxSalary", maxSalary);

        }


        // API REQUEST

        const response = await fetch(

            `${API}/employees?${params.toString()}`,

            {

                headers: authHeaders()

            }

        );


        if (!response.ok) {

            throw new Error("Unable to load employees");

        }


        const data = await response.json();


        totalPages = data.totalPages || 0;

        const employees = data.content || [];


        // TOTAL EMPLOYEES

        document.getElementById("totalEmployees").textContent =
            data.totalElements || 0;


        // ACTIVE EMPLOYEES

        document.getElementById("activeEmployees").textContent =

            employees.filter(e =>

                (e.employeeStatus || "").toUpperCase() === "ACTIVE"

            ).length;


        // INACTIVE EMPLOYEES

        document.getElementById("inactiveEmployees").textContent =

            employees.filter(e =>

                (e.employeeStatus || "").toUpperCase() === "INACTIVE"

            ).length;


        // CLEAR TABLE

        employeeTable.innerHTML = "";


        // DISPLAY EMPLOYEES

        employees.forEach(employee => {

            const row = document.createElement("tr");


            row.innerHTML = `

                <td>${employee.employeeId ?? ""}</td>

                <td>${employee.employeeCode ?? ""}</td>

                <td>${employee.fullName ?? ""}</td>

                <td>${employee.email ?? ""}</td>

                <td>${employee.mobile ?? ""}</td>

                <td>${departmentName(employee.departmentId)}</td>

                <td>${employee.annualSalary ?? ""}</td>

                <td>${employee.employeeStatus ?? ""}</td>

                <td>

                    ${
                        ["ADMIN", "MANAGER"].includes(currentRole)

                        ? `<button onclick="editEmployee(${employee.employeeId})">
                               Edit
                           </button>`

                        : ""
                    }

                    ${
                        currentRole === "ADMIN"

                        ? `<button onclick="deleteEmployee(${employee.employeeId})">
                               Delete
                           </button>`

                        : ""
                    }

                    ${
                        ["ADMIN", "MANAGER"].includes(currentRole)

                        ? `<button onclick="transferEmployee(${employee.employeeId})">
                               Transfer
                           </button>

                           <button onclick="transferHistory(${employee.employeeId})">
                               History
                           </button>`

                        : ""
                    }

                </td>

            `;


            employeeTable.appendChild(row);

        });


        // PAGINATION INFO

        document.getElementById("pageInfo").textContent =

            `Page ${currentPage + 1} of ${Math.max(totalPages, 1)}`;


        document.getElementById("message").textContent = "";


    } catch (error) {

        document.getElementById("message").textContent =
            error.message;

        console.error("Load employees error:", error);

    }

}


// DEPARTMENT NAME
function departmentName(id) {

    const names = {

        1: "IT",

        2: "HR",

        3: "Finance",

        4: "Sales",

        5: "Admin"

    };

    return names[id] || "Unknown";

}


// SHOW FORM
function showForm() {

    document.getElementById("employeeForm").reset();

    document.getElementById("employeeId").value = "";

    document.getElementById("formTitle").textContent =
        "Add Employee";

    document.getElementById("employeeFormContainer").hidden = false;

    document.getElementById("dateOfBirth").max = today;

    document.getElementById("joiningDate").max = today;

}


// HIDE FORM
function hideForm() {

    document.getElementById("employeeFormContainer").hidden = true;

}


// CREATE OR UPDATE EMPLOYEE
document.getElementById("employeeForm").addEventListener("submit", async (e) => {

    e.preventDefault();

    const id = document.getElementById("employeeId").value;


    // EMPLOYEE DATA

    const employee = {

        fullName:
            document.getElementById("fullName").value.trim(),

        email:
            document.getElementById("employeeEmail").value.trim(),

        mobile:
            document.getElementById("mobile").value.trim(),

        // AADHAAR

        aadhaarNumber:
            document.getElementById("aadhaarNumber").value.trim(),

        // PAN

        panNumber:
            document.getElementById("panNumber").value.trim().toUpperCase(),

        // DOB

        dateOfBirth:
            document.getElementById("dateOfBirth").value,

        // GENDER

        gender:
            document.getElementById("gender").value,

        // ADDRESS

        address:
            document.getElementById("address").value.trim(),

        // DEPARTMENT

        departmentId:
            Number(document.getElementById("departmentId").value),

        // ROLE

        roleId:
            Number(document.getElementById("roleId").value),

        // JOINING DATE

        joiningDate:
            document.getElementById("joiningDate").value,

        // SALARY

        annualSalary:
            Number(document.getElementById("annualSalary").value),

        // STATUS

        employeeStatus:
            document.getElementById("employeeStatus").value

    };


    // AADHAAR VALIDATION

    if (!/^[0-9]{12}$/.test(employee.aadhaarNumber)) {

        document.getElementById("message").textContent =
            "Aadhaar must contain exactly 12 digits";

        return;

    }


    // PAN VALIDATION

    if (!/^[A-Z]{5}[0-9]{4}[A-Z]$/.test(employee.panNumber)) {

        document.getElementById("message").textContent =
            "Enter a valid PAN number (ABCDE1234F)";

        return;

    }


    // DOB VALIDATION

    if (!employee.dateOfBirth) {

        document.getElementById("message").textContent =
            "Date of birth is required";

        return;

    }

    if (employee.dateOfBirth > today) {

        document.getElementById("message").textContent =
            "Date of birth cannot be in the future";

        return;

    }


    // GENDER VALIDATION

    if (!["MALE", "FEMALE", "OTHER"].includes(employee.gender)) {

        document.getElementById("message").textContent =
            "Please select a valid gender";

        return;

    }


    // ADDRESS VALIDATION

    if (employee.address.length < 5 ||
        employee.address.length > 250) {

        document.getElementById("message").textContent =
            "Address must be between 5 and 250 characters";

        return;

    }


    // JOINING DATE VALIDATION

    if (employee.joiningDate > today) {

        document.getElementById("message").textContent =
            "Joining date cannot be in the future";

        return;

    }


    try {

        const response = await fetch(

            id

                ? `${API}/employees/${id}`

                : `${API}/employees`,

            {

                method: id ? "PUT" : "POST",

                headers: authHeaders(),

                body: JSON.stringify(employee)

            }

        );


        if (!response.ok) {

            const text = await response.text();

            throw new Error(text || "Unable to save employee");

        }


        document.getElementById("message").textContent =
            "Employee saved successfully";


        hideForm();

        await loadEmployees();


    } catch (error) {

        document.getElementById("message").textContent =
            error.message;

    }

});


// EDIT EMPLOYEE
async function editEmployee(id) {

    try {

        const response = await fetch(

            `${API}/employees/${id}`,

            {

                headers: authHeaders()

            }

        );


        if (!response.ok) {

            throw new Error("Unable to get employee");

        }


        const e = await response.json();


        // EXISTING FIELDS

        document.getElementById("employeeId").value =
            e.employeeId ?? "";

        document.getElementById("fullName").value =
            e.fullName ?? "";

        document.getElementById("employeeEmail").value =
            e.email ?? "";

        document.getElementById("mobile").value =
            e.mobile ?? "";


        // NEW FIELDS

        document.getElementById("aadhaarNumber").value =
            e.aadhaarNumber ?? "";

        document.getElementById("panNumber").value =
            e.panNumber ?? "";

        document.getElementById("dateOfBirth").value =
            e.dateOfBirth ?? "";

        document.getElementById("gender").value =
            e.gender ?? "";

        document.getElementById("address").value =
            e.address ?? "";


        // REMAINING FIELDS

        document.getElementById("departmentId").value =
            e.departmentId ?? "";

        document.getElementById("roleId").value =
            e.roleId ?? "";

        document.getElementById("joiningDate").value =
            e.joiningDate ?? "";

        document.getElementById("annualSalary").value =
            e.annualSalary ?? "";

        document.getElementById("employeeStatus").value =
            e.employeeStatus ?? "";


        document.getElementById("formTitle").textContent =
            "Edit Employee";

        document.getElementById("employeeFormContainer").hidden = false;


        // DATE RESTRICTIONS

        document.getElementById("dateOfBirth").max = today;

        document.getElementById("joiningDate").max = today;


    } catch (error) {

        alert(error.message);

    }

}


// DELETE EMPLOYEE
async function deleteEmployee(id) {

    if (!confirm("Are you sure you want to delete this employee?")) {

        return;

    }


    try {

        const response = await fetch(

            `${API}/employees/${id}`,

            {

                method: "DELETE",

                headers: authHeaders()

            }

        );


        if (!response.ok) {

            throw new Error("Unable to delete employee");

        }


        document.getElementById("message").textContent =
            "Employee deleted successfully";


        await loadEmployees();


    } catch (error) {

        alert(error.message);

    }

}


// TRANSFER EMPLOYEE
async function transferEmployee(id) {

    const department = prompt(

        "Enter new department ID:\n1 IT\n2 HR\n3 Finance\n4 Sales\n5 Admin"

    );


    if (!department) {

        return;

    }


    if (!["1", "2", "3", "4", "5"].includes(department)) {

        alert("Invalid department ID");

        return;

    }


    try {

        const response = await fetch(

            `${API}/employees/${id}/transfer?newDepartmentId=${department}`,

            {

                method: "PUT",

                headers: authHeaders()

            }

        );


        const result = await response.text();


        if (!response.ok) {

            throw new Error(result);

        }


        alert(result);

        await loadEmployees();


    } catch (error) {

        alert(error.message);

    }

}


// TRANSFER HISTORY
async function transferHistory(id) {

    try {

        const response = await fetch(

            `${API}/employees/${id}/transfer-history`,

            {

                headers: authHeaders()

            }

        );


        if (!response.ok) {

            throw new Error("Unable to load history");

        }


        const history = await response.json();


        if (!history.length) {

            alert("No transfer history");

            return;

        }


        const text = history.map(h =>

            `From ${departmentName(h.previousDepartmentId)} to ` +

            `${departmentName(h.newDepartmentId)} on ${h.transferDate}`

        ).join("\n");


        alert(text);


    } catch (error) {

        alert(error.message);

    }

}


// SORT EMPLOYEES
function sortEmployees(column) {

    if (sortBy === column) {

        sortDir = sortDir === "asc" ? "desc" : "asc";

    } else {

        sortBy = column;

        sortDir = "asc";

    }


    currentPage = 0;

    loadEmployees();

}


// PAGINATION
function previousPage() {

    if (currentPage > 0) {

        currentPage--;

        loadEmployees();

    }

}


function nextPage() {

    if (currentPage + 1 < totalPages) {

        currentPage++;

        loadEmployees();

    }

}


// CLEAR FILTERS
function clearFilters() {

    document.getElementById("search").value = "";

    document.getElementById("departmentFilter").value = "";

    document.getElementById("statusFilter").value = "";

    document.getElementById("roleFilter").value = "";

    document.getElementById("genderFilter").value = "";

    document.getElementById("joiningDateFrom").value = "";

    document.getElementById("joiningDateTo").value = "";

    document.getElementById("minSalary").value = "";

    document.getElementById("maxSalary").value = "";


    currentPage = 0;

    document.getElementById("message").textContent = "";

    loadEmployees();

}


// LOGOUT
function logout() {

    token = "";

    currentRole = "";

    currentEmail = "";


    dashboard.hidden = true;

    loginPage.hidden = false;


    document.getElementById("loginForm").reset();

    document.getElementById("loginMessage").textContent = "";


    document.getElementById("employeeForm").reset();

    hideForm();

}

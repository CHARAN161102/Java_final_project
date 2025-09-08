package com.charan.pdfreport.data;

import com.charan.pdfreport.model.Employee;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages employee data - can read from CSV or provide sample data
 */
public class EmployeeDataManager {

    private List<Employee> employees;

    public EmployeeDataManager() {
        this.employees = new ArrayList<>();
        loadSampleData(); // Load sample data by default
    }

    /**
     * Load sample employee data for demonstration
     */
    private void loadSampleData() {
        employees.add(new Employee(1001, "John", "Smith", "john.smith@company.com",
                "Engineering", "Software Engineer", 75000.0, LocalDate.of(2022, 3, 15),
                "+1-555-0123", "123 Main St, Anytown, USA"));

        employees.add(new Employee(1002, "Sarah", "Johnson", "sarah.johnson@company.com",
                "Marketing", "Marketing Manager", 68000.0, LocalDate.of(2021, 8, 22),
                "+1-555-0124", "456 Oak Ave, Somewhere, USA"));

        employees.add(new Employee(1003, "Michael", "Brown", "michael.brown@company.com",
                "Finance", "Financial Analyst", 62000.0, LocalDate.of(2023, 1, 10),
                "+1-555-0125", "789 Pine Rd, Anywhere, USA"));

        employees.add(new Employee(1004, "Emily", "Davis", "emily.davis@company.com",
                "HR", "HR Specialist", 58000.0, LocalDate.of(2022, 11, 5),
                "+1-555-0126", "321 Elm St, Nowhere, USA"));

        employees.add(new Employee(1005, "David", "Wilson", "david.wilson@company.com",
                "Engineering", "Senior Developer", 85000.0, LocalDate.of(2020, 6, 1),
                "+1-555-0127", "654 Maple Dr, Everytown, USA"));

        employees.add(new Employee(1006, "Lisa", "Martinez", "lisa.martinez@company.com",
                "Sales", "Sales Representative", 55000.0, LocalDate.of(2023, 4, 18),
                "+1-555-0128", "987 Cedar Ln, Hometown, USA"));

        employees.add(new Employee(1007, "Robert", "Garcia", "robert.garcia@company.com",
                "Engineering", "DevOps Engineer", 78000.0, LocalDate.of(2021, 12, 8),
                "+1-555-0129", "147 Birch Ct, Yourtown, USA"));

        employees.add(new Employee(1008, "Jennifer", "Taylor", "jennifer.taylor@company.com",
                "Marketing", "Content Specialist", 52000.0, LocalDate.of(2023, 2, 14),
                "+1-555-0130", "258 Spruce Way, Mytown, USA"));

        employees.add(new Employee(1009, "Chris", "Anderson", "chris.anderson@company.com",
                "Finance", "Senior Accountant", 70000.0, LocalDate.of(2022, 7, 30),
                "+1-555-0131", "369 Fir Blvd, Ourtown, USA"));

        employees.add(new Employee(1010, "Amanda", "Thomas", "amanda.thomas@company.com",
                "HR", "HR Manager", 72000.0, LocalDate.of(2021, 4, 12),
                "+1-555-0132", "741 Ash St, Thistown, USA"));
    }

    /**
     * Load employee data from CSV file
     * CSV format: id,firstName,lastName,email,department,position,salary,hireDate,phone,address
     */
    public void loadFromCSV(String filename) throws IOException {
        employees.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }

                String[] values = line.split(",");
                if (values.length >= 10) {
                    Employee employee = new Employee(
                            Integer.parseInt(values[0].trim()),
                            values[1].trim(),
                            values[2].trim(),
                            values[3].trim(),
                            values[4].trim(),
                            values[5].trim(),
                            Double.parseDouble(values[6].trim()),
                            LocalDate.parse(values[7].trim(), formatter),
                            values[8].trim(),
                            values[9].trim()
                    );
                    employees.add(employee);
                }
            }
        }
    }

    /**
     * Get all employees
     */
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }

    /**
     * Get employees by department
     */
    public List<Employee> getEmployeesByDepartment(String department) {
        return employees.stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    /**
     * Get employees with salary above threshold
     */
    public List<Employee> getHighEarners(double salaryThreshold) {
        return employees.stream()
                .filter(emp -> emp.getSalary() > salaryThreshold)
                .collect(Collectors.toList());
    }

    /**
     * Get employees hired in a specific year
     */
    public List<Employee> getEmployeesHiredInYear(int year) {
        return employees.stream()
                .filter(emp -> emp.getHireDate().getYear() == year)
                .collect(Collectors.toList());
    }

    /**
     * Get unique departments
     */
    public List<String> getUniqueDepartments() {
        return employees.stream()
                .map(Employee::getDepartment)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get employee count
     */
    public int getEmployeeCount() {
        return employees.size();
    }

    /**
     * Get average salary
     */
    public double getAverageSalary() {
        return employees.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }
}
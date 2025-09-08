package com.charan.pdfreport;

import com.charan.pdfreport.data.EmployeeDataManager;
import com.charan.pdfreport.generator.PDFReportGenerator;
import com.charan.pdfreport.model.Employee;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for Employee PDF Report Generator
 * Demonstrates various report generation capabilities
 */
public class EmployeeReportApp {

    private static final String OUTPUT_DIR = "generated_reports";
    private static EmployeeDataManager dataManager;
    private static PDFReportGenerator pdfGenerator;
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("=== Employee PDF Report Generator ===");
        System.out.println("Initializing application...\n");

        // Initialize components
        dataManager = new EmployeeDataManager();
        pdfGenerator = new PDFReportGenerator();
        scanner = new Scanner(System.in);

        // Create output directory
        createOutputDirectory();

        // Display menu
        displayMenu();

        scanner.close();
    }

    /**
     * Create output directory for generated reports
     */
    private static void createOutputDirectory() {
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("Created output directory: " + OUTPUT_DIR);
            } else {
                System.err.println("Failed to create output directory");
                System.exit(1);
            }
        }
        System.out.println("Reports will be saved to: " + dir.getAbsolutePath() + "\n");
    }

    /**
     * Display interactive menu
     */
    private static void displayMenu() {
        while (true) {
            System.out.println("\n=== Report Generation Menu ===");
            System.out.println("1. Generate Complete Employee Report");
            System.out.println("2. Generate Department Report");
            System.out.println("3. Generate Salary Analysis Report");
            System.out.println("4. Generate All Reports (Batch)");
            System.out.println("5. View Employee Statistics");
            System.out.println("6. Load Custom CSV Data");
            System.out.println("7. Exit");
            System.out.print("\nEnter your choice (1-7): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        generateCompleteReport();
                        break;
                    case 2:
                        generateDepartmentReport();
                        break;
                    case 3:
                        generateSalaryReport();
                        break;
                    case 4:
                        generateBatchReports();
                        break;
                    case 5:
                        viewStatistics();
                        break;
                    case 6:
                        loadCustomData();
                        break;
                    case 7:
                        System.out.println("Thank you for using Employee PDF Report Generator!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1-7.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Generate complete employee report
     */
    private static void generateCompleteReport() {
        try {
            System.out.println("\nGenerating complete employee report...");
            List<Employee> allEmployees = dataManager.getAllEmployees();
            pdfGenerator.generateEmployeeReport(allEmployees, OUTPUT_DIR);
            System.out.println("✓ Complete employee report generated successfully!");
        } catch (IOException e) {
            System.err.println("Error generating complete report: " + e.getMessage());
        }
    }

    /**
     * Generate department-specific report
     */
    private static void generateDepartmentReport() {
        try {
            // Display available departments
            List<String> departments = dataManager.getUniqueDepartments();
            System.out.println("\nAvailable Departments:");
            for (int i = 0; i < departments.size(); i++) {
                System.out.println((i + 1) + ". " + departments.get(i));
            }

            System.out.print("Select department (1-" + departments.size() + "): ");
            int deptChoice = Integer.parseInt(scanner.nextLine()) - 1;

            if (deptChoice >= 0 && deptChoice < departments.size()) {
                String selectedDept = departments.get(deptChoice);
                List<Employee> deptEmployees = dataManager.getEmployeesByDepartment(selectedDept);

                System.out.println("Generating report for " + selectedDept + " department...");
                pdfGenerator.generateDepartmentReport(deptEmployees, selectedDept, OUTPUT_DIR);
                System.out.println("✓ Department report generated successfully!");
            } else {
                System.out.println("Invalid department selection.");
            }
        } catch (Exception e) {
            System.err.println("Error generating department report: " + e.getMessage());
        }
    }

    /**
     * Generate salary analysis report
     */
    private static void generateSalaryReport() {
        try {
            System.out.println("\nGenerating salary analysis report...");
            List<Employee> allEmployees = dataManager.getAllEmployees();
            pdfGenerator.generateSalaryReport(allEmployees, OUTPUT_DIR);
            System.out.println("✓ Salary analysis report generated successfully!");
        } catch (IOException e) {
            System.err.println("Error generating salary report: " + e.getMessage());
        }
    }

    /**
     * Generate all reports in batch
     */
    private static void generateBatchReports() {
        try {
            System.out.println("\nGenerating all reports...");

            List<Employee> allEmployees = dataManager.getAllEmployees();

            // Complete report
            pdfGenerator.generateEmployeeReport(allEmployees, OUTPUT_DIR);
            System.out.println("✓ Complete employee report generated");

            // Department reports
            List<String> departments = dataManager.getUniqueDepartments();
            for (String dept : departments) {
                List<Employee> deptEmployees = dataManager.getEmployeesByDepartment(dept);
                pdfGenerator.generateDepartmentReport(deptEmployees, dept, OUTPUT_DIR);
                System.out.println("✓ " + dept + " department report generated");
            }

            // Salary report
            pdfGenerator.generateSalaryReport(allEmployees, OUTPUT_DIR);
            System.out.println("✓ Salary analysis report generated");

            System.out.println("\n🎉 All reports generated successfully!");
            System.out.println("Total reports created: " + (departments.size() + 2));

        } catch (IOException e) {
            System.err.println("Error during batch report generation: " + e.getMessage());
        }
    }

    /**
     * View employee statistics
     */
    private static void viewStatistics() {
        List<Employee> employees = dataManager.getAllEmployees();

        System.out.println("\n=== Employee Statistics ===");
        System.out.println("Total Employees: " + dataManager.getEmployeeCount());
        System.out.println("Average Salary: $" + String.format("%.2f", dataManager.getAverageSalary()));
        System.out.println("Departments: " + dataManager.getUniqueDepartments().size());

        System.out.println("\nDepartment Breakdown:");
        for (String dept : dataManager.getUniqueDepartments()) {
            int count = dataManager.getEmployeesByDepartment(dept).size();
            System.out.println("  " + dept + ": " + count + " employees");
        }

        System.out.println("\nSalary Distribution:");
        long under60k = employees.stream().filter(emp -> emp.getSalary() < 60000).count();
        long between60_80k = employees.stream().filter(emp -> emp.getSalary() >= 60000 && emp.getSalary() < 80000).count();
        long over80k = employees.stream().filter(emp -> emp.getSalary() >= 80000).count();

        System.out.println("  Under $60,000: " + under60k + " employees");
        System.out.println("  $60,000 - $80,000: " + between60_80k + " employees");
        System.out.println("  Over $80,000: " + over80k + " employees");
    }

    /**
     * Load custom CSV data
     */
    private static void loadCustomData() {
        System.out.println("\nTo load custom data, place your CSV file in the project directory.");
        System.out.println("CSV format: id,firstName,lastName,email,department,position,salary,hireDate,phone,address");
        System.out.println("Date format: yyyy-MM-dd");
        System.out.print("Enter CSV filename (or press Enter to skip): ");

        String filename = scanner.nextLine().trim();
        if (!filename.isEmpty()) {
            try {
                dataManager.loadFromCSV(filename);
                System.out.println("✓ Custom data loaded successfully!");
                System.out.println("New employee count: " + dataManager.getEmployeeCount());
            } catch (IOException e) {
                System.err.println("Error loading CSV file: " + e.getMessage());
                System.out.println("Continuing with sample data...");
            }
        }
    }
}
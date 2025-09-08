package com.charan.pdfreport.generator;

import com.charan.pdfreport.model.Employee;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generates styled PDF reports for employee data using Apache PDFBox
 */
public class PDFReportGenerator {

    private static final float MARGIN = 50;
    private static final float ROW_HEIGHT = 20;
    private static final float CELL_PADDING = 5;
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    /**
     * Generate a comprehensive employee report
     */
    public void generateEmployeeReport(List<Employee> employees, String outputPath) throws IOException {
        String filename = outputPath + "/Employee_Report_" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float yPosition = page.getMediaBox().getHeight() - MARGIN;

            // Header
            yPosition = drawHeader(contentStream, yPosition, "Employee Report", employees.size());
            yPosition -= 30;

            // Summary Statistics
            yPosition = drawSummarySection(contentStream, yPosition, employees);
            yPosition -= 30;

            // Employee Table
            drawEmployeeTable(contentStream, yPosition, employees, page, document);

            contentStream.close();
            document.save(filename);
            System.out.println("Employee report generated: " + filename);
        }
    }

    /**
     * Generate department-wise report
     */
    public void generateDepartmentReport(List<Employee> employees, String department, String outputPath) throws IOException {
        String filename = outputPath + "/Department_Report_" + department.replaceAll("\\s+", "_") + "_" +
                LocalDateTime.now().format(TIMESTAMP_FORMAT) + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float yPosition = page.getMediaBox().getHeight() - MARGIN;

            // Header
            yPosition = drawHeader(contentStream, yPosition, department + " Department Report", employees.size());
            yPosition -= 30;

            // Department Statistics
            yPosition = drawDepartmentStats(contentStream, yPosition, employees, department);
            yPosition -= 30;

            // Employee Table
            drawEmployeeTable(contentStream, yPosition, employees, page, document);

            contentStream.close();
            document.save(filename);
            System.out.println("Department report generated: " + filename);
        }
    }

    /**
     * Generate salary analysis report
     */
    public void generateSalaryReport(List<Employee> employees, String outputPath) throws IOException {
        String filename = outputPath + "/Salary_Analysis_" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float yPosition = page.getMediaBox().getHeight() - MARGIN;

            // Header
            yPosition = drawHeader(contentStream, yPosition, "Salary Analysis Report", employees.size());
            yPosition -= 30;

            // Salary Statistics
            yPosition = drawSalaryAnalysis(contentStream, yPosition, employees);
            yPosition -= 30;

            // High Earners Table
            List<Employee> highEarners = employees.stream()
                    .filter(emp -> emp.getSalary() > getAverageSalary(employees))
                    .sorted((e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()))
                    .toList();

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText("Above Average Earners");
            contentStream.endText();
            yPosition -= 25;

            drawEmployeeTable(contentStream, yPosition, highEarners, page, document);

            contentStream.close();
            document.save(filename);
            System.out.println("Salary analysis report generated: " + filename);
        }
    }

    /**
     * Draw report header with title and basic info
     */
    private float drawHeader(PDPageContentStream contentStream, float yPosition, String title, int employeeCount) throws IOException {
        // Title
        contentStream.setNonStrokingColor(Color.DARK_GRAY);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        yPosition -= 25;

        // Subtitle with date and count
        contentStream.setNonStrokingColor(Color.GRAY);
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        contentStream.endText();
        yPosition -= 15;

        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Total Employees: " + employeeCount);
        contentStream.endText();
        yPosition -= 15;

        // Horizontal line
        contentStream.setStrokingColor(Color.LIGHT_GRAY);
        contentStream.setLineWidth(1);
        contentStream.moveTo(MARGIN, yPosition);
        contentStream.lineTo(545, yPosition);
        contentStream.stroke();

        return yPosition - 10;
    }

    /**
     * Draw summary statistics section
     */
    private float drawSummarySection(PDPageContentStream contentStream, float yPosition, List<Employee> employees) throws IOException {
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Summary Statistics");
        contentStream.endText();
        yPosition -= 25;

        contentStream.setFont(PDType1Font.HELVETICA, 11);

        // Calculate statistics
        double avgSalary = getAverageSalary(employees);
        double minSalary = employees.stream().mapToDouble(Employee::getSalary).min().orElse(0);
        double maxSalary = employees.stream().mapToDouble(Employee::getSalary).max().orElse(0);
        long departmentCount = employees.stream().map(Employee::getDepartment).distinct().count();

        String[] stats = {
                "Average Salary: $" + String.format("%.2f", avgSalary),
                "Salary Range: $" + String.format("%.2f", minSalary) + " - $" + String.format("%.2f", maxSalary),
                "Departments: " + departmentCount,
                "Latest Hire: " + employees.stream().map(Employee::getHireDate).max(LocalDate::compareTo).orElse(null)
        };

        for (String stat : stats) {
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText(stat);
            contentStream.endText();
            yPosition -= 15;
        }

        return yPosition;
    }

    /**
     * Draw department-specific statistics
     */
    private float drawDepartmentStats(PDPageContentStream contentStream, float yPosition, List<Employee> employees, String department) throws IOException {
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Department Statistics");
        contentStream.endText();
        yPosition -= 25;

        contentStream.setFont(PDType1Font.HELVETICA, 11);

        double avgSalary = getAverageSalary(employees);
        long positionCount = employees.stream().map(Employee::getPosition).distinct().count();

        String[] stats = {
                "Department: " + department,
                "Employee Count: " + employees.size(),
                "Average Salary: $" + String.format("%.2f", avgSalary),
                "Unique Positions: " + positionCount
        };

        for (String stat : stats) {
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText(stat);
            contentStream.endText();
            yPosition -= 15;
        }

        return yPosition;
    }

    /**
     * Draw salary analysis section
     */
    private float drawSalaryAnalysis(PDPageContentStream contentStream, float yPosition, List<Employee> employees) throws IOException {
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Salary Analysis");
        contentStream.endText();
        yPosition -= 25;

        contentStream.setFont(PDType1Font.HELVETICA, 11);

        double avgSalary = getAverageSalary(employees);
        long aboveAvg = employees.stream().filter(emp -> emp.getSalary() > avgSalary).count();
        long belowAvg = employees.stream().filter(emp -> emp.getSalary() < avgSalary).count();

        String[] stats = {
                "Average Salary: $" + String.format("%.2f", avgSalary),
                "Above Average: " + aboveAvg + " employees",
                "Below Average: " + belowAvg + " employees",
                "Highest Paid: " + employees.stream().max((e1, e2) -> Double.compare(e1.getSalary(), e2.getSalary())).map(Employee::getFullName).orElse("N/A")
        };

        for (String stat : stats) {
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText(stat);
            contentStream.endText();
            yPosition -= 15;
        }

        return yPosition;
    }

    /**
     * Draw employee data table with improved alignment
     */
    private void drawEmployeeTable(PDPageContentStream contentStream, float yPosition, List<Employee> employees, PDPage page, PDDocument document) throws IOException {
        // Improved table headers and column widths
        String[] headers = {"ID", "Name", "Department", "Position", "Salary", "Hire Date"};
        float[] columnWidths = {50, 120, 90, 130, 80, 90}; // Adjusted widths for better alignment

        // Draw table header with background
        contentStream.setNonStrokingColor(new Color(240, 240, 240)); // Light gray background
        contentStream.addRect(MARGIN, yPosition - 15, getTotalWidth(columnWidths), ROW_HEIGHT);
        contentStream.fill();

        // Header text
        contentStream.setNonStrokingColor(Color.DARK_GRAY);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);

        float xPosition = MARGIN;
        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(xPosition + CELL_PADDING, yPosition - 10);
            contentStream.showText(headers[i]);
            contentStream.endText();

            // Draw vertical separators for header
            if (i < headers.length - 1) {
                contentStream.setStrokingColor(Color.GRAY);
                contentStream.setLineWidth(0.5f);
                contentStream.moveTo(xPosition + columnWidths[i], yPosition - 15);
                contentStream.lineTo(xPosition + columnWidths[i], yPosition + 5);
                contentStream.stroke();
            }

            xPosition += columnWidths[i];
        }
        yPosition -= ROW_HEIGHT;

        // Draw thick header bottom line
        contentStream.setStrokingColor(Color.DARK_GRAY);
        contentStream.setLineWidth(2);
        contentStream.moveTo(MARGIN, yPosition + 5);
        contentStream.lineTo(MARGIN + getTotalWidth(columnWidths), yPosition + 5);
        contentStream.stroke();

        // Draw employee rows
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA, 9);

        boolean alternateRow = false;

        for (Employee emp : employees) {
            if (yPosition < MARGIN + 50) {
                // Add new page if needed
                contentStream.close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                yPosition = page.getMediaBox().getHeight() - MARGIN;
                alternateRow = false; // Reset alternating pattern on new page
            }

            // Alternate row background color
            if (alternateRow) {
                contentStream.setNonStrokingColor(new Color(248, 248, 248)); // Very light gray
                contentStream.addRect(MARGIN, yPosition - 15, getTotalWidth(columnWidths), ROW_HEIGHT);
                contentStream.fill();
                contentStream.setNonStrokingColor(Color.BLACK); // Reset text color
            }

            xPosition = MARGIN;
            String[] rowData = {
                    String.valueOf(emp.getEmployeeId()),
                    emp.getFullName(),
                    emp.getDepartment(),
                    emp.getPosition(),
                    "$" + String.format("%,d", (int)emp.getSalary()), // Format with commas
                    emp.getHireDate().toString()
            };

            for (int i = 0; i < rowData.length; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition + CELL_PADDING, yPosition - 10);

                String text = rowData[i];
                // Improved text truncation
                int maxChars = getMaxCharsForColumn(columnWidths[i]);
                if (text.length() > maxChars) {
                    text = text.substring(0, maxChars - 3) + "...";
                }
                contentStream.showText(text);
                contentStream.endText();

                // Draw vertical separators for data rows
                if (i < rowData.length - 1) {
                    contentStream.setStrokingColor(new Color(220, 220, 220)); // Light gray separators
                    contentStream.setLineWidth(0.3f);
                    contentStream.moveTo(xPosition + columnWidths[i], yPosition - 15);
                    contentStream.lineTo(xPosition + columnWidths[i], yPosition + 5);
                    contentStream.stroke();
                }

                xPosition += columnWidths[i];
            }

            // Draw horizontal line between rows
            contentStream.setStrokingColor(new Color(230, 230, 230)); // Very light gray
            contentStream.setLineWidth(0.3f);
            contentStream.moveTo(MARGIN, yPosition - 15);
            contentStream.lineTo(MARGIN + getTotalWidth(columnWidths), yPosition - 15);
            contentStream.stroke();

            yPosition -= ROW_HEIGHT;
            alternateRow = !alternateRow; // Toggle for next row
        }

        // Draw table border
        contentStream.setStrokingColor(Color.GRAY);
        contentStream.setLineWidth(1);
        float tableHeight = (employees.size() + 1) * ROW_HEIGHT;
        contentStream.addRect(MARGIN, yPosition, getTotalWidth(columnWidths), tableHeight);
        contentStream.stroke();
    }

    /**
     * Calculate average salary
     */
    private double getAverageSalary(List<Employee> employees) {
        return employees.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
    }

    /**
     * Get total width of all columns
     */
    private float getTotalWidth(float[] columnWidths) {
        float total = 0;
        for (float width : columnWidths) {
            total += width;
        }
        return total;
    }

    /**
     * Calculate maximum characters that fit in a column with better precision
     */
    private int getMaxCharsForColumn(float columnWidth) {
        return (int) ((columnWidth - 2 * CELL_PADDING) / 5.5); // More accurate character width calculation
    }
}
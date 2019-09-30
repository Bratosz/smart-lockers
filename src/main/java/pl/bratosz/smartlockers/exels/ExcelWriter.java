package pl.bratosz.smartlockers.exels;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Employee;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ExcelWriter {
    private List<String> columns;
    private List<Employee> employees;
    private String sheetName;

    public ExcelWriter(List<String> columns, List<Employee> employees, String sheetName) {
        this.columns = columns;
        this.employees = employees;
        this.sheetName = sheetName;
    }

    public void createExcelRaport() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        //Create a font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 13);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        //Create a cell style with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns.get(i));
            cell.setCellStyle(headerCellStyle);
        }

        int counter = 1;
        for (Employee employee : employees) {
            Row row = sheet.createRow(counter++);

//            row.createCell(0).setCellValue(employee.getBoxes().stream().findFirst().get().getEmployee().getId());
            row.createCell(0).setCellValue(employee.getId());
            row.createCell(1).setCellValue(employee.getFirstName());
            row.createCell(2).setCellValue(employee.getLastName());
            row.createCell(3).setCellValue(employee.getDepartment().getName());

            if (employee.getBoxes().size() > 0) {
                Set<Box> boxes;
                boxes = employee.getBoxes();
                for (Box box : boxes) {
                    row.createCell(4).setCellValue(box.getLocker().getLockerNumber());
                    row.createCell(5).setCellValue(box.getBoxNumber());
                    row.createCell(6).setCellValue(box.getLocker().getDepartmentNumber().toString());
                }
            } else {
                row.createCell(3).setCellValue("brak");
                row.createCell(4).setCellValue("szafki");
            }

        }

        //Resize all columns to fit the content
        for(int i = 0; i < columns.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/files to testing/test/" + sheetName + ".xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
    }


    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}

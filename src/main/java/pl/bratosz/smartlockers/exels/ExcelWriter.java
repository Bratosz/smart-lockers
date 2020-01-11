package pl.bratosz.smartlockers.exels;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.bratosz.smartlockers.exels.format.Format;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Employee;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ExcelWriter {
    private List<String> columns;
    private List<Employee> employees;
    private String sheetName;
    private CellStyle cellStyle;
    private Font font;
    private XSSFWorkbook workbook;
    private Sheet sheet;
    private Format format;
    private int labelsInRow;
    private int labelsInColumn;

    public ExcelWriter(LabelsSheetParameters parameters) {
        createFont(parameters);
        format = parameters.getSheetFormat();
        labelsInRow = parameters.getLabelsInRow();
        labelsInColumn = parameters.getLabelsInColumn();
        sheetName = parameters.getSheetName();
        sheet = workbook.createSheet(sheetName);
    }

    public ExcelWriter(List<String> columns, List<Employee> sortedEmployees, String sheetName){
        this.columns = columns;
        this.employees = sortedEmployees;
        this.sheetName = sheetName;
    }

    public XSSFWorkbook createLabels(List<String> labels) {
        int counter = 0;
        int rowCounter = 0;
        int boxCounter = 0;
        Row row = null;

        createLabelsStyle();
        setColumnsAndRowsSize();
        createRequiredNumberOfRows(labels.size());
//        writeLabelsInCells(labels);
        for (int i = 0; i < labels.size(); i++) {
            if (counter == 0) {
                row = sheet.createRow(rowCounter++);
            }
            for (int j = 0; j < 3; j++) {
                if (boxCounter >= labels.size()) {
                    return workbook;
                }
                String label = labels.get(boxCounter++);
                Cell cell = row.createCell(j);
                cell.setCellValue(label);
                cell.setCellStyle(cellStyle);
                if (j == 2) {
                    counter = 0;
                }
            }
        }
        return workbook;
    }

    private void createRequiredNumberOfRows(int size) {
        int numberOfRows = calculateNumberOfRows(size);
        for(int i = 0; i < numberOfRows; i++) {
            sheet.createRow(i);
        }
    }

    private int calculateNumberOfRows(int numberOfLabels) {
        return (int) Math.ceil(numberOfLabels/labelsInRow);
    }

    private void createLabelsStyle() {
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(font);
    }

    private void setColumnsAndRowsSize() {
        int pageWidth = format.getWidth();
        int pageHeight = format.getHeight();
        setColumnsWidth(pageWidth);
        setRowsHeight(pageHeight);
    }

    private void createFont(LabelsSheetParameters parameters) {
        workbook = new XSSFWorkbook();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) parameters.getFontSize());
        font.setFontName(parameters.getFontName());
    }

    private void setColumnsWidth(int pageWidth) {
        int columnWidthInMM = calculateSingleColumnWidth(pageWidth);
        int columnWidthInPoints = (int) convertMillimetersToPointsForWidth(columnWidthInMM);
        sheet.setDefaultColumnWidth(columnWidthInPoints);
    }

    private void setRowsHeight(int height) {
        float rowHeight = calculateSingleRowHeight(height);
        float rowHeightInPoints = convertMillimetersToPointsForHeight(rowHeight);
        sheet.setDefaultRowHeightInPoints(rowHeightInPoints);
    }

    private float convertMillimetersToPointsForWidth(float width){
        return width/2;
    }

    private float convertMillimetersToPointsForHeight(float height) {
        return height*2.8316f;
    }

    private float calculateSingleRowHeight(float pageHeight) {
        return pageHeight/labelsInColumn;
    }

    private int calculateSingleColumnWidth(int pageWidth) {
        return (pageWidth/labelsInRow);
    }


    public XSSFWorkbook createExcelRaportWithEmployees() throws IOException {
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
            row.createCell(1).setCellValue(employee.getLastName());
            row.createCell(2).setCellValue(employee.getFirstName());
            row.createCell(3).setCellValue(employee.getDepartment().getName());

            if (employee.getBoxes().size() > 0) {
                Set<Box> boxes;
                boxes = employee.getBoxes();
                for (Box box : boxes) {
                    row.createCell(4).setCellValue(box.getLocker().getLockerNumber());
                    row.createCell(5).setCellValue(box.getBoxNumber());
                    row.createCell(6).setCellValue(box.getLocker().getDepartmentNumber().getNumber());
                }
            } else {
                row.createCell(3).setCellValue("brak");
                row.createCell(4).setCellValue("szafki");
            }

        }

        //Resize all columns to fit the content
        for (int i = 0; i < columns.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        return (XSSFWorkbook) workbook;
    }

    public static void saveWorkbook(XSSFWorkbook workbook) throws IOException {
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/files_to_testing/Lear/raports/" + workbook.getSheetName(0)
                + ".xlsx");
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

package pl.bratosz.smartlockers.exels;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import pl.bratosz.smartlockers.model.LabelEmployee;

import java.util.LinkedList;
import java.util.List;

public class ExcelEmployeeReader {
    private int firstNameIndex;
    private int lastNameIndex;
    private int lockerNumberIndex;
    private int boxNumberIndex;
    private List<LabelEmployee> employees;

    public ExcelEmployeeReader() {
        employees = new LinkedList<>();
    }


    public List<LabelEmployee> loadEmployees(XSSFSheet sheet) {
        assignColumnsToDataType(sheet);
        loadEmployeesFromAllRows(sheet);
        return employees;
    }

    private void loadEmployeesFromAllRows(XSSFSheet sheet) {
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row.getCell(firstNameIndex).equals(null)) {
                break;
            }
            LabelEmployee emp = loadEmployeeFromRow(row);
            employees.add(emp);
        }
    }

    private LabelEmployee loadEmployeeFromRow(Row row) {
       return new LabelEmployee(row.getCell(firstNameIndex).getStringCellValue(),
                row.getCell(lastNameIndex).getStringCellValue(),
                (int) row.getCell(lockerNumberIndex).getNumericCellValue(),
                (int) row.getCell(boxNumberIndex).getNumericCellValue());
    }

    private void assignColumnsToDataType(XSSFSheet sheet) {
        XSSFRow row = sheet.getRow(0);
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            if (row.getCell(i).getStringCellValue().equals(null)) break;
            String val = row.getCell(i).getStringCellValue().toUpperCase().trim();
            switch (val) {
                case "IMIĘ":
                case "IMIE":
                case "NAME":
                case "FIRSTNAME":
                    firstNameIndex = i;
                    break;
                case "NAZWISKO":
                case "LASTNAME":
                case "SURNAME":
                    lastNameIndex = i;
                    break;
                case "SZAFKA":
                case "SZAFA":
                case "LOCKER":
                    lockerNumberIndex = i;
                    break;
                case "BOX":
                case "SKRYTKA":
                    boxNumberIndex = i;
                    break;
                default:
                    break;
            }
        }
    }
}

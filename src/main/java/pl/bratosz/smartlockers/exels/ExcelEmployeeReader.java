package pl.bratosz.smartlockers.exels;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.RawEmployee;

import java.util.LinkedList;
import java.util.List;

public class ExcelEmployeeReader {
    private int firstNameIndex;
    private int lastNameIndex;
    private int lockerNumberIndex;
    private int boxNumberIndex;


    public List<RawEmployee> loadRawEmployees(XSSFSheet sheet) {
        List<RawEmployee> employees = new LinkedList<>();
        assignColumns(sheet);
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row.getCell(firstNameIndex).equals(null)) break;
            RawEmployee emp = new RawEmployee(row.getCell(firstNameIndex).getStringCellValue(),
                    row.getCell(lastNameIndex).getStringCellValue(),
                    (int) row.getCell(lockerNumberIndex).getNumericCellValue(),
                    (int) row.getCell(boxNumberIndex).getNumericCellValue());
            employees.add(emp);
        }
        return employees;
    }

    private void assignColumns(XSSFSheet sheet) {
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

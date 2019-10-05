package pl.bratosz.smartlockers.exels;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.bratosz.smartlockers.model.Employee;

import java.util.List;

public class WriteInExcel {
    private XSSFWorkbook workbook;

    public WriteInExcel(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }



    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public void writeLockersAndDepNumbersToFile(List<Employee> employeeList) {
        for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            for(Employee employee : employeeList)
        }
    }
}

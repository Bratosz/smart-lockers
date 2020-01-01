package pl.bratosz.smartlockers.exels;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelSave {

    public void save(XSSFWorkbook workbook, String folderName) throws IOException {
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/files_to_testing/Lear/" + folderName + "/"
                + workbook.getSheetName(0) + ".xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
}

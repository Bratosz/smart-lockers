package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class CellTypeResolver {
    public static boolean isCellNumeric(Cell cell) {
        if(cell.getCellTypeEnum().equals(CellType.NUMERIC)){
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCellString(Cell cell) {
        if(cell.getCellTypeEnum().equals(CellType.STRING)) {
            return true;
        } else {
            return false;
        }
    }
}

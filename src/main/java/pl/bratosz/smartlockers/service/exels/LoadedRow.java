package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.Map;

import static pl.bratosz.smartlockers.service.exels.Index.*;

public class LoadedRow {
    private String firstName;
    private String lastName;
    private long barCode;
    private Row row;
    private Map<Index, Integer> indexes;

    public LoadedRow(Row row, Map<Index, Integer> indexes){
        this.row = row;
        this.indexes = indexes;
        setFirstName(FIRST_NAME);
        setLastName(LAST_NAME);
        setBarCode(BAR_CODE);
    }


    private  void setFirstName(Index firstName) {
        this.firstName = getCellByIndex(firstName).getStringCellValue();
    }

    private void setLastName(Index lastName) {
        this.lastName = getCellByIndex(lastName).getStringCellValue();
    }

    private void setBarCode(Index barCode) {
        if(CellTypeResolver.isCellNumeric(getCellByIndex(barCode))) {
            this.barCode = (long) getCellByIndex(barCode).getNumericCellValue();
        } else {
            this.barCode = Long.parseLong(getCellByIndex(barCode).getStringCellValue());
        }
    }

     Cell getCellByIndex(Index index) {
        return row.getCell(indexes.get(index));
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getBarCode() {
        return barCode;
    }
}

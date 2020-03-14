package pl.bratosz.smartlockers.exels;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.LinkedList;
import java.util.List;

public class ExcelClothesReader {
    private static final int BAR_CODE_LENGTH = 13;
    private List<LoadedRow> loadedRows;
    private final Sheet sheet;
    private int lastNameIndex;
    private int firstNameIndex;
    private int barCodeIndex;
    private int ordinalNoIndex;
    private int articleNoIndex;
    private int lockerNoIndex;
    private int boxNoIndex;
    private int releaseDateIndex;
    private int washingDateIndex;

    private ExcelClothesReader(Sheet sheet) {
        this.sheet = sheet;
        loadedRows = new LinkedList<>();
        releaseDateIndex = 99;
    }

    public static ExcelClothesReader create(Sheet sheet) {
        return new ExcelClothesReader(sheet);
    }

    public List<LoadedRow> loadRows() {
        assignColumnsToDataType();
        loadAllRows();
        return loadedRows;
    }

    private void loadAllRows() {
        Row row;
        for(int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if(isRowCorrect(row)) {
                LoadedRow loadedRow = loadValues(row);
                loadedRows.add(loadedRow);
            } else {
                continue;
            }
        }
    }


    private LoadedRow loadValues(Row row) {
        return new LoadedRow(
                row.getCell(lastNameIndex).getStringCellValue(),
                row.getCell(firstNameIndex).getStringCellValue(),
                row.getCell(barCodeIndex).getStringCellValue(),
                row.getCell(ordinalNoIndex).getNumericCellValue(),
                row.getCell(articleNoIndex).getNumericCellValue(),
                row.getCell(lockerNoIndex).getNumericCellValue(),
                row.getCell(boxNoIndex).getNumericCellValue(),
                row.getCell(washingDateIndex).getDateCellValue());
    }

    private boolean isRowCorrect(Row row) {
        if(!isCellNull(row.getCell(barCodeIndex))
                && isBarCodeCorrect(row.getCell(barCodeIndex))) {
            return true;
        } else {
            return false;
        }
    }


    private boolean isBarCodeCorrect(Cell cell) {
        if(cell.getStringCellValue().length() == BAR_CODE_LENGTH) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCellNull(Cell cell) {
        if(null == cell) {
            return true;
        } else {
            return false;
        }
    }


    private void assignColumnsToDataType() {
        Row row = sheet.getRow(0);
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            if (isCellValueInvalid(row.getCell(i))) continue;
            String content = getNormalizedValue(row.getCell(i));
            switch (content) {
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
                case "KOD KRESKOWY":
                case "KODKRESKOWY":
                case "BARCODE":
                case "BAR-CODE":
                    barCodeIndex = i;
                    break;
                case "LP":
                case "LICZBA PORZĄDKOWA":
                case "LICZBA PORZADKOWA":
                    ordinalNoIndex = i;
                    break;
                case "NRART":
                case "NR ART":
                case "NUMER ARTYKUŁU":
                case "NUMER ARTYKULU":
                case "ARTICLE NUMBER":
                    articleNoIndex = i;
                    break;
                case "SZAFKA":
                case "SZAFA":
                case "LOCKER":
                    lockerNoIndex = i;
                    break;
                case "BOX":
                case "SKRYTKA":
                    boxNoIndex = i;
                    break;
                case "DATAWYDANIA":
                case "DATA WYDANIA":
                case "RELEASEDATE":
                case "RELEASE DATE":
                    releaseDateIndex = i;
                    break;
                case "DATA PRANIA":
                case "DATAPRANIA":
                case "OSTATNIE PRANIE":
                case "OSTATNIEPRANIE":
                case "WPRANIU":
                case "W PRANIU":
                case "WASHINGDATE":
                case "WASHING DATE":
                    washingDateIndex = i;
                    break;
                default:
                    break;
            }
        }
    }

    private String getNormalizedValue(Cell cell) {
        String content = cell.getStringCellValue();
        return content.trim().toUpperCase();
    }

    private boolean isCellValueInvalid(Cell cell) {
        String content = cell.getStringCellValue();
        if (content.length() > 0) {
            return false;
        } else {
            return true;
        }
    }
}

package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;

public class ExcelClothesReader {
    private static final int CORRECT_BAR_CODE_LENGTH = 13;
    private static final long BAR_CODE_MINIMAL_VALUE = 1000000000000l;
    private List<LoadedRow> rows;
    private final Sheet sheet;
    private Map<Index, Integer> indexes;
    private ClothOperationType clothOperationType;

    private ExcelClothesReader(Sheet sheet) {
        this.sheet = sheet;
        rows = new LinkedList<>();
        indexes = new HashMap<>();
    }

    public static ExcelClothesReader create(Sheet sheet, ClothOperationType clothOperationType) {
        ExcelClothesReader excelClothesReader = new ExcelClothesReader(sheet);
        excelClothesReader.setClothOperationType(clothOperationType);
        return excelClothesReader;
    }

    public <T extends LoadedRow> List<T> loadRows() {
        assignColumnsToDataType();
        List<T> loadedRows = loadAllRows();
        return loadedRows;
    }

    private <T extends LoadedRow> List<T> loadAllRows() {
        Row row;
        List<T> loadedRows = new LinkedList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (isRowCorrect(row)) {
                T loadedRow = loadValues(row);
                loadedRows.add(loadedRow);
            } else {
                continue;
            }
        }
        return loadedRows;
    }


    private <T extends LoadedRow> T loadValues(Row row) {
        switch (clothOperationType) {
            case CLOTHES_ROTATION_UPLOAD:
                return (T) new RowForRotationUpdate(row, indexes);
            case RELEASED_ROTATIONAL_CLOTHING_UPDATE:
                return (T) new RowReleasedRotationalClothes(row, indexes);
            default:
                return null;
        }
    }

    private boolean isRowCorrect(Row row) {
        if (!isCellNull(row.getCell(indexes.get(Index.BAR_CODE)))
                && isBarCodeCorrect(row.getCell(indexes.get(Index.BAR_CODE)))) {
            return true;
        } else {
            return false;
        }
    }


    private boolean isBarCodeCorrect(Cell cell) {
        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue().length() == CORRECT_BAR_CODE_LENGTH;
            case NUMERIC:
                return cell.getNumericCellValue() >= BAR_CODE_MINIMAL_VALUE;
            default:
                return false;
        }
    }

    private boolean isCellNull(Cell cell) {
        if (null == cell) {
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
                    indexes.put(Index.FIRST_NAME, i);
                    break;
                case "NAZWISKO":
                case "LASTNAME":
                case "SURNAME":
                    indexes.put(Index.LAST_NAME, i);
                    break;
                case "KOD KRESKOWY":
                case "KODKRESKOWY":
                case "KOD":
                case "BARCODE":
                case "BAR-CODE":
                case "BAR CODE":
                    indexes.put(Index.BAR_CODE, i);
                    break;
                case "LP":
                case "LICZBA PORZĄDKOWA":
                case "LICZBA PORZADKOWA":
                    indexes.put(Index.ORDINAL_NO, i);
                    break;
                case "NRART":
                case "NR ART":
                case "NUMER ARTYKUŁU":
                case "NUMER ARTYKULU":
                case "ARTICLE NUMBER":
                    indexes.put(Index.ARTICLE_NO, i);
                    break;
                case "SZAFKA":
                case "SZAFA":
                case "LOCKER":
                    indexes.put(Index.LOCKER_NO, i);
                    break;
                case "BOX":
                case "SKRYTKA":
                    indexes.put(Index.BOX_NO, i);
                    break;
                case "DATAWYDANIA":
                case "DATA WYDANIA":
                case "RELEASEDATE":
                case "RELEASE DATE":
                    indexes.put(Index.RELEASE_DATE, i);
                    break;
                case "DATA PRANIA":
                case "DATAPRANIA":
                case "OSTATNIE PRANIE":
                case "OSTATNIEPRANIE":
                case "WPRANIU":
                case "W PRANIU":
                case "WASHINGDATE":
                case "WASHING DATE":
                    indexes.put(Index.WASHING_DATE, i);
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

    public void setClothOperationType(ClothOperationType clothOperationType) {
        this.clothOperationType = clothOperationType;
    }
}

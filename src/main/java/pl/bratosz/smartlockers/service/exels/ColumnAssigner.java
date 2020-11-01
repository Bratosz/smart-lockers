package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.HashMap;
import java.util.Map;

import static pl.bratosz.smartlockers.service.exels.CellValueManager.*;
import static pl.bratosz.smartlockers.service.exels.ColumnType.*;

public class ColumnAssigner {
    Row headerRow;

    public ColumnAssigner(Row headerRow) {
        this.headerRow = headerRow;
    }

    public Map<ColumnType, Integer> assignColumnsToTheirTypeWithIndexes() {
        Map<ColumnType, Integer> columnIndexes = new HashMap<>();
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            Cell cell = headerRow.getCell(i);
            if (isCellValueValid(cell)) {
                String content = getNormalizedStringValue(cell);
                switch (content) {
                    case "IMIĘ":
                    case "IMIE":
                    case "NAME":
                    case "FIRSTNAME":
                        columnIndexes.put(FIRST_NAME, i);
                        break;
                    case "NAZWISKO":
                    case "LASTNAME":
                    case "SURNAME":
                        columnIndexes.put(LAST_NAME, i);
                        break;
                    case "KOD KRESKOWY":
                    case "KODKRESKOWY":
                    case "KOD":
                    case "BARCODE":
                    case "BAR-CODE":
                    case "BAR CODE":
                        columnIndexes.put(BAR_CODE, i);
                        break;
                    case "LP":
                    case "LICZBA PORZĄDKOWA":
                    case "LICZBA PORZADKOWA":
                    case "ORDINAL NUMBER":
                        columnIndexes.put(ORDINAL_NUMBER, i);
                        break;
                    case "NRART":
                    case "NR ART":
                    case "NUMER ARTYKUŁU":
                    case "NUMER ARTYKULU":
                    case "ARTICLE NUMBER":
                        columnIndexes.put(ARTICLE_NUMBER, i);
                        break;
                    case "SZAFA":
                    case "NUMER SZAFY":
                    case "LOCKER":
                    case "LOCKER NUMBER":
                        columnIndexes.put(LOCKER_NUMBER, i);
                        break;
                    case "BOX":
                    case "BOX NUMBER":
                    case "NUMER BOXA":
                    case "NUMER SKRYTKI":
                    case "SKRYTKA":
                        columnIndexes.put(BOX_NUMBER, i);
                        break;
                    case "STATUS BOXA":
                    case "STATUS SZAFKI":
                    case "BOX STATUS":
                    case "BOXSTATUS":
                        columnIndexes.put(BOX_STATUS, i);
                        break;
                    case "DATA WYDANIA":
                    case "RELEASEDATE":
                    case "RELEASE DATE":
                        columnIndexes.put(RELEASE_DATE, i);
                        break;
                    case "DATA PRANIA":
                    case "OSTATNIE PRANIE":
                    case "W PRANIU":
                    case "WASHINGDATE":
                    case "WASHING DATE":
                        columnIndexes.put(WASHING_DATE, i);
                        break;
                    case "PLANT NUMBER":
                    case "PLANTNUMBER":
                    case "NUMER ZAKŁADU":
                        columnIndexes.put(PLANT_NUMBER, i);
                        break;
                    case "DEPARTMENT":
                    case "ODDZIAŁ":
                    case "NAZWA ODDZIAŁU":
                        columnIndexes.put(DEPARTMENT_NAME, i);
                        break;
                    case "LOCATION":
                    case "LOCKER LOCATION":
                    case "LOKALIZACJA SZAFKI":
                    case "LOKALIZACJA":
                    case "LOKACJA":
                        columnIndexes.put(LOCATION_NAME, i);
                        break;
                    case "POJEMNOŚĆ":
                    case "CAPACITY":
                        columnIndexes.put(CAPACITY, i);
                        break;
                    default:
                        break;
                }
            }
        }
        return columnIndexes;
    }
}

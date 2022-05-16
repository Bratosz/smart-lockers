package pl.bratosz.smartlockers.service.exels.plant.template;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.bratosz.smartlockers.exception.MyException;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.files.TemplateTypeForPlantLoad;
import pl.bratosz.smartlockers.service.exels.plant.template.data.*;
import pl.bratosz.smartlockers.strings.MyString;
import pl.bratosz.smartlockers.utils.Utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.poi.ss.usermodel.CellType.*;
import static pl.bratosz.smartlockers.model.files.TemplateTypeForPlantLoad.TEMPLATE_WITH_BOXES;
import static pl.bratosz.smartlockers.model.files.TemplateTypeForPlantLoad.TEMPLATE_WITH_BOXES_AND_SIZES;
import static pl.bratosz.smartlockers.service.exels.plant.template.data.SheetTypeForPlantLoad.*;

public class PlantDataFromFileExtractor {
    private Map<SheetTypeForPlantLoad, MySheet> sheets = new HashMap<>();
    private final TemplateTypeForPlantLoad templateType;

    private PlantDataFromFileExtractor(TemplateTypeForPlantLoad templateType) {
        this.templateType = templateType;
    }

    public static PlantDataFromFileExtractor create(TemplateTypeForPlantLoad templateType) {
        return new PlantDataFromFileExtractor(templateType);
    }

    public PlantDataContainer getInitialPlantData(XSSFWorkbook workbook, List<ClientArticle> clientArticles) throws MyException {
        validateAndExtractSheets(workbook);
        return validateAndExtractData(clientArticles);
    }

    private void validateAndExtractSheets(XSSFWorkbook workbook) throws MyException {
        for (SheetTypeForPlantLoad sheetType : templateType.getSheetTypes()) {
            XSSFSheet sheet;
            sheet = workbook.getSheet(sheetType.getName());
            if (sheet == null) throw new MyException("Brak arkusza o nazwie: " + sheetType.getName());
            int lastColIndex = resolveLastColumnIndex(sheet);
            int lastRowIndex = resolveLastRowIndex(sheet, sheetType, lastColIndex);
            sheets.put(sheetType, new MySheet(sheet, lastRowIndex, lastColIndex));
        }
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    private PlantDataContainer validateAndExtractData(List<ClientArticle> clientArticles) throws MyException {
        PlantDataContainer dataContainer = new PlantDataContainer(clientArticles);
        dataContainer.setLocations(
                extract(
                        sheets.get(LOCATIONS),
                        this::extractStringData));
        dataContainer.setDepartments(
                extract(
                        sheets.get(DEPARTMENTS),
                        this::extractStringData));
        dataContainer.setPositions(
                extractPositionsWithArticles(sheets));
                //CREATE POSITIONS WITH ARTICLES AND QUANTITIES
        checkAndUpdatePositions(
                extract(
                        sheets.get(POSITIONS_AND_DEPARTMENTS),
                        this::extractPositionWithDepartment),
                dataContainer.getPositions());
        dataContainer.setLockers(
                extract(
                        sheets.get(LOCKERS),
                        this::extractLocker));
        dataContainer.setEmployees(
                extractEmployees(
                        sheets.get(EMPLOYEES),
                        dataContainer.getPositions()));
        checkLockers(
                dataContainer.getLockers(),
                dataContainer.getLocations());
        switch (templateType) {
            case TEMPLATE_WITH_LOCATIONS:
                checkEmployees(
                        dataContainer.getEmployees(),
                        dataContainer.getLocations());
                checkLockersCapacity(
                        dataContainer.getLockers(),
                        dataContainer.getEmployees());
                break;
            case TEMPLATE_WITH_LOCATIONS_AND_SIZES:
                checkEmployees(
                        dataContainer.getEmployees(),
                        dataContainer.getLocations());
//                extractSizes(
//                        dataContainer.getEmployees(),
//                        dataContainer.getpo
//                )
                checkLockersCapacity(
                        dataContainer.getLockers(),
                        dataContainer.getEmployees());
                break;
            case TEMPLATE_WITH_BOXES:
                checkThatEmployeesFitInLockers(
                        dataContainer.getEmployees(),
                        dataContainer.getLockers()
                );
                break;
            case TEMPLATE_WITH_BOXES_AND_SIZES:
                checkThatEmployeesFitInLockers(
                        dataContainer.getEmployees(),
                        dataContainer.getLockers()
                );
                break;
        }
        return dataContainer;
    }

    private void checkThatEmployeesFitInLockers(Set<TemplateEmployee> employees, Set<TemplateLockers> lockers) throws MyException {
        EmployeesToBoxesAssignmentValidator validator =
                new EmployeesToBoxesAssignmentValidator(employees, lockers);
        validator.validate();
    }


    private void checkLockersCapacity(
            Set<TemplateLockers> lockers,
            Set<TemplateEmployee> employees) throws MyException {
        LockersCapacityValidator validator =
                new LockersCapacityValidator(lockers, employees);
        validator.validate();
    }


    private <R> Set<R> extract(MySheet mySheet, Function<XSSFRow, R> extractRow) {
        XSSFSheet sheet = mySheet.getSheet();
        Set<R> elements = new HashSet<>();
        for (int rowNo = 1; rowNo <= mySheet.getLastRowIndex(); rowNo++) {
            elements.add(
                    extractRow.apply(sheet.getRow(rowNo)));
        }
        return elements;
    }

    private Set<TemplatePosition> extractFromRow(MySheet mySheet, int rowIndex) {
        XSSFSheet sheet = mySheet.getSheet();
        Set<TemplatePosition> positions = new HashSet<>();
        XSSFRow row = sheet.getRow(rowIndex);
        for (int cellNo = 1; cellNo <= mySheet.getLastColIndex(); cellNo++) {
            String name = getCellValue(row.getCell(cellNo));
            positions.add(new TemplatePosition(name));
        }
        return positions;
    }

    private Set<TemplateEmployee> extractEmployees(MySheet mySheet, Set<TemplatePosition> positions) throws MyException {
        XSSFSheet sheet = mySheet.getSheet();
        Set<TemplateEmployee> employees = new LinkedHashSet<>();
        for (int rowNo = 1; rowNo <= mySheet.getLastRowIndex(); rowNo++) {
            employees.add(
                    extractEmployeeAndCheckPositions(sheet.getRow(rowNo), positions)
            );
        }
        return employees;
    }

    private TemplateEmployee extractEmployeeAndCheckPositions(XSSFRow row, Set<TemplatePosition> positions) throws MyException {
        String personalNumber = resolvePersonalNumber(getCellValue(row.getCell(0)));
        String firstName = getCellValue(row.getCell(1));
        String lastName = getCellValue(row.getCell(2));
        String departmentName = getCellValue(row.getCell(3));
        String positionName = getCellValue(row.getCell(4));
        TemplatePosition position = getPosition(positionName, positions);
        if (templateType.equals(TEMPLATE_WITH_BOXES) ||
                templateType.equals(TEMPLATE_WITH_BOXES_AND_SIZES)) {
            String lockerNumber = getCellValue(row.getCell(5));
            String boxNumber = getCellValue(row.getCell(6));
            return new TemplateEmployee(
                    personalNumber,
                    firstName,
                    lastName,
                    departmentName,
                    position,
                    Float.valueOf(lockerNumber).intValue(),
                    Float.valueOf(boxNumber).intValue());
        } else {
            String location = getCellValue(row.getCell(5));
            return new TemplateEmployee(
                    personalNumber,
                    firstName,
                    lastName,
                    departmentName,
                    position,
                    location);
        }
    }

    private TemplatePosition getPosition(String positionName, Set<TemplatePosition> positions) throws MyException {
        Map<String, TemplatePosition> positionsMap = Utils.toMapWithToStringKey(positions);
        TemplatePosition position = positionsMap.get(positionName);
        if (position == null) {
            throw new MyException("W arkuszu " + POSITIONS_AND_ARTICLES.getName() +
                    " brakuje stanowiska: " + positionName);
        } else {
            return position;
        }
    }


    private String extractStringData(XSSFRow row) {
        String name = getCellValue(row.getCell(0));
        return name;
    }

    private TemplatePositionWithDepartment extractPositionWithDepartment(XSSFRow row) {
        String positionName = getCellValue(row.getCell(1));
        String departmentName = getCellValue(row.getCell(0));
        return new TemplatePositionWithDepartment(positionName, departmentName);
    }

    private Set<TemplatePosition> extractPositionsWithArticles(Map<SheetTypeForPlantLoad, MySheet> sheets) {
        Set<TemplatePosition> templatePositions = extractFromRow(
                sheets.get(POSITIONS_AND_ARTICLES),
                0);

    }

    private TemplateLockers extractLocker(XSSFRow row) {
        String firstLockerNumber = getCellValue(row.getCell(0));
        String lastLockerNumber = getCellValue(row.getCell(1));
        String capacity = getCellValue(row.getCell(2));
        String location = getCellValue(row.getCell(3));
        return new TemplateLockers(firstLockerNumber, lastLockerNumber, capacity, location);
    }

    private String getCellValue(XSSFCell cell) {
        CellType cellType = cell.getCellTypeEnum();
        if (cellType.equals(NUMERIC))
            return MyString.create(
                    String.valueOf(cell.getNumericCellValue())).get();
        else
            return MyString.create(
                    cell.getStringCellValue()).get();
    }


    private void checkEmployees(
            Set<TemplateEmployee> employees,
            Set<String> locations) throws MyException {
        List<String> missingLocations = employees.stream()
                .filter(e -> !locations.contains(e.getLocation()))
                .map(e -> e.getLocation())
                .collect(Collectors.toList());
        if (!missingLocations.isEmpty()) throw new MyException("W arkuszu " + LOCATIONS.getName() +
                " brakuje lokalizacji: " + missingLocations.toString());
    }

    private void checkAndUpdatePositions(
            Set<TemplatePositionWithDepartment> positionWithDepartments,
            Set<TemplatePosition> positions) throws MyException {
        Set<String> missingPositions = new HashSet<>();
        positions.stream().forEach(p -> {
            if (positionWithDepartments.stream()
                    .filter(pwd -> pwd.getPositionName().equals(p.getName()))
                    .count() == 0) {
                missingPositions.add(p.getName());
            }
        });
        if (!missingPositions.isEmpty()) {
            throw new MyException("W arkuszu " + POSITIONS_AND_DEPARTMENTS +
                    " brakuje stanowisk: " + missingPositions);
        }
        positionWithDepartments.stream()
                .forEach(pwd -> positions.stream()
                        .filter(p -> p.getName().equals(pwd.getPositionName()))
                        .findFirst()
                        .get()
                        .addDepartment(pwd.getDepartmentName()));
    }

    private void checkLockers(
            Set<TemplateLockers> lockers,
            Set<String> locations) throws MyException {
        List<String> missingLocations = lockers.stream()
                .filter(l -> !locations.contains(l.getLocation()))
                .map(l -> l.getLocation())
                .collect(Collectors.toList());
        if (!missingLocations.isEmpty()) throw new MyException("W arkuszu " + LOCATIONS.getName() +
                " brakuje lokalizacji: " + missingLocations.toString());
    }

    private int resolveLastRowIndex(
            XSSFSheet sheet,
            SheetTypeForPlantLoad sheetTypeForPlantLoad,
            int lastColIndex) throws MyException {
        if (sheet.getPhysicalNumberOfRows() <= 1 && sheetTypeForPlantLoad.equals(EMPLOYEES_AND_SIZES))
            return -1;
        else if (sheet.getPhysicalNumberOfRows() <= 1)
            throw new MyException(
                    "Arkusz " + sheet.getSheetName() + " jest pusty.");
        else {
            int lastRowIndex = 0;
            for (int rowNo = 0; rowNo < sheet.getPhysicalNumberOfRows(); rowNo++) {
                try {
                    if (rowIsEmpty(sheet.getRow(rowNo), lastColIndex)) break;
                    else lastRowIndex = rowNo;
                } catch (MyException e) {
                    throw new MyException("W arkuszu " + sheetTypeForPlantLoad.getName() +
                            " w wierszu nr " + (rowNo + 1) + " jest błąd.");
                }
            }
            return lastRowIndex;
        }
    }

    private int resolveLastColumnIndex(XSSFSheet sheet) {
        XSSFRow row = sheet.getRow(0);
        int numberOfCells = row.getPhysicalNumberOfCells();
        int numberOfFilledCells = 0;
        for (int colIndex = 0; colIndex < numberOfCells; colIndex++) {
            XSSFCell cell = row.getCell(colIndex);
            if (cellIsFilled(cell)) {
                numberOfFilledCells++;
            } else {
                return numberOfFilledCells - 1;
            }
        }
        return numberOfFilledCells - 1;
    }


    private boolean rowIsEmpty(XSSFRow row, int lastColumnIndex) throws MyException {
        if (row == null) return true;
        int filledCells = 0;
        for (int colNo = 0; colNo <= lastColumnIndex; colNo++) {
            XSSFCell cell = row.getCell(colNo);
            if (cellIsFilled(cell)) filledCells++;
        }
        if (filledCells == lastColumnIndex + 1) {
            return false;
        } else if (filledCells == 0) {
            return true;
        } else {
            throw new MyException();
        }
    }

    private boolean cellIsFilled(XSSFCell cell) {
        if (cell == null)
            return false;
        else {
            String cellValue = getCellValue(cell);
            if (cellValue == null || cellValue.equals(""))
                return false;
            else
                return true;
        }
    }

    private String resolvePersonalNumber(String s) {
        if (s.equals(""))
            return "0";
        else
            return s;
    }


}

package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.bratosz.smartlockers.calculator.CalculateClothesValue;
import pl.bratosz.smartlockers.date.CurrentDateForFiles;
import pl.bratosz.smartlockers.date.FormatDate;
import pl.bratosz.smartlockers.service.exels.ExcelWriter;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.payload.UploadFileResponse;
import pl.bratosz.smartlockers.service.*;
import sun.util.calendar.BaseCalendar;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.service.exels.ExcelWriter.saveWorkbook;

@RestController
@RequestMapping("/files")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);


    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private EmployeeController employeeController;
    @Autowired
    private BoxesController boxesController;
    @Autowired
    private LockersController lockersController;
    @Autowired
    private FileService fileService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private BoxesService boxesService;
    private LabelsService labelsService;

    public FileController(LabelsService labelsService) {
        this.labelsService = labelsService;
    }

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(),
                file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files")
                                                                MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/import_employees")
    public List<Employee> importEmployeesFromExcelFileToDB(@RequestParam("file") MultipartFile employeesFile) throws IOException, IllegalArgumentException {
        XSSFWorkbook workbook = new XSSFWorkbook(employeesFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        List<Employee> employeeList = new LinkedList<>();
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);

            //creating instance of employee from row
            Employee employee = new Employee();
            employee.setFirstName(row.getCell(2).getStringCellValue());
            employee.setLastName(row.getCell(1).getStringCellValue());
            employee.setDepartment(Department.valueOf(row.getCell(3).getStringCellValue()));


            //adding employee to box
            Employee loadedEmployee = employeeService.createEmployee(
                    Locker.DepartmentNumber.valueOf(row.getCell(4).getStringCellValue()),
                    (int) row.getCell(5).getNumericCellValue(),
                    (int) row.getCell(6).getNumericCellValue(),
                    employee);
            employeeList.add(loadedEmployee);
        }
        return employeeList;
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/add_employees")
    public List<Employee> addNewEmployeesFromExcelFile(@RequestParam("file") MultipartFile newEmployeesFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(newEmployeesFile.getInputStream());
        List<Employee> employeeList = new LinkedList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String sheetName = workbook.getSheetName(i).toUpperCase().trim();
            if (!(sheetName.equals("METAL") || sheetName.equals("JIT"))) {
                continue;
            }
            XSSFSheet worksheet = workbook.getSheetAt(i);
            for (int j = 1; j < worksheet.getPhysicalNumberOfRows(); j++) {
                XSSFRow row = worksheet.getRow(j);
                if (row.getCell(1) == null || row.getCell(1).getStringCellValue().trim().length() == 0) {
                    continue;
                }
                String departmentCellValue = row.getCell(5).getRawValue();
                if(departmentCellValue.equals("385") || departmentCellValue.equals("384")){
                    continue;
                }

                Employee employee = new Employee();
                employee.setFirstName(row.getCell(2).getStringCellValue().trim().toUpperCase());
                employee.setLastName(row.getCell(1).getStringCellValue().trim().toUpperCase());
                employee.setDepartment(Department.valueOf(sheetName));

                Locker.Location location;
                departmentCellValue = row.getCell(5).getStringCellValue();
                if (departmentCellValue.equals("stara")) {
                    location = Locker.Location.OLDSIDE;
                } else {
                    location = Locker.Location.NEWSIDE;
                }

                //creating emploee and assign it to the next free box
                Employee createdEmployee = employeeController.createEmployee(employee.getDepartment(), location, employee);
                Box box = createdEmployee.getBoxes().stream().findFirst().get();
                row.getCell(3).setCellValue(box.getLocker().getLockerNumber());
                row.getCell(4).setCellValue(box.getBoxNumber());
                row.getCell(5).setCellValue(box.getLocker().getDepartmentNumber().getNumber());
                row.getCell(6).setCellValue(employee.getDepartment().getName());
                employeeList.add(createdEmployee);
            }
        }
        CurrentDateForFiles date = new CurrentDateForFiles();
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/files_to_testing/Lear/raports/" + date.getDate()
                + " pomiary" + ".xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
        return employeeList;
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/dismiss_by_id")
    public List<Box> dismissEmployeesFromFileByID(@RequestParam("file") MultipartFile employeesToDelete) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(employeesToDelete.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);

        List<Box> releasedBoxes = new LinkedList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            long id = (long) sheet.getRow(i).getCell(0).getNumericCellValue();

            //Adding employee to raport list of deleted employees
            Employee employee = employeeController.getEmployeeById(id);
            Set<Box> boxes = employee.getBoxes();
            for (Box box : boxes) {
                releasedBoxes.add(boxesController.dismissEmployeeByBox(box));
            }
        }
        return releasedBoxes;
    }

    @PostMapping("/reformat")
    public void reformat(@RequestParam("file") MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheetToLoad = workbook.getSheetAt(0);
        Row row;
        List<EmployeeRow> employees = new LinkedList<>();
        int counter;
        XSSFSheet sheet = workbook.createSheet();
        for(int i = 1; i < sheetToLoad.getPhysicalNumberOfRows(); i++){
            long[] barCodes = new long[5];
            String firstName;
            String lastName;
            Date date;
            counter = 0;
            row = sheetToLoad.getRow(i);
            if(row.getCell(0) == null){continue;}
            lastName = row.getCell(0).getStringCellValue().toUpperCase().trim();
            firstName = row.getCell(1).getStringCellValue().toUpperCase().trim();
            date = row.getCell(7).getDateCellValue();
            final String stringDate = FormatDate.getDate(date);
            int j = 2;
            while(j < 7) {
                if(row.getCell(j) == null || row.getCell(j).getStringCellValue().length() < 2) {
                    j++;
                    continue;}
                barCodes[counter] =  Long.parseLong(row.getCell(j).getStringCellValue().substring(1));
                j++;
                counter++;
            }
            for(int k = 0; k < counter; k++) { employees.add(new EmployeeRow(firstName, lastName, barCodes[k], stringDate));
            }
        }
        for(int i = 0; i < employees.size(); i++){
            XSSFRow createdRow = sheet.createRow(i);
            EmployeeRow employeeRow = employees.get(i);
            createdRow.createCell(0).setCellValue(employeeRow.lastName);
            createdRow.createCell(1).setCellValue(employeeRow.firstName);
            createdRow.createCell(2).setCellValue(employeeRow.barCode);
            createdRow.createCell(3).setCellValue(employeeRow.releaseDate);
        }
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/files_to_testing/Lear/raports/rotacja.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    class EmployeeRow {
        String firstName;
        String lastName;
        long barCode;
        String releaseDate;

        public EmployeeRow(String firstName, String lastName, long barCode, String date) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.barCode = barCode;
            this.releaseDate = date;
        }
    }


    @PostMapping("/load_lockers/{sheetToLoad}")
    public void loadLockersFromExcelFile(@RequestParam("file") MultipartFile lockersToLoad,
                                         @PathVariable int sheetToLoad) throws IOException {
        sheetToLoad = sheetToLoad - 1;
        XSSFWorkbook workbook = new XSSFWorkbook(lockersToLoad.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(sheetToLoad);

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);

            Locker locker = new Locker();
            locker.setLockerNumber((int) row.getCell(1).getNumericCellValue());
            locker.setCapacity((int) row.getCell(2).getNumericCellValue());
            locker.setDepartmentNumber(Locker.DepartmentNumber.valueOf(row.getCell(3).getStringCellValue()));
            locker.setDepartment(Department.valueOf(row.getCell(4).getStringCellValue()));
            locker.setLocation(Locker.Location.valueOf(row.getCell(5).getStringCellValue()));

            lockersController.create(locker);
        }
    }

    @GetMapping("/calculate_clothes_value/{articleColumnNo}/{releaseDateColumnNo}/{resultColumnNo}")
    public Float calculateClothesValueFromExcelFile(@PathVariable Integer articleColumnNo,
                                                    @PathVariable Integer releaseDateColumnNo,
                                                    @PathVariable Integer resultColumnNo,
                                                    @RequestParam("file") MultipartFile clothesToCount) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(clothesToCount.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);
        Float totalAmount = 0.0f;

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            String articleNumber = String.valueOf((int) row.getCell(articleColumnNo - 1).getNumericCellValue());
            Date releaseDate = row.getCell(releaseDateColumnNo - 1).getDateCellValue();

            Integer clothValue = CalculateClothesValue.calculateValueForCloth(articleNumber, releaseDate);
            row.createCell(resultColumnNo - 1).setCellValue(clothValue);
            totalAmount += clothValue;
        }
        sheet.createRow(sheet.getPhysicalNumberOfRows()).createCell(resultColumnNo).setCellValue(totalAmount);
        saveWorkbook(workbook);
        return totalAmount;
    }

    @JsonView(Views.InternalForBoxes.class)
    @PostMapping("/change_boxes")
    public List<Box> changeBoxesForEmployeesFromExcelFileAndCreateExcelRaport(@RequestParam("file")
                                                                                      MultipartFile employeesToMove) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(employeesToMove.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        List<Box> boxes = new LinkedList<>();

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            Row row = worksheet.getRow(i);
            int lockerNumber = ((int) row.getCell(3).getNumericCellValue());
            int boxNumber = ((int) row.getCell(4).getNumericCellValue());
            Locker.DepartmentNumber depNumber = Locker.DepartmentNumber.valueOf(row.getCell(5).getStringCellValue());
            Department targetDepartment = Department.valueOf(row.getCell(10).getStringCellValue());
            Locker.Location targetLocation = Locker.Location.valueOf(row.getCell(11).getStringCellValue());
            Locker.DepartmentNumber targetDepNumber = Locker.DepartmentNumber.valueOf(row.getCell(12).getStringCellValue());

            Box box = employeeService.changeEmployeeBoxOnNextFree(lockerNumber, boxNumber, depNumber,
                    targetDepartment, targetLocation, targetDepNumber);

            boxes.add(box);
            row.getCell(7).setCellValue(box.getLocker().getLockerNumber());
            row.getCell(8).setCellValue(box.getBoxNumber());

        }
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/files_to_testing/Lear/raports/" + worksheet.getSheetName() + ".xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        return boxes;
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find_employees")
    public List<Employee> findEmployeesFromExcelFileAndGenerateExcelRaport(@RequestParam("file") MultipartFile employeesToFind) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(employeesToFind.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        int doubledEmployees = 0;
        List<Employee> employeesToFile = new LinkedList<>();
        List<SimpleEmployee> omittedEmployees = new LinkedList<>();
        String previousFirstName = "";
        String previousLastName = "";

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            XSSFRow row = worksheet.getRow(i);
            if (row.getCell(1) == null) continue;
            String lastName = row.getCell(1).getStringCellValue();
            String firstName = row.getCell(2).getStringCellValue();

            //skip row if person doubles
            if ((previousFirstName == firstName) && (previousLastName == lastName)) {
                doubledEmployees++;
                continue;
            } else {
                previousFirstName = firstName;
                previousLastName = lastName;
            }

            //get all employees with particular name
            List<Employee> employeesFromDB = employeeController.getEmployeesByFirstNameAndLastName(firstName, lastName);
            //add employees to final list
            employeesFromDB.stream().forEach(employee -> employeesToFile.add(employee));
            //if there is no employee then add it to list with omitted employees
            if (employeesFromDB.size() == 0) {
                omittedEmployees.add(new SimpleEmployee(firstName, lastName));
            }
        }

        List<Employee> sortedEmployees = employeeController.sortEmployeesByDepartmentLockerAndBox(employeesToFile);
        employeeController.sortEmployeesByDepartmentLockerAndBox(sortedEmployees);


        List<String> columnHeaders = new LinkedList<>();
        Row row = worksheet.getRow(0);
        for (Cell cell : row) {
            String cellHeader = cell.getStringCellValue();
            columnHeaders.add(cellHeader);
        }

        String sheetName = worksheet.getSheetName();
        ExcelWriter excelWriter = new ExcelWriter(columnHeaders, sortedEmployees, sheetName);
        XSSFWorkbook excelRaportWithEmployees = excelWriter.createExcelRaportWithEmployees();

        XSSFSheet sheet = excelRaportWithEmployees.createSheet("Raport");
        sheet.createRow(0).createCell(0).setCellValue("Podwójni:");
        sheet.getRow(0).createCell(1).setCellValue(doubledEmployees);
        sheet.createRow(1).createCell(0).setCellValue("Pominięci:");
        for (int i = 2; i <= omittedEmployees.size() + 1; i++) {
            Row myRow = sheet.createRow(i);
            myRow.createCell(0).setCellValue(omittedEmployees.get(i - 2).getLastName());
            myRow.createCell(1).setCellValue(omittedEmployees.get(i - 2).getFirstName());
        }

        saveWorkbook(excelRaportWithEmployees);

        return sortedEmployees;
    }

    @GetMapping("/download_labels/{fileName.:}")
    public ResponseEntity<Resource> downloadLabels(
            @PathVariable String filename, HttpServletRequest request) {

        Resource resource = fileStorageService.loadFileAsResource(filename);
        String contentType = checkContentType(request, resource);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + resource.getFilename() + "\"")
                .body(resource);

    }

    private String checkContentType(HttpServletRequest request, Resource resource) {

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }

    private XSSFSheet getSheetAtFromFile(int index, MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        return workbook.getSheetAt(index);
    }


}
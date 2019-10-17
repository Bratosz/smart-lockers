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
import pl.bratosz.smartlockers.date.CurrentDate;
import pl.bratosz.smartlockers.exels.ExcelWriter;
//import pl.bratosz.smartlockers.exels.WriteInExcel;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.payload.UploadFileResponse;
import pl.bratosz.smartlockers.service.EmployeeService;
import pl.bratosz.smartlockers.service.FileService;
import pl.bratosz.smartlockers.service.FileStorageService;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/downloadfile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName,
                                                 HttpServletRequest request) {
        //Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        //Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        //Fallback to the default content type if the type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + resource.getFilename() + "\"")
                .body(resource);
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
            Employee loadedEmployee = employeeController.createEmployee(
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
            if (!(workbook.getSheetName(i).equals("METAL") || workbook.getSheetName(i).equals("JIT"))) {
                continue;
            }
            XSSFSheet worksheet = workbook.getSheetAt(i);
            for (int j = 1; j < worksheet.getPhysicalNumberOfRows(); j++) {
                XSSFRow row = worksheet.getRow(j);

                Employee employee = new Employee();
                employee.setFirstName(row.getCell(2).getStringCellValue());
                employee.setLastName(row.getCell(1).getStringCellValue());
                employee.setDepartment(Department.valueOf(worksheet.getSheetName()));

                Locker.Location location;
                if (row.getCell(5).getStringCellValue().equals("stara")) {
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
        CurrentDate date = new CurrentDate();
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

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find_employees")
    public List<Employee> findEmployeesFromExcelFileAndGenerateExcelRaport(@RequestParam("file") MultipartFile employeesToFind) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(employeesToFind.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        List<Employee> employeesToFile = new LinkedList<>();
        String previousFirstName = "";
        String previousLastName = "";

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            String firstName = row.getCell(2).getStringCellValue();
            String lastName = row.getCell(1).getStringCellValue();

            if ((previousFirstName == firstName) && (previousLastName == lastName)) {
                continue;
            } else {
                previousFirstName = firstName;
                previousLastName = lastName;
            }

            //get all employees with particular name
            List<Employee> employeesFromDB = employeeController.getEmployeesByFirstNameAndLastName(firstName, lastName);
            //add employees to final list
            employeesFromDB.stream().forEach(employee -> employeesToFile.add(employee));

            //checking reverse firstName and lastName
            employeesFromDB = employeeController.getEmployeesByFirstNameAndLastName(lastName, firstName);
            employeesFromDB.stream().forEach(employee -> employeesToFile.add(employee));
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
        excelWriter.createExcelRaportWithEmployees();

        return sortedEmployees;
    }


}
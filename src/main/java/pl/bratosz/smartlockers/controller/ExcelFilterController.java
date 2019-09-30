//package pl.bratosz.smartlockers.controller;
//
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//import pl.bratosz.smartlockers.model.TshirtSize;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Set;
//
//@RestController
//@RequestMapping("/excel")
//public class ExcelFilterController {
//    @PostMapping("/countSizes")
//    public void coutTshirtSizesFromExcelFile(@RequestParam("file") MultipartFile sizesToCount) throws IOException {
//        listOfOversizedWomen.clear();
//        listOfUncountedEmployees.clear();
//        XSSFWorkbook workbook = new XSSFWorkbook(sizesToCount.getInputStream());
//        XSSFSheet sheet = workbook.getSheetAt(0);
//
//        int xs = 0;
//        int s = 0;
//        int m = 0;
//        int l = 0;
//        int xl = 0;
//        int xxl = 0;
//        int xxxl = 0;
//        int xxxxl = 0;
//        int xsF = 0;
//        int sF = 0;
//        int mF = 0;
//        int lF = 0;
//        int xlF = 0;
//        int xxlF = 0;
//        int xxxlF = 0;
//        int xxxxlF = 0;
//        int xxxxxlF = 0;
//        int employeesAmount = 0;
//
//
//
//        Set<Integer> articles = new HashSet<>();
//        articles.add(1828);
//        articles.add(3526);
//        articles.add(5011);
//        articles.add(5012);
//        articles.add(5014);
//        articles.add(5016);
//        articles.add(5017);
//        articles.add(5018);
//        articles.add(5019);
//        articles.add(5064);
//        articles.add(5070);
//        articles.add(5071);
//        articles.add(5072);
//        articles.add(5073);
//        articles.add(5074);
//        articles.add(5075);
//        articles.add(5077);
//        articles.add(5099);
//        articles.add(5115);
//        articles.add(5116);
//
//        TshirtSize employee = new TshirtSize(0, 0, false, "");
//
//        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
//            Row row = sheet.getRow(i);
////            if(row.getCell(0).getStringCellValue().length() == 0){
////                break;
////            }
//            int lockerNo = (int) row.getCell(10).getNumericCellValue();
//            int boxNo = (int) row.getCell(11).getNumericCellValue();
//            String firstName = row.getCell(1).getStringCellValue().toLowerCase().trim();
//
//            if (!((employee.getLockerNo() == lockerNo) && (employee.getBoxNo() == boxNo))) {
//                if(employee.isCounted() == false)addUncountedEmployee(employee);
//                employee = new TshirtSize(lockerNo, boxNo, false, firstName);
//                employeesAmount ++;
//            }
//            if (employee.isCounted() == false) {
//                int articleNo = (int) row.getCell(7).getNumericCellValue();
//
//                if (articles.contains(articleNo)) {
//                    String size = row.getCell(8).getStringCellValue();
//                    if (firstName.endsWith("a")) {
//                        if ((articleNo == 5099) || (articleNo == 5115)) {
//                            switch (size) {
//                                case "XS":
//                                    xsF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "S":
//                                    sF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "M":
//                                    mF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "L":
//                                    lF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "XL":
//                                    xlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "XXL":
//                                    xxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "2XL":
//                                    xxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "XXXL":
//                                    xxxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "3XL":
//                                    xxxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "XXXXL":
//                                    xxxxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "4XL":
//                                    xxxxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "5XL":
//                                    xxxxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "XXXXXL":
//                                    xxxxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "NS":
//                                    break;
//                                case "NTP":
//                                    break;
//                            }
//                        } else {
//                            switch (size) {
//                                case "XS":
//                                    lF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "S":
//                                    xlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "M":
//                                    xxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "L":
//                                    xxxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "XL":
//                                    xxxxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "XXL":
//                                    xxxxxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "2XL":
//                                    xxxxxlF++;
//                                    employee.setCounted(true);
//                                    break;
//                                case "XXXL":
//                                    addToListOfOversizeWomen(size, employee);
//                                    employee.setCounted(true);
//                                    break;
//                                case "3XL":
//                                    addToListOfOversizeWomen(size, employee);
//                                    employee.setCounted(true);
//                                    break;
//                                case "XXXXL":
//                                    addToListOfOversizeWomen(size, employee);
//                                    employee.setCounted(true);
//                                    break;
//                                case "4XL":
//                                    addToListOfOversizeWomen(size, employee);
//                                    employee.setCounted(true);
//                                    break;
//                                case "5XL":
//                                    addToListOfOversizeWomen(size, employee);
//                                    employee.setCounted(true);
//                                    break;
//                                case "XXXXXL":
//                                    addToListOfOversizeWomen(size, employee);
//                                    employee.setCounted(true);
//                                    break;
//                                case "NS":
//                                    break;
//                                case "NTP":
//                                    break;
//                            }
//                        }
//                    } else {
//                        switch (size) {
//                            case "XS":
//                                xs++;
//                                employee.setCounted(true);
//                                break;
//                            case "S":
//                                s++;
//                                employee.setCounted(true);
//                                break;
//                            case "M":
//                                m++;
//                                employee.setCounted(true);
//                                break;
//                            case "L":
//                                l++;
//                                employee.setCounted(true);
//                                break;
//                            case "XL":
//                                xl++;
//                                employee.setCounted(true);
//                                break;
//                            case "XXL":
//                                xxl++;
//                                employee.setCounted(true);
//                                break;
//                            case "2XL":
//                                xxl++;
//                                employee.setCounted(true);
//                                break;
//                            case "XXXL":
//                                xxxl++;
//                                employee.setCounted(true);
//                                break;
//                            case "3XL":
//                                xxxl++;
//                                employee.setCounted(true);
//                                break;
//                            case "XXXXL":
//                                xxxxl++;
//                                employee.setCounted(true);
//                                break;
//                            case "4XL":
//                                xxxxl++;
//                                employee.setCounted(true);
//                                break;
//                            case "5XL":
//                                xxxxl++;
//                                employee.setCounted(true);
//                                break;
//                            case "XXXXXL":
//                                xxxxl++;
//                                employee.setCounted(true);
//                                break;
//                            case "NS":
//                                break;
//                            case "NTP":
//                                break;
//                        }
//
//                    }
//
//                }
//            }
//        }
//        List<Integer> sizes = new LinkedList<>();
//        sizes.add(xs);
//        sizes.add(s);
//        sizes.add(m);
//        sizes.add(l);
//        sizes.add(xl);
//        sizes.add(xxl);
//        sizes.add(xxxl);
//        sizes.add(xxxxl);
//        sizes.add(xsF);
//        sizes.add(sF);
//        sizes.add(mF);
//        sizes.add(lF);
//        sizes.add(xlF);
//        sizes.add(xxlF);
//        sizes.add(xxxlF);
//        sizes.add(xxxxlF);
//        sizes.add(xxxxxlF);
//        int sum = 0;
//        XSSFSheet summary = workbook.createSheet("podsumowanie" + workbook.getNumberOfSheets());
//        for (int i = 0; i < sizes.size(); i++) {
//            summary.createRow(i).createCell(0).setCellValue(sizes.get(i));
//            sum += sizes.get(i);
//        }
//        summary.createRow(sizes.size()).createCell(0).setCellValue(sum);
//        summary.createRow(sizes.size() +1).createCell(0).setCellValue(employeesAmount);
//        summary.getRow(0)
//                .createCell(2)
//                .setCellValue("Lista niestandardowych rozmiarów:");
//        for(int i = 0; i < listOfOversizedWomen.size(); i++){
//            summary.getRow(i).createCell(3)
//                    .setCellValue(listOfOversizedWomen.get(i));
//        }
//        XSSFSheet skipedBoxes = workbook.createSheet("pominięci" + workbook.getNumberOfSheets());
//        for(int i = 0; i < listOfUncountedEmployees.size(); i++){
//            skipedBoxes.createRow(i).createCell(5)
//                    .setCellValue(listOfUncountedEmployees.get(i));
//        }
//        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/KLS/Koszulki/rozmiary_koszulek_" + workbook.getSheetName(0) + ".xlsx");
//        workbook.write(fileOut);
//        fileOut.close();
//        workbook.close();
//
//    }
//
//
//
//    static List<String> listOfOversizedWomen = new LinkedList<>();
//    static List<String> listOfUncountedEmployees = new LinkedList<>();
//
//    private static void addToListOfOversizeWomen(String size, TshirtSize employee){
//        String message = employee.getLockerNo() + "/" + employee.getBoxNo() + " męska " + size
//                + " " + employee.getFirstName();
//        listOfOversizedWomen.add(message);
//    }
//    private static void addUncountedEmployee(TshirtSize employee) {
//        String message = employee.getLockerNo() + "/" + employee.getBoxNo();
//        listOfUncountedEmployees.add(message);
//    }
//}

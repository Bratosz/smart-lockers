//package pl.bratosz.smartlockers.controller;
//
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//import pl.bratosz.smartlockers.service.ExcelService;
//
//
//@RestController
//@RequestMapping("/rotational_clothing")
//public class RotationalClothingController {
//
//    private ExcelService excelService;
//
//    public RotationalClothingController(ExcelService excelService) {
//        this.excelService = excelService;
//    }
//
//    @PostMapping("/load")
//    public void load(@RequestParam("file") MultipartFile file) {
//        XSSFSheet workSheet = excelService.extractSheet(file, ExcelDataType.ROTATIONAL_CLOTHING);
//        rotationalClothingService.load(workSheet);
//    }
//}

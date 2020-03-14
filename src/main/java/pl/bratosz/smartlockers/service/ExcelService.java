package pl.bratosz.smartlockers.service;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.bratosz.smartlockers.exels.ExcelDataType;

@Service
public class ExcelService {

    public ExcelService(){}


    public XSSFSheet extractSheet(MultipartFile file, ExcelDataType dataType){
        return null;
    }

}

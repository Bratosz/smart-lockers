package pl.bratosz.smartlockers.service;


import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.property.FileStorageProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ExcelReaderService {
    private final String fileStorageLocation;

    @Autowired
    public ExcelReaderService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize().toString();
    }


}

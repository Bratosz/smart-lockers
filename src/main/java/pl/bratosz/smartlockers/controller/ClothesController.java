package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.bratosz.smartlockers.service.exels.ClothOperationType;
import pl.bratosz.smartlockers.service.exels.ExcelExtractor;
import pl.bratosz.smartlockers.model.Cloth;
import pl.bratosz.smartlockers.model.RotationalCloth;
import pl.bratosz.smartlockers.service.ClothesService;

import java.io.IOException;
import java.util.List;

import static pl.bratosz.smartlockers.model.Views.*;

@RestController
@RequestMapping("/clothes")
public class ClothesController {

    private ClothesService clothesService;

    @Autowired
    public ClothesController(ClothesService clothesService) {
        this.clothesService = clothesService;
    }

    @JsonView(InternalForClothes.class)
    @PostMapping("/upload/{clothOperationType}")
    public List<Cloth> uploadClothes(
            @RequestParam("file")MultipartFile file,
            @PathVariable ClothOperationType clothOperationType) throws IOException {
        Sheet sheet = ExcelExtractor.getSheet(file);
        return clothesService.uploadClothesRotation(sheet, clothOperationType);
    }

    @JsonView(InternalForClothes.class)
    @PostMapping("/upload_released_rotation/{clothOperationType}")
    public List<RotationalCloth> uploadReleasedRotation(
            @RequestParam("file")MultipartFile file,
            @PathVariable ClothOperationType clothOperationType) throws IOException {
        Sheet sheet = ExcelExtractor.getSheet(file);
        return clothesService.updateReleasedRotation(sheet, clothOperationType);
    }

    @JsonView(InternalForClothes.class)
    @GetMapping("/{id}")
    public Cloth getClothById(@PathVariable Long id){
        return clothesService.getClothById(id);
    }

    @JsonView(InternalForClothes.class)
    @GetMapping("/rotational_raport")
    public List<RotationalCloth> getRotationalClothRaport() throws IOException {
        return clothesService.getRotationalClothRaport();
    }
}

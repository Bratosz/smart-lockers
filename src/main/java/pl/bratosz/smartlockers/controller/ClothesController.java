package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.bratosz.smartlockers.exels.ExcelExtractor;
import pl.bratosz.smartlockers.model.Cloth;
import pl.bratosz.smartlockers.model.EmployeeCloth;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.service.ClothesService;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/clothes")
public class ClothesController {

    private ClothesService clothesService;

    @Autowired
    public ClothesController(ClothesService clothesService) {
        this.clothesService = clothesService;
    }

    @JsonView(Views.InternalForClothes.class)
    @PostMapping("/update_clothes")
    public Set<Cloth> uploadClothes(@RequestParam("file")MultipartFile file) throws IOException {
        Sheet sheet = ExcelExtractor.getSheet(file);
        Set<Cloth> cloths = clothesService.update(sheet);
        return cloths;
    }

    @JsonView(Views.InternalForClothes.class)
    @GetMapping("/{id}")
    public Cloth getClothById(@PathVariable Long id){
        return clothesService.getClothById(id);
    }
}

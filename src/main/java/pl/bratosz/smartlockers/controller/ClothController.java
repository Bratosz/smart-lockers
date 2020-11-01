package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.bratosz.smartlockers.model.OrderType;
import pl.bratosz.smartlockers.response.ResponseClothAcceptance;
import pl.bratosz.smartlockers.service.exels.LoadType;
import pl.bratosz.smartlockers.service.exels.ExcelExtractor;
import pl.bratosz.smartlockers.model.Cloth;
import pl.bratosz.smartlockers.service.ClothService;

import java.io.IOException;
import java.util.List;

import static pl.bratosz.smartlockers.model.Views.*;

@RestController
@RequestMapping("/clothes")
public class ClothController {

    private ClothService clothesService;

    @Autowired
    public ClothController(ClothService clothesService) {
        this.clothesService = clothesService;
    }

    @JsonView(InternalForClothes.class)
    @PostMapping("/upload/{loadType}")
    public List<Cloth> uploadClothes(
            @RequestParam("file")MultipartFile file,
            @PathVariable LoadType loadType) throws IOException {
        Sheet sheet = ExcelExtractor.getSheet(file);
        return clothesService.uploadClothesRotation(sheet, loadType);
    }

    @JsonView(InternalForClothes.class)
    @PostMapping("/upload_released_rotation/{loadType}")
    public List<Cloth> uploadReleasedRotation(
            @RequestParam("file")MultipartFile file,
            @PathVariable LoadType loadType) throws IOException {
        Sheet sheet = ExcelExtractor.getSheet(file);
        return clothesService.updateReleasedRotation(sheet, loadType);
    }

    @JsonView(InternalForClothes.class)
    @GetMapping("/{id}")
    public Cloth getClothById(@PathVariable Long id){
        return clothesService.getClothById(id);
    }

    @JsonView(InternalForClothes.class)
    @GetMapping("/rotational_raport")
    public List<Cloth> getRotationalClothRaport() throws IOException {
        return clothesService.getRotationalClothRaport();
    }

    @JsonView(InternalForClothes.class)
    @PostMapping("/acceptance/{clientId}/{userId}/{clothId}/{orderType}")
    public int accept(
            @PathVariable long userId, @PathVariable long clientId,
            @PathVariable long clothId, @PathVariable OrderType orderType) {
        clothesService.accept(clientId, userId, clothId, orderType);
        return 1;
    }
}

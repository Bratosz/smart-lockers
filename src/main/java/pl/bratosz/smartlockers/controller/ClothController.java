package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.response.ResponseClothAcceptance;
import pl.bratosz.smartlockers.service.ClothService;

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
    @GetMapping("/{id}")
    public Cloth getClothById(@PathVariable long id){
        return clothesService.getClothById(id);
    }

    @JsonView(InternalForClothes.class)
    @PostMapping("/acceptance/{clientId}/{userId}/{clothBarCode}/{orderType}")
    public ResponseClothAcceptance accept(
            @PathVariable long clientId,
            @PathVariable long userId,
            @PathVariable long clothBarCode,
            @PathVariable OrderType orderType) {
        return clothesService.accept(clientId, userId, clothBarCode, orderType);
    }
}

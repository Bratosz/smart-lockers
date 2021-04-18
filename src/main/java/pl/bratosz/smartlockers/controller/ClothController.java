package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.response.ResponseClothAcceptance;
import pl.bratosz.smartlockers.response.ResponseClothAssignment;
import pl.bratosz.smartlockers.service.ClothService;

import javax.xml.ws.Response;

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

    @PostMapping("/assign-withdrawn-cloth/{clientId}/{userId}/{employeeId}/{articleNumber}/{size}")
    public ResponseClothAssignment assignWithdrawnCloth(
            @PathVariable long clientId,
            @PathVariable long userId,
            @PathVariable long employeeId,
            @PathVariable int articleNumber,
            @PathVariable ClothSize size,
            @RequestBody Cloth withdrawnCloth) {
        return clothesService.assignWithdrawnCloth(
                clientId, userId, employeeId, articleNumber, size, withdrawnCloth);
    }

    @PostMapping("/release-rotational-cloth/{clientId}/{userId}/{employeeId}/{barcode}")
    public ResponseClothAssignment releaseRotationalCloth(
            long clientId,
            long userId,
            long employeeId,
            long barcode) {
        return clothesService.releaseRotationalCloth(
                clientId, userId, employeeId, barcode);
    }
}

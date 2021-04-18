package pl.bratosz.smartlockers.service.managers;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothDestination;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.service.SimpleBoxService;

import java.util.List;

@Service
public class EmployeeManager {
    private ClothesManager clothesManager;
    private BoxManager boxManager;
    private Employee employee;
    private SimpleBoxService simpleBoxService;
    private User user;

    public EmployeeManager(ClothesManager clothesManager, BoxManager boxManager, SimpleBoxService simpleBoxService) {
        this.clothesManager = clothesManager;
        this.boxManager = boxManager;
        this.simpleBoxService = simpleBoxService;
    }

    //ubrania do ustawienia do wycofania,
    //  zamówienia na ubraniach do anulowania
    //szafka do zwolnienia

    public Employee dismiss(Employee employee, User user) {
        this.employee = employee;
        this.user = user;
        updateBoxAndSetAsPastBoxForDismiss();
        updateClothesForDismiss();
        updateEmployeeForDismiss();
        return this.employee;
    }

    private void updateEmployeeForDismiss() {
        employee.setActive(false);
        employee.setBox(null);
    }

    private void updateClothesForDismiss() {
        List<Cloth> clothes = employee.getClothes();
        clothes = clothesManager.set(ClothDestination.FOR_WITHDRAW_AND_DELETE, clothes, user);
        employee.setClothes(clothes);
    }

    private void updateBoxAndSetAsPastBoxForDismiss() {
        Box box = employee.getBox();
        boxManager.release(box);
        employee.setAsPastBox(
                simpleBoxService.createSimpleBox(box, employee));
    }
}
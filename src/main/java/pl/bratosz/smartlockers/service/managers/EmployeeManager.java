package pl.bratosz.smartlockers.service.managers;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothDestination;
import pl.bratosz.smartlockers.model.users.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeManager {
    private ClothesManager clothesManager;
    private Employee employee;
    private User user;

    public EmployeeManager(ClothesManager clothesManager) {
        this.clothesManager = clothesManager;
    }

    //ubrania do ustawienia do wycofania,
    //  zamówienia na ubraniach do anulowania
    //szafka do zwolnienia

    public Employee dismiss(Employee employee, User user) {
        this.employee = employee;
        this.user = user;
        updateEmployeeForDismiss();
        updateBoxForDismiss();
        updateClothesForDismiss();
        return employee;
    }

    private void updateEmployeeForDismiss() {
        employee.setActive(false);
    }

    private void updateClothesForDismiss() {
        List<Cloth> clothes = employee.getClothes();
        clothes = clothesManager.set(ClothDestination.FOR_WITHDRAW_AND_DELETE, clothes, user);
        employee.setClothes(clothes);
    }

    private void updateBoxForDismiss() {
        Box box = employee.getBox();
        BoxManager boxManager = new BoxManager(user);
        box = boxManager.release(box);
        employee.setAsPastBox(box);
    }
}
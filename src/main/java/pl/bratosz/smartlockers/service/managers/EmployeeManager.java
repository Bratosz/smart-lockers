package pl.bratosz.smartlockers.service.managers;

import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothDestination;
import pl.bratosz.smartlockers.model.users.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class EmployeeManager {
    private User user;
    private Date date;
    private Employee employee;

    public EmployeeManager (User user) {
        this.user = user;
        this.date = new Date();
    }

    public EmployeeManager (Employee employee, User user) {
        this.employee = employee;
        this.user = user;
        this.date = new Date();
    }

    //ubrania do ustawienia do wycofania,
    //  zamówienia na ubraniach do anulowania
    //szafka do zwolnienia

    public Employee dismiss() {
        updateEmployeeForDismiss();
        updateBoxForDismiss();
        updateClothesForDismiss();
        return employee;
    }

    public Employee dismiss(Employee employee) {
        this.employee = employee;
        return dismiss();
    }

    private void updateEmployeeForDismiss() {
        employee.setActive(false);
    }

    private void updateClothesForDismiss() {
        List<Cloth> clothes = employee.getClothes();
        ClothesManager clothesManager = new ClothesManager(user, date);
        clothes = clothesManager.set(ClothDestination.FOR_WITHDRAW_AND_DELETE, clothes);
        employee.setClothes(clothes);
    }

    private void updateBoxForDismiss() {
        Box box = employee.getBox();
        BoxManager boxManager = new BoxManager (user, date);
        box = boxManager.release(box);
        employee.setAsPastBox(box);
    }
}
package pl.bratosz.smartlockers.service.managers.creators;

import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.EmployeeDummy;
import pl.bratosz.smartlockers.model.Locker;


import javax.persistence.criteria.CriteriaBuilder;
import java.util.LinkedList;
import java.util.List;

public class BoxCreator {

    public static List<Box> createBoxes(Locker locker) {
        List<Box> boxes = new LinkedList<>();
        int lockerCapacity = locker.getCapacity();
        for (int i = 1; i <= lockerCapacity; i++) {
            boxes.add(
                    createBox(i, locker));
        }
        return boxes;
    }

    public static List<Box> createBoxesWithCustomNumbers(Locker locker, List<Integer> boxNumbers) {
        List<Box> boxes = new LinkedList<>();
        for(Integer boxNumber : boxNumbers) {
            boxes.add(
                    createBox(boxNumber, locker));
        }
        return boxes;
    }

    public static Box createBox(int boxNumber, Locker locker) {
        Box box = new Box(boxNumber);
        EmployeeDummy employeeDummy = new EmployeeDummy(box);
        box.setEmployee(employeeDummy);
        box.setEmployeeDummy(employeeDummy);
        box.setLocker(locker);
        return box;
    }
}

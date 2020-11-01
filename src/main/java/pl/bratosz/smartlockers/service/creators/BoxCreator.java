package pl.bratosz.smartlockers.service.creators;

import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.EmployeeDummy;


import java.util.LinkedList;
import java.util.List;

public class BoxCreator {

    public List<Box> createBoxesForLocker(int lockerCapacity) {
        List<Box> boxes = new LinkedList<>();
        for (int i = 1; i <= lockerCapacity; i++) {
            Box box = new Box(i);
            EmployeeDummy employeeDummy = new EmployeeDummy(box);
            box.setEmployee(employeeDummy);
            box.setEmployeeDummy(employeeDummy);
            boxes.add(box);
        }
        return boxes;
    }
}

package pl.bratosz.smartlockers.service.managers.creators;

import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.EmployeeDummy;
import pl.bratosz.smartlockers.model.Locker;


import java.util.LinkedList;
import java.util.List;

public class BoxCreator {

    public List<Box> createBoxesForLocker(Locker locker) {
        List<Box> boxes = new LinkedList<>();
        int lockerCapacity = locker.getCapacity();
        for (int i = 1; i <= lockerCapacity; i++) {
            Box box = new Box(i);
            EmployeeDummy employeeDummy = new EmployeeDummy(box);
            box.setEmployee(employeeDummy);
            box.setEmployeeDummy(employeeDummy);
            box.setLocker(locker);
            boxes.add(box);
        }
        return boxes;
    }
}

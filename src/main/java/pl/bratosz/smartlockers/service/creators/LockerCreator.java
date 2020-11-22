package pl.bratosz.smartlockers.service.creators;

import pl.bratosz.smartlockers.exception.EmptyElementException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.service.exels.RowForBasicDataBaseUpload;

import java.util.List;
import java.util.NoSuchElementException;

public class LockerCreator {
    private Client client;

    public LockerCreator(Client client) {
        this.client = client;
    }

    public Locker createFromRowWithBoxes(RowForBasicDataBaseUpload row) {
        int lockerNumber = row.getLockerNumber();
        int capacity = row.getCapacity();
        try {
            Plant plant = client.getPlantByNumber(row.getPlantNumber());
            Location location = client.getLocationByName(row.getLocationName());
            Department department = client.getDepartmentByName(row.getDepartmentName());

            Locker locker = new Locker();
            locker.setCapacity(capacity);
            BoxCreator boxCreator = new BoxCreator();
            List<Box> boxes = boxCreator.createBoxesForLocker(locker);

            locker.setLockerNumber(lockerNumber);
            locker.setPlant(plant);
            locker.setDepartment(department);
            locker.setLocation(location);
            locker.setBoxes(boxes);

            return locker;
        } catch (NoSuchElementException e) {
            throw new EmptyElementException("Locker #" + lockerNumber + " has not been created.\n" + row.getLocationName() + "\n"+ row.getLastName());
        }

    }


}

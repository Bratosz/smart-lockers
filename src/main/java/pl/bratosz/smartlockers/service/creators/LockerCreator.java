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
            BoxCreator boxCreator = new BoxCreator();
            List<Box> boxes = boxCreator.createBoxesForLocker(capacity);
            return new Locker(lockerNumber, capacity, plant,
                    department, location, boxes);
        } catch (NoSuchElementException e) {
            throw new EmptyElementException("Locker #" + lockerNumber + " has not been created.\n" + row.getLocationName() + "\n"+ row.getLastName());
        }

    }


}

package pl.bratosz.smartlockers.comparators;

import pl.bratosz.smartlockers.model.Employee;

import java.util.Comparator;

public class DepartmentNumberSorter implements Comparator<Employee> {

    @Override
    public int compare(Employee o1, Employee o2) {
        return o1.getFirstLockerPlantNumber().getNumber()
                - o2.getFirstLockerPlantNumber().getNumber();
    }
}

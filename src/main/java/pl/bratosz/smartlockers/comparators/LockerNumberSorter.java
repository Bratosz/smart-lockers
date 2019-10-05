package pl.bratosz.smartlockers.comparators;

import pl.bratosz.smartlockers.model.Employee;

import java.util.Comparator;

public class LockerNumberSorter implements Comparator<Employee> {
    @Override
    public int compare(Employee o1, Employee o2) {
        return o1.getFirstLockerNumber() - o2.getFirstLockerNumber();
    }
}

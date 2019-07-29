package pl.bratosz.smartlockers.converters;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.stereotype.Component;
import pl.bratosz.smartlockers.model.Locker;

@Component
public class DepartmentNumberConverter extends EnumConverter<Locker.DepartmentNumber> {
    public DepartmentNumberConverter() {
        super(Locker.DepartmentNumber.class);
    }
}

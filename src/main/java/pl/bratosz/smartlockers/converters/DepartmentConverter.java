package pl.bratosz.smartlockers.converters;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.stereotype.Component;
import pl.bratosz.smartlockers.model.Department;

@Component
public class DepartmentConverter extends EnumConverter<Department> {
    public DepartmentConverter() {
        super(Department.class);
    }
}

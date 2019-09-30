package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import pl.bratosz.smartlockers.converters.DepartmentConverter;

public enum Department {
    METAL("Structures"),
    JIT("JIT"),
    MANTRANS("Mantrans"),
    COMMON("Wspólny");

    private String name;


    Department(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

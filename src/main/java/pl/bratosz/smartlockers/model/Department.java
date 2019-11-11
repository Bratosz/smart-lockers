package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import pl.bratosz.smartlockers.converters.DepartmentConverter;

public enum Department {
    METAL("STRUCTURES"),
    JIT("JIT"),
    MANTRANS("MANTRANS"),
    COMMON("WSPÓLNY"),
    NOTFOUND("Nie znaleziono pracownika");

    private String name;


    Department(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

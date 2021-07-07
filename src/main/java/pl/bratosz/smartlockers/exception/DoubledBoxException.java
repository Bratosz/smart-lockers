package pl.bratosz.smartlockers.exception;

import pl.bratosz.smartlockers.model.SimpleEmployee;

public class DoubledBoxException extends Exception {
    public SimpleEmployee employee;

    public DoubledBoxException() {
    }

    public DoubledBoxException(SimpleEmployee employee) {
        this.employee = employee;
    }

    public SimpleEmployee getEmployee() {
        return employee;
    }
}

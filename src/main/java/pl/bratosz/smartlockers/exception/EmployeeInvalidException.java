package pl.bratosz.smartlockers.exception;

public class EmployeeInvalidException extends Exception {

    public EmployeeInvalidException() {
    }

    public EmployeeInvalidException(String message) {
        super(message);
    }
}

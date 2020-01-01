package pl.bratosz.smartlockers.exception;

public class BoxNotAvailableException extends RuntimeException {
    public BoxNotAvailableException() {
    }

    public BoxNotAvailableException(String message) {
        super(message);
    }
}

package pl.bratosz.smartlockers.exception;

import java.util.function.Supplier;

public class EmptyElementException extends RuntimeException {
    public EmptyElementException(String message) {
        super(message);
    }
}

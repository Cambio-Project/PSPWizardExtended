package psp.io_api.exceptions;

import java.io.IOException;

public class UnsupportedTypeException extends IOException {
    public UnsupportedTypeException(String errorMessage) {
        super(errorMessage);
    }
}
package restapi.psp_mapping.exceptions;

import java.io.IOException;

public class UnsupportedTypeException extends IOException {
    public UnsupportedTypeException(String errorMessage) {
        super(errorMessage);
    }
}
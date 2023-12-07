package psp.io_api.exceptions;

import java.io.IOException;

public class NullFieldException  extends IOException {
  public NullFieldException(String errorMessage) {
    super(errorMessage);
}
}

package psp.io_api.exceptions;

import java.io.IOException;

public class IllegalScopeEvents extends IOException {
  public IllegalScopeEvents(String errorMessage) {
    super(errorMessage);
}
}

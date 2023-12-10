package restapi.psp_mapping.exceptions;

import java.io.IOException;

public class IllegalScopeEvents extends IOException {
  public IllegalScopeEvents(String errorMessage) {
    super(errorMessage);
}
}

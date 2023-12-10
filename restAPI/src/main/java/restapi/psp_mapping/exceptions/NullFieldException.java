package restapi.psp_mapping.exceptions;

import java.io.IOException;

public class NullFieldException  extends IOException {
  public NullFieldException(String errorMessage) {
    super(errorMessage);
}
}

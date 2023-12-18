package psp.io_api.json_processing.data_objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class PSPCorrectMappingResponse implements PSPMappingResponse {

  private final String TYPE = "correct_response";
  private final Map<String, String> payload;

  public PSPCorrectMappingResponse(String seg, String mappedPSP){
    this.payload = new HashMap<>();
    payload.put("mapping", mappedPSP);
    payload.put("seg", seg);
  }

  public Map<String, String> getPayload() {
    return payload;
  }

  public String getType() {
    return TYPE;
  }

  @Override
  public String toJSON() throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(this);
  }
}

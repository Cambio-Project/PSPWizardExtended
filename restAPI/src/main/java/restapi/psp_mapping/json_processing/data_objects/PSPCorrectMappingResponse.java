package restapi.psp_mapping.json_processing.data_objects;

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
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof PSPCorrectMappingResponse))
      return false;
    PSPCorrectMappingResponse other = (PSPCorrectMappingResponse)o;
    return this.payload.equals(other.getPayload());
  }

  @Override
  public String toJSON() throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(this);
  }
}

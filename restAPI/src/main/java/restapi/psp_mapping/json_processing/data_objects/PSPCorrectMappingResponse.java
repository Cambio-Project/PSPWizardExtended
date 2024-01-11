package restapi.psp_mapping.json_processing.data_objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code PSPCorrectMappingResponse} class represents a correct response from a PSP mapping operation.
 * It implements the {@link PSPMappingResponse} interface and provides methods to retrieve the response type,
 * payload details, and convert the response to its JSON representation.
 *
 * @author Aref El-Maarawi
 *
 */
public class PSPCorrectMappingResponse implements PSPMappingResponse {

  /**
   * The response type indicating a correct mapping response.
   */
  private final String TYPE = "correct_response";

  /**
   * The payload containing mapping details and SEG representation.
   */
  private final Map<String, String> payload;


  /**
   * Constructs a new PSPCorrectMappingResponse with the provided SEG and mapped PSP details.
   *
   * @param seg       The SEG (Structured English Grammar) representation.
   * @param mappedPSP The mapped PSP (Pattern Specificity Pattern) details.
   */
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

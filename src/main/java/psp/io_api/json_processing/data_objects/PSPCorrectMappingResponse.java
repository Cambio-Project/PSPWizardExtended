package psp.io_api.json_processing.data_objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PSPCorrectMappingResponse implements PSPMappingResponse{

  private String seg;
  @JsonProperty("mapped_psp")
  private String mappedPSP;

  public PSPCorrectMappingResponse(String seg, String mappedPSP){
    this.seg = seg;
    this.mappedPSP = mappedPSP;
  }

  public String getMappedPSP() {
    return mappedPSP;
  }

  public String getSeg() {
    return seg;
  }
}

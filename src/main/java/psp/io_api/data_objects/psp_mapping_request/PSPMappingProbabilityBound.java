package psp.io_api.data_objects.psp_mapping_request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PSPMappingProbabilityBound {
  @JsonProperty("type")
  private String type;
  private double probability;

  public PSPMappingProbabilityBound probabilityBound;

  public String getTpye() {
    return type;
  }

  public double getprobability() {
    return probability;
  }
}

package restapi.psp_mapping.data_objects.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PSPMappingPatternSpecifications {
  @JsonProperty("time_unit")
  private String timeUnit;
  @JsonProperty("upper_limit")
  private long upperLimit;
  private long frequency;

  public PSPMappingPatternSpecifications() {
  }

  public String getTimeUnit() {
    return timeUnit;
  }

  public long getUpperLimit(){
    return upperLimit;
  }

  public long getFrequency(){
    return frequency;
  }

}

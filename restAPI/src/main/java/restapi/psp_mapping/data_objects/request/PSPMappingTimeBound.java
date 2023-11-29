package restapi.psp_mapping.data_objects.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PSPMappingTimeBound {
  private String type;
  @JsonProperty("time_unit")
  private String timeUnit;
  @JsonProperty("upper_limit")
  private long upperLimit;
  @JsonProperty("lower_limit")
  private long lowerLimit;

  public PSPMappingTimeBound(){}

  public String getType() {
    return type;
  }

  public String getTimeUnit() {
    return timeUnit;
  }

  public long getUpperLimit(){
    return upperLimit;
  }

  public long getLowerLimit(){
    return lowerLimit;
  }
}

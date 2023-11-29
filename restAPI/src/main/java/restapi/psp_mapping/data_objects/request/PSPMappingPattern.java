package restapi.psp_mapping.data_objects.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PSPMappingPattern {
  private String type;
  private PSPMappingEvent event;
  @JsonProperty("pattern_specifications")
  private PSPMappingPatternSpecifications patternSpecifications;
  @JsonProperty("time_bound")
  private PSPMappingTimeBound timeBound;
  @JsonProperty("probability_bound")
  private PSPMappingProbabilityBound probabilityBound;


  public PSPMappingPattern(){}

  public String getType() {
  return type;
  }

  public PSPMappingEvent getEvent() {
    return event;
  }

  public PSPMappingPatternSpecifications getPatternSpecifications() {
    return patternSpecifications;
  }

  public PSPMappingTimeBound getTimeBound(){
    return timeBound;
  }

  public PSPMappingProbabilityBound getProbabilityBound() {
    return probabilityBound;
  }
}

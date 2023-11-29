package psp.io_api.data_objects.psp_mapping_request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PSPMappingScope {

  private String type;
  @JsonProperty("r_event")
  private PSPMappingEvent qEvent;
  @JsonProperty("q_event")
  private PSPMappingEvent rEvent;


  public PSPMappingScope(){}

  public String getType(){
    return type;
  }

  public PSPMappingEvent getQEvent(){
    return qEvent;
  }

  public PSPMappingEvent getREvent(){
    return rEvent;
  }

}

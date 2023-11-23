package psp.io_api.data_objects.psp_mapping_request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PSPMappingRequest {

    private PSPMappingScope scope;
    private PSPMappingPattern pattern;
    @JsonProperty("target_logic")
    private String targetLogic;

    public PSPMappingRequest(){}

    public PSPMappingScope getScope() {
        return scope;
    }

    public PSPMappingPattern getPattern() {
        return pattern;
    }

    public String getTargetLogic() {
        return targetLogic;
    }
}



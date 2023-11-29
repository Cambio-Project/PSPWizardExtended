package restapi.psp_mapping.data_objects.request;

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



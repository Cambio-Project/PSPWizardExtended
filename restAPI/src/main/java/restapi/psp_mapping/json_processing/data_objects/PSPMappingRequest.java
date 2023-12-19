package restapi.psp_mapping.json_processing.data_objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import psp.mappings.PatternMapper;
import psp.sel.scopes.Scope;
import psp.sel.patterns.Pattern;
import restapi.psp_mapping.json_processing.custom_deserializers.MapperDeserializer;
import restapi.psp_mapping.json_processing.custom_deserializers.PatternDeserializer;
import restapi.psp_mapping.json_processing.custom_deserializers.ScopeDeserializer;

public class PSPMappingRequest {

    @JsonProperty("scope") @JsonDeserialize(using = ScopeDeserializer.class)
    private Scope scope;
    @JsonProperty("pattern") @JsonDeserialize(using = PatternDeserializer.class)
    private Pattern pattern;
    @JsonProperty("target_logic") @JsonDeserialize(using = MapperDeserializer.class)
    private PatternMapper mapper;

    public Scope getScope() {
        return scope;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public PatternMapper getMapper() {
        return mapper;
    }
}



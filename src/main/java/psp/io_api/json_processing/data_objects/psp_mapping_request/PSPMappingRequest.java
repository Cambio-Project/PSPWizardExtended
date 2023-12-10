package psp.io_api.json_processing.data_objects.psp_mapping_request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import psp.io_api.json_processing.custom_deserializers.MapperDeserializer;
import psp.io_api.json_processing.custom_deserializers.PatternDeserializer;
import psp.io_api.json_processing.custom_deserializers.ScopeDeserializer;
import psp.mappings.PatternMapper;
import psp.sel.scopes.Scope;
import psp.sel.patterns.Pattern;

public class PSPMappingRequest {

    @JsonDeserialize(using = ScopeDeserializer.class)
    private Scope scope;
    @JsonDeserialize(using = PatternDeserializer.class)
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



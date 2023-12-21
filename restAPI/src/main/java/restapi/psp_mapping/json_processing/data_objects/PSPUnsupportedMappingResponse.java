package restapi.psp_mapping.json_processing.data_objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class PSPUnsupportedMappingResponse implements PSPMappingResponse{
    private final String TYPE = "unsupported_mapping";
    private final Map<String, String> payload;

    public Map<String, String> getPayload() {
        return payload;
    }

    public String getType() {
        return TYPE;
    }


    public PSPUnsupportedMappingResponse(String mappingErrorMessage, String seg) {
        this.payload = new HashMap<>();
        payload.put("error", mappingErrorMessage);
        payload.put("seg", seg);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PSPUnsupportedMappingResponse))
            return false;
        PSPUnsupportedMappingResponse other = (PSPUnsupportedMappingResponse)o;
        return this.payload.equals(other.getPayload());
    }

    @Override
    public String toJSON() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}

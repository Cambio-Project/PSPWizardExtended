package restapi.psp_mapping.json_processing.data_objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * The {@code PSPUnsupportedMappingResponse} class represents an unsupported mapping response
 * from a PSP (Pattern Specificity Pattern) mapping operation.
 * It implements the {@link PSPMappingResponse} interface and provides methods to retrieve
 * the response type, payload details, and convert the response to its JSON representation.
 *
 * @author Aref El-Maarawi
 *
 */
public class PSPUnsupportedMappingResponse implements PSPMappingResponse{

    /**
     * The response type indicating an unsupported mapping response.
     */
    private final String TYPE = "unsupported_mapping";

    /**
     * The payload containing error message and SEG representation.
     */
    private final Map<String, String> payload;

    public Map<String, String> getPayload() {
        return payload;
    }

    public String getType() {
        return TYPE;
    }

    /**
     * Constructs a new PSPUnsupportedMappingResponse with the provided error message and SEG details.
     *
     * @param mappingErrorMessage The error message describing the unsupported mapping.
     * @param seg                 The SEG (Structured English Grammar) representation.
     */
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

package restapi.psp_mapping.json_processing.data_objects;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface PSPMappingResponse {
    String toJSON() throws JsonProcessingException;
}

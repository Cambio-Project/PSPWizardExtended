package psp.io_api.json_processing.data_objects;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface PSPMappingResponse {
    public String toJSON() throws JsonProcessingException;
}

package restapi.psp_mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import psp.mappings.PatternMapper;
import psp.mappings.SELMapper;
import psp.sel.patterns.Pattern;
import psp.sel.scopes.Scope;
import restapi.psp_mapping.json_processing.data_objects.*;

import java.io.IOException;


/**
 * A service that is responsible for mapping a PSP, formatted as a json object, to a logic formula,
 * using PSPWizard domain conventions (like objects' types).
 * This service delegates the actual mapping to the PSPWizard engine.
 *
 * @author Aref El-Maarawi & Marvin Taube
 *
 */
@Service
public class PSPMappingService {

    /**
     * Maps a PSP to a mapping response.
     *
     * @param jsonMappingRequest The JSON representation of the PSP mapping request.
     * @return PSPMappingResponse object that represents the mapping result.
     * @throws IOException If an error occurs during JSON processing, such as issues with deserialization.
     */
    public PSPMappingResponse mapPSPRequestToTargetLogic(String jsonMappingRequest) throws IOException {

        PSPMappingRequest request = mapJsonToPSPRequest(jsonMappingRequest);
        Scope scope = request.getScope();
        Pattern pattern = request.getPattern();
        PatternMapper requestedPSPMapper = request.getMapper();
        PatternMapper selMapper = new SELMapper();

        String seg =  selMapper.getMapping(scope, pattern);
        String mapping = requestedPSPMapper.getMapping(scope, pattern);

        if (mapping.isEmpty()){
            String errorMessage = requestedPSPMapper.getNotSupportedMessage();
            return new PSPUnsupportedMappingResponse(errorMessage, seg);
        }
        else {
            return new PSPCorrectMappingResponse(seg, mapping);
        }

    }

    /**
     * Maps a JSON string representing a PSPMappingRequest to a PSPMappingRequest object.
     *
     * @param jsonStr The JSON string representing a PSPMappingRequest.
     * @return PSPMappingRequest object.
     * @throws IOException If an error occurs during JSON processing, such as issues with deserialization.
     */
    private PSPMappingRequest mapJsonToPSPRequest(String jsonStr) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PSPMappingRequest psp;

        psp = mapper.readValue(jsonStr, PSPMappingRequest.class);
        return psp;
    }
}

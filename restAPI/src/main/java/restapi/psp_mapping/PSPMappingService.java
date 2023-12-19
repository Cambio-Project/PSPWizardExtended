package restapi.psp_mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import psp.mappings.PatternMapper;
import psp.mappings.SELMapper;
import psp.sel.patterns.Pattern;
import psp.sel.scopes.Scope;
import restapi.psp_mapping.json_processing.data_objects.*;

import java.io.IOException;

@Service
public class PSPMappingService {

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

    private PSPMappingRequest mapJsonToPSPRequest(String jsonStr) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PSPMappingRequest psp;

        psp = mapper.readValue(jsonStr, PSPMappingRequest.class);
        return psp;
    }
}

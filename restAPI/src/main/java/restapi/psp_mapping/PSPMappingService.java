package restapi.psp_mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import psp.mappings.PatternMapper;
import psp.sel.patterns.Pattern;
import psp.sel.scopes.Scope;
import restapi.psp_mapping.json_processing.data_objects.PSPCorrectMappingResponse;
import restapi.psp_mapping.json_processing.data_objects.PSPMappingRequest;

import java.io.File;
import java.io.IOException;

@Service
public class PSPMappingService {

    public String mapPSPRequestToTargetLogic(String jsonPSPRequest) {
        PSPMappingRequest request = mapJsonToPSPRequest(jsonPSPRequest);
        Scope scope = request.getScope();
        Pattern pattern = request.getPattern();
        PatternMapper mapper = request.getMapper();
        String seg = pattern.getSpecificationAsSEL();
        String mapping = mapper.getMapping(scope, pattern);

        PSPCorrectMappingResponse response = new PSPCorrectMappingResponse(seg,mapping);

        return mapPSPMappingResultToJson(response);
    }
    public String mapPSPRequestToTargetLogic(File pspRequest) {
        PSPMappingRequest request = mapJsonToPSPRequest(pspRequest);
        Scope scope = request.getScope();
        Pattern pattern = request.getPattern();
        PatternMapper mapper = request.getMapper();
        String seg = pattern.getSpecificationAsSEL();
        String mapping = mapper.getMapping(scope, pattern);

        PSPCorrectMappingResponse response = new PSPCorrectMappingResponse(seg,mapping);

        return mapPSPMappingResultToJson(response);
    }

    public PSPMappingRequest mapJsonToPSPRequest(String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();
        PSPMappingRequest psp;
        try {
            psp = mapper.readValue(jsonStr, PSPMappingRequest.class);
            return psp;
        } catch (IOException e){
            System.out.println(e);
            return null;
        }
    }

    private PSPMappingRequest mapJsonToPSPRequest(File file) {
        ObjectMapper mapper = new ObjectMapper();
        PSPMappingRequest psp;
        try {
            psp = mapper.readValue(file, PSPMappingRequest.class);
            return psp;
        } catch (IOException e){

            System.out.println(e);
            return null;
        }
    }

    private String mapPSPMappingResultToJson(PSPCorrectMappingResponse response){
        ObjectMapper mapper = new ObjectMapper();
        String psp;
        try {
            psp = mapper.writeValueAsString(response);
            return psp;
        } catch (IOException e){
            System.out.println(e);
            return null;
        }
    }
}

package restapi.io_api;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import restapi.psp_mapping.data_objects.response.PSPCorrectMappingResponse;
import restapi.psp_mapping.data_objects.request.PSPMappingEvent;
import restapi.psp_mapping.data_objects.request.PSPMappingPattern;
import restapi.psp_mapping.data_objects.request.PSPMappingRequest;
import restapi.psp_mapping.data_objects.request.PSPMappingScope;
import psp.mappings.LTLMapper;
import psp.mappings.MTLMapper;
import psp.mappings.PatternMapper;
import psp.mappings.PrismMapper;
import psp.mappings.QuantitativePrismMapper;
import psp.mappings.TimedTBVMapper;
import psp.mappings.UntimedTBVMapper;
import psp.sel.EventImpl;
import psp.sel.patterns.Pattern;
import psp.sel.patterns.occurrence.Universality;
import psp.sel.scopes.Globally;
import psp.sel.scopes.Scope;

public class MappingRequestsHandler {
    public static void main(String args[]) {

    }


// TODO: Add error handling

    public static String mapPSPRequestToTargetLogic(File pspRequest) {
        PSPMappingRequest request = mapJsonToPSPRequest(pspRequest);
        PatternMapper selectedMapper = getSelectedMapper(request.getTargetLogic());
        Scope selectedScope = getSelectedScope(request.getScope());
        Pattern selectedPattern = getSelectedPattern(request.getPattern());

        String seg = selectedPattern.getSpecificationAsSEL();
        String mapping = selectedMapper.getMapping(selectedScope, selectedPattern);

        PSPCorrectMappingResponse response = new PSPCorrectMappingResponse(seg, mapping);

        return mapPSPMappingResultToJson(response);
    }


    // TODO: deal with other bounds and patterns.
    private static Pattern getSelectedPattern(PSPMappingPattern selectedPattern) {
        PSPMappingEvent selectedEventP = selectedPattern.getEvent();
        EventImpl eventP = new EventImpl(selectedEventP.getName(), selectedEventP.getSpecification());
        Pattern pattern = null;
        switch (selectedPattern.getType()) {
            case "Universality":
                pattern = new Universality(eventP, null, null);
        }
        return pattern;
    }

    // TODO: get from pool of mappers after init
    // TODO: add safty checks!
    private static PatternMapper getSelectedMapper(String targetLogic) {
        Map<String, PatternMapper> mappers = Map.of("LTL", new LTLMapper(),
                "MTL", new MTLMapper(), "Prism", new PrismMapper(), "Quantitative Prism", new QuantitativePrismMapper(), "TBV (timed)", new TimedTBVMapper(), "TBV (untimed)", new UntimedTBVMapper());
        return mappers.get(targetLogic);
    }

    //TODO: consider other scopes, deal with events.
    private static Scope getSelectedScope(PSPMappingScope selectedScope) {
        Scope scope = null;
        switch (selectedScope.getType()) {
            case "Globally":
                scope = new Globally();
        }
        return scope;
    }

    private static PSPMappingRequest mapJsonToPSPRequest(String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();
        PSPMappingRequest psp;
        try {
            psp = mapper.readValue(jsonStr, PSPMappingRequest.class);
            return psp;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    private static PSPMappingRequest mapJsonToPSPRequest(File file) {
        ObjectMapper mapper = new ObjectMapper();
        PSPMappingRequest psp;
        try {
            psp = mapper.readValue(file, PSPMappingRequest.class);
            return psp;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    private static String mapPSPMappingResultToJson(PSPCorrectMappingResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        String psp;
        try {
            psp = mapper.writeValueAsString(response);
            return psp;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }
}
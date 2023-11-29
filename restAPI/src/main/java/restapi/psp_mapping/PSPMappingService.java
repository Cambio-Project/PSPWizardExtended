package restapi.psp_mapping;

import org.springframework.stereotype.Service;
import psp.mappings.*;
import psp.sel.EventImpl;
import psp.sel.patterns.Pattern;
import psp.sel.patterns.occurrence.Universality;
import psp.sel.scopes.Globally;
import psp.sel.scopes.Scope;
import restapi.psp_mapping.data_objects.request.PSPMappingEvent;
import restapi.psp_mapping.data_objects.request.PSPMappingPattern;
import restapi.psp_mapping.data_objects.request.PSPMappingRequest;
import restapi.psp_mapping.data_objects.request.PSPMappingScope;
import restapi.psp_mapping.data_objects.response.PSPCorrectMappingResponse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class PSPMappingService {

    public PSPCorrectMappingResponse mapPSPRequestToTargetLogic(PSPMappingRequest request) {

        PatternMapper selectedMapper = getSelectedMapper(request.getTargetLogic());
        Scope selectedScope = getSelectedScope(request.getScope());
        Pattern selectedPattern = getSelectedPattern(request.getPattern());

        String seg = selectedPattern.getSpecificationAsSEL();
        String mapping = selectedMapper.getMapping(selectedScope, selectedPattern);

        return new PSPCorrectMappingResponse(seg, mapping);
    }

    // TODO: get from pool of mappers after init
    // TODO: add safty checks!
    private static PatternMapper getSelectedMapper(String targetLogic) {
        Map<String, PatternMapper> mappers = Map.of("LTL", new LTLMapper(),
                "MTL", new MTLMapper(), "Prism", new PrismMapper(), "Quantitative Prism", new QuantitativePrismMapper(), "TBV (timed)", new TimedTBVMapper(), "TBV (untimed)", new UntimedTBVMapper());
        return mappers.get(targetLogic);
    }

    // TODO: deal with other bounds and patterns.
    private Pattern getSelectedPattern(PSPMappingPattern selectedPattern) {
        PSPMappingEvent selectedEventP = selectedPattern.getEvent();
        EventImpl eventP = new EventImpl(selectedEventP.getName(), selectedEventP.getSpecification());
        Pattern pattern = null;
        switch (selectedPattern.getType()) {
            case "Universality":
                pattern = new Universality(eventP, null, null);
        }
        return pattern;
    }

    //TODO: consider other scopes, deal with events.
    private Scope getSelectedScope(PSPMappingScope selectedScope) {
        Scope scope = null;
        switch (selectedScope.getType()) {
            case "Globally":
                scope = new Globally();
        }
        return scope;
    }
}

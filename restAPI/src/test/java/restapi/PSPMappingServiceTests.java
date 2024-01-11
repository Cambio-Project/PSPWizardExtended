package restapi;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import restapi.psp_mapping.PSPMappingService;
import restapi.psp_mapping.json_processing.data_objects.PSPCorrectMappingResponse;
import restapi.psp_mapping.json_processing.data_objects.PSPUnsupportedMappingResponse;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class PSPMappingServiceTests {

    private PSPMappingService testedService;


    @BeforeEach
    void setup() {
        testedService = new PSPMappingService();
    }

    @Test()
    void correctMappingResponseShouldBeDelivered() throws IOException {
        String request = """
                {
                    "scope": {
                        "type": "Globally"
                    },
                    "pattern": {
                        "type": "Universality",
                        "p_event": {
                            "name": "pEvent(p)",
                            "specification": "pEventSpec"
                        }
                    },
                    "target_logic": "LTL"
                }
                """;
        String expectedSeg = "Globally, it is always the case that {pEvent(p)} [holds].";
        String expectedMapping = "☐ (pEvent(p))";
        PSPCorrectMappingResponse expected = new PSPCorrectMappingResponse(expectedSeg, expectedMapping);
        assertEquals(expected, testedService.mapPSPRequestToTargetLogic(request));
    }

    @Test()
    void correctMappingShouldBeDelivered() throws IOException {
        String request = """
                {
                    "scope": {
                        "type": "Globally"
                    },
                    "pattern": {
                        "type": "Universality",
                        "p_event": {
                            "name": "pEvent(p)",
                            "specification": "pEventSpec"
                        }
                    },
                    "target_logic": "MTL"
                }
                """;
        String expectedSeg = "Globally, it is always the case that {pEvent(p)} [holds].";
        String expectedMapping = "☐ (pEvent(p))";
        PSPCorrectMappingResponse expected = new PSPCorrectMappingResponse(expectedSeg, expectedMapping);
        assertEquals(expected, testedService.mapPSPRequestToTargetLogic(request));
    }

    @Test
    void unsupportedMappingResponseShouldBeDelivered() throws IOException {
        String request = """
                {
                    "scope": {
                        "type": "Globally"
                    },
                    "pattern": {
                        "type": "BoundedExistence",
                        "p_event": {
                            "name": "pEvent(p)",
                            "specification": "pEventSpec"
                        }
                    },
                    "target_logic": "Prism"
                }
                """;
        String expectedSeg = "Globally, {pEvent(p)} [holds] at most 1 times.";
        String expectedErrorMessage = "Mapping not supported for Prism Property Specification.";
        PSPUnsupportedMappingResponse expected = new PSPUnsupportedMappingResponse(expectedErrorMessage,expectedSeg);
        assertEquals(expected, testedService.mapPSPRequestToTargetLogic(request));
    }


    @Test
    void exceptionIsThrownOnEmptyInput() {
        String request = "";
        assertThrows(IOException.class, () -> testedService.mapPSPRequestToTargetLogic(request));
    }

    @Test
    void mappingExceptionIsThrownOnUnsupportedPatternType() {
        String request = """
                {
                    "scope": {
                        "type": "Globally"
                    },
                    "pattern": {
                        "type": "SomePattern",
                        "p_event": {
                            "name": "pEvent(p)",
                            "specification": "pEventSpec"
                        }
                    },
                    "target_logic": "Prism"
                }
                """;
        assertThrows(JsonMappingException.class, () -> testedService.mapPSPRequestToTargetLogic(request));
    }

}

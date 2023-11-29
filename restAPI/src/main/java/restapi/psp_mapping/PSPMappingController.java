package restapi.psp_mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import restapi.psp_mapping.data_objects.request.PSPMappingRequest;
import restapi.psp_mapping.data_objects.response.PSPCorrectMappingResponse;
import restapi.psp_mapping.data_objects.response.PSPMappingResponse;

@RestController
public class PSPMappingController {

    private final PSPMappingService pspMappingService;

    Logger logger = LoggerFactory.getLogger(PSPMappingController.class);

    @Autowired
    public PSPMappingController(PSPMappingService pspMappingService) {
        this.pspMappingService = pspMappingService;
    }

    @PostMapping("/transformPattern")
    public ResponseEntity<PSPMappingResponse> handleDashboardRequest(@RequestBody PSPMappingRequest requestPayload){
        if (requestPayload == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            PSPCorrectMappingResponse response = pspMappingService.mapPSPRequestToTargetLogic(requestPayload);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

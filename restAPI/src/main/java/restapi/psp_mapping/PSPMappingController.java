package restapi.psp_mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import restapi.psp_mapping.json_processing.data_objects.PSPMappingRequest;
import restapi.psp_mapping.json_processing.data_objects.PSPMappingResponse;

@RestController
public class PSPMappingController {

    private final PSPMappingService pspMappingService;

    Logger logger = LoggerFactory.getLogger(PSPMappingController.class);

    @Autowired
    public PSPMappingController(PSPMappingService pspMappingService) {
        this.pspMappingService = pspMappingService;
    }

    @PostMapping("/transformPattern")
    public ResponseEntity<PSPMappingResponse> handleDashboardRequest(@RequestBody String request){
        if (request == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            logger.info(request);
            try {
                String response = pspMappingService.mapPSPRequestToTargetLogic(request);
                logger.info(response);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception exception){
            logger.error(exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package restapi.psp_mapping;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import restapi.psp_mapping.exceptions.UnsupportedTypeException;
import restapi.psp_mapping.json_processing.data_objects.PSPMappingResponse;
import restapi.psp_mapping.json_processing.schema_validation.JSONRequestSchemaValidator;

import java.io.IOException;

@CrossOrigin
@RestController
public class PSPMappingController {

    private final PSPMappingService pspMappingService;

    Logger logger = LoggerFactory.getLogger(PSPMappingController.class);

    @Autowired
    public PSPMappingController(PSPMappingService pspMappingService) {
        this.pspMappingService = pspMappingService;
    }

    @PostMapping("/transformPattern")
    public ResponseEntity<String> handleDashboardRequest(@RequestBody String request){
        if (request == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            ProcessingReport report= JSONRequestSchemaValidator.validateSchema(request);
            System.out.println(report);
            if (report.isSuccess()) {
                PSPMappingResponse response = pspMappingService.mapPSPRequestToTargetLogic(request);
                return new ResponseEntity<>(response.toJSON(), HttpStatus.OK);
            }
            else {
                logger.error(report.toString());
                return new ResponseEntity<>("Bad request format. Please refer to the JSON schema definition."
                         ,HttpStatus.BAD_REQUEST);
            }

        } catch (IOException e) {
            if (e instanceof UnsupportedTypeException) {
                logger.error(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } else {
                logger.error(e.getMessage());
                return new ResponseEntity<>("Unexpected JSON serialization error.", HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

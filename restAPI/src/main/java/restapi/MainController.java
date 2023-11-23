package restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import psp.sel.patterns.Pattern;

@RestController
public class MainController {

    private final RequestService requestService;

    Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    public MainController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/transformPattern")
    public ResponseEntity<ResponsePayload> handleDashboardRequest(@RequestBody RequestPayload payload){
        if (payload == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            logger.info(objectMapper.writeValueAsString(payload));
            Pattern pattern = null;
            try {
                pattern = requestService.createPatternFromRequest(payload);
                logger.info("Pattern created: " + pattern.toString());
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
            return new ResponseEntity<>(new ResponsePayload(
                    "SEG Holder",
                    "Request Login Holder"
            ),HttpStatus.OK);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

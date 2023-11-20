package restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    Logger logger = LoggerFactory.getLogger(MainController.class);

    @PostMapping("/transformPattern")
    public ResponseEntity<ResponsePayload> handleDashboardRequest(@RequestBody RequestPayload payload){
        if (payload == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            //TODO Handle Request
            ObjectMapper objectMapper = new ObjectMapper();
            logger.info(objectMapper.writeValueAsString(payload));
            return new ResponseEntity<>(new ResponsePayload("Test SEG", "TestLogic"),HttpStatus.OK);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

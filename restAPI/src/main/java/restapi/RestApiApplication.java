package restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import psp.ui.PSPWizard;

import java.io.File;

import static restapi.io_api.MappingRequestsHandler.mapPSPRequestToTargetLogic;

@SpringBootApplication
public class RestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
//        File file = new File("test_pattern.json");
//        System.out.println(mapPSPRequestToTargetLogic(file));
    }
}

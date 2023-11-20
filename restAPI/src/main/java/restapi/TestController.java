package restapi;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String helloWorld(){
        return "Hello World!";
    }

    @PostMapping("/")
    public String objectLoop(@RequestBody JsonNode testPayload){
        System.out.println(testPayload.toString());
        return "Ok";
    }
}

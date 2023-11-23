package restapi;

import org.springframework.stereotype.Service;
import psp.sel.EventImpl;
import psp.sel.patterns.Pattern;
import psp.sel.patterns.occurrence.Universality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class RequestService {

    public Pattern createPatternFromRequest(RequestPayload payload){

        HashMap<String, EventImpl> eventList = new HashMap<>();

        for (EventPayload eventPayload : payload.getEvents()) {
            eventList.put(eventPayload.name, new EventImpl(eventPayload.name, eventPayload.specification));
        }

        switch (payload.getPattern()){
            case "Universality":
                return new Universality(eventList.get(payload.getPatternProps().get("event").asText()), null, null);
            default: return null;
        }
    }
}

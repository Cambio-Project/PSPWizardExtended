package restapi;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class RequestPayload {
    private String scope;
    private String pattern;
    private String targetLogic;
    private List<EventPayload> events;
    private JsonNode patternProps;

    public RequestPayload(String scope, String pattern, String targetLogic, List<EventPayload> events, JsonNode patternProps) {
        this.scope = scope;
        this.pattern = pattern;
        this.targetLogic = targetLogic;
        this.events = events;
        this.patternProps = patternProps;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getTargetLogic() {
        return targetLogic;
    }

    public void setTargetLogic(String targetLogic) {
        this.targetLogic = targetLogic;
    }

    public List<EventPayload> getEvents() {
        return events;
    }

    public void setEvents(List<EventPayload> events) {
        this.events = events;
    }

    public JsonNode getPatternProps() {
        return patternProps;
    }

    public void setPatternProps(JsonNode patternProps) {
        this.patternProps = patternProps;
    }
}

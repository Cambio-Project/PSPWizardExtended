package psp.mappings.elements;

import java.util.Objects;

public class EventuallyElement implements TemporalOperators {
    private final String definition;

    public EventuallyElement(String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }

}

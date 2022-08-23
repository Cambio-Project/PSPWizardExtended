package psp.mappings.elements;

import java.util.Objects;

public class TimeBoundElement implements TemporalOperators {
    private final String definition;

    public TimeBoundElement(String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }
}

package psp.mappings.elements;

import java.util.Objects;

public class NextElement implements TemporalOperators {
    private final String definition;

    public NextElement(String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }

}

package psp.mappings.elements;

import java.util.Objects;

public class UntilElement implements TemporalOperators {
    private final String definition;

    public UntilElement(String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }

}

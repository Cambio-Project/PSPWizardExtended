package psp.mappings.elements;

import java.util.Objects;

public class WeakUntilElement implements TemporalOperators {
    private final String definition;

    public WeakUntilElement(String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }

}

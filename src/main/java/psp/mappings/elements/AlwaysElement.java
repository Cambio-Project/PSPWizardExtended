package psp.mappings.elements;

import java.util.Objects;

public class AlwaysElement implements TemporalOperators, UnaryOperator {
    private final String definition;

    public AlwaysElement(final String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }

}

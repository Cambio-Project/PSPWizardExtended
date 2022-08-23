package psp.mappings.elements;

import java.util.Objects;

public class ProbabilityBoundElement implements TemporalOperators {
    private final String definition;

    public ProbabilityBoundElement(String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }

}

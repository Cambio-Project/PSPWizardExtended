package psp.mappings.elements;

import java.util.Objects;

public class AndElement implements BooleanOperators {
    private final String definition;

    public AndElement(String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }

}

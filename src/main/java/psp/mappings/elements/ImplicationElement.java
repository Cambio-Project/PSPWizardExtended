package psp.mappings.elements;

import java.util.Objects;

public class ImplicationElement implements BooleanOperators {
    private final String definition;

    public ImplicationElement(String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }

}

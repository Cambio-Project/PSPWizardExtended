package psp.mappings.elements;

import java.util.Objects;

public class OrElement implements BooleanOperators {
    private final String definition;

    public OrElement(String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }

}

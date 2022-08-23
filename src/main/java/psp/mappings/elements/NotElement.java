package psp.mappings.elements;

import java.util.Objects;

public class NotElement implements BooleanOperators {
    private final String definition;

    public NotElement(String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }

}

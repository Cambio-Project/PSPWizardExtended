package psp.mappings.elements;

import java.util.Objects;

public class MiscElement implements Element {
    private final String definition;

    public MiscElement(String definition) {
        super();
        Objects.nonNull(definition);
        this.definition = definition;
    }

    public String getContent() {
        return definition;
    }

}

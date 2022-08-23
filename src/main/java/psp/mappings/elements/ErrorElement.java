package psp.mappings.elements;

public class ErrorElement implements Element {

    private final Exception e;

    public ErrorElement(Exception e) {
        super();
        this.e = e;
    }

    @Override
    public String getContent() {
        return e.getMessage();
    }

}

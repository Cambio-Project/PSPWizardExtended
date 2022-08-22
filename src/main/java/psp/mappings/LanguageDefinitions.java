package psp.mappings;

import java.util.Optional;

public class LanguageDefinitions {
    private final Optional<String> inf;
    private final Optional<String> always;
    private final Optional<String> eventually;
    private final Optional<String> next;
    private final Optional<String> implication;
    private final Optional<String> not;
    private final Optional<String> and;
    private final Optional<String> or;
    private final Optional<String> until;
    private final Optional<String> weakUntil;

    public LanguageDefinitions(String inf, String always, String eventually, String next, String implication,
        String not, String and, String or, String until, String weakUntil) {
        super();
        this.inf = Optional.ofNullable(inf);
        this.always = Optional.ofNullable(always);
        this.eventually = Optional.ofNullable(eventually);
        this.next = Optional.ofNullable(next);
        this.implication = Optional.ofNullable(implication);
        this.not = Optional.ofNullable(not);
        this.and = Optional.ofNullable(and);
        this.or = Optional.ofNullable(or);
        this.until = Optional.ofNullable(until);
        this.weakUntil = Optional.ofNullable(weakUntil);
    }

    private String defaultForUnsupported() {
        return "UNSUPPORTED";
    }

    public String getInf() {
        return inf.orElseGet(this::defaultForUnsupported);
    }

    public String getAlways() {
        return always.orElseGet(this::defaultForUnsupported);
    }

    public String getEventually() {
        return eventually.orElseGet(this::defaultForUnsupported);
    }

    public String getNext() {
        return next.orElseGet(this::defaultForUnsupported);
    }

    public String getImplication() {
        return implication.orElseGet(this::defaultForUnsupported);
    }

    public String getNot() {
        return not.orElseGet(this::defaultForUnsupported);
    }

    public String getAnd() {
        return and.orElseGet(this::defaultForUnsupported);
    }

    public String getOr() {
        return or.orElseGet(this::defaultForUnsupported);
    }

    public String getUntil() {
        return until.orElseGet(this::defaultForUnsupported);
    }

    public String getWeakUntil() {
        return weakUntil.orElseGet(this::defaultForUnsupported);
    }

}

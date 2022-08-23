package psp.mappings;

import java.util.Optional;

import psp.mappings.elements.AlwaysElement;
import psp.mappings.elements.AndElement;
import psp.mappings.elements.EventuallyElement;
import psp.mappings.elements.ImplicationElement;
import psp.mappings.elements.NextElement;
import psp.mappings.elements.NotElement;
import psp.mappings.elements.OrElement;
import psp.mappings.elements.UntilElement;
import psp.mappings.elements.WeakUntilElement;

public class LanguageDefinitions {
    private final Optional<String> inf;
    private final AlwaysElement always;
    private final EventuallyElement eventually;
    private final NextElement next;
    private final ImplicationElement implication;
    private final NotElement not;
    private final AndElement and;
    private final OrElement or;
    private final UntilElement until;
    private final WeakUntilElement weakUntil;

    public LanguageDefinitions(String inf, AlwaysElement always, EventuallyElement eventually, NextElement next, ImplicationElement implication,
        NotElement not, AndElement and, OrElement or, UntilElement until, WeakUntilElement weakUntil) {
        super();
        this.inf = Optional.ofNullable(inf);
        this.always = always;
        this.eventually = eventually;
        this.next = next;
        this.implication = implication;
        this.not = not;
        this.and = and;
        this.or = or;
        this.until = until;
        this.weakUntil = weakUntil;
    }

    private String defaultForUnsupported() {
        return "UNSUPPORTED";
    }

    public String getInf() {
        return inf.orElseGet(this::defaultForUnsupported);
    }

    public AlwaysElement getAlways() {
        return always;
    }

    public EventuallyElement getEventually() {
        return eventually;
    }

    public NextElement getNext() {
        return next;
    }

    public ImplicationElement getImplication() {
        return implication;
    }

    public NotElement getNot() {
        return not;
    }

    public AndElement getAnd() {
        return and;
    }

    public OrElement getOr() {
        return or;
    }

    public UntilElement getUntil() {
        return until;
    }

    public WeakUntilElement getWeakUntil() {
        return weakUntil;
    }

}

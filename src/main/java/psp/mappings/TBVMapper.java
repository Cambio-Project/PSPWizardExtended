package psp.mappings;

public class TBVMapper extends MTLMapper {
    private static final String defaultInf = "∞";
    private static final String defaultAlways = "always";
    private static final String defaultEventually = "once";
    // private static final String defaultNext = "○";
    private static final String defaultImplication = " -> ";
    private static final String defaultNot = "!";
    private static final String defaultAnd = " and ";
    private static final String defaultOr = " or ";
    private static final String defaultUntil = " since";
    private static final String defaultWeakUntil = " weak_since ";
    private static final LanguageDefinitions TBV_LANGUAGE_DEFINITION = new LanguageDefinitions(defaultInf,
        defaultAlways, defaultEventually, null, defaultImplication, defaultNot, defaultAnd, defaultOr, defaultUntil,
        defaultWeakUntil);

    public TBVMapper() {
        super();
        this.setLanguageDefinitions(TBV_LANGUAGE_DEFINITION);
    }

    public String toString() {
        return "TBV";
    }
}

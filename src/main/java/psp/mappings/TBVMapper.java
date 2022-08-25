package psp.mappings;

import psp.mappings.elements.AlwaysElement;
import psp.mappings.elements.AndElement;
import psp.mappings.elements.EventuallyElement;
import psp.mappings.elements.ImplicationElement;
import psp.mappings.elements.NextElement;
import psp.mappings.elements.NotElement;
import psp.mappings.elements.OrElement;
import psp.mappings.elements.UntilElement;
import psp.mappings.elements.WeakUntilElement;
import psp.mappings.postprocessing.MultipleWhiteSpaceReplacer;
import psp.mappings.postprocessing.WeakUntilSubstituter;

public class TBVMapper extends MTLMapper {
    private static final String defaultInf = "inf";
    private static final AlwaysElement defaultAlways = new AlwaysElement("always");
    private static final EventuallyElement defaultEventually = new EventuallyElement("once");
    private static final NextElement defaultNext = new NextElement("pre");
    private static final ImplicationElement defaultImplication = new ImplicationElement(" -> ");
    private static final NotElement defaultNot = new NotElement(" not ");
    private static final AndElement defaultAnd = new AndElement(" and ");
    private static final OrElement defaultOr = new OrElement(" or ");
    private static final UntilElement defaultUntil = new UntilElement(" since");
    private static final WeakUntilElement defaultWeakUntil = new WeakUntilElement(" weak_since ");
    private static final LanguageDefinitions TBV_LANGUAGE_DEFINITION = new LanguageDefinitions(defaultInf,
        defaultAlways, defaultEventually, defaultNext, defaultImplication, defaultNot, defaultAnd, defaultOr,
        defaultUntil, defaultWeakUntil);

    public TBVMapper() {
        super();
        this.setLanguageDefinitions(TBV_LANGUAGE_DEFINITION);
        this.register(new WeakUntilSubstituter(TBV_LANGUAGE_DEFINITION));
        this.register(new MultipleWhiteSpaceReplacer());
    }

    public String toString() {
        return "TBV";
    }
}

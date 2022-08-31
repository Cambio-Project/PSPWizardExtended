package psp.mappings.postprocessing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import psp.mappings.LanguageDefinitions;
import psp.mappings.UntimedTBVMapper;
import psp.mappings.elements.AlwaysElement;
import psp.mappings.elements.AndElement;
import psp.mappings.elements.Element;
import psp.mappings.elements.Event;
import psp.mappings.elements.EventuallyElement;
import psp.mappings.elements.NotElement;
import psp.mappings.elements.OrElement;
import psp.mappings.elements.RoundBracketCloseElement;
import psp.mappings.elements.RoundBracketOpenElement;
import psp.mappings.elements.SpaceElement;
import psp.mappings.elements.TimeBoundElement;
import psp.mappings.elements.UntilElement;
import psp.sel.EventImpl;

public class WeakUntilSubstituterTest {
    private WeakUntilSubstituter resolver;
    private LanguageDefinitions languageDefinitions;
    private List<Element> elementToProcess;
    private List<Element> processedElements;
    private int checkIndex;

    @BeforeEach
    public void setup() {
        final UntimedTBVMapper mapper = new UntimedTBVMapper();
        languageDefinitions = mapper.getLanguageDefinitions();
        resolver = new WeakUntilSubstituter(languageDefinitions);
        elementToProcess = new ArrayList<>();
        processedElements = new ArrayList<>();
        checkIndex = 0;
    }

    @Test
    public void simpleUntimedTest() {
        Event eventA = new EventImpl("A");
        Event eventB = new EventImpl("B");

        elementToProcess.add(eventA);
        elementToProcess.add(languageDefinitions.getWeakUntil());
        elementToProcess.add(new SpaceElement());
        elementToProcess.add(eventB);

        execute();

        assertEquals(8, processedElements.size());

        check(RoundBracketOpenElement.class);
        check(eventA);
        check(UntilElement.class);
        check(eventB);
        check(RoundBracketCloseElement.class);
        check(OrElement.class);
        check(AlwaysElement.class);
        check(eventA);
    }

    @Test
    public void simpleTimedTest() {
        Event eventA = new EventImpl("A");
        Event eventB = new EventImpl("B");
        TimeBoundElement timeBound = new TimeBoundElement("[0,3]");

        elementToProcess.add(eventA);
        elementToProcess.add(languageDefinitions.getWeakUntil());
        elementToProcess.add(timeBound);
        elementToProcess.add(new SpaceElement());
        elementToProcess.add(eventB);

        execute();

        assertEquals(10, processedElements.size());

        check(RoundBracketOpenElement.class);
        check(eventA);
        check(UntilElement.class);
        check(timeBound);
        check(eventB);
        check(RoundBracketCloseElement.class);
        check(OrElement.class);
        check(AlwaysElement.class);
        check(timeBound);
        check(eventA);
    }

    @Test
    public void negatedBeforeTest() {
        Event eventA = new EventImpl("A");
        Event eventB = new EventImpl("B");
        TimeBoundElement timeBound = new TimeBoundElement("[0,3]");

        elementToProcess.add(languageDefinitions.getNot());
        elementToProcess.add(eventA);
        elementToProcess.add(languageDefinitions.getWeakUntil());
        elementToProcess.add(timeBound);
        elementToProcess.add(new SpaceElement());
        elementToProcess.add(eventB);

        execute();

        assertEquals(12, processedElements.size());

        check(RoundBracketOpenElement.class);
        check(NotElement.class);
        check(eventA);
        check(UntilElement.class);
        check(timeBound);
        check(eventB);
        check(RoundBracketCloseElement.class);
        check(OrElement.class);
        check(AlwaysElement.class);
        check(timeBound);
        check(NotElement.class);
        check(eventA);
    }

    @Test
    public void eventuallyBeforeTest() {
        Event eventA = new EventImpl("A");
        Event eventB = new EventImpl("B");
        TimeBoundElement timeBound = new TimeBoundElement("[0,3]");
        TimeBoundElement timeBound2 = new TimeBoundElement("[0,3]");

        elementToProcess.add(languageDefinitions.getEventually());
        elementToProcess.add(timeBound2);
        elementToProcess.add(eventA);
        elementToProcess.add(languageDefinitions.getWeakUntil());
        elementToProcess.add(timeBound);
        elementToProcess.add(new SpaceElement());
        elementToProcess.add(eventB);

        // format currently not supported
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            execute();
        });

    }

    @Test
    public void bracketBeforeTest() {
        Event eventA = new EventImpl("A");
        Event eventB = new EventImpl("B");
        Event eventC = new EventImpl("C");
        TimeBoundElement timeBound = new TimeBoundElement("[0,3]");

        elementToProcess.add(new RoundBracketOpenElement());
        elementToProcess.add(eventA);
        elementToProcess.add(languageDefinitions.getAnd());
        elementToProcess.add(eventC);
        elementToProcess.add(new RoundBracketCloseElement());
        elementToProcess.add(languageDefinitions.getWeakUntil());
        elementToProcess.add(timeBound);
        elementToProcess.add(new SpaceElement());
        elementToProcess.add(eventB);

        execute();

        assertEquals(18, processedElements.size());

        check(RoundBracketOpenElement.class);
        check(RoundBracketOpenElement.class);
        check(eventA);
        check(AndElement.class);
        check(eventC);
        check(RoundBracketCloseElement.class);
        check(UntilElement.class);
        check(timeBound);
        check(eventB);
        check(RoundBracketCloseElement.class);
        check(OrElement.class);
        check(AlwaysElement.class);
        check(timeBound);
        check(RoundBracketOpenElement.class);
        check(eventA);
        check(AndElement.class);
        check(eventC);
        check(RoundBracketCloseElement.class);
    }

    @Test
    public void negatedAfterTest() {
        Event eventA = new EventImpl("A");
        Event eventB = new EventImpl("B");
        TimeBoundElement timeBound = new TimeBoundElement("[0,3]");

        elementToProcess.add(eventA);
        elementToProcess.add(languageDefinitions.getWeakUntil());
        elementToProcess.add(timeBound);
        elementToProcess.add(new SpaceElement());
        elementToProcess.add(languageDefinitions.getNot());
        elementToProcess.add(eventB);

        execute();

        assertEquals(11, processedElements.size());

        check(RoundBracketOpenElement.class);
        check(eventA);
        check(UntilElement.class);
        check(timeBound);
        check(NotElement.class);
        check(eventB);
        check(RoundBracketCloseElement.class);
        check(OrElement.class);
        check(AlwaysElement.class);
        check(timeBound);
        check(eventA);
    }

    @Test
    public void evemtuallyAfterTest() {
        Event eventA = new EventImpl("A");
        Event eventB = new EventImpl("B");
        TimeBoundElement timeBound = new TimeBoundElement("[0,3]");
        TimeBoundElement timeBound2 = new TimeBoundElement("[0,5]");

        elementToProcess.add(eventA);
        elementToProcess.add(languageDefinitions.getWeakUntil());
        elementToProcess.add(timeBound);
        elementToProcess.add(new SpaceElement());
        elementToProcess.add(languageDefinitions.getEventually());
        elementToProcess.add(timeBound2);
        elementToProcess.add(new SpaceElement());
        elementToProcess.add(eventB);

        execute();

        assertEquals(12, processedElements.size());

        check(RoundBracketOpenElement.class);
        check(eventA);
        check(UntilElement.class);
        check(timeBound);
        check(EventuallyElement.class);
        check(timeBound2);
        check(eventB);
        check(RoundBracketCloseElement.class);
        check(OrElement.class);
        check(AlwaysElement.class);
        check(timeBound);
        check(eventA);
    }

    @Test
    public void bracketAfterTest() {
        Event eventA = new EventImpl("A");
        Event eventB = new EventImpl("B");
        Event eventC = new EventImpl("C");
        TimeBoundElement timeBound = new TimeBoundElement("[0,3]");

        elementToProcess.add(eventA);
        elementToProcess.add(languageDefinitions.getWeakUntil());
        elementToProcess.add(timeBound);
        elementToProcess.add(new SpaceElement());
        elementToProcess.add(new RoundBracketOpenElement());
        elementToProcess.add(eventB);
        elementToProcess.add(languageDefinitions.getAnd());
        elementToProcess.add(eventC);
        elementToProcess.add(new RoundBracketCloseElement());

        execute();

        assertEquals(14, processedElements.size());

        check(RoundBracketOpenElement.class);
        check(eventA);
        check(UntilElement.class);
        check(timeBound);
        check(RoundBracketOpenElement.class);
        check(eventB);
        check(AndElement.class);
        check(eventC);
        check(RoundBracketCloseElement.class);
        check(RoundBracketCloseElement.class);
        check(OrElement.class);
        check(AlwaysElement.class);
        check(timeBound);
        check(eventA);
    }

    private void execute() {
        processedElements = resolver.process(elementToProcess);
        processedElements.removeIf(element -> element instanceof SpaceElement);
    }

    private void check(Class<?> clazz) {
        assertTrue(clazz.isInstance(processedElements.get(checkIndex)));
        checkIndex++;
    }

    private void check(Element element) {
        assertEquals(element, processedElements.get(checkIndex));
        checkIndex++;
    }

}

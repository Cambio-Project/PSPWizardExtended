package psp.mappings.postprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import psp.mappings.LanguageDefinitions;
import psp.mappings.elements.BooleanOperators;
import psp.mappings.elements.Element;
import psp.mappings.elements.Event;
import psp.mappings.elements.NotElement;
import psp.mappings.elements.RoundBracketCloseElement;
import psp.mappings.elements.RoundBracketOpenElement;
import psp.mappings.elements.SpaceElement;
import psp.mappings.elements.TemporalOperators;
import psp.mappings.elements.TimeBoundElement;
import psp.mappings.elements.UnaryOperator;
import psp.mappings.elements.WeakUntilElement;

/**
 * Experimental (!) post processor that simplifies the weak until operator.
 * 
 * @author Sebastian Frank
 *
 */
public class WeakUntilSubstituter implements MappingPostprocessor {
    private final LanguageDefinitions tbvLangaugeDefinitions;

    public WeakUntilSubstituter(LanguageDefinitions tbvLangaugeDefinitions) {
        super();
        this.tbvLangaugeDefinitions = tbvLangaugeDefinitions;
    }

    @Override
    public List<Element> process(List<Element> elements) {
        final List<Element> processedElements = new ArrayList<>(elements);
        int weakestUntilIndex = findWeakestUntilAtDeepestPosition(processedElements);
        while (weakestUntilIndex >= 0) {
            // find relevant blocks
            final List<Element> beforeWeakUntil = findElementBlockBefore(processedElements, weakestUntilIndex);
            final List<Element> weakUntil = findElementUntilBlock(processedElements, weakestUntilIndex);
            final int indexAfterUntilElements = weakestUntilIndex + weakUntil.size();
            final List<Element> afterWeakUntil = findElementBlockAfter(processedElements, indexAfterUntilElements);

            // remove old weak until formula
            final int indexAtStartOfBeforeWeakUntil = weakestUntilIndex - beforeWeakUntil.size();
            final int numberOfAllElements = beforeWeakUntil.size() + weakUntil.size() + afterWeakUntil.size();
            removeNumberOfElementsFromPosition(processedElements, indexAtStartOfBeforeWeakUntil, numberOfAllElements);

            // set transformed until formula
            final List<Element> replacedElements = constructReplacedWeakUntil(beforeWeakUntil, weakUntil,
                afterWeakUntil);
            processedElements.addAll(indexAtStartOfBeforeWeakUntil, replacedElements);

            // prepare for next iteration
            weakestUntilIndex = findWeakestUntilAtDeepestPosition(processedElements);
        }
        return processedElements;
    }

    private List<Element> constructReplacedWeakUntil(List<Element> beforeElements, List<Element> untilElements,
        List<Element> afterElements) {
        replaceWeakUntilWithUntil(untilElements);
        Optional<Element> timebound = getTimeBound(untilElements);

        List<Element> elements = new ArrayList<>();
        elements.add(new RoundBracketOpenElement());
        elements.addAll(beforeElements);
        elements.addAll(untilElements);
        elements.addAll(afterElements);
        elements.add(new RoundBracketCloseElement());
        elements.add(tbvLangaugeDefinitions.getOr());
        elements.add(tbvLangaugeDefinitions.getAlways());
        if (!timebound.isEmpty()) {
            elements.add(timebound.get());
        }
        elements.add(new SpaceElement());
        elements.addAll(beforeElements);
        return elements;
    }

    private Optional<Element> getTimeBound(List<Element> elements) {
        return elements.stream().filter(element -> element instanceof TimeBoundElement).findFirst();
    }

    private void replaceWeakUntilWithUntil(List<Element> elements) {
        elements.replaceAll(element -> {
            if (element instanceof WeakUntilElement) {
                return tbvLangaugeDefinitions.getUntil();
            } else {
                return element;
            }
        });
    }

    private void removeNumberOfElementsFromPosition(final List<Element> elements, final int atIndex,
        final int numberOfElementsToRemove) {
        for (int i = 0; i < numberOfElementsToRemove; i++) {
            elements.remove(atIndex);
        }
    }

    private List<Element> findElementBlockBefore(final List<Element> allElements, final int index) {
        List<Element> beforeElements = new ArrayList<>();
        Element previousElement = allElements.get(index - 1);
        if (previousElement instanceof Event) {
            beforeElements = findElementBlockBeforeForSimpleEvent(allElements, index);
        } else if (previousElement instanceof RoundBracketCloseElement) {
            beforeElements = findElementBlockBeforeForRoundBrackets(allElements, index);
        } else {
            throw new IllegalStateException(String
                .format("Found unexpected element %s while trying to replace weak until.", previousElement.getClass()));
        }
        return new ArrayList<Element>(beforeElements);
    }

    private List<Element> findElementBlockBeforeForRoundBrackets(final List<Element> allElements, final int index) {
        int depth = 1;
        int currentIndex = index - 2;
        while (depth > 0) {
            final Element currentPreElement = allElements.get(currentIndex);
            if (currentPreElement instanceof RoundBracketCloseElement) {
                depth++;
            } else if (currentPreElement instanceof RoundBracketOpenElement) {
                depth--;
            }
            currentIndex--;
        }
        return allElements.subList(currentIndex + 1, index);
    }

    private List<Element> findElementBlockBeforeForSimpleEvent(final List<Element> allElements, final int index) {
        List<Element> beforeElements = new ArrayList<>();
        Element previousElement = allElements.get(index - 1);
        beforeElements.add(0, previousElement);
        if (index >= 2) {
            Element prePreviousElement = allElements.get(index - 2);
            if (prePreviousElement instanceof NotElement) {
                beforeElements.add(0, prePreviousElement);
            } else if ((prePreviousElement instanceof SpaceElement) || (prePreviousElement instanceof TimeBoundElement)
                || (prePreviousElement instanceof UnaryOperator)) {
                throw new UnsupportedOperationException(
                    "the experimental implementation does not support complex before-elements");
            }
        }
        return beforeElements;
    }

    private List<Element> findElementUntilBlock(final List<Element> allElements, final int index) {
        int currentIndex = index;
        currentIndex++;
        currentIndex = increaseIndexForElementType(allElements, currentIndex, TimeBoundElement.class);
        currentIndex = increaseIndexForWhiteSpaces(allElements, currentIndex);
        List<Element> untilElements = allElements.subList(index, currentIndex);
        return new ArrayList<Element>(untilElements);
    }

    private int increaseIndexForElementType(final List<Element> allElements, int currentIndex, final Class<?> type,
        final Class<?> type2) {
        Element currentElement = allElements.get(currentIndex);
        if (type.isInstance(currentElement) && type2.isInstance(currentElement)) {
            currentIndex++;
        }
        return currentIndex;
    }

    private int increaseIndexForElementType(final List<Element> allElements, int currentIndex, final Class<?> type) {
        if (type.isInstance(allElements.get(currentIndex))) {
            currentIndex++;
        }
        return currentIndex;
    }

    private int increaseIndexForWhiteSpaces(final List<Element> allElements, int currentIndex) {
        Element nextElement = allElements.get(currentIndex);
        while (nextElement instanceof SpaceElement) {
            currentIndex++;
            nextElement = allElements.get(currentIndex);
        }
        return currentIndex;
    }

    private List<Element> findElementBlockAfter(final List<Element> allElements, final int index) {
        int currentIndex = index;
        currentIndex = increaseIndexForElementType(allElements, currentIndex, UnaryOperator.class,
            TemporalOperators.class);
        currentIndex = increaseIndexForElementType(allElements, currentIndex, TimeBoundElement.class);
        currentIndex = increaseIndexForWhiteSpaces(allElements, currentIndex);
        currentIndex = increaseIndexForElementType(allElements, currentIndex, UnaryOperator.class,
            BooleanOperators.class);
        currentIndex = increaseIndexForWhiteSpaces(allElements, currentIndex);

        Element currentElement = allElements.get(currentIndex);
        if (currentElement instanceof Event) {
            currentIndex++;
        } else if (currentElement instanceof RoundBracketOpenElement) {
            currentIndex = findElementBlockAfterForRoundBrackets(allElements, currentIndex + 1);
        }

        if (index == currentIndex) {
            throw new IllegalStateException("Something went wrong when trying to substitute weak-until-operator.");
        }
        List<Element> afterElements = allElements.subList(index, currentIndex);

        return new ArrayList<Element>(afterElements);
    }

    private int findElementBlockAfterForRoundBrackets(final List<Element> allElements, int currentIndex) {
        int depth = 1;
        while (depth > 0) {
            final Element currentPreElement = allElements.get(currentIndex);
            if (currentPreElement instanceof RoundBracketOpenElement) {
                depth++;
            } else if (currentPreElement instanceof RoundBracketCloseElement) {
                depth--;
            }
            currentIndex++;
        }
        return currentIndex;
    }

    private int findWeakestUntilAtDeepestPosition(final List<Element> elements) {
        int deepestIndex = -1;
        int deepestIndexDepth = -1;
        int depth = 0;
        for (int index = 0; index < elements.size(); index++) {
            final Element currentElement = elements.get(index);
            if (currentElement instanceof RoundBracketOpenElement) {
                depth++;
            } else if (currentElement instanceof RoundBracketCloseElement) {
                depth--;
            } else if (currentElement instanceof WeakUntilElement) {
                if (deepestIndexDepth < depth) {
                    deepestIndex = index;
                    deepestIndexDepth = depth;
                }
            }
        }
        return deepestIndex;
    }

    private void print(List<Element> elements) {
        elements.forEach(x -> System.out.print(x.getContent()));
        System.out.println();
    }

}

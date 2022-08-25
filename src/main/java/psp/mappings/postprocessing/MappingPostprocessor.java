package psp.mappings.postprocessing;

import java.util.List;

import psp.mappings.elements.Element;

@FunctionalInterface
public interface MappingPostprocessor {

    List<Element> process(final List<Element> elements);

}

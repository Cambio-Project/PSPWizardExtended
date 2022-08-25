package psp.mappings.postprocessing;

public class MultipleWhiteSpaceReplacer implements PatternFormatter {

    @Override
    public String format(String pattern) {
        return pattern.replaceAll("\\s+", " ");
    }

}

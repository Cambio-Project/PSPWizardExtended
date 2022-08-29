package psp.mappings;

public class TimeLanguageDefinitions {
    private String defaultStartTimeBoundBracket = "[";
    private String defaultEndTimeBoundBracket = "]";
    private String infinityStartTimeBoundBracket = "(";
    private String infinityEndTimeBoundBracket = ")";

    public TimeLanguageDefinitions(String defaultStartTimeBoundBracket, String defaultEndTimeBoundBracket,
        String infinityStartTimeBoundBracket, String infinityEndTimeBoundBracket) {
        super();
        this.defaultStartTimeBoundBracket = defaultStartTimeBoundBracket;
        this.defaultEndTimeBoundBracket = defaultEndTimeBoundBracket;
        this.infinityStartTimeBoundBracket = infinityStartTimeBoundBracket;
        this.infinityEndTimeBoundBracket = infinityEndTimeBoundBracket;
    }

    public String getDefaultStartTimeBoundBracket() {
        return defaultStartTimeBoundBracket;
    }

    public String getDefaultEndTimeBoundBracket() {
        return defaultEndTimeBoundBracket;
    }

    public String getInfinityStartTimeBoundBracket() {
        return infinityStartTimeBoundBracket;
    }

    public String getInfinityEndTimeBoundBracket() {
        return infinityEndTimeBoundBracket;
    }

}

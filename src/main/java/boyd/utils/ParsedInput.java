package boyd.utils;

import java.util.List;

/**
 * Simple value object holding a parsed description and tags.
 */
public class ParsedInput {
    final String description;
    final List<String> tags;

    ParsedInput(String description, List<String> tags) {
        this.description = description;
        this.tags = tags;
    }

    /**
     * Returns the parsed description without tags.
     *
     * @return description text
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the parsed tags (without the leading '#').
     *
     * @return list of tags
     */
    public List<String> getTags() {
        return this.tags;
    }
}

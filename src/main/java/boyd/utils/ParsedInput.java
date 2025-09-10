package boyd.utils;

import java.util.List;

public class ParsedInput {
    final String description;
    final List<String> tags;

    ParsedInput(String description, List<String> tags) {
        this.description = description;
        this.tags = tags;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<? extends String> tagsToAdd) {
        this.tags.addAll(tagsToAdd);
    }
}

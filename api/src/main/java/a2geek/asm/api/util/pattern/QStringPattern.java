package a2geek.asm.api.util.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QStringPattern extends QPattern {
    private final String string;
    private final boolean echo;

    public QStringPattern(String string, boolean echo) {
        Objects.requireNonNull(string);
        this.string = string.toLowerCase();
        this.echo = echo;
    }

    @Override
    public List<String> match(String test) {
        Objects.requireNonNull(test);

        if (!test.toLowerCase().startsWith(string)) {
            return null;
        }

        List<String> results = new ArrayList<>();
        if (test.length() > string.length()) {
            results = matchNext(test.substring(string.length()));
        }
        if (results != null && echo) {
            results.addFirst(test.substring(0, this.string.length()));
        }
        return results;
    }

    @Override
    public String toString() {
        if (this.next == null) {
            return this.string;
        }
        return String.format("%s%s", this.string, this.next.toString());
    }
}

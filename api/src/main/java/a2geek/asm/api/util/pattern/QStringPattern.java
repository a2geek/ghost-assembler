package a2geek.asm.api.util.pattern;

import java.util.Objects;

public class QStringPattern extends QPattern {
    private final String string;

    public QStringPattern(String string, boolean echo) {
        Objects.requireNonNull(string);
        this.string = string.toLowerCase();
    }

    @Override
    public boolean match(QMatch qmatch, String test) {
        Objects.requireNonNull(test);

        if (!test.toLowerCase().startsWith(string)) {
            return false;
        }

        boolean matched = true;
        if (test.length() > string.length()) {
            matched = matchNext(qmatch, test.substring(string.length()));
        }
        if (matched && qmatch.inGroup()) {
            qmatch.addResult(test.substring(0, this.string.length()));
        }
        return matched;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public String toString() {
        return String.format("%s%s", this.string, super.toString());
    }
}

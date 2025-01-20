package a2geek.asm.api.util.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Support using the question mark as a wildcard in a simple pattern matching scheme.
 * Each match is returned in a string array. If the array is <code>null</code>, then there
 * was no match. If the array is empty, there was a match but no results (either no pattern
 * or no wildcards, such as "LDA A" type syntax where "A" is the pattern). Otherwise, the
 * return is a list of strings for the <code>?</code> matches.
 */
public abstract class QPattern {
    protected QPattern next;

    public final void append(QPattern matcher) {
        if (next == null) {
            next = matcher;
        }
        else {
            next.append(matcher);
        }
    }

    public final QMatch match(String test) {
        Objects.requireNonNull(test);
        QMatch qmatch = new QMatch();
        boolean matched = match(qmatch, test);
        qmatch.setMatched(matched);
        return qmatch;
    }

    protected abstract boolean match(QMatch qmatch, String test);

    protected final boolean matchNext(QMatch qmatch, String test) {
        Objects.requireNonNull(qmatch);
        Objects.requireNonNull(test);
        if (this.next != null) {
            return this.next.match(qmatch, test);
        }
        // we're successful if this is a terminal QPattern AND nothing else to match
        return test.isEmpty();
    }

    public final int length() {
        return size() + (this.next == null ? 0 : this.next.length());
    }
    protected abstract int size();

    @Override
    public String toString() {
        if (this.next == null) {
            return "";
        }
        return this.next.toString();
    }

    public static QPattern build(String pattern) {
        if (pattern == null) {
            pattern = "";
        }

        QPattern qpattern = new QRootPattern();
        StringBuilder sb = new StringBuilder();
        List<Character> chars = new ArrayList<>();
        for (char ch : pattern.toCharArray()) {
            chars.add(ch);
        }

        while (!chars.isEmpty()) {
            char ch = chars.removeFirst();
            if (ch == '?') {
                if (!sb.isEmpty()) {
                    qpattern.append(new QStringPattern(sb.toString(), false));
                    sb.setLength(0);
                }
                qpattern.append(new QWildcardPattern());
            }
            else if (ch == '{') {
                if (!sb.isEmpty()) {
                    qpattern.append(new QStringPattern(sb.toString(), false));
                    sb.setLength(0);
                }
                qpattern.append(new QBeginGroupPattern());
            }
            else if (ch == '}') {
                if (!sb.isEmpty()) {
                    qpattern.append(new QStringPattern(sb.toString(), false));
                    sb.setLength(0);
                }
                qpattern.append(new QEndGroupPattern());
            }
            else {
                sb.append(ch);
            }
        }
        if (!sb.isEmpty()) {
            qpattern.append(new QStringPattern(sb.toString(), false));
        }
        return qpattern;
    }
}

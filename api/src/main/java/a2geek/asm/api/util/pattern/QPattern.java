package a2geek.asm.api.util.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Support using the question mark as a wildcard in a simple pattern matching scheme.
 * Each match is returned in a string array. If the array is <code>null</code>, then there
 * was no match. If the array is empty, there was a match but no results (either no pattern
 * or no wildcards, such as "LDA A" type syntax where "A" is the pattern). Otherwise, the
 * return is a list of strings for the <code>?</code> matches.
 */
public abstract class QPattern {
    protected QPattern next;

    public void append(QPattern matcher) {
        if (next == null) {
            next = matcher;
        }
        else {
            next.append(matcher);
        }
    }

    public abstract List<String> match(String test);

    protected List<String> matchNext(String test) {
        Objects.requireNonNull(test);
        if (this.next != null) {
            return this.next.match(test);
        }
        if (test.isEmpty()) {
            return new ArrayList<>();
        }
        return null;
    }

    @Override
    public String toString() {
        if (this.next == null) {
            return "-empty-";
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
                qpattern.append(buildEcho(chars));
            }
            else if (ch == '}') {
                var msg = String.format("too many echo close operators in pattern '%s'",
                    pattern);
                throw new RuntimeException(msg);
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
    private static QEchoPattern buildEcho(List<Character> chars) {
        StringBuilder sb = new StringBuilder();
        QEchoPattern qpattern = new QEchoPattern();
        int wildcardCount = 0;
        while (!chars.isEmpty()) {
            char ch = chars.removeFirst();
            if (ch == '?') {
                if (!sb.isEmpty()) {
                    qpattern.appendEcho(new QStringPattern(sb.toString(), true));
                    sb.setLength(0);
                }
                qpattern.appendEcho(new QWildcardPattern());
                wildcardCount++;
            }
            else if (ch == '{') {
                throw new RuntimeException("cannot nest echo patterns");
            }
            else if (ch == '}') {
                if (!sb.isEmpty()) {
                    qpattern.appendEcho(new QStringPattern(sb.toString(), true));
                }
                if (wildcardCount != 1) {
                    throw new RuntimeException("only one '?' allowed per echo pattern");
                }
                return qpattern;
            }
            else {
                sb.append(ch);
            }
        }
        throw new RuntimeException("unterminated echo group");
    }
}

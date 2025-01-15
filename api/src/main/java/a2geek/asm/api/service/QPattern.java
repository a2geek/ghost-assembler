package a2geek.asm.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Support using the question mark as a wildcard in a simple pattern matching scheme.
 * Each match is returned in a string array. If the array is <code>null</code>, then there
 * was no match. If the array is empty, there was a match but no results (either no pattern
 * or no wildcards, such as "LDA A" type syntax where "A" is the pattern). Otherwise, the
 * return is a list of strings for the <code>?</code> matches.
 * @see a2geek.asm.api.service.QPatternTest
 */
public class QPattern {
    public static List<String> match(String pattern, String test) {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(test);

        QMatcher matcher = new QRootMatcher();
        StringBuilder sb = new StringBuilder();
        for (var ch : pattern.toUpperCase().toCharArray()) {
            if (ch == '?') {
                if (!sb.isEmpty()) {
                    matcher.append(new QStringMatcher(sb.toString()));
                }
                matcher.append(new QWildcardMatcher());
                sb.setLength(0);
            }
            else {
                sb.append(ch);
            }
        }
        if (!sb.isEmpty()) {
            matcher.append(new QStringMatcher(sb.toString()));
        }
        return matcher.match(test);
    }

    public static abstract class QMatcher {
        protected QMatcher next;

        public void append(QMatcher matcher) {
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
    }
    public static class QRootMatcher extends QMatcher {
        @Override
        public List<String> match(String test) {
            if (this.next == null) {
                throw new RuntimeException("empty pattern expression found");
            }
            return this.next.match(test);
        }
    }
    public static class QStringMatcher extends QMatcher {
        private final String string;

        public QStringMatcher(String string) {
            Objects.requireNonNull(string);
            this.string = string.toLowerCase();
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
                if (results == null) {
                    return null;
                }
            }
            return results;
        }
    }
    public static class QWildcardMatcher extends QMatcher {
        @Override
        public List<String> match(final String test) {
            Objects.requireNonNull(test);

            List<String> results = null;
            StringBuilder match = new StringBuilder();
            String work = test;
            while (!work.isEmpty()) {
                results = matchNext(work);
                if (results != null) {
                    break;
                }
                match.append(work.charAt(0));
                work = work.substring(1);
            }

            if (results == null) {
                if (this.next != null) {
                    throw new RuntimeException(String.format("no pattern match for '%s'", test));
                }
                results = new ArrayList<>();
            }
            if (!match.isEmpty()) {
                results.addFirst(match.toString().trim());
            }
            return results;
        }
    }
}

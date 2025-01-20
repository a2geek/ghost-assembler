package a2geek.asm.api.util.pattern;

import java.util.Objects;

public class QWildcardPattern extends QPattern {
    @Override
    public boolean match(QMatch qmatch, final String test) {
        Objects.requireNonNull(qmatch);
        Objects.requireNonNull(test);

        StringBuilder match = new StringBuilder();
        String work = test;
        boolean matched = true;
        while (true) {
            matched = matchNext(qmatch, work);
            if (matched || work.isEmpty()) break;
            match.append(work.charAt(0));
            work = work.substring(1);
        }

        if (match.isEmpty()) {
            matched = false;
        }
        else {
            qmatch.addResult(match.toString().trim());
        }
        return matched;
    }

    @Override
    public String toString() {
        return String.format("?%s", super.toString());
    }
}

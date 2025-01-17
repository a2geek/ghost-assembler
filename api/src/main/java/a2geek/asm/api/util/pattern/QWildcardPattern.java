package a2geek.asm.api.util.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QWildcardPattern extends QPattern {
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
            results = new ArrayList<>();
        }
        if (!match.isEmpty()) {
            results.addFirst(match.toString().trim());
        }
        return results;
    }

    @Override
    public String toString() {
        if (this.next == null) {
            return "?";
        }
        return String.format("?%s", this.next.toString());
    }
}

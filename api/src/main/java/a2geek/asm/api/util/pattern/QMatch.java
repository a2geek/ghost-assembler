package a2geek.asm.api.util.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QMatch {
    private boolean matched = false;
    private List<String> results = new ArrayList<>();
    private StringBuilder group = null;

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
    public boolean isMatched() {
        return matched;
    }

    public List<String> getResults() {
        if (!matched) {
            throw new RuntimeException("unbalanced group");
        }
        return results;
    }
    public int getSize() {
        if (results == null || !matched) {
            return 0;
        }
        return results.size();
    }
    public String getResult(int n) {
        if (results == null || !matched) {
            throw new RuntimeException("no results to retrieve");
        }
        return results.get(n);
    }

    public void addResult(String result) {
        Objects.requireNonNull(result);
        // QPattern should be testing the next before adding, so we're reversing the order
        if (group == null) {
            results.addFirst(result);
        }
        else {
            group.insert(0, result);
        }
    }

    public boolean inGroup() {
        return group != null;
    }
    public void beginGroup(String current) {
        if (group != null) {
            throw new RuntimeException("cannot nest groups");
        }
        group = new StringBuilder(current);
    }
    public String endGroup() {
        if (group == null) {
            throw new RuntimeException("must start group before ending it");
        }
        String current = group.toString();
        group = null;
        return current;
    }
}

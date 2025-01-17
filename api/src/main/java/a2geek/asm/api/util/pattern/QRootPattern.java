package a2geek.asm.api.util.pattern;

import java.util.List;

public class QRootPattern extends QPattern {
    @Override
    public List<String> match(String test) {
        if (this.next == null) {
            throw new RuntimeException("empty pattern expression found");
        }
        return this.next.match(test);
    }

    @Override
    public String toString() {
        if (this.next == null) {
            return "-empty-";
        }
        return this.next.toString();
    }
}

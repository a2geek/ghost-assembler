package a2geek.asm.api.util.pattern;

public class QRootPattern extends QPattern {
    @Override
    public boolean match(QMatch qmatch, String test) {
        return matchNext(qmatch, test);
    }

    @Override
    public String toString() {
        if (this.next == null) {
            return "-empty-";
        }
        return this.next.toString();
    }
}

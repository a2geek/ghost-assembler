package a2geek.asm.api.util.pattern;

public class QBeginGroupPattern extends QPattern {
    @Override
    protected boolean match(QMatch qmatch, String test) {
        qmatch.beginGroup("");
        boolean matched = matchNext(qmatch, test);
        String current = qmatch.endGroup();
        if (matched) {
            qmatch.addResult(current);
        }
        return matched;
    }

    @Override
    public String toString() {
        return String.format("{%s", super.toString());
    }
}

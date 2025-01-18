package a2geek.asm.api.util.pattern;

public class QEndGroupPattern extends QPattern {
    @Override
    public boolean match(QMatch qmatch, String test) {
        String current = qmatch.endGroup();
        boolean matched = matchNext(qmatch, test);
        qmatch.beginGroup(current);
        return matched;
    }

    @Override
    public String toString() {
        return String.format("%s}", super.toString());
    }
}

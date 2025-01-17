package a2geek.asm.api.util.pattern;

import java.util.List;

public class QEchoPattern extends QPattern {
    private QPattern echo;

    public void appendEcho(QPattern matcher) {
        if (echo == null) {
            echo = matcher;
        }
        else {
            echo.append(matcher);
        }
    }

    @Override
    public List<String> match(String test) {
        if (this.echo != null) {
            List<String> match = this.echo.match(test);
            if (match != null) {
                String result = match.stream().reduce("", (a,b) -> a+b);
                match.clear();
                match.add(result);
            }
            return match;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.echo != null) {
            sb.append(this.echo.toString());
        } else {
            sb.append("-empty-");
        }
        sb.append("}");
        if (this.next != null) {
            sb.append(this.next.toString());
        }
        return sb.toString();
    }
}

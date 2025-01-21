package a2geek.asm.api.util.pattern;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class QPatternTest {
    private void check(String pattern, String testcase, final String... expected) {
        QPattern qpattern = QPattern.build(pattern);
        final QMatch actual = qpattern.match(testcase);
        if (!actual.isMatched()) {
            fail("Expecting a match, but no match found");
        }
        int len = Math.min(expected.length, actual.getSize());
        for (int i = 0; i<len; i++) {
            assertEquals(expected[i], actual.getResult(i),
                inequalityError(expected[i], actual.getResult(i)));
        }
        assertEquals(expected.length, actual.getSize(), () ->
            String.format("Expecting actual and expected to match: %s vs %s",
                actual, List.of(expected)));
    }
    private Supplier<String> inequalityError(String a, String b) {
        return () -> String.format("'%s' != '%s'", a, b);
    }

    @Test
    public void testOneWildcardMatch() {
        check("?", "varName1", "varName1");
        check("?", "\n\t varName1", "varName1");
        check("?", "varName1  \n\t", "varName1");
    }

    @Test
    public void testOneWildcardWithPrefix() {
        check("#?", "#1", "1");
        check("#?", "#a+b+c", "a+b+c");
    }

    @Test
    public void testOneWildcardWithSuffix() {
        check("?,x", "$abcd,x", "$abcd");
    }

    @Test
    public void testOneWildcardPreAndSuffix() {
        check("(?,x)", "(address,X)", "address");
        check("(?),y", "(Address),y", "Address");
    }

    @Test
    public void testTwoWildcard() {
        check("R?,?", "R3,$1234", "3", "$1234");
    }

    @Test
    public void testCombined() {
        check("{R?}", "R3", "R3");
        check("@{R?}", "@R3", "R3");
        check("{R?},?", "R3,$1234", "R3", "$1234");
    }

    @Test
    public void testProblemStuff() {
        check("#?", "#<(code-1)", "<(code-1)");
    }
}

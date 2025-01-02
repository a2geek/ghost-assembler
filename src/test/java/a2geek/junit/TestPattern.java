package a2geek.junit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestPattern {
	@Test
	public void testSimpleRe() {
		Pattern pattern = Pattern.compile("[a-z]+");
		Assertions.assertFalse(pattern.matcher("apples,oranges").matches());
		Assertions.assertFalse(pattern.matcher("1orange").matches());
		Assertions.assertFalse(pattern.matcher("apples-").matches());
		Assertions.assertTrue(pattern.matcher("apples").matches());
	}
	@Test
	public void testGroupRe() {
		Pattern pattern = Pattern.compile("([a-z]+)");
		Matcher matcher = pattern.matcher("apples");
		Assertions.assertTrue(matcher.matches());
		Assertions.assertEquals("apples", matcher.group());
		Assertions.assertFalse(pattern.matcher("apples,oranges").matches());
	}
	@Test
	public void testComplexRe1() {
		Pattern pattern = Pattern.compile("([a-zA-Z0-9-+\\*\\.]+)");
		Matcher matcher = pattern.matcher("0x35");
		Assertions.assertTrue(matcher.matches());
		Assertions.assertEquals("0x35", matcher.group());
		Assertions.assertFalse(pattern.matcher("0x35,x").matches());
	}
	@Test
	public void testComplexRe2() {
		Pattern pattern = Pattern.compile("([a-z0-9-+\\*\\.]+),[x]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher("0x35,x");
		Assertions.assertTrue(matcher.matches());
		Assertions.assertEquals("0x35", matcher.group(1));
		Assertions.assertFalse(pattern.matcher("0x35,y").matches());
		Assertions.assertFalse(pattern.matcher("0x35").matches());
	}
	@Test
	public void testUsingBraces() {
		Assertions.assertTrue(Pattern.matches("A,\\[[0-9_:a-z-/'\\+\\*\\.\\$]+\\]", "A,[over_here]"));
	}
	
	@Test
	public void testSimilarMnemonic() {
		Pattern pattern = Pattern.compile("OR([1234F])", Pattern.CASE_INSENSITIVE);
		Assertions.assertFalse(pattern.matcher("XOR2").matches());
		Assertions.assertFalse(pattern.matcher("xorf").matches());
		Assertions.assertFalse(pattern.matcher("OR").matches());
		Assertions.assertTrue(pattern.matcher("OR2").matches());
		Assertions.assertTrue(pattern.matcher("orf").matches());
	}
	/** Prototype test for non-grouped text extraction. */
	@Test
	public void testGetUngroupedText() {
		Pattern pattern = Pattern.compile(".+([123F])", Pattern.CASE_INSENSITIVE);
		String text ="xorf";
		Matcher matcher = pattern.matcher(text);
		Assertions.assertTrue(matcher.matches());
		StringBuilder nonPatternText = new StringBuilder();
		int pos = 0;
		for (int g=1; g<=matcher.groupCount(); g++) {
			if (matcher.start(g) > 1) {
				nonPatternText.append(text, pos, matcher.start(g));
			}
			pos = matcher.end(g);
		}
		if (pos >= text.length()) {
			nonPatternText.append(text.substring(pos));
		}
		Assertions.assertEquals("xor", nonPatternText.toString());
	}
}

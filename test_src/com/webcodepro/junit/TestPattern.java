package com.webcodepro.junit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class TestPattern {
	@Test
	public void testSimpleRe() {
		Pattern pattern = Pattern.compile("[a-z]+");
		Assert.assertFalse(pattern.matcher("apples,oranges").matches());
		Assert.assertFalse(pattern.matcher("1orange").matches());
		Assert.assertFalse(pattern.matcher("apples-").matches());
		Assert.assertTrue(pattern.matcher("apples").matches());
	}
	@Test
	public void testGroupRe() {
		Pattern pattern = Pattern.compile("([a-z]+)");
		Matcher matcher = pattern.matcher("apples");
		Assert.assertTrue(matcher.matches());
		Assert.assertEquals("apples", matcher.group());
		Assert.assertFalse(pattern.matcher("apples,oranges").matches());
	}
	@Test
	public void testComplexRe1() {
		Pattern pattern = Pattern.compile("([a-zA-Z0-9-+\\*\\.]+)");
		Matcher matcher = pattern.matcher("0x35");
		Assert.assertTrue(matcher.matches());
		Assert.assertEquals("0x35", matcher.group());
		Assert.assertFalse(pattern.matcher("0x35,x").matches());
	}
	@Test
	public void testComplexRe2() {
		Pattern pattern = Pattern.compile("([a-z0-9-+\\*\\.]+),[x]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher("0x35,x");
		Assert.assertTrue(matcher.matches());
		Assert.assertEquals("0x35", matcher.group(1));
		Assert.assertFalse(pattern.matcher("0x35,y").matches());
		Assert.assertFalse(pattern.matcher("0x35").matches());
	}
	@Test
	public void testUsingBraces() {
		Assert.assertTrue(Pattern.matches("A,\\[[0-9_:a-z-/'\\+\\*\\.\\$]+\\]", "A,[over_here]"));
	}
	
	@Test
	public void testSimilarMnemonic() {
		Pattern pattern = Pattern.compile("OR([1234F])", Pattern.CASE_INSENSITIVE);
		Assert.assertFalse(pattern.matcher("XOR2").matches());
		Assert.assertFalse(pattern.matcher("xorf").matches());
		Assert.assertFalse(pattern.matcher("OR").matches());
		Assert.assertTrue(pattern.matcher("OR2").matches());
		Assert.assertTrue(pattern.matcher("orf").matches());
	}
	/** Prototype test for non-grouped text extraction. */
	@Test
	public void testGetUngroupedText() {
		Pattern pattern = Pattern.compile(".+([123F])", Pattern.CASE_INSENSITIVE);
		String text ="xorf";
		Matcher matcher = pattern.matcher(text);
		Assert.assertTrue(matcher.matches());
		StringBuilder nonPatternText = new StringBuilder();
		int pos = 0;
		for (int g=1; g<=matcher.groupCount(); g++) {
			if (matcher.start(g) > 1) {
				nonPatternText.append(text.substring(pos, matcher.start(g)));
			}
			pos = matcher.end(g);
		}
		if (pos >= text.length()) {
			nonPatternText.append(text.substring(pos));
		}
		Assert.assertEquals("xor", nonPatternText.toString());
	}
}

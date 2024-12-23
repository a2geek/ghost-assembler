package com.webcodepro.asm.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test and explore the Pattern class.
 * 
 * @author Rob
 */
public class RegularExpressionTest {
	@Test
	public void testPattern() {
		// Note the "\\" is because of the String.
		// The real RE is "\[(.*)\],A"
		Pattern p = Pattern.compile("\\[(.*)\\],A");
		Matcher m = p.matcher("[expression],A");
		Assert.assertTrue(m.matches());
		Assert.assertEquals(1, m.groupCount());
		Assert.assertEquals("expression", m.group(1));
		
		m = p.matcher("[Address+1],A");
		Assert.assertTrue(m.matches());
		Assert.assertEquals(1, m.groupCount());
		Assert.assertEquals("Address+1", m.group(1));
	}
	@Test
	public void testFunction() {
		Pattern p = Pattern.compile("([a-z]+)\\((.+?)\\)");
		
		Matcher m = p.matcher("byte(addr)");
		Assert.assertTrue(m.matches());
		Assert.assertEquals(2, m.groupCount());
		Assert.assertEquals("byte", m.group(1));
		Assert.assertEquals("addr", m.group(2));

		m = p.matcher("byte(expr,3)");
		Assert.assertTrue(m.matches());
		Assert.assertEquals(2, m.groupCount());
		Assert.assertEquals("byte", m.group(1));
		Assert.assertEquals("expr,3", m.group(2));

		m = p.matcher("substr(expr,3,92)");
		Assert.assertTrue(m.matches());
		Assert.assertEquals(2, m.groupCount());
		Assert.assertEquals("substr", m.group(1));
		Assert.assertEquals("expr,3,92", m.group(2));

		m = p.matcher("fn( 5 + 3 )");
		Assert.assertTrue(m.matches());
		Assert.assertEquals(2, m.groupCount());
		Assert.assertEquals("fn", m.group(1));
		Assert.assertEquals("5 + 3", m.group(2).trim());
	}
}

package a2geek.asm.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		Assertions.assertTrue(m.matches());
		Assertions.assertEquals(1, m.groupCount());
		Assertions.assertEquals("expression", m.group(1));
		
		m = p.matcher("[Address+1],A");
		Assertions.assertTrue(m.matches());
		Assertions.assertEquals(1, m.groupCount());
		Assertions.assertEquals("Address+1", m.group(1));
	}
	@Test
	public void testFunction() {
		Pattern p = Pattern.compile("([a-z]+)\\((.+?)\\)");
		
		Matcher m = p.matcher("byte(addr)");
		Assertions.assertTrue(m.matches());
		Assertions.assertEquals(2, m.groupCount());
		Assertions.assertEquals("byte", m.group(1));
		Assertions.assertEquals("addr", m.group(2));

		m = p.matcher("byte(expr,3)");
		Assertions.assertTrue(m.matches());
		Assertions.assertEquals(2, m.groupCount());
		Assertions.assertEquals("byte", m.group(1));
		Assertions.assertEquals("expr,3", m.group(2));

		m = p.matcher("substr(expr,3,92)");
		Assertions.assertTrue(m.matches());
		Assertions.assertEquals(2, m.groupCount());
		Assertions.assertEquals("substr", m.group(1));
		Assertions.assertEquals("expr,3,92", m.group(2));

		m = p.matcher("fn( 5 + 3 )");
		Assertions.assertTrue(m.matches());
		Assertions.assertEquals(2, m.groupCount());
		Assertions.assertEquals("fn", m.group(1));
		Assertions.assertEquals("5 + 3", m.group(2).trim());
	}
}

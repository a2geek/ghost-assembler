package com.webcodepro.asm.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Exercise the ExpressionService.
 * 
 * @author Rob
 */
public class ExpressionServiceTest {
	/**
	 * Test a few simple values through the expression evaluation.
	 */
	@Test
	public void testSimpleValues() throws AssemblerException {
		testExpression(25, "25");
		testExpression(13, "0xd");
		testExpression(100, "0x64");
		testExpression(101, "65h");
		testExpression(102, "$66");
	}
	/**
	 * Test some negative values.
	 */
	@Test
	public void testNegativeValues() throws AssemblerException {
		testExpression(-1, "-1");
		testExpression(-10, "-10");
	}
	/**
	 * Test a few basic expressions.
	 */
	@Test
	public void testSimpleExpressions() throws AssemblerException {
		testExpression(8, "5+3");
		testExpression(35, "5*7");
		testExpression(-2, "8-10");
		testExpression(4, "8/2");
		testExpression(1, "5%2");
	}
	/**
	 * Test parenthesis.
	 */
	@Test
	public void testParenthesis() throws AssemblerException {
		testExpression(5, "(5)");
		testExpression(3, "(2+1)");
		testExpression(8, "2*(2*2)");
	}
	/**
	 * Test variable evaluation.
	 */
	@Test
	public void testVariables() throws AssemblerException {
		Map<String,Long> variables = new HashMap<String,Long>();
		variables.put("ADDR", 0x312L);
		testExpression(0x312, "ADDR", variables);
		testExpression(0x300, "ADDR-0x12", variables);
		testExpression(0x313, "ADDR+1", variables);
		testExpression(0x3, "(ADDR+1)/256", variables);
	}
	/**
	 * Ensure that whitespace doesn't cause problems.
	 */
	@Test
	public void testWhitespace() throws AssemblerException {
		testExpression(1, " 1 ");
		testExpression(5, "4 + 1");
		testExpression(12, "\t( 4 + 2 )\t * 2    ");
	}
	/**
	 * Verify that unary minus works. 
	 */
	@Test
	public void testUnaryOperators() throws AssemblerException {
		testExpression(-1, "-1");
		testExpression(6, "4 - -2");
		testExpression(-6, "-2 * 3");
		testExpression(-12, "(4+2)*-2");
	}
	/**
	 * Ensure various number format evaluate appropriately.
	 */
	@Test
	public void testEvaluateValue() throws AssemblerException {
		testValue(5, "5");				// decimal (base 10)
		testValue(100, "0x64");			// hexidecimal (base 16)
		testValue(127, "0b01111111");	// binary (base 2)
		testValue(511, "0777");			// octal (base 8)
	}
	/**
	 * Test the bit-wise operands (&, |, &lt;&lt;, &gt;&gt;).
	 */
	@Test
	public void testBitOperands() throws AssemblerException {
		testExpression(0xa, "0b1000 | 0b0010");
		testExpression(0xc, "0b1111 & 0b1100");
		testExpression(0xa, "0b1100 ^ 0b0110");
		testExpression(0x8, "0b1 << 3");
		testExpression(0x1, "0b1000 >> 3");
	}
	/**
	 * Test &lt;, &gt;, &lt;=, &gt;=, =, !=.
	 */
	@Test
	public void testComparisons() throws AssemblerException {
		testExpression(1, "2 > 1");
		testExpression(0, "2 < 1");
		testExpression(1, "3 = 0x3");
		testExpression(1, "2 >= 2");
		testExpression(1, "2 <= 2");
		testExpression(1, "2 != 3");
	}
	/**
	 * Test unbalanced expressions.
	 */
	@Test
	public void testUnbalancedExpressions() {
		testExpressionError("(1+2");
		testExpressionError("1+3)");
		testExpressionError("1+");
	}
	/**
	 * Test logical AND (&amp;&amp;) as well as OR (||).
	 */
	@Test
	public void testLogicalOperators() throws AssemblerException {
		testExpression(0x1, "0x1 && 0x2");
		testExpression(0x0, "0x1 && 0x0");
		testExpression(0x1, "0x5 || 0x0");
		testExpression(0x1, "0x00000 || 0x5123");
		testExpression(0x0, "0x0000 || 0x0");
	}
	/**
	 * Check that floating point numbers are evaluated correctly.
	 * Note that we actually don't understand floating point, we just need
	 * to make sure the correct truncated number is evaluated.
	 */
	@Test
	public void testFloatingPoint() throws AssemblerException {
		testExpression(10, "10.0");
		testExpression(10, "10.5");
	}
	/**
	 * Test character expressions. 
	 */
	@Test
	public void testCharacterExpression() throws AssemblerException {
		testExpression(0x41, "'A'");				// $41 (Standard ASCII)
		testExpression(0xC1, "'A'|0x80");			// $C1 (Apple2 uses high bit on)
		testExpression(0x20, "' '");				// A space can cause problems
	}
	/**
	 * Test string expressions.
	 */
	@Test
	public void testStringExpression() throws AssemblerException {
		testExpression("Hello", "\"Hello\"");
		testExpression("HelloWorld", "\"Hello\" + \"World\"");
	}
	
	/**
	 * Evaluate an expression against an expected value.  Convenience method
	 * that supplies an empty variable map.  
	 */
	protected void testExpression(long expected, String expression) throws AssemblerException {
		testExpression(expected, expression, new HashMap<String,Long>());
	}
	/**
	 * Evaluate an expression against an expected value using the supplied list
	 * of variable values.
	 */
	protected void testExpression(long expected, String expression, Map<String,Long> variables) throws AssemblerException {
		Long value = (Long)ExpressionService.evaluate(expression, variables);
		Assertions.assertEquals(expected, value.longValue());
	}
	protected void testExpression(String expected, String expression) throws AssemblerException {
		String value = (String)ExpressionService.evaluate(expression, new HashMap<String,Long>());
		Assertions.assertEquals(expected, value);
	}
	/**
	 * Evaluate an expression that is expected to cause an error.
	 * Fails if no error condition is raised.
	 */
	protected void testExpressionError(String expression) {
		try {
			ExpressionService.evaluate(expression);
			Assertions.fail("This expression should have been in error.");
		} catch (AssemblerException ex) {
			// this is good
		}
	}
	/**
	 * Evaluate a value against its expected value.
	 */
	protected void testValue(long expected, String expression) throws AssemblerException {
		Long value = (Long)ExpressionService.evaluateValue(expression, new HashMap<String,Long>());
		Assertions.assertEquals(expected, value.longValue());
	}
}

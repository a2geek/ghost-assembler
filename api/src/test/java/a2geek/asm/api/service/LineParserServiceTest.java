package a2geek.asm.api.service;

import a2geek.asm.api.assembler.LineParts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Exercise the LineParserService.
 * 
 * @author Rob
 */
public class LineParserServiceTest {
	@Test
	public void testComment() {
		LineParts parts = LineParserService.parseLine("; this is a comment");
		Assertions.assertEquals("; this is a comment", parts.getComment());
		Assertions.assertFalse(parts.isGlobalLabel());
		Assertions.assertFalse(parts.isLocalLabel());
		Assertions.assertNull(parts.getLabel());
		Assertions.assertNull(parts.getOpcode());
		Assertions.assertNull(parts.getExpression());
	}
	@Test
	public void testLabel() {
		LineParts parts = LineParserService.parseLine("label");
		Assertions.assertEquals("label", parts.getLabel());
		Assertions.assertTrue(parts.isGlobalLabel());
		Assertions.assertNull(parts.getOpcode());
		Assertions.assertNull(parts.getExpression());
		Assertions.assertNull(parts.getComment());
	}
	@Test
	public void testLocalLabel() {
		LineParts parts = LineParserService.parseLine(":temp");
		Assertions.assertEquals(":temp", parts.getLabel());
		Assertions.assertTrue(parts.isLocalLabel());
		Assertions.assertNull(parts.getOpcode());
		Assertions.assertNull(parts.getExpression());
		Assertions.assertNull(parts.getComment());
	}
	@Test
	public void testOpcode() {
		LineParts parts = LineParserService.parseLine("\topcode");
		Assertions.assertEquals("opcode", parts.getOpcode());
		Assertions.assertNull(parts.getLabel());
		Assertions.assertNull(parts.getExpression());
		Assertions.assertNull(parts.getComment());
	}
	@Test
	public void testOpcodeAndExpression() {
		LineParts parts = LineParserService.parseLine("\topcode [expression],A");
		Assertions.assertEquals("opcode", parts.getOpcode());
		Assertions.assertEquals("[expression],A", parts.getExpression());
		Assertions.assertNull(parts.getLabel());
		Assertions.assertNull(parts.getComment());
	}
	@Test
	public void testLabelOpcodeExpressionAndComment() {
		LineParts parts = LineParserService.parseLine("label\topcode [expression],A\t; comment");
		Assertions.assertEquals("label", parts.getLabel());
		Assertions.assertEquals("opcode", parts.getOpcode());
		Assertions.assertEquals("[expression],A", parts.getExpression());
		Assertions.assertEquals("; comment", parts.getComment());
	}
	@Test
	public void testConstant() {
		LineParts parts = LineParserService.parseLine("address = 0x300");
		Assertions.assertEquals("address", parts.getLabel());
		Assertions.assertEquals("=", parts.getOpcode());
		Assertions.assertEquals("0x300", parts.getExpression());
		Assertions.assertNull(parts.getComment());
	}
	@Test
	public void testNegativeConstant() {
		LineParts parts = LineParserService.parseLine("value = -32");
		Assertions.assertEquals("value", parts.getLabel());
		Assertions.assertEquals("=", parts.getOpcode());
		Assertions.assertEquals("-32", parts.getExpression());
		Assertions.assertNull(parts.getComment());
	}
	@Test
	public void testOpcodeSpacing() {
		LineParts parts = LineParserService.parseLine("   opcode  expression");
		Assertions.assertNull(parts.getLabel());
		Assertions.assertEquals("opcode", parts.getOpcode());
		Assertions.assertEquals("expression", parts.getExpression());
		Assertions.assertNull(parts.getComment());
	}
}

package com.webcodepro.asm.service;

import org.junit.Assert;
import org.junit.Test;

import com.webcodepro.asm.assembler.LineParts;

/**
 * Exercise the LineParserService.
 * 
 * @author Rob
 */
public class LineParserServiceTest {
	@Test
	public void testComment() {
		LineParts parts = LineParserService.parseLine("; this is a comment");
		Assert.assertEquals("; this is a comment", parts.getComment());
		Assert.assertFalse(parts.isGlobalLabel());
		Assert.assertFalse(parts.isLocalLabel());
		Assert.assertNull(parts.getLabel());
		Assert.assertNull(parts.getOpcode());
		Assert.assertNull(parts.getExpression());
	}
	@Test
	public void testLabel() {
		LineParts parts = LineParserService.parseLine("label");
		Assert.assertEquals("label", parts.getLabel());
		Assert.assertTrue(parts.isGlobalLabel());
		Assert.assertNull(parts.getOpcode());
		Assert.assertNull(parts.getExpression());
		Assert.assertNull(parts.getComment());
	}
	@Test
	public void testLocalLabel() {
		LineParts parts = LineParserService.parseLine(":temp");
		Assert.assertEquals(":temp", parts.getLabel());
		Assert.assertTrue(parts.isLocalLabel());
		Assert.assertNull(parts.getOpcode());
		Assert.assertNull(parts.getExpression());
		Assert.assertNull(parts.getComment());
	}
	@Test
	public void testOpcode() {
		LineParts parts = LineParserService.parseLine("\topcode");
		Assert.assertEquals("opcode", parts.getOpcode());
		Assert.assertNull(parts.getLabel());
		Assert.assertNull(parts.getExpression());
		Assert.assertNull(parts.getComment());
	}
	@Test
	public void testOpcodeAndExpression() {
		LineParts parts = LineParserService.parseLine("\topcode [expression],A");
		Assert.assertEquals("opcode", parts.getOpcode());
		Assert.assertEquals("[expression],A", parts.getExpression());
		Assert.assertNull(parts.getLabel());
		Assert.assertNull(parts.getComment());
	}
	@Test
	public void testLabelOpcodeExpressionAndComment() {
		LineParts parts = LineParserService.parseLine("label\topcode [expression],A\t; comment");
		Assert.assertEquals("label", parts.getLabel());
		Assert.assertEquals("opcode", parts.getOpcode());
		Assert.assertEquals("[expression],A", parts.getExpression());
		Assert.assertEquals("; comment", parts.getComment());
	}
	@Test
	public void testConstant() {
		LineParts parts = LineParserService.parseLine("address = 0x300");
		Assert.assertEquals("address", parts.getLabel());
		Assert.assertEquals("=", parts.getOpcode());
		Assert.assertEquals("0x300", parts.getExpression());
		Assert.assertNull(parts.getComment());
	}
	@Test
	public void testNegativeConstant() {
		LineParts parts = LineParserService.parseLine("value = -32");
		Assert.assertEquals("value", parts.getLabel());
		Assert.assertEquals("=", parts.getOpcode());
		Assert.assertEquals("-32", parts.getExpression());
		Assert.assertNull(parts.getComment());
	}
	@Test
	public void testOpcodeSpacing() {
		LineParts parts = LineParserService.parseLine("   opcode  expression");
		Assert.assertNull(parts.getLabel());
		Assert.assertEquals("opcode", parts.getOpcode());
		Assert.assertEquals("expression", parts.getExpression());
		Assert.assertNull(parts.getComment());
	}
}

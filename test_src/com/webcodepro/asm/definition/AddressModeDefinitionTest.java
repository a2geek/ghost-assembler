package com.webcodepro.asm.definition;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * Exercise the interesting functions in AddressModeDefinition.
 * 
 * @author Rob
 */
public class AddressModeDefinitionTest {
	@Test
	public void test6502Implied() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("");
		Pattern p = am.getRegexPattern();
		Assert.assertTrue(p.matcher("").matches());
		Assert.assertFalse(p.matcher("0x12").matches());
	}
	@Test
	public void test6502Accumulator() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("A");
		Pattern p = am.getRegexPattern();
		Assert.assertTrue(p.matcher("a").matches());
		Assert.assertTrue(p.matcher("a").matches());
		Assert.assertFalse(p.matcher("").matches());
		Assert.assertFalse(p.matcher("0x12").matches());
	}
	@Test
	public void test6502Immediate() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("#([:expr:]+)");
		Pattern p = am.getRegexPattern();
		Assert.assertTrue(p.matcher("#0x12").matches());
		Assert.assertTrue(p.matcher("#'c'").matches());
		Assert.assertFalse(p.matcher("").matches());
		Assert.assertFalse(p.matcher("0x12").matches());
		Assert.assertFalse(p.matcher("0x12,x").matches());
	}
	@Test
	public void test6502ZeroPageX_AbsoluteX() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("([:expr:]+),x");
		Pattern p = am.getRegexPattern();
		Assert.assertTrue(p.matcher("0x12,X").matches());
		Assert.assertTrue(p.matcher("addr,x").matches());
		Assert.assertFalse(p.matcher("").matches());
		Assert.assertFalse(p.matcher("0x12,y").matches());
		Assert.assertFalse(p.matcher("0x23").matches());
		Assert.assertFalse(p.matcher("#0x12").matches());
	}
	@Test
	public void test6502ZeroPageY_AbsoluteY() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("([:expr:]+),y");
		Pattern p = am.getRegexPattern();
		Assert.assertTrue(p.matcher("0x12,y").matches());
		Assert.assertTrue(p.matcher("addr,Y").matches());
		Assert.assertFalse(p.matcher("").matches());
		Assert.assertFalse(p.matcher("0x12,x").matches());
		Assert.assertFalse(p.matcher("0x23").matches());
		Assert.assertFalse(p.matcher("#0x12").matches());
	}
	@Test
	public void test6502Indirect() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("\\(([:expr:]+)\\)");
		Pattern p = am.getRegexPattern();
		Assert.assertTrue(p.matcher("(0x1234)").matches());
		Assert.assertTrue(p.matcher("(address)").matches());
		Assert.assertFalse(p.matcher("(0x12),y").matches());
		Assert.assertFalse(p.matcher("0x1234").matches());
	}
	@Test
	public void test6502IndirectX() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("\\(([:expr:]+),x\\)");
		Pattern p = am.getRegexPattern();
		Assert.assertTrue(p.matcher("(0x12,x)").matches());
		Assert.assertTrue(p.matcher("(address,x)").matches());
		Assert.assertFalse(p.matcher("(0x12)").matches());
		Assert.assertFalse(p.matcher("0x1234,x").matches());
	}
	@Test
	public void test6502IndirectY() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("\\(([:expr:]+)\\),y");
		Pattern p = am.getRegexPattern();
		Assert.assertTrue(p.matcher("(0x12),y").matches());
		Assert.assertTrue(p.matcher("(address),y").matches());
		Assert.assertFalse(p.matcher("(0x12,x)").matches());
		Assert.assertFalse(p.matcher("0x1234,y").matches());
	}
	@Test
	public void test6502ZeroPage_Relative_Absolute() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("([:expr:]+)");
		Pattern p = am.getRegexPattern();
		Assert.assertTrue(p.matcher("0x12").matches());
		Assert.assertTrue(p.matcher("addr").matches());
		Assert.assertTrue(p.matcher("54322").matches());
		Assert.assertFalse(p.matcher("addr,y").matches());
		Assert.assertFalse(p.matcher("0x12,y").matches());
		Assert.assertFalse(p.matcher("(addr)").matches());
	}
	
	@Test
	public void testMnemonicPatternMatch() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setMnemonicFormat(".+([124F])");
		Assert.assertEquals("OR", am.getBaseMnemonic("ORF"));
		Assert.assertEquals("or", am.getBaseMnemonic("or2"));
		Assert.assertEquals("Store", am.getBaseMnemonic("Storef"));
		Assert.assertNull(am.getBaseMnemonic("OR"));
		Assert.assertNull(am.getBaseMnemonic("FOR"));
	}
	@Test
	public void testGetMnemonicMatch() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setMnemonicFormat(".+([124F])");
		Assert.assertEquals("4", am.getMnemonicMatch("or4"));
		Assert.assertEquals("F", am.getMnemonicMatch("STOREF"));
		Assert.assertNull(am.getMnemonicMatch("GOTO"));
	}
	
	public static void setFormat(AddressModeDefinition mode, String string) {
		mode.setFormat(string);
	}
}

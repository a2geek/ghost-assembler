package com.webcodepro.asm.definition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

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
		Assertions.assertTrue(p.matcher("").matches());
		Assertions.assertFalse(p.matcher("0x12").matches());
	}
	@Test
	public void test6502Accumulator() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("A");
		Pattern p = am.getRegexPattern();
		Assertions.assertTrue(p.matcher("a").matches());
		Assertions.assertTrue(p.matcher("a").matches());
		Assertions.assertFalse(p.matcher("").matches());
		Assertions.assertFalse(p.matcher("0x12").matches());
	}
	@Test
	public void test6502Immediate() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("#([:expr:]+)");
		Pattern p = am.getRegexPattern();
		Assertions.assertTrue(p.matcher("#0x12").matches());
		Assertions.assertTrue(p.matcher("#'c'").matches());
		Assertions.assertFalse(p.matcher("").matches());
		Assertions.assertFalse(p.matcher("0x12").matches());
		Assertions.assertFalse(p.matcher("0x12,x").matches());
	}
	@Test
	public void test6502ZeroPageX_AbsoluteX() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("([:expr:]+),x");
		Pattern p = am.getRegexPattern();
		Assertions.assertTrue(p.matcher("0x12,X").matches());
		Assertions.assertTrue(p.matcher("addr,x").matches());
		Assertions.assertFalse(p.matcher("").matches());
		Assertions.assertFalse(p.matcher("0x12,y").matches());
		Assertions.assertFalse(p.matcher("0x23").matches());
		Assertions.assertFalse(p.matcher("#0x12").matches());
	}
	@Test
	public void test6502ZeroPageY_AbsoluteY() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("([:expr:]+),y");
		Pattern p = am.getRegexPattern();
		Assertions.assertTrue(p.matcher("0x12,y").matches());
		Assertions.assertTrue(p.matcher("addr,Y").matches());
		Assertions.assertFalse(p.matcher("").matches());
		Assertions.assertFalse(p.matcher("0x12,x").matches());
		Assertions.assertFalse(p.matcher("0x23").matches());
		Assertions.assertFalse(p.matcher("#0x12").matches());
	}
	@Test
	public void test6502Indirect() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("\\(([:expr:]+)\\)");
		Pattern p = am.getRegexPattern();
		Assertions.assertTrue(p.matcher("(0x1234)").matches());
		Assertions.assertTrue(p.matcher("(address)").matches());
		Assertions.assertFalse(p.matcher("(0x12),y").matches());
		Assertions.assertFalse(p.matcher("0x1234").matches());
	}
	@Test
	public void test6502IndirectX() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("\\(([:expr:]+),x\\)");
		Pattern p = am.getRegexPattern();
		Assertions.assertTrue(p.matcher("(0x12,x)").matches());
		Assertions.assertTrue(p.matcher("(address,x)").matches());
		Assertions.assertFalse(p.matcher("(0x12)").matches());
		Assertions.assertFalse(p.matcher("0x1234,x").matches());
	}
	@Test
	public void test6502IndirectY() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("\\(([:expr:]+)\\),y");
		Pattern p = am.getRegexPattern();
		Assertions.assertTrue(p.matcher("(0x12),y").matches());
		Assertions.assertTrue(p.matcher("(address),y").matches());
		Assertions.assertFalse(p.matcher("(0x12,x)").matches());
		Assertions.assertFalse(p.matcher("0x1234,y").matches());
	}
	@Test
	public void test6502ZeroPage_Relative_Absolute() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setRegex("([:expr:]+)");
		Pattern p = am.getRegexPattern();
		Assertions.assertTrue(p.matcher("0x12").matches());
		Assertions.assertTrue(p.matcher("addr").matches());
		Assertions.assertTrue(p.matcher("54322").matches());
		Assertions.assertFalse(p.matcher("addr,y").matches());
		Assertions.assertFalse(p.matcher("0x12,y").matches());
		Assertions.assertFalse(p.matcher("(addr)").matches());
	}
	
	@Test
	public void testMnemonicPatternMatch() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setMnemonicFormat(".+([124F])");
		Assertions.assertEquals("OR", am.getBaseMnemonic("ORF"));
		Assertions.assertEquals("or", am.getBaseMnemonic("or2"));
		Assertions.assertEquals("Store", am.getBaseMnemonic("Storef"));
		Assertions.assertNull(am.getBaseMnemonic("OR"));
		Assertions.assertNull(am.getBaseMnemonic("FOR"));
	}
	@Test
	public void testGetMnemonicMatch() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setMnemonicFormat(".+([124F])");
		Assertions.assertEquals("4", am.getMnemonicMatch("or4"));
		Assertions.assertEquals("F", am.getMnemonicMatch("STOREF"));
		Assertions.assertNull(am.getMnemonicMatch("GOTO"));
	}
	
	public static void setFormat(AddressModeDefinition mode, String string) {
		mode.setFormat(string);
	}
}

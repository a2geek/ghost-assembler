package a2geek.asm.api.definition;

import a2geek.asm.api.util.pattern.QPattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Exercise the interesting functions in AddressModeDefinition.
 * 
 * @author Rob
 */
public class AddressModeDefinitionTest {
	@Test
	public void test6502Implied() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setPattern("");
		QPattern p = am.getQPattern();
		Assertions.assertTrue(p.match("").isMatched());
		Assertions.assertFalse(p.match("0x12").isMatched());
	}
	@Test
	public void test6502Accumulator() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setPattern("A");
		QPattern p = am.getQPattern();
		Assertions.assertTrue(p.match("a").isMatched());
		Assertions.assertTrue(p.match("a").isMatched());
		Assertions.assertFalse(p.match("").isMatched());
		Assertions.assertFalse(p.match("0x12").isMatched());
	}
	@Test
	public void test6502Immediate() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setPattern("#?");
		QPattern p = am.getQPattern();
		Assertions.assertTrue(p.match("#0x12").isMatched());
		Assertions.assertTrue(p.match("#'c'").isMatched());
		Assertions.assertFalse(p.match("").isMatched());
		Assertions.assertFalse(p.match("0x12").isMatched());
		Assertions.assertFalse(p.match("0x12,x").isMatched());
	}
	@Test
	public void test6502ZeroPageX_AbsoluteX() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setPattern("?,x");
		QPattern p = am.getQPattern();
		Assertions.assertTrue(p.match("0x12,X").isMatched());
		Assertions.assertTrue(p.match("addr,x").isMatched());
		Assertions.assertFalse(p.match("").isMatched());
		Assertions.assertFalse(p.match("0x12,y").isMatched());
		Assertions.assertFalse(p.match("0x23").isMatched());
		Assertions.assertFalse(p.match("#0x12").isMatched());
	}
	@Test
	public void test6502ZeroPageY_AbsoluteY() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setPattern("?,y");
		QPattern p = am.getQPattern();
		Assertions.assertTrue(p.match("0x12,y").isMatched());
		Assertions.assertTrue(p.match("addr,Y").isMatched());
		Assertions.assertFalse(p.match("").isMatched());
		Assertions.assertFalse(p.match("0x12,x").isMatched());
		Assertions.assertFalse(p.match("0x23").isMatched());
		Assertions.assertFalse(p.match("#0x12").isMatched());
	}
	@Test
	public void test6502Indirect() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setPattern("(?)");
		QPattern p = am.getQPattern();
		Assertions.assertTrue(p.match("(0x1234)").isMatched());
		Assertions.assertTrue(p.match("(address)").isMatched());
		Assertions.assertFalse(p.match("(0x12),y").isMatched());
		Assertions.assertFalse(p.match("0x1234").isMatched());
	}
	@Test
	public void test6502IndirectX() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setPattern("(?,x)");
		QPattern p = am.getQPattern();
		Assertions.assertTrue(p.match("(0x12,x)").isMatched());
		Assertions.assertTrue(p.match("(address,x)").isMatched());
		Assertions.assertFalse(p.match("(0x12)").isMatched());
		Assertions.assertFalse(p.match("0x1234,x").isMatched());
	}
	@Test
	public void test6502IndirectY() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setPattern("(?),y");
		QPattern p = am.getQPattern();
		Assertions.assertTrue(p.match("(0x12),y").isMatched());
		Assertions.assertTrue(p.match("(address),y").isMatched());
		Assertions.assertFalse(p.match("(0x12,x)").isMatched());
		Assertions.assertFalse(p.match("0x1234,y").isMatched());
	}
	@Test
	public void test6502ZeroPage_Relative_Absolute() {
		AddressModeDefinition am = new AddressModeDefinition();
		am.setPattern("?");
		QPattern p = am.getQPattern();
		Assertions.assertTrue(p.match("0x12").isMatched());
		Assertions.assertTrue(p.match("addr").isMatched());
		Assertions.assertTrue(p.match("54322").isMatched());
		// Note: The following match since "?" has no constraints beyond length at this time.
		//Assertions.assertFalse(p.match("addr,y").isMatched());
		//Assertions.assertFalse(p.match("0x12,y").isMatched());
		//Assertions.assertFalse(p.match("(addr)").isMatched());
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

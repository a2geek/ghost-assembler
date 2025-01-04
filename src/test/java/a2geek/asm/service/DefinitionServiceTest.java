package a2geek.asm.service;

import a2geek.asm.definition.*;
import a2geek.asm.definition.CpuDefinition.OperationMatch;
import a2geek.asm.service.DefinitionService.ValidationType;
import a2geek.junit.AsmAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provide a very basic test to confirm that the basics of the DefinitionService
 * are setup correctly to reconstitute the XML CPU definition into the appropriate
 * Java model.
 * 
 * @author Rob
 */
public class DefinitionServiceTest {
	protected CpuDefinition getCodeAutomationCpu() throws IOException {
		return DefinitionService.load(AsmAssert.location("CODE Automation CPU.xml"), ValidationType.VALIDATE);
	}
	@Test
	public void testMemory() throws IOException {
		CpuDefinition cpu = getCodeAutomationCpu();
		Assertions.assertEquals(16, cpu.getAddressSpace().getBitSize());
		Assertions.assertNull(cpu.getAddressSpace().getMemoryLocations());
	}
	@Test
	public void testRegisters() throws IOException {
		CpuDefinition cpu = getCodeAutomationCpu();
		Assertions.assertEquals(1, cpu.getRegisters().size());
		Register register = cpu.getRegisters().getFirst();
		Assertions.assertEquals("A", register.getId());
		Assertions.assertEquals(8, register.getBitSize());
		Assertions.assertEquals("Accumulator", register.getName());
	}
	@Test
	public void testAddressModes() throws IOException {
		CpuDefinition cpu = getCodeAutomationCpu();
		Assertions.assertEquals(4, cpu.getAddressModes().size());
		AddressModeDefinition mode = cpu.getAddressModes().getFirst();
		Assertions.assertEquals("absolute-into-A", mode.getId());
		Assertions.assertEquals("A,[addr]", mode.getFormat());
		Assertions.assertEquals("A,\\[(" + AddressModeDefinition.MATCH_REGEX + "+)\\]", 
				mode.getRegexPattern().pattern());
		Assertions.assertEquals(3, mode.getByteCode().size());
		Assertions.assertEquals("opcode", mode.getByteCode().get(0).getExpression());
		Assertions.assertEquals("addr>>8", mode.getByteCode().get(1).getExpression());
		Assertions.assertEquals("addr", mode.getByteCode().get(2).getExpression());
		mode = cpu.getAddressModes().get(1);
		Assertions.assertEquals("absolute-from-A", mode.getId());
		Assertions.assertEquals("[addr],A", mode.getFormat());
		Assertions.assertEquals("\\[(" + AddressModeDefinition.MATCH_REGEX + "+)\\],A", 
				mode.getRegexPattern().pattern());
		Assertions.assertEquals(3, mode.getByteCode().size());
		Assertions.assertEquals("opcode", mode.getByteCode().get(0).getExpression());
		Assertions.assertEquals("addr>>8", mode.getByteCode().get(1).getExpression());
		Assertions.assertEquals("addr", mode.getByteCode().get(2).getExpression());
		mode = cpu.getAddressModes().get(2);
		Assertions.assertEquals("absolute-address", mode.getId());
		Assertions.assertEquals("addr", mode.getFormat());
		Assertions.assertEquals("(" + AddressModeDefinition.MATCH_REGEX + "+)", 
				mode.getRegexPattern().pattern());
		Assertions.assertEquals(3, mode.getByteCode().size());
		Assertions.assertEquals("opcode", mode.getByteCode().get(0).getExpression());
		Assertions.assertEquals("addr>>8", mode.getByteCode().get(1).getExpression());
		Assertions.assertEquals("addr", mode.getByteCode().get(2).getExpression());
		mode = cpu.getAddressModes().get(3);
		Assertions.assertEquals("none", mode.getId());
		Assertions.assertEquals("", mode.getFormat());
		Assertions.assertNull(mode.getRegex());
		Assertions.assertEquals(1, mode.getByteCode().size());
		Assertions.assertEquals("opcode", mode.getByteCode().getFirst().getExpression());
	}
	@Test
	public void testOperations() throws IOException {
		CpuDefinition cpu = getCodeAutomationCpu();
		Assertions.assertEquals(12, cpu.getOperations().size());
		verifyOperation(cpu, 0, "LOD", "0x10", "absolute-into-A");
		verifyOperation(cpu, 1, "STO", "0x11", "absolute-from-A");
		verifyOperation(cpu, 2, "ADD", "0x20", "absolute-into-A");
		verifyOperation(cpu, 3, "SUB", "0x21", "absolute-into-A");
		verifyOperation(cpu, 4, "ADC", "0x22", "absolute-into-A");
		verifyOperation(cpu, 5, "SBB", "0x23", "absolute-into-A");
		verifyOperation(cpu, 6, "JMP", "0x30", "absolute-address");
		verifyOperation(cpu, 7, "JZ", "0x31", "absolute-address");
		verifyOperation(cpu, 8, "JC", "0x32", "absolute-address");
		verifyOperation(cpu, 9, "JNZ", "0x33", "absolute-address");
		verifyOperation(cpu, 10, "JNC", "0x34", "absolute-address");
		verifyOperation(cpu, 11, "HLT", "0xff", "none");
	}
	protected void verifyOperation(CpuDefinition cpu, int position, 
			String mnemonic, String opcode, String nameRef) {
		Operation op = cpu.getOperations().get(position);
		Assertions.assertEquals(mnemonic, op.getMnemonic());
		Assertions.assertEquals(1, op.getAddressingModes().size());
		String opcodeCheck = op.getAddressingModes().getFirst().getOpcode();
		Assertions.assertEquals(opcode, opcodeCheck);
	}
	@Test
	public void testValidationOfGoodDefinition() throws IOException {
		CpuDefinition cpu = getCodeAutomationCpu();
		List<String> issues = new ArrayList<>();
		boolean valid = DefinitionService.validate(cpu, issues);
		if (!valid) System.out.println(issues);
		Assertions.assertTrue(valid);
	}
	@Test
	public void testValidationOfBadDefinition() throws IOException {
		CpuDefinition cpu = getCodeAutomationCpu();
		
		AddressModeDefinition mode = (AddressModeDefinition)cpu.findAddressModeById("absolute-address");
		String oldFormat = mode.getFormat();
		AddressModeDefinitionTest.setFormat(mode, "");
		Assertions.assertFalse(DefinitionService.validate(cpu, null));
		AddressModeDefinitionTest.setFormat(mode, oldFormat);
		
		OperationMatch match = cpu.findOperation("HLT", null);
		Operation operation = match.getOperation();
		String oldMnemonic = operation.getMnemonic();
		OperationHelper.setMnemonic(operation, null);
		Assertions.assertFalse(DefinitionService.validate(cpu, null));
		OperationHelper.setMnemonic(operation, oldMnemonic);
	}
}

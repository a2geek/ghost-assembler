package com.webcodepro.asm.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.webcodepro.asm.definition.AddressModeDefinition;
import com.webcodepro.asm.definition.AddressModeDefinitionTest;
import com.webcodepro.asm.definition.CpuDefinition;
import com.webcodepro.asm.definition.CpuDefinition.OperationMatch;
import com.webcodepro.asm.definition.Operation;
import com.webcodepro.asm.definition.OperationHelper;
import com.webcodepro.asm.definition.Register;
import com.webcodepro.asm.service.DefinitionService.ValidationType;
import com.webcodepro.junit.AsmAssert;

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
		Assert.assertEquals(16, cpu.getAddressSpace().getBitSize());
		Assert.assertNull(cpu.getAddressSpace().getMemoryLocations());
	}
	@Test
	public void testRegisters() throws IOException {
		CpuDefinition cpu = getCodeAutomationCpu();
		Assert.assertEquals(1, cpu.getRegisters().size());
		Register register = (Register)cpu.getRegisters().get(0);
		Assert.assertEquals("A", register.getId());
		Assert.assertEquals(8, register.getBitSize());
		Assert.assertEquals("Accumulator", register.getName());
	}
	@Test
	public void testAddressModes() throws IOException {
		CpuDefinition cpu = getCodeAutomationCpu();
		Assert.assertEquals(4, cpu.getAddressModes().size());
		AddressModeDefinition mode = (AddressModeDefinition)cpu.getAddressModes().get(0);
		Assert.assertEquals("absolute-into-A", mode.getId());
		Assert.assertEquals("A,[addr]", mode.getFormat());
		Assert.assertEquals("A,\\[(" + AddressModeDefinition.MATCH_REGEX + "+)\\]", 
				mode.getRegexPattern().pattern());
		Assert.assertEquals(3, mode.getByteCode().size());
		Assert.assertEquals("opcode", mode.getByteCode().get(0).getExpression());
		Assert.assertEquals("addr>>8", mode.getByteCode().get(1).getExpression());
		Assert.assertEquals("addr", mode.getByteCode().get(2).getExpression());
		mode = (AddressModeDefinition)cpu.getAddressModes().get(1);
		Assert.assertEquals("absolute-from-A", mode.getId());
		Assert.assertEquals("[addr],A", mode.getFormat());
		Assert.assertEquals("\\[(" + AddressModeDefinition.MATCH_REGEX + "+)\\],A", 
				mode.getRegexPattern().pattern());
		Assert.assertEquals(3, mode.getByteCode().size());
		Assert.assertEquals("opcode", mode.getByteCode().get(0).getExpression());
		Assert.assertEquals("addr>>8", mode.getByteCode().get(1).getExpression());
		Assert.assertEquals("addr", mode.getByteCode().get(2).getExpression());
		mode = (AddressModeDefinition)cpu.getAddressModes().get(2);
		Assert.assertEquals("absolute-address", mode.getId());
		Assert.assertEquals("addr", mode.getFormat());
		Assert.assertEquals("(" + AddressModeDefinition.MATCH_REGEX + "+)", 
				mode.getRegexPattern().pattern());
		Assert.assertEquals(3, mode.getByteCode().size());
		Assert.assertEquals("opcode", mode.getByteCode().get(0).getExpression());
		Assert.assertEquals("addr>>8", mode.getByteCode().get(1).getExpression());
		Assert.assertEquals("addr", mode.getByteCode().get(2).getExpression());
		mode = (AddressModeDefinition)cpu.getAddressModes().get(3);
		Assert.assertEquals("none", mode.getId());
		Assert.assertEquals("", mode.getFormat());
		Assert.assertNull(mode.getRegex());
		Assert.assertEquals(1, mode.getByteCode().size());
		Assert.assertEquals("opcode", mode.getByteCode().get(0).getExpression());
	}
	@Test
	public void testOperations() throws IOException {
		CpuDefinition cpu = getCodeAutomationCpu();
		Assert.assertEquals(12, cpu.getOperations().size());
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
		Operation op = (Operation)cpu.getOperations().get(position);
		Assert.assertEquals(mnemonic, op.getMnemonic());
		Assert.assertEquals(1, op.getAddressingModes().size());
		String opcodeCheck = (String)op.getAddressingModes().get(0).getOpcode();
		Assert.assertEquals(opcode, opcodeCheck);
	}
	@Test
	public void testValidationOfGoodDefinition() throws IOException {
		CpuDefinition cpu = getCodeAutomationCpu();
		List<String> issues = new ArrayList<String>();
		boolean valid = DefinitionService.validate(cpu, issues);
		if (!valid) System.out.println(issues.toString());
		Assert.assertTrue(valid);
	}
	@Test
	public void testValidationOfBadDefinition() throws IOException {
		CpuDefinition cpu = getCodeAutomationCpu();
		
		AddressModeDefinition mode = (AddressModeDefinition)cpu.findAddressModeById("absolute-address");
		String oldFormat = mode.getFormat();
		AddressModeDefinitionTest.setFormat(mode, "");
		Assert.assertFalse(DefinitionService.validate(cpu, null));
		AddressModeDefinitionTest.setFormat(mode, oldFormat);
		
		OperationMatch match = cpu.findOperation("HLT", null);
		Operation operation = match.getOperation();
		String oldMnemonic = operation.getMnemonic();
		OperationHelper.setMnemonic(operation, null);
		Assert.assertFalse(DefinitionService.validate(cpu, null));
		OperationHelper.setMnemonic(operation, oldMnemonic);
	}
}

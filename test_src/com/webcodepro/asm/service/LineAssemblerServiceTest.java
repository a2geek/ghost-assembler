package com.webcodepro.asm.service;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.webcodepro.asm.assembler.LineParts;
import com.webcodepro.asm.definition.CpuDefinition;
import com.webcodepro.asm.service.DefinitionService.ValidationType;
import com.webcodepro.junit.AsmAssert;

/**
 * Exercise the LineAssemblerService.
 * @author Rob
 */
public class LineAssemblerServiceTest {
	@Before
	public void setup() throws IOException {
		AssemblerState.init((String)null);
		AssemblerState state = AssemblerState.get();

		CpuDefinition cpu = DefinitionService.load(AsmAssert.location("Test-1 CPU.xml"), ValidationType.VALIDATE);
		state.setCpuDefinition(cpu);
		
		state.addGlobalVariable("var", new Long(4321));
		state.addGlobalVariable("opcode", new Long(0x99));
	}

	@Test
	public void testSize() throws AssemblerException {
		Assert.assertEquals(1, LineAssemblerService.size(LineParserService.parseLine(" HLT")));
		Assert.assertEquals(3, LineAssemblerService.size(LineParserService.parseLine(" JMP 0x0012")));
		Assert.assertEquals(3, LineAssemblerService.size(LineParserService.parseLine(" LOD A,[0x3]")));
	}
	
	@Test
	public void testInstructionHLT() throws AssemblerException {
		LineParts lineParts = new LineParts();
		lineParts.setOpcode("HLT");
		int length = LineAssemblerService.assemble(lineParts);
		Assert.assertEquals(1, length);
		byte[] code = AssemblerState.get().getOutput().toByteArray();
		Assert.assertNotNull(code);
		Assert.assertEquals(1, code.length);
		Assert.assertEquals((byte)0xff, code[0]);
	}
	@Test
	public void testInstructionJMP() throws AssemblerException {
		LineParts lineParts = new LineParts();
		lineParts.setOpcode("JMP");
		lineParts.setExpression("0x1234");
		int length = LineAssemblerService.assemble(lineParts);
		Assert.assertEquals(3, length);
		byte[] code = AssemblerState.get().getOutput().toByteArray();
		Assert.assertNotNull(code);
		Assert.assertEquals(3, code.length);
		AsmAssert.assertEquals(new byte[] { 0x30, 0x12, 0x34 }, code);
	}
	@Test
	public void testInstructionJZ() throws AssemblerException {
		LineParts lineParts = new LineParts();
		lineParts.setOpcode("JZ");
		lineParts.setExpression("0xab83");
		int length = LineAssemblerService.assemble(lineParts);
		Assert.assertEquals(3, length);
		byte[] code = AssemblerState.get().getOutput().toByteArray();
		Assert.assertNotNull(code);
		Assert.assertEquals(3, code.length);
		AsmAssert.assertEquals(new byte[] { 0x31, (byte)0xab, (byte)0x83 }, code);
	}
	@Test
	public void testInstructionLOD() throws AssemblerException {
		LineParts lineParts = new LineParts();
		lineParts.setOpcode("LOD");
		lineParts.setExpression("A,[0x518d]");
		int length = LineAssemblerService.assemble(lineParts);
		Assert.assertEquals(3, length);
		byte[] code = AssemblerState.get().getOutput().toByteArray();
		Assert.assertNotNull(code);
		Assert.assertEquals(3, code.length);
		AsmAssert.assertEquals(new byte[] { 0x10, 0x51, (byte)0x8d }, code);
	}
	/**
	 * Test a "group" register instruction.
	 */
	@Test
	public void testInstructionLDR() throws AssemblerException {
		LineParts lineParts = new LineParts();
		lineParts.setOpcode("LDR");
		lineParts.setExpression("R1,0x4321");
		int length = LineAssemblerService.assemble(lineParts);
		Assert.assertEquals(3, length);
		byte[] code = AssemblerState.get().getOutput().toByteArray();
		Assert.assertNotNull(code);
		Assert.assertEquals(3, code.length);
		AsmAssert.assertEquals(new byte[] { (byte)0x81, 0x43, 0x21 }, code);
	}
	/**
	 * Test an instruction that contains a negative value.
	 */
	@Test
	public void testInstructionWithNegativeValue() throws AssemblerException {
		LineParts lineParts = new LineParts();
		lineParts.setOpcode("JMP");
		lineParts.setExpression("-1");
		int length = LineAssemblerService.assemble(lineParts);
		Assert.assertEquals(3, length);
		byte[] code = AssemblerState.get().getOutput().toByteArray();
		Assert.assertNotNull(code);
		Assert.assertEquals(3, code.length);
		AsmAssert.assertEquals(new byte[] { 0x30, (byte)0xff, (byte)0xff }, code);
	}
}

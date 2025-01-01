package com.webcodepro.asm.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.webcodepro.junit.AsmAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test the assembler.  This starts out with a simple CPU and simple types of code
 * and progressively becomes more complex.
 * 
 * @author Rob
 */
public class AssemblerServiceTest {
	@BeforeEach
	public void setUp() throws IOException {
		AssemblerState.init((String)null);
	}
	
	/**
	 * Plain assembly, no variables, using the Automation CPU as defined
	 * in the book "CODE".
	 */
	@Test
	public void testSimpleAssembly() throws IOException, AssemblerException {
		byte[] expected = {
				0x10, 0x10, 0x05,	// lod	A,[1005h]
				0x20, 0x10, 0x01,	// add	A,[1001h]
				0x11, 0x10, 0x05,	// sto	[1005h],A
				0x10, 0x10, 0x04,	// lod	A,[1004h] 
				0x22, 0x10, 0x00,	// adc	A,[1000h] 
				0x11, 0x10, 0x04,	// sto	[1004h],A 
				0x10, 0x10, 0x03,	// lod	A,[1003h]
				0x20, 0x00, 0x1e,	// add	A,[001eh]
				0x11, 0x10, 0x03,	// sto	[1003h],A
				0x33, 0x00, 0x00,	// jnz	0000h
				(byte)0xff,			// hlt
				0x00, (byte)0xa7,	// 00h, a7h
				0x00, 0x1c,			// 00h, 1ch
				0x00, 0x00	 		// 00h, 00h
			};
		AsmAssert.assemble("Test Assemble 1.asm", expected);
	}
	/**
	 * From the Automation CPU in the book "CODE" but uses labels. 
	 */
	@Test
	public void testSimpleAssemblyWithLabels() throws IOException, AssemblerException {
		byte[] expected = {
				0x10, 0x00, 0x24,	// lod	A,[result+1]
				0x20, 0x00, 0x20,	// add	A,[num1+1]
				0x11, 0x00, 0x24,	// sto	[result+1],A
				0x10, 0x00, 0x23,	// lod	A,[result]
				0x22, 0x00, 0x1f,	// adc	A,[num1]
				0x11, 0x00, 0x23,	// sto	[result],A
				0x10, 0x00, 0x22,	// lod	A,[num2+1]
				0x20, 0x00, 0x1e,	// add	A,[neg1]
				0x11, 0x00, 0x22,	// sto	[num2+1],A
				0x33, 0x00, 0x00,	// jnz	begin
				(byte)0xff,			// hlt
				0x00, (byte)0xa7,	// 00h, a7h (num1)
				0x00, 0x1c,			// 00h, 1ch (num2)
				0x00, 0x00	 		// 00h, 00h	(result)	
			};
		AsmAssert.assemble("Test Assemble 2.asm", expected);
	}
	/**
	 * From the Automation CPU in the book "CODE" but uses labels. 
	 */
	@Test
	public void testAssemblyWithOddLabels() throws IOException, AssemblerException {
		byte[] expected = {
				0x10, 0x00, 0x04,	// lod	A,[over_here]
				(byte)0xff,			// hlt
				0x00		 		// 00h, 00h	(over_here)	
			};
		AsmAssert.assemble("Test Assemble 3.asm", expected);
	}
	/**
	 * Using the Automation CPU to test negative constants. 
	 */
	@Test
	public void testAssemblyWithNegativeConstants() throws IOException, AssemblerException {
		byte[] expected = {
				0x10, (byte)0xff, (byte)0xff,	//	lod A,[-1]
				0x11, (byte)0xff, (byte)0xfe,	//	sto [over_here],A
				(byte)0xff						//  hlt
			};
		AsmAssert.assemble("Test Assemble 4.asm", expected);
	}
	/**
	 * Sweet-16 code which is a more complex CPU definition.  This code is an
	 * example that clears a block of memory.
	 * <p>
	 * The code was grabbed from 6502.org and includes the original write-up
	 * by Steve Wozniak as well as Carsten Strotmann.  See
	 * <a href='http://www.6502.org/source/interpreters/sweet16.htm'>6502.org</a> 
	 * for more details. 
	 */
	@Test
	public void testSweet16a() throws IOException, AssemblerException {
		byte[] expected = {
				0x10, 0x00, 0x00,	// set R0,0
				0x11, 0x00, 0x0a,	// set R1,block
				0x12, 0x34, 0x02, 	// set R2,N
				0x51,				// st @R1
				(byte)0xf2,			// dcr R2
				0x07, (byte)0xfc,	// bnz loop
				0x00				// rtn
			};
		AsmAssert.assemble("Test-SWEET16-1.asm", expected);
	}
	/**
	 * Sweet-16 code which is a more complex CPU definition.  This code is to move
	 * a block of memory.
	 */
	@Test
	public void testSweet16b() throws IOException, AssemblerException {
		byte[] expected = {
				0x11, 0x00, 0x0a,			// set R1,source
				0x12, (byte) 0x80, 0x0a,	// set R2,dest
				0x13, 0x23, 0x00,			// set R3,N
				0x41,						// ld @R1
				0x52, 						// st @R2
				(byte) 0xf3,				// dcr R3
				0x07, (byte) 0xfb,			// bnz loop
				0x00, 						// rtn
			};
		AsmAssert.assemble("Test-SWEET16-2.asm", expected);
	}
	/**
	 * Sweet-16 code which is a more complex CPU definition.  This code is to 
	 * renumber S-C assembler source code.
	 */
	@Test
	public void testSweet16c() throws IOException, AssemblerException {
		byte[] expected = {
				0x11, (byte) 0xca, 0x00,	// set R1,pp
				0x61,						// ldd @R1
				0x31, 						// set R1
				0x12, 0x0a, 0x00,			// set R2,10
				0x13, 0x4c, 0x00,			// set R3,himem
				0x63, 						// ldd @R3
				0x33,						// st R3
				0x14, (byte) 0xde, 0x03,	// set R4,990
				0x21,						// ld R1			
				(byte) 0xd3,				// cpr R3
				0x03, 0x0e,					// bc end
				0x41,						// ld @R1
				0x35,						// st R5
				0x24,						// ld R4
				(byte) 0xa2,				// add R2
				0x34,						// st R4
				0x71,						// std @R1
				(byte) 0xf1, 				// dcr R1
				(byte) 0xf1,				// dcr R1
				(byte) 0xf1,				// dcr R1
				0x21,						// ld R1
				(byte) 0xa5,				// add R5
				0x31,						// st R1
				0x01, (byte) 0xee,			// br loop
				0x00,						// rtn
			};
		AsmAssert.assemble("Test-SWEET16-3.asm", expected);
	}
	/**
	 * Test local label functionality.
	 */
	@Test
	@Disabled
	public void testLocalLabels() throws IOException, AssemblerException {
		byte[] expected = {
				(byte)0xa9, 0x00,			// LDA #0x00
				(byte)0xa8,					// TAY
				(byte)0x91, 0x00,			// STA (TOPNT),Y
				(byte)0xc8,					// INY
				(byte)0xca,					// DEX
				(byte)0xd0, (byte)0xfa,		// BNE :loop
				0x60,						// RTS
				(byte)0xa9, 0x00,			// LDA #0x00
				(byte)0x88,					// DEY
				(byte)0x91, 0x00, 			// STA (TOPNT),Y
				(byte)0xd0, (byte)0xfb,		// BNE :loop
				0x60,						// RTS
				(byte)0xa0, 0x0a,			// ldy #10
				(byte)0xb1, 0x00,			// lda (0x00),y
				(byte)0x88,					// dey
				(byte)0xd0, (byte)0xfb,		// bne :0
				(byte)0xa0, 0x00,			// ldy #0
				(byte)0xb1, 0x00,			// lda (0x00),y
				(byte)0x88,					// dey
				(byte)0xd0, (byte)0xfb,		// bne :0
				0x60,						// rts
			};
		AsmAssert.assemble("Test-Assemble-local-labels.asm", expected);
	}
	/**
	 * Test string functionality.
	 */
	@Test
	public void testStringAssemble() throws IOException, AssemblerException {
		byte h = (byte)0x80;
		byte[] expected = {
				// .byte "Hello, World!",0
				'H','e','l','l','o',',',' ','W','o','r','l','d','!',0,
				// .ascii "Hello, World!",0
				'H','e','l','l','o',',',' ','W','o','r','l','d','!',0,
				// .asciih "Hello, World!",0
				(byte)('H'|h),(byte)('e'|h),(byte)('l'|h),(byte)('l'|h),(byte)('o'|h),(byte)(','|h),(byte)(' '|h),
				(byte)('W'|h),(byte)('o'|h),(byte)('r'|h),(byte)('l'|h),(byte)('d'|h),(byte)('!'|h),0,
				// .string "Hello, World!"
				13,'H','e','l','l','o',',',' ','W','o','r','l','d','!',
				// .stringh "Hello, World!",0
				13,(byte)('H'|h),(byte)('e'|h),(byte)('l'|h),(byte)('l'|h),(byte)('o'|h),(byte)(','|h),
				(byte)(' '|h),(byte)('W'|h),(byte)('o'|h),(byte)('r'|h),(byte)('l'|h),(byte)('d'|h),(byte)('!'|h),
			};
		AsmAssert.assemble("Test-Assemble-strings.asm", expected);
	}
	/**
	 * Test handling of .define, .ifdef, .ifndef, and .endif.
	 */
	@Test
	@Disabled
	public void testIfDef() throws IOException, AssemblerException {
		byte[] expected = { 1, 4 };
		AsmAssert.assemble("Test-Assemble-ifdef.asm", expected);
	}
	/**
	 * Test lable duplicate error handling.
	 */
	@Test
	public void testLabelDuplicates() throws IOException, AssemblerException {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			AssemblerService.assemble(pw, new File(AsmAssert.location("Test-Assemble-label-duplicates.asm")));
			Assertions.fail("Expecting duplicates to cause error.");
		} catch (AssemblerException expected) {
			// Expecting the error
		}
	}
	/**
	 * Test the parseCommas method.
	 */
	@Test
	public void testParseCommas() {
		AsmAssert.assertEquals(new String[] { "a","b","c" }, AssemblerService.parseCommas("a,b,c"));
		AsmAssert.assertEquals(new String[] { "a" }, AssemblerService.parseCommas("a"));
		AsmAssert.assertEquals(new String[] { "\"a,b,c\"" }, AssemblerService.parseCommas("\"a,b,c\""));
		AsmAssert.assertEquals(new String[] { "'a,b,c'" }, AssemblerService.parseCommas("'a,b,c'"));
		AsmAssert.assertEquals(new String[] { "\"a,'b,c" }, AssemblerService.parseCommas("\"a,'b,c"));
		AsmAssert.assertEquals(new String[] { "a","\"b,c\"" }, AssemblerService.parseCommas("a,\"b,c\""));
	}
}

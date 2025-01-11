package a2geek.asm.service;

import a2geek.junit.AsmAssert;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test the 6502 definition.
 * 
 * @author Rob
 */
public class Test65C02Definition {
	@Test
	public void test_ADC_to_BRK() throws IOException, AssemblerException {
		byte[] expected = {
				0x69, 0x30,				// adc #0x30
				0x65, 0x44,				// adc 0x44
				0x75, 0x56,				// adc 0x56,x
				0x6d, 0x34, 0x12,		// adc 0x1234
				0x7d, 0x45, 0x23,		// adc 0x2345,x
				0x79, 0x56, 0x34,		// adc 0x3456,y
				0x61, 0x12,				// adc (0x12,x)
				0x71, 0x23,				// adc (0x23),y
				0x29, 0x30,				// and #0x30
				0x25, 0x44,				// and 0x44
				0x35, 0x56,				// and 0x56,x
				0x2d, 0x34, 0x12,		// and 0x1234
				0x3d, 0x45, 0x23,		// and 0x2345,x
				0x39, 0x56, 0x34,		// and 0x3456,y
				0x21, 0x12,				// and (0x12,x)
				0x31, 0x23,				// and (0x23),y
				0x0a,					// asl a
				0x06, 0x43,				// asl 0x43
				0x16, 0x44,				// asl 0x44,x
				0x0e, 0x41, 0x63,		// asl 0x6341
				0x1e, 0x43, 0x65,		// asl 0x6543,x
				(byte)0x90, 0x4d,		// bcc 0x80
				(byte)0xb0, 0x4b,		// bcs 0x80
				(byte)0xf0, 0x49,		// beq 0x80
				0x24, 0x01,				// bit 0x01
				0x2c, 0x34, 0x12,		// bit 0x1234
				0x30, 0x42,				// bmi 0x80
				(byte)0xd0, 0x40,		// bne 0x80
				0x10, 0x3e,				// bpl 0x80
				0x00,					// brk
			};
		AsmAssert.assemble("Test-65C02-adc-to-brk.asm", expected);
	}
	@Test
	public void test_BVC_to_EOR() throws IOException, AssemblerException {
		byte[] expected = {
				0x50, 0x7e,				// bvc 0x80
				0x70, 0x7c,				// bvs 0x80
				0x18,					// clc
				(byte)0xd8,				// cld
				0x58,					// cli
				(byte)0xb8,				// clv
				(byte)0xc9, 0x20,		// cmp #0x20
				(byte)0xc5, 0x21,		// cmp 0x21
				(byte)0xd5, 0x22,		// cmp 0x22,x
				(byte)0xcd, 0x34, 0x12,	// cmp 0x1234
				(byte)0xdd, 0x45, 0x23,	// cmp 0x2345,x
				(byte)0xd9, 0x56, 0x34,	// cmp 0x3456,y
				(byte)0xc1, 0x45,		// cmp (0x45,x)
				(byte)0xd1, 0x56,		// cmp (0x56),y
				(byte)0xe0, 0x33,		// cpx #0x33
				(byte)0xe4, 0x34,		// cpx 0x34
				(byte)0xec, 0x67, 0x45,	// cpx 0x4567
				(byte)0xc0, 0x44,		// cpy #0x44
				(byte)0xc4, 0x45,		// cpy 0x45
				(byte)0xcc, 0x67, 0x45,	// cpy 0x4567
				(byte)0xc6, 0x01,		// dec 0x01
				(byte)0xd6, 0x02,		// dec 0x02,x
				(byte)0xce, 0x45, 0x03,	// dec 0x0345
				(byte)0xde, 0x56, 0x34,	// dec 0x3456,x
				(byte)0xca,				// dex
				(byte)0x88,				// dey
				0x49, 0x01,				// eor #0x01
				0x45, 0x02,				// eor 0x02
				0x55, 0x03,				// eor 0x03,x
				0x4d, 0x05, 0x04,		// eor 0x0405
				0x5d, 0x07, 0x06, 		// eor 0x0607,x
				0x59, 0x09, 0x08,		// eor 0x0809,y
				0x41, 0x0a,				// eor (0x0a,x)
				0x51, 0x0b, 			// eor (0x0b),y
			};
		AsmAssert.assemble("Test-65C02-bvc-to-eor.asm", expected);
	}
	@Test
	public void test_INC_to_LDY() throws IOException, AssemblerException {
		byte[] expected = {
				(byte)0xe6, 0x20,		// inc 0x20
				(byte)0xf6, 0x21, 		// inc 0x21,x
				(byte)0xee, 0x23, 0x22,	// inc 0x2223
				(byte)0xfe, 0x25, 0x24,	// inc 0x2425,x
				(byte)0xe8,				// inx
				(byte)0xc8,				// iny
				0x4c, 0x27, 0x26,		// jmp 0x2627
				0x6c, 0x29, 0x28,		// jmp (0x2829)
				0x20, 0x2b, 0x2a,		// jsr 0x2a2b
				(byte)0xa9, 0x2c,		// lda #0x2c
				(byte)0xa5, 0x2d,		// lda 0x2d
				(byte)0xb5, 0x2e,		// lda 0x2e,x
				(byte)0xad, 0x30, 0x2f,	// lda 0x2f30
				(byte)0xbd, 0x32, 0x31,	// lda 0x3132,x
				(byte)0xb9, 0x34, 0x33,	// lda 0x3334,y
				(byte)0xa1, 0x35, 		// lda (0x35,x)
				(byte)0xb1, 0x36,		// lda (0x36),y
				(byte)0xa2, 0x37,		// ldx #0x37
				(byte)0xa6, 0x38,		// ldx 0x38
				(byte)0xb6, 0x39,		// ldx 0x39,y
				(byte)0xae, 0x3b, 0x3a,	// ldx 0x3a3b
				(byte)0xbe, 0x3d, 0x3c,	// ldx 0x3c3d,y
				(byte)0xa0, 0x3e,		// ldy #0x3e
				(byte)0xa4, 0x3f,		// ldy 0x3f
				(byte)0xb4, 0x40,		// ldy 0x40,x
				(byte)0xac, 0x42, 0x41,	// ldy 0x4142
				(byte)0xbc, 0x44, 0x43,	// ldy 0x4344,x
			};
		AsmAssert.assemble("Test-65C02-inc-to-ldy.asm", expected);
	}
	@Test
	public void test_LSR_to_SBC() throws IOException, AssemblerException {
		byte[] expected = {
				0x4a,					// lsr
				0x46, 0x01,				// lsr 0x01
				0x56, 0x02,				// lsr 0x02,x
				0x4e, 0x04, 0x03,		// lsr 0x0304
				0x5e, 0x06, 0x05,		// lsr 0x0506,x
				(byte)0xea,				// nop
				0x09, 0x07,				// ora #0x07
				0x05, 0x08,				// ora 0x08
				0x15, 0x09,				// ora 0x09,x
				0x0d, 0x0b, 0x0a,		// ora 0x0a0b
				0x1d, 0x0d, 0x0c,		// ora 0x0c0d,x
				0x19, 0x0f, 0x0e,		// ora 0x0e0f,y
				0x01, 0x10, 			// ora (0x10,x)
				0x11, 0x11, 			// ora (0x11),y
				0x48,					// pha
				0x08,					// php
				0x68,					// pla
				0x28,					// plp
				0x2a,					// rol
				0x26, 0x12,				// rol 0x12
				0x36, 0x13, 			// rol 0x13,x
				0x2e, 0x15, 0x14,		// rol 0x1415
				0x3e, 0x17, 0x16,		// rol 0x1617,x
				0x6a,					// ror
				0x66, 0x18, 			// ror 0x18
				0x76, 0x19,				// ror 0x19,x
				0x6e, 0x1b, 0x1a,		// ror 0x1a1b
				0x7e, 0x1d, 0x1c,		// ror 0x1c1d,x
				0x40,					// rti
				0x60,					// rts
				(byte)0xe9, 0x1e,		// sbc #0x1e
				(byte)0xe5, 0x1f,		// sbc 0x1f
				(byte)0xf5, 0x20,		// sbc 0x20,x
				(byte)0xed, 0x22, 0x21,	// sbc 0x2122
				(byte)0xfd, 0x24, 0x23,	// sbc 0x2324,x
				(byte)0xf9, 0x26, 0x25,	// sbc 0x2526,y
				(byte)0xe1, 0x27,		// sbc (0x27,x)
				(byte)0xf1, 0x28,		// sbc (0x28),y
			};
		AsmAssert.assemble("Test-65C02-lsr-to-sbc.asm", expected);
	}
	@Test
	public void test_SEC_to_TXS() throws IOException, AssemblerException {
		byte[] expected = {
				0x38,					// sec
				(byte)0xf8,				// sed
				0x78,					// sei
				(byte)0x85, 0x01,		// sta 0x01
				(byte)0x95, 0x02,		// sta 0x02,x
				(byte)0x8d, 0x04, 0x03,	// sta 0x0304
				(byte)0x9d, 0x06, 0x05,	// sta 0x0506,x
				(byte)0x99, 0x08, 0x07,	// sta 0x0708,y
				(byte)0x81, 0x09,		// sta (0x09,x)
				(byte)0x91, 0x0a,		// sta (0x0a),y
				(byte)0x86, 0x0b,		// stx 0x0b
				(byte)0x96, 0x0c,		// stx 0x0c,y
				(byte)0x8e, 0x0e, 0x0d,	// stx 0x0d0e
				(byte)0x84, 0x0f,		// sty 0x0f
				(byte)0x94, 0x10,		// sty 0x10,x
				(byte)0x8c, 0x12, 0x11,	// sty 0x1112
				(byte)0xaa,				// tax
				(byte)0xa8,				// tay
				(byte)0xba,				// tsx
				(byte)0x8a,				// txa
				(byte)0x9a,				// txs
				(byte)0x98,				// tya
			};
		AsmAssert.assemble("Test-65C02-sec-to-tya.asm", expected);
	}
	@Test
	public void testNewInstructions() throws IOException, AssemblerException {
		byte[] expected = {
				0x12, 0x01,				// ora (0x01)
				0x32, 0x02,				// and (0x02)
				0x52, 0x03,				// eor (0x03)
				0x72, 0x04,				// adc (0x04)
				(byte)0x92, 0x05,		// sta (0x05)
				(byte)0xb2, 0x06,		// lda (0x06)
				(byte)0xd2, 0x07,		// cmp (0x07)
				(byte)0xf2, 0x08,		// sbc (0x08)
				0x7c, 0x0a, 0x09,		// jmp (0x090a,x)
				(byte)0x89, 0x0b,		// bit #0x0b
				0x34, 0x0c,				// bit 0x0c,x
				0x3c, 0x0e, 0x0d,		// bit 0x0d0e,x
				0x04, 0x0f,				// tsb 0x0f
				0x0c, 0x11, 0x10,		// tsb 0x1011
				0x14, 0x12,				// trb 0x12
				0x1c, 0x14, 0x13,		// trb 0x1314
				0x64, 0x15,				// stz 0x15
				(byte)0x9c, 0x17, 0x16,	// stz 0x1617
				0x74, 0x18,				// stz 0x18,x
				(byte)0x9e, 0x1a, 0x19,	// stz 0x191a,x
				(byte)0x80, 0x50,		// bra 0x80
				0x1a,					// inc
				0x3a,					// dec
				0x5a,					// phy
				0x7a,					// ply
				(byte)0xda,				// phx
				(byte)0xfa,				// plx
			};
		AsmAssert.assemble("Test-65C02-new-instructions.asm", expected);
	}
}

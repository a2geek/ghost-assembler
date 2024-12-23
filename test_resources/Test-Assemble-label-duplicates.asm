		.cpu <MOS6502>

; Simply intended to ensure labels cannot be redefined.		

junk_code
		ldy #10
:0		lda (0x00),y
		dey
		bne :0

		ldy #0
:0		lda (0x00),y
		dey
		bne :0
		rts

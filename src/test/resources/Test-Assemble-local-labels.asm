		.cpu <MOS6502>
		
; ==============================================
; Most of this code was taken from 6502.org as a
; quick and easy way of generating something
; interesting for the unit tests.
; ==============================================

; These routines simply ensure that 
; (a)  ":" local labels are allowed
; (b)  local labels are reset at a global label

TOPNT	=	0

		.org 0x800

CLRMEM  LDA #0x00       ;Set up zero value
        TAY             ;Initialize index pointer
:loop   STA (TOPNT),Y   ;Clear memory location
        INY             ;Advance index pointer
        DEX             ;Decrement counter
        BNE :loop       ;Not zero, continue checking
        RTS             ;Return


CLRMEM2 LDA #0x00       ;Set up zero value
:loop   DEY             ;Decrement counter
        STA (TOPNT),Y   ;Clear memory location
        BNE :loop       ;Not zero, continue checking
        RTS             ;RETURN

; This is a junk routine to ensure that
; ".reset locals" does, in fact, reset local
; variables.

junk_code
		ldy #10
:0		lda (0x00),y
		dey
		bne :0
		.reset locals
		ldy #0
:0		lda (0x00),y
		dey
		bne :0
		rts

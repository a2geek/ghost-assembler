		.cpu <SWEET16>
		
; =======================================
; Clear a block of memory
; =======================================

block	=	0xa00
N		=	0x234

		.org 0x0800
clear	set R0,0		; 0 for clearing with
		set R1,block	; address of block to clear
		set R2,N		; # of bytes to clear
loop	st @R1			; store in block
		dcr R2
		bnz loop		; not finished yet
		rtn

		.cpu <SWEET16>
		
; =======================================
; Move a block of memory
; =======================================

source	=	0xa00
dest	=	0xa80
N		=	0x23

		.org 0x0800
move	set R1,source	; address of source block
		set R2,dest		; address of destination block
		set R3,N		; # of bytes to move
loop	ld @R1
		st @R2
		dcr R3			; not finished yet
		bnz loop
		rtn

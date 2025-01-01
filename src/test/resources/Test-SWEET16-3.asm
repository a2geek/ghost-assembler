		.cpu <SWEET16>
		
; =======================================
; Renumber S-C assembler source code
; =======================================

himem	=	0x4c
pp		=	0xca

		.org 0x800
renumber
		set R1,pp		; pp has address of source code
		ldd @R1			; get address of source code
		st R1			; ... in R1
		set R2,10		; increment = 10
		set R3,himem	; himem has addr of end of source
		ldd @R3			; get address in himem
		st R3			; ... in R3
		set R4,990		; start = 990 (1st line will be 1000)
loop	ld R1			; test if finished
		cpr R3
		bc end			; yes
		ld @R1			; get # bytes in this source line
		st R5			; ... into R5
		ld R4			; get sequence number
		add R2			; add increment
		st R4			; ... into R4 again
		std @R1			; ... and also into source line
		dcr R1			; back up pointer
		dcr R1
		dcr R1
		ld R1			; add length of line to pointer
		add R5
		st R1			; point at next source line
		br loop
end		rtn

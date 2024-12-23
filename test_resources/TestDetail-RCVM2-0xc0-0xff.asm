				|; Test a bunch of the RCVM2 opcodes
				|; (range 0xc0 through 0xff)
				|
				| .org 0x1000
				| .cpu <RCVM2>
				|
d2 c3 d6 cd		|; 'RCVM' identifier
02				|; version #2
cf 01			|; control stack (hard-wired to 0x1cf)
				|
c0 12			| vload1 0x12
c1 12			| vload2 0x12
c2 12			| vload4 0x12
c3 12			| vloadf 0x12
c4 12			| vstore1 0x12
c5 12			| vstore2 0x12
c6 12			| vstore4 0x12
c7 12			| vstoref 0x12

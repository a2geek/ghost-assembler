				|; Test a bunch of the RCVM2 opcodes
				|; (range 0x80 through 0xbf)
				|
				| .org 0x1000
				| .cpu <RCVM2>
				|
d2 c3 d6 cd		|; 'RCVM' identifier
02				|; version #2
cf 01			|; control stack (hard-wired to 0x1cf)
				|
80				| shl1
81				| shl2
82				| shl4
83				| shlf
84				| shr1
85				| shr2
86				| shr4
87				| shrf
88				| dup1
89				| dup2
8a				| dup4
8b				| dupf
8c 12			| const1 0x12
8d 34 12		| const2 0x1234
8e 78 56 34 12	| const4 0x12345678
				| ; skipping constf as it's somewhat in limbo
90 34 12		| decjnz1 0x1234
91 34 12		| decjnz2 0x1234
92 34 12		| decjnz4 0x1234

94				| divmods1
95				| divmods2
96				| divmods4

98				| divmodu1
99				| divmodu2
9a				| divmodu4

a0				| convs11
a1				| convs12
a2				| convs14
a3				| convs1f
a4				| convs21
a5				| convs22
a6				| convs24
a7				| convs2f
a8				| convs41
a9				| convs42
aa				| convs44
ab				| convs4f
ac				| convsf1
ad				| convsf2
ae				| convsf4
af				| convsff

b0				| convu11
b1				| convu12
b2				| convu14
b3				| convu1f
b4				| convu21
b5				| convu22
b6				| convu24
b7				| convu2f
b8				| convu41
b9				| convu42
ba				| convu44
bb				| convu4f
bc				| convuf1
bd				| convuf2
be				| convuf4
bf				| convuff

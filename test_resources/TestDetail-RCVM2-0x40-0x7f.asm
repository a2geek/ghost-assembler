			|; Test a bunch of the RCVM2 opcodes
			|; (range 0x40 through 0x7f)
			|
			| .org 0x1000
			| .cpu <RCVM2>
			|
d2 c3 d6 cd	|; 'RCVM' identifier
02			|; version #2
cf 01		|; control stack (hard-wired to 0x1cf)
			|
40			| inc1
41			| inc2
42			| inc4
43			| incf
44			| dec1
45			| dec2
46			| dec4
47			| decf
48			| neg1
49			| neg2
4a			| neg4
4b			| negf
4c			| eq1
4d			| eq2
4e			| eq4
4f			| eqf
50			| ne1
51			| ne2
52			| ne4
53			| nef
54			| lt1
55			| lt2
56			| lt4
57			| ltf
58			| le1
59			| le2
5a			| le4
5b			| lef
5c			| gt1
5d			| gt2
5e			| gt4
5f			| gtf
60			| ge1
61			| ge2
62			| ge4
63			| gef
64			| sload1
65			| sload2
66			| sload4
67			| sloadf
68			| sstore1
69			| sstore2
6a			| sstore4
6b			| sstoref

74			| or1
75			| or2
76			| or4
77			| orf
78			| and1
79			| and2
7a			| and4
7b			| andf
7c			| xor1
7d			| xor2
7e			| xor4
7f			| xorf

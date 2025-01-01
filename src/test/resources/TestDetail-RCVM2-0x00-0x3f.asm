			|; Test a bunch of the RCVM2 opcodes
			|; (range 0x00 through 0x3f)
			|
			| .org 0x1000
			| .cpu <RCVM2>
			|
d2 c3 d6 cd	|; 'RCVM' identifier
02			|; version #2
cf 01		|; control stack (hard-wired to 0x1cf)
			|
00			| exit
01 34 12	| goto 0x1234
02 34 12	| call 0x1234
03			| return
04 34 12 e0 | ncall 0x1234, 0xe0
05 0a		| reserve 0x0a
06 0b		| pushz 0x0b

0e 34 12	| iftrue 0x1234
0f 34 12	| iffalse 0x1234
10 34 12	| mload1 0x1234
11 34 12	| mload2 0x1234
12 34 12	| mload4 0x1234
13 34 12	| mloadf 0x1234
14 34 12	| mstore1 0x1234
15 34 12	| mstore2 0x1234
16 34 12	| mstore4 0x1234
17 34 12	| mstoref 0x1234
18			| drop1
19			| drop2
1a			| drop4
1b			| dropf
1c			| swap1
1d			| swap2
1e			| swap4
1f			| swapf
20			| add1
21			| add2
22			| add4
23			| addf
24			| sub1
25			| sub2
26			| sub4
27			| subf
28			| muls1
29			| muls2
2a			| muls4
2b			| mulsf
2c			| divs1
2d			| divs2
2e			| divs4
2f			| divsf
30			| mods1
31			| mods2
32			| mods4
33			| modsf
34			| mulu1
35			| mulu2
36			| mulu4
38			| divu1
39			| divu2
3a			| divu4
3c			| modu1
3d			| modu2
3e			| modu4

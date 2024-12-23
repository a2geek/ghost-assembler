				|; ****************************************
				|; Factorial
				|; ****************************************
				|
				| .org 0x1000
				| .cpu <RCVM2>
				|
d2 c3 d6 cd		|; 'RCVM' header bytes
02				|; version #2
cf 01			|; control stack size (0x1cf)
				|
				|; factorial(4)
06 04			|main	pushz 4			; return value
8e 04 00 00 00	|		const4 4		; parameter (#4)
02 13 10		|		call factorial
1a				|		drop4
00				|		exit			; result left on stack
				|
				|; uint32 factorial(uint32 n)
				|factorial				; no local variables, so RESERVE not used
				|
				|; if (n == 1) return 1
c2 00			|		vload4 0		; load parameter
8e 01 00 00 00 	|		const4 1
4e				|		eq4
0e 2d 10		|		iftrue _factorial1
				|
				|; return n * factorial(n-1)
c2 00			|		vload4 0		; load N for multiply
06 04			|		pushz 4			; return value
c2 00			|		vload4 0		; load N (for N-1 in method call)
46				|		dec4			; top of stack is N-1
02 13 10		|		call factorial	; factorial(n-1)
1a				|		drop4			; drop parameter
36				|		mulu4			; multiply return value * N
01 32 10		|		goto _factorial2
				|
				|_factorial1
8e 01 00 00 00	|		const4 1		; return 1
				|
				|_factorial2
c6 04			|		vstore4 4		; place TOS into return value
03				|		return

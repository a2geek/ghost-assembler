		.cpu "test_resources/CODE Automation CPU"
			
begin	lod	A,[result+1]
		add	A,[num1+1]			; Add low-order byte
		sto	[result+1],A
			
		lod	A,[result]
		adc	A,[num1]			; Add high-order byte
		sto	[result],A
			
		lod	A,[num2+1]
		add	A,[neg1]			; Decrement second number
		sto	[num2+1],A
			
		jnz	begin
			
neg1	hlt

num1	.byte 00h, a7h
num2	.byte 00h, 1ch
result	.byte 00h, 00h
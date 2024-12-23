		.cpu "test_resources/CODE Automation CPU"
			
		lod	A,[1005h]
		add	A,[1001h]
		sto	[1005h],A
			
		lod	A,[1004h]
		adc	A,[1000h]
		sto	[1004h],A
			
		lod	A,[1003h]
		add	A,[001eh]
		sto	[1003h],A
			
		jnz	0000h
			
		hlt

		.byte 00h, a7h
		.byte 00h, 1ch
		.byte 00h, 00h
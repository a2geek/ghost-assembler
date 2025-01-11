		.cpu "src/test/resources/CODE Automation CPU"

over_here = -2

; Test that negative values are allowed
		lod	A,[-1]
		sto [over_here],A
			
		hlt

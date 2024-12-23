		.cpu "test_resources/CODE Automation CPU"

; Test the underscores are allowed
		lod	A,[over_here]
			
		hlt

over_here
		.byte 00h

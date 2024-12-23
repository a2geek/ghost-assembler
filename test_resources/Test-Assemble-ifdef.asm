; Testing .ifdef/.ifndef and .define

		.define apple
		
		.ifdef apple
		.byte 1
		.endif
		
		.ifdef orange
		.byte 2
		.endif
		
		.ifndef apple
		.byte 3
		.endif
		
		.ifndef orange
		.byte 4
		.endif
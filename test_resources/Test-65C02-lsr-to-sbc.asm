; Provides a very basic test of the inherited 65C02 instructions
		.cpu <WDC65C02>

		lsr
		lsr 0x01
		lsr 0x02,x
		lsr 0x0304
		lsr 0x0506,x
		
		nop
		
		ora #0x07
		ora 0x08
		ora 0x09,x
		ora 0x0a0b
		ora 0x0c0d,x
		ora 0x0e0f,y
		ora (0x10,x)
		ora (0x11),y
		
		pha
		php
		pla
		plp
		
		rol
		rol 0x12
		rol 0x13,x
		rol 0x1415
		rol 0x1617,x
		
		ror
		ror 0x18
		ror 0x19,x
		ror 0x1a1b
		ror 0x1c1d,x
		
		rti
		rts
		
		sbc #0x1e
		sbc 0x1f
		sbc 0x20,x
		sbc 0x2122
		sbc 0x2324,x
		sbc 0x2526,y
		sbc (0x27,x)
		sbc (0x28),y

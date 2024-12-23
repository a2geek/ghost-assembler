; Provides a very basic test of the inherited 65C02 instructions
		.cpu <WDC65C02>

		sec
		sed
		sei
		
		sta 0x01
		sta 0x02,x
		sta 0x0304
		sta 0x0506,x
		sta 0x0708,y
		sta (0x09,x)
		sta (0x0a),y
		
		stx 0x0b
		stx 0x0c,y
		stx 0x0d0e
		
		sty 0x0f
		sty 0x10,x
		sty 0x1112
		
		tax
		tay
		tsx
		txa
		txs
		tya

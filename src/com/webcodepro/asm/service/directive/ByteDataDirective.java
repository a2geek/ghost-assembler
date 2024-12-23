package com.webcodepro.asm.service.directive;

import java.io.IOException;

import com.webcodepro.asm.service.Directive;
import com.webcodepro.asm.service.DirectiveDocumentation;

/**
 * Handle the .byte data directives.
 * 
 * @author Rob
 * @see Directive
 * @see AbstractDataDirective
 */
public class ByteDataDirective extends AbstractDataDirective {
	public ByteDataDirective() {
		super(0x00, false);
	}
	
	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
	@Override
	public String getOpcodeMnemonic() {
		return ".byte";
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "Bytes", 
				getClass().getResourceAsStream("byte.html"));
	}
}

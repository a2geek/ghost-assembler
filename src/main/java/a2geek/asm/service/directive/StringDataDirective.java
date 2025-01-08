package a2geek.asm.service.directive;

import a2geek.asm.service.Directive;
import a2geek.asm.service.DirectiveDocumentation;

import java.io.IOException;

/**
 * Handle the .byte data directives.
 * 
 * @author Rob
 * @see Directive
 * @see AbstractDataDirective
 */
public class StringDataDirective extends AbstractDataDirective {
	public StringDataDirective() {
		super(0x00, true);
	}
	
	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
	@Override
	public String getOpcodeMnemonic() {
		return ".string";
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "String Text", "string.peb");
	}
}

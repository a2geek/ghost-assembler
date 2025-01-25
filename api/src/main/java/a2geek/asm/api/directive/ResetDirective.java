package a2geek.asm.api.directive;

import a2geek.asm.api.service.AssemblerState;
import a2geek.asm.api.service.Directive;
import a2geek.asm.api.service.DirectiveDocumentation;
import a2geek.asm.api.util.LineParts;

/**
 * Handle the .reset directives.
 * 
 * @see Directive
 */
public class ResetDirective implements Directive {
	public static final String MNEMONIC = ".reset";
	
	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
	@Override
	public String getOpcodeMnemonic() {
		return MNEMONIC;
	}

	/**
	 * Process this directive using the given line details, updating
	 * the AssemblerState as needed. 
	 */
	@Override
	public void process(LineParts parts) {
		AssemblerState state = AssemblerState.get();
		state.nextLocalScope();
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() {
		// TODO
		return null;
	}
}

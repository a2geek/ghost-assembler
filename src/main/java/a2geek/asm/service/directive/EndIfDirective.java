package a2geek.asm.service.directive;

import a2geek.asm.assembler.LineParts;
import a2geek.asm.service.AssemblerException;
import a2geek.asm.service.AssemblerState;
import a2geek.asm.service.Directive;
import a2geek.asm.service.DirectiveDocumentation;

import java.io.IOException;

/**
 * Handle the .endif directives.
 * 
 * @author Rob
 * @see Directive
 */
public class EndIfDirective implements Directive {
	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
	@Override
	public String getOpcodeMnemonic() {
		return ".endif";
	}

	/**
	 * Process this directive using the given line details, updating
	 * the AssemblerState as needed. 
	 */
	@Override
	public void process(LineParts parts) throws AssemblerException {
		AssemblerState state = AssemblerState.get();
		state.setActive(true);
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "End conditional IF", 
				getClass().getResourceAsStream("endif.html"), IfDirective.MNEMONIC, 4);
	}
}

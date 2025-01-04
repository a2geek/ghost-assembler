package a2geek.asm.service.directive;

import a2geek.asm.assembler.LineParts;
import a2geek.asm.service.AssemblerState;
import a2geek.asm.service.Directive;
import a2geek.asm.service.DirectiveDocumentation;

import java.io.IOException;

/**
 * Handle the .else directives.
 * 
 * @author Rob
 * @see Directive
 */
public class ElseDirective implements Directive {
	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
	@Override
	public String getOpcodeMnemonic() {
		return ".else";
	}

	/**
	 * Process this directive using the given line details, updating
	 * the AssemblerState as needed. 
	 */
	@Override
	public void process(LineParts parts) {
		//FIXME Does not know if (a) prior if or elif was true and (b) handle nested if statements
		AssemblerState state = AssemblerState.get();
		state.setActive(state.isActive());
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "Conditional ELSE", 
				getClass().getResourceAsStream("else.html"), IfDirective.MNEMONIC, 3);
	}
}

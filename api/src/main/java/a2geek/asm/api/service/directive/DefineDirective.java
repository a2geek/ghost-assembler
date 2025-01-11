package a2geek.asm.api.service.directive;

import a2geek.asm.api.assembler.LineParts;
import a2geek.asm.api.service.AssemblerState;
import a2geek.asm.api.service.Directive;
import a2geek.asm.api.service.DirectiveDocumentation;

import java.io.IOException;

/**
 * Handle define directives.
 * 
 * @author Rob
 * @see Directive
 */
public class DefineDirective implements Directive {
	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
	@Override
	public String getOpcodeMnemonic() {
		return ".define";
	}

	/**
	 * Process this directive using the given line details, updating
	 * the AssemblerState as needed. 
	 */
	@Override
	public void process(LineParts parts) {
		AssemblerState state = AssemblerState.get();
		state.addGlobalVariable(parts.getExpression(), 0L);
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "Define a variable", "define.peb");
	}
}

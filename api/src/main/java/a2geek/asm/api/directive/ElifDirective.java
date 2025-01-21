package a2geek.asm.api.directive;

import a2geek.asm.api.assembler.LineParts;
import a2geek.asm.api.service.*;
import a2geek.asm.api.util.AssemblerException;

import java.io.IOException;

/**
 * Handle the .elif directives.
 * 
 * @author Rob
 * @see Directive
 */
public class ElifDirective implements Directive {
	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
	@Override
	public String getOpcodeMnemonic() {
		return ".elif";
	}

	/**
	 * Process this directive using the given line details, updating
	 * the AssemblerState as needed. 
	 */
	@Override
	public void process(LineParts parts) throws AssemblerException {
		//FIXME Does not know if (a) prior if or elif was true and (b) handle nested if statements
		AssemblerState state = AssemblerState.get();
		Long value = (Long) ExpressionService.evaluate(parts.getExpression());
		state.setActive(0L != value);
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "Conditional ELSE-IF", "elif.peb", IfDirective.MNEMONIC, 2);
	}
}

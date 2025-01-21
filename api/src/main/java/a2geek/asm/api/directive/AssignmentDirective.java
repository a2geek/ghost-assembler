package a2geek.asm.api.directive;

import a2geek.asm.api.assembler.LineParts;
import a2geek.asm.api.service.*;
import a2geek.asm.api.util.AssemblerException;

import java.io.IOException;

/**
 * Handle assignment (=) directives.
 * 
 * @author Rob
 * @see Directive
 */
public class AssignmentDirective implements Directive {
	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
	@Override
	public String getOpcodeMnemonic() {
		return "=";
	}

	/**
	 * Process this directive using the given line details, updating
	 * the AssemblerState as needed. 
	 */
	@Override
	public void process(LineParts parts) throws AssemblerException {
		AssemblerState state = AssemblerState.get();
		Long value = (Long) ExpressionService.evaluate(parts.getExpression());
		state.addGlobalVariable(parts.getLabel(), value);
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "Assignment", "assignment.peb");
	}
}

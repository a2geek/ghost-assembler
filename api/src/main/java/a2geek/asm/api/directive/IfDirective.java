package a2geek.asm.api.directive;

import a2geek.asm.api.service.AssemblerState;
import a2geek.asm.api.service.Directive;
import a2geek.asm.api.service.DirectiveDocumentation;
import a2geek.asm.api.service.ExpressionService;
import a2geek.asm.api.util.AssemblerException;
import a2geek.asm.api.util.LineParts;

import java.io.IOException;

/**
 * Handle the .if directives.
 * 
 * @author Rob
 * @see Directive
 */
public class IfDirective implements Directive {
	public static final String MNEMONIC = ".if";
	
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
	public void process(LineParts parts) throws AssemblerException {
		//FIXME Does not handle nested if statements
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
		return new DirectiveDocumentation(getOpcodeMnemonic(), "Conditional IF", "if.peb", MNEMONIC, 1);
	}
}

package a2geek.asm.api.service.directive;

import a2geek.asm.api.assembler.LineParts;
import a2geek.asm.api.service.AssemblerState;
import a2geek.asm.api.service.Directive;
import a2geek.asm.api.service.DirectiveDocumentation;
import a2geek.asm.api.service.ExpressionService;
import a2geek.asm.api.util.AssemblerException;

import java.io.IOException;

/**
 * Handle ORG directives.
 * 
 * @author Rob
 * @see Directive
 */
public class OrgDirective implements Directive {
	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
	@Override
	public String getOpcodeMnemonic() {
		return ".org";
	}

	/**
	 * Process this directive using the given line details, updating
	 * the AssemblerState as needed. 
	 */
	@Override
	public void process(LineParts parts) throws AssemblerException {
		AssemblerState state = AssemblerState.get();
		Long value = (Long) ExpressionService.evaluate(parts.getExpression());
		state.setPC(value.intValue());
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "Change the code address origin", "org.peb");
	}
}

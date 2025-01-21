package a2geek.asm.api.service.directive;

import a2geek.asm.api.assembler.LineParts;
import a2geek.asm.api.service.*;
import a2geek.asm.api.util.AssemblerException;

import java.io.IOException;

/**
 * Handle the .addr data directives.
 * 
 * @author Rob
 * @see Directive
 */
public class AddrDataDirective implements Directive {

	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
	@Override
	public String getOpcodeMnemonic() {
		return ".addr";
	}

	/**
	 * Process this directive using the given line details, updating
	 * the AssemblerState as needed.
	 */
	@Override
	public void process(LineParts parts) throws AssemblerException {
		AssemblerState state = AssemblerState.get();
		for (String subExpression : AssemblerService.parseCommas(parts.getExpression())) {
			Object result = ExpressionService.evaluate(subExpression, state.getVariables());
			if (result instanceof Long value) {
				var addr = value.shortValue();
				state.getOutput().write(addr & 0xff);
				state.incrementPC();
				state.getOutput().write(addr >> 8);
				state.incrementPC();
			}
		}
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "Address", null);
	}
}

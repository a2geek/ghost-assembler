package com.webcodepro.asm.service.directive;

import com.webcodepro.asm.assembler.LineParts;
import com.webcodepro.asm.service.*;

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
		Long value = (Long)ExpressionService.evaluate(parts.getExpression());
		state.setActive(0L != value);
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "Conditional ELSE-IF", 
				getClass().getResourceAsStream("elif.html"), IfDirective.MNEMONIC, 2);
	}
}

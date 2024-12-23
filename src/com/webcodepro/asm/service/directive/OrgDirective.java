package com.webcodepro.asm.service.directive;

import java.io.IOException;

import com.webcodepro.asm.assembler.LineParts;
import com.webcodepro.asm.service.AssemblerException;
import com.webcodepro.asm.service.AssemblerState;
import com.webcodepro.asm.service.Directive;
import com.webcodepro.asm.service.DirectiveDocumentation;
import com.webcodepro.asm.service.ExpressionService;

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
		Long value = (Long)ExpressionService.evaluate(parts.getExpression());
		state.setPC(value.intValue());
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "Change the code address origin", 
				getClass().getResourceAsStream("org.html"));
	}
}

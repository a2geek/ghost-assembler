package com.webcodepro.asm.service.directive;

import com.webcodepro.asm.assembler.LineParts;
import com.webcodepro.asm.service.AssemblerException;
import com.webcodepro.asm.service.AssemblerState;
import com.webcodepro.asm.service.Directive;
import com.webcodepro.asm.service.DirectiveDocumentation;

import java.io.IOException;

/**
 * Handle the .ifndef directives.
 * 
 * @see Directive
 */
public class IfNdefDirective implements Directive {
	public static final String MNEMONIC = ".ifndef";
	
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
		AssemblerState state = AssemblerState.get();
		state.setActive(!state.getVariables().containsKey(parts.getExpression()));
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		// TODO
		return null;
	}
}
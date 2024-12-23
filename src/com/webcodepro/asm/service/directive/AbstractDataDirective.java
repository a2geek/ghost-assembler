package com.webcodepro.asm.service.directive;

import com.webcodepro.asm.assembler.LineParts;
import com.webcodepro.asm.service.AssemblerException;
import com.webcodepro.asm.service.AssemblerService;
import com.webcodepro.asm.service.AssemblerState;
import com.webcodepro.asm.service.Directive;
import com.webcodepro.asm.service.ExpressionService;

/**
 * Handle various data directives.
 * 
 * @author Rob
 * @see Directive
 */
public abstract class AbstractDataDirective implements Directive {
	private int orMask;
	private boolean length;
	
	protected AbstractDataDirective(int orMask, boolean length) {
		this.orMask = orMask;
		this.length = length;
	}
	
	@Override
	public void process(LineParts parts) throws AssemblerException {
		processExpression(parts.getExpression(), orMask, length);
	}
	
	/**
	 * Process this directive using the given line details, updating
	 * the AssemblerState as needed. 
	 */
	public static void processExpression(String expression, int orMask, boolean length) throws AssemblerException {
		AssemblerState state = AssemblerState.get();
		for (String subExpression : AssemblerService.parseCommas(expression)) {
			Object result = ExpressionService.evaluate(subExpression, state.getVariables());
			if (result instanceof String) {
				String value = (String)result;
				if (length) {
					state.getOutput().write(value.length());
					state.incrementPC();
				}
				for (int c = 0; c<value.length(); c++) {
					state.getOutput().write((byte)(value.charAt(c)|orMask));
					state.incrementPC();
				}
			} else {
				Long value = (Long)result;
				state.getOutput().write((byte)value.intValue());
				state.incrementPC();

			}
		}
	}
}

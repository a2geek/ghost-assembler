package a2geek.asm.api.service.directive;

import a2geek.asm.api.assembler.LineParts;
import a2geek.asm.api.service.*;

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
			if (result instanceof String value) {
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

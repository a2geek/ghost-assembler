package a2geek.asm.api.service.directive;

import a2geek.asm.api.assembler.LineParts;
import a2geek.asm.api.definition.CpuDefinition;
import a2geek.asm.api.service.*;
import a2geek.asm.api.service.DefinitionService.ValidationType;

import java.io.IOException;

/**
 * Handle CPU directives.
 * 
 * @author Rob
 * @see Directive
 */
public class CpuDirective implements Directive {
	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
	@Override
	public String getOpcodeMnemonic() {
		return ".cpu";
	}

	/**
	 * Process this directive using the given line details, updating
	 * the AssemblerState as needed. 
	 */
	@Override
	public void process(LineParts parts) throws AssemblerException {
		AssemblerState state = AssemblerState.get();
		try {
			String filename = parts.getExpression();
			CpuDefinition cpu = DefinitionService.load(filename, ValidationType.NONE);
			state.setCpuDefinition(cpu);
			if (cpu.getOutputFormat() != null && cpu.getOutputFormat().getHeaderBytes() != null) {
				if (state.getPC() == 0) {	// Assume we didn't set starting location
					throw new AssemblerException(String.format(
							"CPU '%s' has header bytes and that requires the source address to be set", 
							filename));
				}
				for (String expr : cpu.getOutputFormat().getHeaderBytes()) {
					if (state.isIdentifyLabels()) {
						state.incrementPC();
					} else {
						Long value = (Long)ExpressionService.evaluate(expr);
						state.getOutput().write(value.byteValue());
						state.incrementPC();
					}
				}
			}
		} catch (Exception ex) {
			if (ex instanceof AssemblerException) {
				throw (AssemblerException)ex;
			}
			AssemblerException.toss(
				"Unable to load CPU definition file '%s' specified on line #%d!",
				parts.getExpression(), state.getReader().getLineNumber());
		}
	}

	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
	@Override
	public DirectiveDocumentation getDocumentation() throws IOException {
		return new DirectiveDocumentation(getOpcodeMnemonic(), "Load CPU definition", "cpu.peb");
	}
}

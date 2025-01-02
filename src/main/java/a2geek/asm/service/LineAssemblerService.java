package a2geek.asm.service;

import a2geek.asm.assembler.LineParts;
import a2geek.asm.definition.AddressModeDefinition;
import a2geek.asm.definition.ByteCode;
import a2geek.asm.definition.CpuDefinition.OperationMatch;
import a2geek.asm.definition.Register;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Provides services around assembling of an individual line of code.
 * 
 * @author Rob
 */
public class LineAssemblerService {
	/**
	 * Assemble a line of code.
	 */
	public static int assemble(LineParts parts) throws AssemblerException {
		AssemblerState state = AssemblerState.get();
		OperationMatch operationMatch = state.getCpuDefinition().findOperation(parts.getOpcode(), parts.getExpression());
		AddressModeDefinition mode = operationMatch.getOperationAddressing().getAddressMode();
		String opcode = operationMatch.getOperationAddressing().getOpcode();
		if (opcode == null) {
			AssemblerException.toss("Unable to assemble line '%s'.", parts.toString());
		}
		
		Map<String,Long> generateVariables = new HashMap<String,Long>();
		generateVariables.put("opcode", (Long)ExpressionService.evaluate(opcode));
		generateVariables.put("PC", state.getPC());
		if (parts.getExpression() != null) {
			Matcher expression = mode.getRegexPattern().matcher(parts.getExpression());
			Matcher variable = mode.getRegexPattern().matcher(mode.getFormat());
			if (!expression.matches() || !variable.matches() ||
					expression.groupCount() != variable.groupCount()) {
				AssemblerException.toss("Expression on line '%s' did not match expected format of '%s'.",
						parts.toString(), mode.getFormat());
			}
			for (int i=0; i<expression.groupCount(); i++) {
				String registerGroup = variable.group(1+i);
				String registerName = expression.group(1+i);
				if (state.getCpuDefinition().isRegisterGroup(registerGroup)) {
					Register register = state.getCpuDefinition().findRegister(registerGroup, registerName);
					if (register != null) {
						registerName = register.getValue();
					} else {
						AssemblerException.toss("Expecting register on line '%s' but found '%s'.",
								parts.toString(), registerName);
					}
				}
				generateVariables.put(registerGroup, (Long)ExpressionService.evaluate(registerName, state.getVariables()));
			}
		}
		
		evaluateCode(mode.getByteCode(), operationMatch.getMnemonicMatchCode(), generateVariables);
		
		return size(parts);
	}
	
	/**
	 * Determine the number of bytes that a line will use.
	 */
	public static int size(LineParts parts) throws AssemblerException {
		OperationMatch match = AssemblerState.get().getCpuDefinition().findOperation(parts.getOpcode(), parts.getExpression());
		if (match == null) {
			AssemblerException.toss("Unknown operator in line '%s'!", parts.toString());
		}
		if (match.getOperationAddressing() == null) {
			AssemblerException.toss("Unknown address mode for %s in line '%s'!", match.getOperation().getMnemonic(), parts.toString());
		}
		return match.getOperationAddressing().getAddressMode().getByteCodeSize();
	}

	/**
	 * Evaluate and create byte-level instruction as defined by the CPU. 
	 */
	protected static void evaluateCode(List<ByteCode> byteCodes, String mnemonicMatch, Map<String,Long> variables) throws AssemblerException {
		for (ByteCode byteCode : byteCodes) {
			if (byteCode.isMnemonicMatch(mnemonicMatch)) {
				Long value = (Long)ExpressionService.evaluate(byteCode.getExpression(), variables);
				AssemblerState.get().getOutput().write(value.byteValue());
			}
		}
	}
}
	

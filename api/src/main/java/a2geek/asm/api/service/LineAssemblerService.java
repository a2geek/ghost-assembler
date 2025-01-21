package a2geek.asm.api.service;

import a2geek.asm.api.assembler.LineParts;
import a2geek.asm.api.definition.AddressModeDefinition;
import a2geek.asm.api.definition.ByteCode;
import a2geek.asm.api.definition.CpuDefinition.OperationMatch;
import a2geek.asm.api.definition.Register;
import a2geek.asm.api.util.AssemblerException;
import a2geek.asm.api.util.pattern.QMatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			throw new AssemblerException("Unable to assemble line #%d: '%s'.", parts.getLineNumber(), parts.toString());
		}
		
		Map<String,Long> generateVariables = new HashMap<>();
		generateVariables.put("opcode", (Long)ExpressionService.evaluate(opcode));
		generateVariables.put("PC", state.getPC());
		if (parts.getExpression() != null) {
			QMatch exprMatch = mode.getQPattern().match(parts.getExpression());
			QMatch varMatch = mode.getQPattern().match(mode.getFormat());
			if (!exprMatch.isMatched() || !varMatch.isMatched() ||
					exprMatch.getSize() != varMatch.getSize()) {
				throw new AssemblerException("Expression on line #%d ('%s') did not match expected format of '%s'.",
						parts.getLineNumber(), parts.toString(), mode.getFormat());
			}
			for (int i=0; i< exprMatch.getSize(); i++) {
				String registerGroup = varMatch.getResult(i);
				String registerName = exprMatch.getResult(i);
				if (state.getCpuDefinition().isRegisterGroup(registerGroup)) {
					Register register = state.getCpuDefinition().findRegister(registerGroup, registerName);
					if (register != null) {
						registerName = register.getValue();
					} else {
						throw new AssemblerException("Expecting register on line #%d ('%s') but found '%s'.",
								parts.getLineNumber(), parts.toString(), registerName);
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
			throw new AssemblerException("Unknown operator in line #%d ('%s')!", parts.getLineNumber(), parts.toString());
		} else if (match.getOperationAddressing() == null) {
			throw new AssemblerException("Unknown address mode for %s in line #%d ('%s')!", match.getOperation().getMnemonic(),
					parts.getLineNumber(), parts.toString());
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

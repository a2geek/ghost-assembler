package a2geek.asm.api.definition;

import a2geek.asm.api.definition.Operation.MnemonicMatch;
import a2geek.asm.api.service.AssemblerException;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A CPU definition.
 * 
 * @author Rob
 */
@XmlRootElement(name = "definition")
@XmlType(name = "DefinitionType")
public class CpuDefinition {
	@XmlAttribute(name = "inherit-from")
	private String inheritFrom;

	@XmlElement(name = "name", required = true)
	private String name;
	
	@XmlElement(name = "overview")
	private Overview overview;
	
	@XmlElement(name = "output-format")
	private OutputFormat outputFormat;
	
	@XmlElement(name = "address-space", required = true)
	private AddressSpace addressSpace;

	@XmlElementWrapper(name = "registers")
	@XmlElement(name = "register", required = true)
	private List<Register> registers = new ArrayList<>();

	@XmlElementWrapper(name = "address-modes", required = true)
	@XmlElements({
			@XmlElement(name = "address-mode", type = AddressModeDefinition.class),
			@XmlElement(name = "address-mode-reference", type = AddressModeReference.class)
		})
	private List<AddressMode> addressModes = new ArrayList<>();

	@XmlElementWrapper(name = "operations", required = true)
	@XmlElement(name = "operation", required = true)
	private List<Operation> operations = new ArrayList<>();
	
	/**
	 * Inherit the parents attributes.
	 */
	public void inherit(CpuDefinition parent) throws AssemblerException {
		if (this.addressSpace == null) {
			this.addressSpace = parent.addressSpace;
		} else {
			this.addressSpace.inherit(parent.addressSpace);
		}
		for (Register reg : parent.registers) {
			if (!this.registers.contains(reg)) registers.add(reg);
		}
		for (AddressMode am : parent.addressModes) {
			AddressMode existing = findAddressModeById(am.getId());
			if (existing instanceof AddressModeReference) {
				this.addressModes.remove(existing);
				existing = null;
			}
			if (existing == null) {
				addressModes.add(am);
			}
		}
		for (Operation parentOperation : parent.operations) {
			int index = this.operations.indexOf(parentOperation);
			if (index != -1) {
				this.operations.get(index).inherit(parentOperation);
			} else {
				operations.add(parentOperation);
			}
		}
		for (Operation op : operations) {
			op.patch(this);
		}
	}
	
	public List<Register> getRegisters() {
		return registers;
	}
	public Register getRegister(int number) {
		return registers.get(number);
	}
	public boolean isRegisterGroup(String registerGroup) {
		for (int i=0; i<registers.size(); i++) {
			Register register = getRegister(i);
			if (register.isGroup(registerGroup)) return true;
		}
		return false;
	}
	public Register findRegister(String registerGroup, String registerId) {
		for (int i=0; i<registers.size(); i++) {
			Register register = getRegister(i);
			if (register.getGroups().equals(registerGroup) 
					&& register.getId().equals(registerId)) {
				return register;
			}
		}
		return null;
	}
	
	public List<AddressModeDefinition> getAddressModes() {
		List<AddressModeDefinition> list = new ArrayList<>();
		for (AddressMode mode : addressModes) {
			if (mode instanceof AddressModeDefinition) {
				list.add((AddressModeDefinition)mode);
			}
		}
		return list;
	}
	/**
	 * Locate the AddressMode by the name given.
	 */
	public AddressMode findAddressModeById(String id) {
        for (AddressMode addressMode : addressModes) {
            if (id.equals(addressMode.getId())) return addressMode;
        }
		return null;
	}
	public List<Operation> getOperations() {
		return operations;
	}
	/** 
	 * Try to identify the proper operation based on the given mnemonic.
	 */
	public OperationMatch findOperation(String mnemonic, String expression) {
		for (Operation operation : operations) {
			MnemonicMatch match = operation.testMnemonic(mnemonic);
			if (match != null) {
				return new OperationMatch(operation, match, expression);
			}
		}
		return null;
	}

	public String getInheritFrom() {
		return inheritFrom;
	}
	public String getName() {
		return name;
	}
	public Overview getOverview() {
		return overview;
	}
	public OutputFormat getOutputFormat() {
		return outputFormat;
	}
	public AddressSpace getAddressSpace() {
		return addressSpace;
	}

	public String toString() {
		return String.format("CpuDefinition '%s' (parent: %s): %s, %s, %s, %s",
				name, inheritFrom, addressSpace, registers, addressModes, operations);
	}

	@XmlTransient
	public static class OperationMatch {
		private Operation operation;
		private OperationAddressing operationAddressing;
		private String mnemonicMatchCode;
		
		public OperationMatch(Operation operation, MnemonicMatch mnemonicMatch, String expression) {
			this.operation = operation;
			this.mnemonicMatchCode = mnemonicMatch.getMnemonicMatchString();
			try {
				switch (mnemonicMatch.getMnemonicMatchType()) {
				case DIRECT_MATCH:
					this.operationAddressing = operation.findOperationAddressingByExpression(expression);
					break;
				case PATTERN_MATCH:
					this.operationAddressing = mnemonicMatch.getOperationAddressing();
					break;
				}
			} catch (AssemblerException e) {
				throw new RuntimeException(e);
			}
		}
		public Operation getOperation() {
			return operation;
		}
		public OperationAddressing getOperationAddressing() {
			return operationAddressing;
		}
		public String getMnemonicMatchCode() {
			return mnemonicMatchCode;
		}
	}
}
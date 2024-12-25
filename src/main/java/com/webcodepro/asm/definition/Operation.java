package com.webcodepro.asm.definition;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import com.webcodepro.asm.service.AssemblerException;

/**
 * An individual operation.
 * <p>
 * Note:  (of historical importance)
 * We store opcodes by addressModeId instead of object because inheritance causes 
 * issues with object relations.  (If parent and child share an addressing mode,
 * and they each have an AddressMode object, which one is to be used?  How about
 * back-patching the appropriate instance of AddressMode?  Etc.)
 * 
 * @author Rob
 */
@XmlType(name = "OperationType")
public class Operation {
	@XmlElement(name = "mnemonic", required = true)
	private String mnemonic;
	
	@XmlElement(name = "description")
	private String description;
	
	@XmlElement(name = "addressing-mode")
	private List<OperationAddressing> addressingModes;
	
	/**
	 * Inherit attributes from the parent.
	 */
	public void inherit(Operation parent) throws AssemblerException {
		if (description == null) this.description = parent.description;
		// Merge the two lists to perform inheritance
		if (addressingModes == null) {
			this.addressingModes = parent.addressingModes;
		} else {
			this.addressingModes.addAll(parent.addressingModes);
		}
	}
	/**
	 * Patch all addressing modes with the real address modes.
	 */
	public void patch(CpuDefinition cpu) throws AssemblerException {
		for (OperationAddressing opaddr : addressingModes) {
			opaddr.patch(cpu);
		}
	}
	public String getDescription() {
		return description;
	}
	public String getMnemonic() {
		return mnemonic;
	}
	
	public OperationAddressing findOperationAddressingById(String id) {
		for (OperationAddressing opaddr : addressingModes) {
			if (id.equals(opaddr.getAddressMode().getId())) {
				return opaddr;
			}
		}
		return null;
	}
	/**
	 * Performs a pattern match of the expression against the format
	 * masks given in each AddressMode.  Test first those expressions 
	 * with a constraint, followed by those expressions with no constraint.
	 * The general assumption is that if a constraint matches, that must be
	 * the one we want; else the others are fairly clear-cut and the first
	 * one that works is correct.
	 */
	public OperationAddressing findOperationAddressingByExpression(String expression) throws AssemblerException {
		if (expression == null) expression = "";
		for (OperationAddressing opaddr : addressingModes) {
			try {
				if (opaddr.getAddressMode().hasConstraint() && opaddr.getAddressMode().matches(expression)) {
					return opaddr;
				}
			} catch (AssemblerException ex) {
				// Ignored.  This is a hack - while determining label locations, if the address is undefined
				// we currently just ignore the addressing mode.  This works in a pinch for the 6502 family
				// but likely won't carry further than that... time will tell.
			}
		}
		for (OperationAddressing opaddr : addressingModes) {
			if (!opaddr.getAddressMode().hasConstraint() && opaddr.getAddressMode().matches(expression)) {
				return opaddr;
			}
		}
		return null;
	}
	public List<OperationAddressing> getAddressingModes() {
		return addressingModes;
	}
	
	public String toString() {
		return String.format("%s:%s", mnemonic, addressingModes);
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Operation)) return false;
		Operation op = (Operation)obj;
		return this.mnemonic.equals(op.mnemonic);
	}
	
	/** For unit tests */
	void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}
	
	/**
	 * Test the given mnemonic against the rules for this operation.
	 * Many times, this will be a straight 1-to-1 mapping.
	 * Other times, this involves looking at the addressing modes and using the
	 * mnemonic modifier to then identify the operation. 
	 */
	public MnemonicMatch testMnemonic(String text) {
		// Direct match
		if (this.mnemonic.equalsIgnoreCase(text)) {
			return new MnemonicMatch(MnemonicMatchType.DIRECT_MATCH, null, null);
		}
		// Mnemonic code match
		for (OperationAddressing opaddr : addressingModes) {
			String base = opaddr.getAddressMode().getBaseMnemonic(text);
			if (base != null && base.equalsIgnoreCase(this.mnemonic)) {
				String matchString = opaddr.getAddressMode().getMnemonicMatch(text);
				return new MnemonicMatch(MnemonicMatchType.PATTERN_MATCH, opaddr, matchString);
			}
		}
		return null;
	}
	
	/**
	 * Contains information regarding the type of Mnemonic match that was found
	 * as well as supplementary information.
	 */
	public class MnemonicMatch {
		private MnemonicMatchType mnemonicMatchType;
		private OperationAddressing operationAddressing;
		private String mnemonicMatchString;
		
		public MnemonicMatch(MnemonicMatchType mnemonicMatchType, OperationAddressing operationAddressing, 
				String mnemonicMatchString) {
			this.mnemonicMatchType = mnemonicMatchType;
			this.operationAddressing = operationAddressing;
			this.mnemonicMatchString = mnemonicMatchString;
		}
		public MnemonicMatchType getMnemonicMatchType() {
			return mnemonicMatchType;
		}
		public OperationAddressing getOperationAddressing() {
			return operationAddressing;
		}
		public String getMnemonicMatchString() {
			return mnemonicMatchString;
		}
	}
	/** 
	 * These are the types of Mnemonic matches that can happen.
	 * Note that NONE is not an option as a null reference is returned.
	 */
	public enum MnemonicMatchType {
		DIRECT_MATCH,
		PATTERN_MATCH
	}
}

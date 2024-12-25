package com.webcodepro.asm.definition;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlType;

import com.webcodepro.asm.service.AssemblerException;

/**
 * Operation addressing represents one of potentially many addressing
 * modes that an operation will support.
 * 
 * @author Rob
 */
@XmlType(name = "OperationAddressingType")
public class OperationAddressing {
	@XmlAttribute(name = "ref", required = true)
	@XmlIDREF
	private AddressMode addressMode;
	
	@XmlAttribute(name = "opcode", required = true)
	private String opcode;
	
	public AddressModeDefinition getAddressMode() {
		if (addressMode instanceof AddressModeDefinition) {
			return (AddressModeDefinition)addressMode;
		}
		throw new RuntimeException(String.format("Opcode '%s' does not have a concrete address-mode", opcode));
	}
	public String getOpcode() {
		return opcode;
	}
	
	/** Replace any references with the real AddressMode. */
	public void patch(CpuDefinition cpu) throws AssemblerException {
		if (addressMode instanceof AddressModeReference) {
			AddressMode am = cpu.findAddressModeById(addressMode.getId());
			if (am == null) {
				AssemblerException.toss("Unable to inherit address-mode-reference '%s' for opcode '%s'", 
						addressMode.getId(), opcode);
			}
			addressMode = am;
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s", opcode, addressMode.getId());
	}
}

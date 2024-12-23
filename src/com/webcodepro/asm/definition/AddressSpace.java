package com.webcodepro.asm.definition;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Definition of the address space available for the defined CPU.
 * This includes the memory size (defined in bits) as well as any
 * intrinsic memory locations for the CPU.
 * 
 * @author Rob
 */
@XmlType(name = "AddressSpaceType")
public class AddressSpace {
	@XmlAttribute(name = "bit-size", required = true)
	private int bitSize;
	
	@XmlElement(name = "memory-location")
	private List<MemoryLocation> memoryLocations;
	
	public void inherit(AddressSpace parent) {
		if (bitSize == 0) this.bitSize = parent.bitSize;
		if (parent.memoryLocations != null) {
			if (this.memoryLocations == null) this.memoryLocations = new ArrayList<MemoryLocation>();
			for (MemoryLocation loc : parent.memoryLocations) {
				if (!this.memoryLocations.contains(loc)) this.memoryLocations.add(loc);
			}
		}
	}

	public int getBitSize() {
		return bitSize;
	}
	public List<MemoryLocation> getMemoryLocations() {
		return memoryLocations;
	}
	public boolean hasMemoryLocations() {
		return memoryLocations != null && !memoryLocations.isEmpty();
	}
	
	@Override
	public String toString() {
		return String.format("address-space is %d bits wide; defined locations are %s", 
				bitSize, memoryLocations);
	}
}

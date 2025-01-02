package a2geek.asm.definition;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * A memory location.
 * 
 * @author Rob
 */
@XmlType(name = "MemoryLocationType")
public class MemoryLocation {
	@XmlElement(name = "name", required = true)
	private String name;
	@XmlElement(name = "address", required = true)
	private String address;
	
	public String getAddress() {
		return address;
	}
	public String getName() {
		return name;
	}
	public String toString() {
		return name + "=" + address;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MemoryLocation)) return false;
		MemoryLocation loc = (MemoryLocation)obj;
		return this.name.equals(loc.name);
	}
}

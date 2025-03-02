package a2geek.asm.api.definition;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Base class for addressing modes.  
 * 
 * @author Rob
 * @see AddressModeDefinition
 * @see AddressModeReference
 */
@XmlType(name = "AddressModeType")
public abstract class AddressMode {
	@XmlAttribute(name = "id", required = true)
	@XmlID
	private String id;
	
	public String getId() {
		return id;
	}
}

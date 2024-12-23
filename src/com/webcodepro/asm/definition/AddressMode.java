package com.webcodepro.asm.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;

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

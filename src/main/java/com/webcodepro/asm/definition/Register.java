package com.webcodepro.asm.definition;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Definition of a CPU register.
 * 
 * @author Rob
 */
@XmlType(name = "RegisterType")
public class Register {
	@XmlAttribute(name = "id", required = true)
	@XmlID
	private String id;

	@XmlAttribute(name = "bit-size")
	private short bitSize;

	@XmlAttribute(name = "value")
	private String value;

	@XmlAttribute(name = "groups")
	private String groups;

	@XmlElement(name = "name", required = true)
	private String name;
	
	@XmlElement(name = "description")
	private String description;
	
	public String getGroups() {
		return groups;
	}
	public boolean isGroup(String groupName) {
		return groups != null && groups.equals(groupName);
	}
	
	public String getValue() {
		return value;
	}
	public short getBitSize() {
		return bitSize;
	}
	public String getDescription() {
		return description;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	public String toString() {
		return String.format("%s:%d:%s", id, bitSize, name);
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Register)) return false;
		Register reg = (Register)obj;
		return this.id.equals(reg.id);
	}
}

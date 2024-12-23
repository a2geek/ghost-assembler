package com.webcodepro.asm.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * Overview of the CPU definition.
 * 
 * @author Rob
 */
@XmlType(name = "OverviewType")
public class Overview {
	@XmlValue
	private String text;
	
	@XmlAttribute(name = "href")
	@XmlSchemaType(name = "anyURI")
	private String referenceUrl;

	public String getText() {
		return text;
	}
	public String getReferenceUrl() {
		return referenceUrl;
	}
}

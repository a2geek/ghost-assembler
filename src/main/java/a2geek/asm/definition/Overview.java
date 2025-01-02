package a2geek.asm.definition;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

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

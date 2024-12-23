package com.webcodepro.asm.definition;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines any output specifics of the assembled file.
 * This may include magic bytes required by the CPU or interpreter.
 * Usage is expected to be rare.
 * 
 * NOTE: This is *NOT* an operating-system or file-system type container!!
 * 
 * @author Rob
 */
@XmlType(name = "OutputFormatType")
public class OutputFormat {
	@XmlElementWrapper(name = "header")
	@XmlElement(name = "byte")
	private List<String> headerBytes;
	
	public List<String> getHeaderBytes() {
		return headerBytes;
	}
}

package com.webcodepro.asm.definition;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;

import java.util.List;

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

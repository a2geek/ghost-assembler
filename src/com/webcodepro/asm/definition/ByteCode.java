package com.webcodepro.asm.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(name = "ByteCodeType")
public class ByteCode {
	@XmlAttribute(name = "mnemonic-match")
	private String mnemonicMatch;

	@XmlValue
	private String expression;
	
	/** 
	 * Test the mnemonic coding specified in the mnemonic format to see if it matches.
	 * If there is no mnemonic match specified, assume it is a match.
	 */
	public boolean isMnemonicMatch(String matchCode) {
		return mnemonicMatch == null ? true : mnemonicMatch.equalsIgnoreCase(matchCode);
	}

	public String getMnemonicMatch() {
		return mnemonicMatch;
	}
	public String getExpression() {
		return expression;
	}
	
	@Override
	public String toString() {
		if (mnemonicMatch != null) {
			return String.format("[%s]%s", mnemonicMatch, expression);
		} else {
			return expression;
		}
	}
}

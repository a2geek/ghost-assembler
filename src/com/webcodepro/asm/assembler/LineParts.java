package com.webcodepro.asm.assembler;

/**
 * Contains all the parts of a source assembly line.
 * 
 * @author Rob
 */
public class LineParts {
	private String comment;
	private String label;
	private String opcode;
	private String expression;
	
	public boolean isGlobalLabel() {
		return label != null && !isLocalLabel();
	}
	public boolean isLocalLabel() {
		return label != null && label.startsWith(":");
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getOpcode() {
		return opcode;
	}
	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (label != null) {
			buf.append(label);
			buf.append(' ');
		}
		if (opcode != null) {
			buf.append(opcode);
			buf.append(' ');
		}
		if (expression != null) {
			buf.append(expression);
			buf.append(' ');
		}
		if (comment != null) {
			buf.append(comment);
		}
		return buf.toString();
	}
}

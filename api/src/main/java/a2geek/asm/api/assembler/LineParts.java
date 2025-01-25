package a2geek.asm.api.assembler;

/**
 * Contains all the parts of a source assembly line.
 * 
 * @author Rob
 */
public class LineParts {
	private int lineNumber=-1;
	private String comment;
	private String label;
	private String opcode;
	private String expression;
	
	public boolean isGlobalLabel() {
		return label != null && !isLocalLabel();
	}
	public boolean isLocalLabel() {
		return label != null && (label.startsWith(":") || label.startsWith("@"));
	}

	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
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
		StringBuilder buf = new StringBuilder();
		if (label != null) {
			buf.append(label);
		}
		if (opcode != null) {
			if (!buf.isEmpty()) buf.append(' ');
			buf.append(opcode);
		}
		if (expression != null) {
			if (!buf.isEmpty()) buf.append(' ');
			buf.append(expression);
		}
		if (comment != null) {
			if (!buf.isEmpty()) buf.append(' ');
			buf.append(comment);
		}
		return buf.toString();
	}
}

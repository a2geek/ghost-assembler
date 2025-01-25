package a2geek.asm.api.service;

import a2geek.asm.api.util.LineParts;

/**
 * Provide services surrounding lines of assembly code.
 * 
 * @author Rob
 */
public class LineParserService {
	/** Indicates the current state of line parsing. */
	enum State {
		LABEL,
		OPCODE,
		EXPRESSION,
		COMMENT;
		
		/** Answer with the next state */
		public State next() {
			return values()[(ordinal() + 1) % values().length];
		}
	}
	
	/**
	 * Parse a line into its constituent parts.
	 */
	public static LineParts parseLine(String line) {
		LineParts parts = new LineParts();
		if (line == null) return parts;
		
		StringBuffer buf = new StringBuffer();
		State state = State.LABEL;
		for (int i=0; i<line.length(); i++) {
			char ch = line.charAt(i);
			if (i==0 && ch=='.') {
				// Special handling for directives that start in left-hand column
				state = State.OPCODE;
			}
			if (isAtEndOfPart(state, ch, buf.length())) {
				savePart(state, buf, parts);
				state = state.next();
				// We may have the start of a comment and we don't want to 
				// lose it!
				if (state == State.COMMENT) buf.append(ch);
			} else {
				if (!Character.isWhitespace(ch) || state.ordinal() >= State.EXPRESSION.ordinal()) {
					buf.append(ch);
				}
				if (ch == ';' && buf.length() == 1) {
					state = State.COMMENT;
				}
			}
		}
		if (!buf.isEmpty()) {
			savePart(state, buf, parts);
		}
		
		return parts;
	}
	
	/**
	 * Identifies end-of-part conditions.
	 */
	protected static boolean isAtEndOfPart(State state, char ch, int length) {
		boolean isWhitespace = Character.isWhitespace(ch);
		return (state == State.LABEL && isWhitespace)
			|| (state == State.OPCODE && isWhitespace && length > 0)
			|| (state == State.EXPRESSION && ch == ';');
	}

	/**
	 * Determines where a part belongs in LineParts and updates the next
	 * state as appropriate.
	 */
	protected static void savePart(State state, StringBuffer buf, LineParts parts) {
		if (buf.isEmpty()) return;
		String part = buf.toString().trim();
		buf.setLength(0);
		switch (state) {
		case LABEL:
			parts.setLabel(part);
			break;
		case OPCODE:
			parts.setOpcode(part);
			break;
		case  EXPRESSION:
			parts.setExpression(part);
			break;
		case COMMENT:
			parts.setComment(part);
			break;
		}
	}
}

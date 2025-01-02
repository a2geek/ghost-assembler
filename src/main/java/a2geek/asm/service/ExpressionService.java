package a2geek.asm.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A simple expression parser.  All expressions resolve to a Long or a String.
 * All functions are available for a Long, String supports "+" only.
 * Functions are: parenthesis for grouping, addition/subtraction,
 * multiplication/division, and modulo.  Unary minus is supported, but the
 * operand is changed from a "-" to a "~" to distinguish between unary and
 * binary minus.  The expression evaluation should be whitespace insensitive.
 * Additionally, bit-wise OR, AND, and XOR as well as bit-shifting operators
 * (&lt;&lt;, &gt;&gt;) are available.  The equality/inequality operators
 * are present and will return a 1 if true or 0 if false (&lt;, &gt;, =,
 * &gt;=, &lt;=, !=).  Logical AND and OR are present in the form of &amp;&amp;
 * and ||.
 * <p>
 * The processing is fairly brute-force.
 * 
 * @author Rob
 */
public class ExpressionService {
	private static final String PATTERN_DECIMAL = "\\d+";
	private static final String PATTERN_FLOATINGPOINT = "\\d+\\.\\d+";
	private static final String PATTERN_HEXIDECIMAL = "0x\\p{XDigit}+";
	private static final String PATTERN_HEXIDECIMAL2 = "\\p{XDigit}+h";
	private static final String PATTERN_HEXIDECIMAL3 = "\\$\\p{XDigit}+";
	private static final String PATTERN_BINARY = "0b[01]+";
	private static final String PATTERN_OCTAL = "0[0-7]+";
	private static final String PATTERN_VARIABLE = "[a-z:_A-Z]\\w*";
	private static final String PATTERN_CHARACTER = "'.'";
	private static final String PATTERN_STRING = "\"[^\"]*\"";
	
	/** States of expression parsing. */
	enum State {
		START,
		NAMEVALUE,
		OPERAND
	}

	/** All operators understood within an expression. */
	enum Operator {
		ADD(1),
		SUBTRACT(1),
		MULTIPLY(2),
		DIVIDE(2),
		MODULO(2),
		UNARY_MINUS(3),
		LEFT_PAREN(0),
		RIGHT_PAREN(0),
		BITWISE_OR(2),
		BITWISE_AND(2),
		BITWISE_XOR(2),
		SHIFT_LEFT(1),
		SHIFT_RIGHT(1),
		LESS_THAN(1),
		GREATER_THAN(1),
		EQUALS(1),
		GREATER_EQUALS(1),
		LESS_EQUALS(1),
		NOT_EQUALS(1),
		AND(0),
		OR(0);
		
		/**
		 * Determine the priority of operations. The higher numbered operands
		 * should be evaluated first.
		 */
		public int priority;
		
		private Operator(int priority) {
			this.priority = priority;
		}
	}
	
	/**
	 * A convenience method to evaluate a simple expression with no variables
	 * and an assumed initial local scope.
	 */
	public static Object evaluate(String expression) throws AssemblerException {
		return evaluate(expression, new HashMap<String,Long>());
	}
	
	/**
	 * Evaluate an expression with the given set of variables.  The Map should
	 * be keyed by the variable name and contain a Long.
	 */
	public static Object evaluate(String expression, Map<String,Long> variables) throws AssemblerException {
		State state = State.START;
		State priorState = State.START;
		StringBuffer value = new StringBuffer();
		Stack<Operator> opStack = new Stack<Operator>();
		Stack<Object> valStack = new Stack<Object>();
		char inQuotes = 0;
		for (int i=0; i<expression.length(); i++) {
			char ch = expression.charAt(i);
			if (inQuotes == 0) {
				if (ch == '\'' || ch == '"') inQuotes = ch;
				else if (isWhiteSpace(ch)) continue;
			} else {
				if (ch == '\'' || ch == '"') inQuotes = 0;
			}
			if (state == State.START) {
				if (inQuotes != 0 || isNameOrValue(ch)) {
					state = State.NAMEVALUE;
				} else if (isOperand(ch)) {
					state = State.OPERAND;
				} else {
					throw new AssemblerException(
							"Unexpected character in expression '" + ch + "'!");
				}
			}
			if (state == State.NAMEVALUE) {
				if (inQuotes != 0 || isNameOrValue(ch)) {
					value.append(ch);
				} else if (isOperand(ch)) {
					valStack.push(evaluateValue(value.toString(), variables));
					value.setLength(0);
					state = State.OPERAND;
					priorState = State.NAMEVALUE;
					// we cheat here - knowing that OPERAND is the next if statement!
				}
			}
			if (state == State.OPERAND) {
				Operator operator = getOperator(ch);
				if (!isSingleCharacterOperand(ch)) {
					if (i+1 < expression.length()) {
						char ch2 = expression.charAt(i+1);
						if (getOperator(ch,ch2) != null) {
							i++;
							operator = getOperator(ch,ch2);
						}
					}
				}
				if (operator == Operator.SUBTRACT	// unary minus and minus look the same  
						&& (priorState == State.START || priorState == State.OPERAND)) {
					opStack.push(Operator.UNARY_MINUS);
				} else if (opStack.isEmpty()) {
					opStack.push(operator);
				} else if (operator == Operator.LEFT_PAREN) {
					opStack.push(operator);
				} else if (operator == Operator.RIGHT_PAREN) {
					while (true) {
						if (opStack.isEmpty()) {
							throw new AssemblerException(
									"Operator stack empty when balancing parenthesis.");
						}
						operator = opStack.peek();
						if (operator == Operator.LEFT_PAREN) {
							opStack.pop();
							break;
						}
						reduce(opStack, valStack);
					}
				} else if (operator.priority > opStack.peek().priority) {
					opStack.push(operator);
				} else {
					reduce(opStack, valStack);
					opStack.push(operator);
				}
				state = State.START;
				priorState = State.OPERAND;
			}
		}
		if (value.length() > 0) {
			valStack.push(evaluateValue(value.toString(), variables));
		}
		while (!opStack.isEmpty()) {
			reduce(opStack, valStack);
		}
		if (valStack.size() != 1) {
			throw new AssemblerException("Expected op stack to be empty and " +
					"value stack to be a depth of 1, but they aren't!");
		}
		return valStack.pop();
	}
	
	/**
	 * Identify single character operator.
	 */
	protected static Operator getOperator(char ch) {
		switch (ch) {
			case '+': return Operator.ADD;
			case '-': return Operator.SUBTRACT;
			case '<': return Operator.LESS_THAN;
			case '>': return Operator.GREATER_THAN;
			case '*': return Operator.MULTIPLY;
			case '/': return Operator.DIVIDE;
			case '%': return Operator.MODULO;
			case '(': return Operator.LEFT_PAREN;
			case ')': return Operator.RIGHT_PAREN;
			case '|': return Operator.BITWISE_OR;
			case '&': return Operator.BITWISE_AND;
			case '^': return Operator.BITWISE_XOR;
			case '=': return Operator.EQUALS;
		}
		return null;
	}
	
	/**
	 * Identify double character operator.
	 */
	protected static Operator getOperator(char ch1, char ch2) {
		String op = new String(new char[] { ch1, ch2 });
		if ("<<".equals(op)) return Operator.SHIFT_LEFT;
		if (">>".equals(op)) return Operator.SHIFT_RIGHT;
		if ("<=".equals(op)) return Operator.LESS_EQUALS;
		if (">=".equals(op)) return Operator.GREATER_EQUALS;
		if ("!=".equals(op)) return Operator.NOT_EQUALS;
		if ("||".equals(op)) return Operator.OR;
		if ("&&".equals(op)) return Operator.AND;
		return null;
	}
	
	/**
	 * Reduce one operand from the operand stack. This will pop the top-most
	 * operand from the operand stack and pop off as many arguments as are 
	 * required from the value stack. The resulting value will be placed on
	 * the value stack.
	 */
	protected static void reduce(Stack<Operator> opStack, Stack<Object> valStack) throws AssemblerException {
		if (valStack.peek() instanceof Long) {
			reduceNumber(opStack, valStack);
		} else if (valStack.peek() instanceof String) {
			reduceString(opStack, valStack);
		} else {
			throw new AssemblerException("Unknown object type on the value stack! (" 
					+ valStack.peek().getClass().getName() + ")");
		}
	}
	/**
	 * Reduce one numerical operand from the operand stack.
	 */
	protected static void reduceNumber(Stack<Operator> opStack, Stack<Object> valStack) throws AssemblerException {
		Operator op = opStack.pop();
		if (valStack.isEmpty()) {
			throw new AssemblerException("Value stack empty when evaluating a Number operation.");
		}
		long value2 = (Long)valStack.pop();
		long result = 0;
		if (op == Operator.UNARY_MINUS) {
			result = -value2;
		} else {
			if (valStack.isEmpty()) {
				throw new AssemblerException("Value stack empty when evaluating binary Number operation.");
			}
			long value1 = (Long)valStack.pop();
			switch (op) {
				case ADD:
					result = value1 + value2;
					break;
				case SUBTRACT:
					result = value1 - value2;
					break;
				case MULTIPLY:
					result = value1 * value2;
					break;
				case DIVIDE:
					result = value1 / value2;
					break;
				case MODULO:
					result = value1 % value2;
					break;
				case BITWISE_OR:
					result = value1 | value2;
					break;
				case BITWISE_AND:
					result = value1 & value2;
					break;
				case BITWISE_XOR:
					result = value1 ^ value2;
					break;
				case SHIFT_LEFT:
					result = value1 << value2;
					break;
				case SHIFT_RIGHT:
					result = value1 >> value2;
					break;
				case EQUALS:
					result = value1 == value2 ? 1 : 0; 
					break;
				case GREATER_EQUALS:
					result = value1 >= value2 ? 1 : 0; 
					break;
				case GREATER_THAN:
					result = value1 > value2 ? 1 : 0; 
					break;
				case LESS_EQUALS:
					result = value1 <= value2 ? 1 : 0; 
					break;
				case LESS_THAN:
					result = value1 < value2 ? 1 : 0; 
					break;
				case NOT_EQUALS:
					result = value1 != value2 ? 1 : 0;
					break;
				case OR:
					result = (value1 != 0) || (value2 != 0) ? 1 : 0;
					break;
				case AND:
					result = (value1 != 0) && (value2 != 0) ? 1 : 0;
					break;
			}
		}
		valStack.push(result);
	}
	/**
	 * Reduce one string operand from the operand stack.
	 */
	protected static void reduceString(Stack<Operator> opStack, Stack<Object> valStack) throws AssemblerException {
		Operator op = opStack.pop();
		if (valStack.isEmpty()) {
			throw new AssemblerException("Value stack empty when evaluating a String operation.");
		}
		String value2 = (String)valStack.pop();
		if (valStack.isEmpty()) {
			throw new AssemblerException("Value stack empty when evaluating binary String operation.");
		}
		Object result = null;
		String value1 = (String)valStack.pop();
		switch (op) {
			case ADD:
				result = value1 + value2;
				break;
			case EQUALS:
				result = value1.equals(value2) ? 1L : 0L; 
				break;
			case GREATER_EQUALS:
				result = value1.compareTo(value2) >= 0 ? 1L : 0L;
				break;
			case GREATER_THAN:
				result = value1.compareTo(value2) > 0 ? 1L : 0L;
				break;
			case LESS_EQUALS:
				result = value1.compareTo(value2) <= 0 ? 1L : 0L;
				break;
			case LESS_THAN:
				result = value1.compareTo(value2) < 0 ? 1L : 0L;
				break;
			case NOT_EQUALS:
				result = !value1.equals(value2) ? 1L : 0L;
				break;
			default:
				new AssemblerException("The operator is not supported for strings.");
		}
		valStack.push(result);
	}
	
	/**
	 * Indicates if this character is part of a name or a value.
	 */
	protected static boolean isNameOrValue(char ch) {
		return Character.isLetterOrDigit(ch) || ch == '_' || ch == ':' || ch == '.' || ch == '$' || ch == '\'' || ch == '"';
	}
	/**
	 * Indicates if this character is an operand.
	 */
	protected static boolean isOperand(char ch) {
		return "+-*/%()!<>=&|^".indexOf(ch) != -1;
	}
	/**
	 * Indicates if this is a single-character operand.
	 * (Everything but &lt;&lt;, &gt;&gt;, &lt;=, &gt;=,
	 * &amp;&amp;, and || at this time.)
	 */
	protected static boolean isSingleCharacterOperand(char ch) {
		return "+-*/%()=^".indexOf(ch) != -1;
	}
	/**
	 * Indicates if this character is whitespace.
	 */
	protected static boolean isWhiteSpace(char ch) {
		return Character.isWhitespace(ch);
	}

	/**
	 * Evaluate a value. Recognized patterns are:<ul>
	 * <li>Hexadecimal (0x[0-9a-f]+);</li>
	 * <li>Binary (0b[01]+);</li>
	 * <li>Octal (0[0-7]+);</li>
	 * <li>Decimal ([0-9]+); or</li>
	 * <li>Variables which are looked up in the Map.</li>
	 * </ul>
	 */
	protected static Object evaluateValue(String value, Map<String,Long> variables) throws AssemblerException {
		if (value.matches(PATTERN_HEXIDECIMAL)) {
			return Long.valueOf(value.substring(2), 16);
		} else if (value.matches(PATTERN_HEXIDECIMAL2)) {
			return Long.valueOf(value.substring(0, value.length()-1), 16);
		} else if (value.matches(PATTERN_HEXIDECIMAL3)) {
			return Long.valueOf(value.substring(1), 16);
		} else if (value.matches(PATTERN_BINARY)) {
			return Long.valueOf(value.substring(2), 2);
		} else if (value.matches(PATTERN_OCTAL)) {
			return Long.valueOf(value.substring(1), 8);
		} else if (value.matches(PATTERN_DECIMAL)) {
			return Long.valueOf(value);
		} else if (value.matches(PATTERN_FLOATINGPOINT)) {
			return Double.valueOf(value).longValue();
		} else if (value.matches(PATTERN_VARIABLE)) {
			if (variables.containsKey(value)) {
				return (Long)variables.get(value);
			}
			if (AssemblerState.get().isIdentifyLabels()) {
				return AssemblerState.get().getPC();
			}
			throw new AssemblerException("Variable '" 
					+ value + "' not found");
		} else if (value.matches(PATTERN_CHARACTER)) {
			return Long.valueOf(value.charAt(1));	// '.' -> we return the "."
		} else if (value.matches(PATTERN_STRING)) {
			return value.substring(1, value.length()-1);
		}
		throw new AssemblerException("Unknown expression format of '"
				+ value + "'!");
	}
}

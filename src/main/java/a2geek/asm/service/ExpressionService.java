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
		public final int priority;
		
		Operator(int priority) {
			this.priority = priority;
		}
	}
	
	/**
	 * A convenience method to evaluate a simple expression with no variables
	 * and an assumed initial local scope.
	 */
	public static Object evaluate(String expression) throws AssemblerException {
		return evaluate(expression, new HashMap<>());
	}
	
	/**
	 * Evaluate an expression with the given set of variables.  The Map should
	 * be keyed by the variable name and contain a Long.
	 */
	public static Object evaluate(String expression, Map<String,Long> variables) throws AssemblerException {
		State state = State.START;
		State priorState = State.START;
		StringBuilder value = new StringBuilder();
		Stack<Operator> opStack = new Stack<>();
		Stack<Object> valStack = new Stack<>();
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
		if (!value.isEmpty()) {
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
        return switch (ch) {
            case '+' -> Operator.ADD;
            case '-' -> Operator.SUBTRACT;
            case '<' -> Operator.LESS_THAN;
            case '>' -> Operator.GREATER_THAN;
            case '*' -> Operator.MULTIPLY;
            case '/' -> Operator.DIVIDE;
            case '%' -> Operator.MODULO;
            case '(' -> Operator.LEFT_PAREN;
            case ')' -> Operator.RIGHT_PAREN;
            case '|' -> Operator.BITWISE_OR;
            case '&' -> Operator.BITWISE_AND;
            case '^' -> Operator.BITWISE_XOR;
            case '=' -> Operator.EQUALS;
            default -> null;
        };
    }
	
	/**
	 * Identify double character operator.
	 */
	protected static Operator getOperator(char ch1, char ch2) {
		String op = new String(new char[] { ch1, ch2 });
		return switch (op) {
			case "<<" -> Operator.SHIFT_LEFT;
			case ">>" -> Operator.SHIFT_RIGHT;
			case "<=" -> Operator.LESS_EQUALS;
			case ">=" -> Operator.GREATER_EQUALS;
			case "!=" -> Operator.NOT_EQUALS;
			case "||" -> Operator.OR;
			case "&&" -> Operator.AND;
			default -> null;
		};
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
            result = switch (op) {
                case ADD -> value1 + value2;
                case SUBTRACT -> value1 - value2;
                case MULTIPLY -> value1 * value2;
                case DIVIDE -> value1 / value2;
                case MODULO -> value1 % value2;
                case BITWISE_OR -> value1 | value2;
                case BITWISE_AND -> value1 & value2;
                case BITWISE_XOR -> value1 ^ value2;
                case SHIFT_LEFT -> value1 << value2;
                case SHIFT_RIGHT -> value1 >> value2;
                case EQUALS -> value1 == value2 ? 1 : 0;
                case GREATER_EQUALS -> value1 >= value2 ? 1 : 0;
                case GREATER_THAN -> value1 > value2 ? 1 : 0;
                case LESS_EQUALS -> value1 <= value2 ? 1 : 0;
                case LESS_THAN -> value1 < value2 ? 1 : 0;
                case NOT_EQUALS -> value1 != value2 ? 1 : 0;
                case OR -> (value1 != 0) || (value2 != 0) ? 1 : 0;
                case AND -> (value1 != 0) && (value2 != 0) ? 1 : 0;
                default -> result;
            };
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
                throw new AssemblerException("The operator is not supported for strings.");
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
				return variables.get(value);
			}
			if (AssemblerState.get().isIdentifyLabels()) {
				return AssemblerState.get().getPC();
			}
			throw new AssemblerException("Variable '" 
					+ value + "' not found");
		} else if (value.matches(PATTERN_CHARACTER)) {
			return (long) value.charAt(1);	// '.' -> we return the "."
		} else if (value.matches(PATTERN_STRING)) {
			return value.substring(1, value.length()-1);
		}
		throw new AssemblerException("Unknown expression format of '"
				+ value + "'!");
	}
}

package a2geek.asm.api.definition;

import a2geek.asm.api.service.AssemblerException;
import a2geek.asm.api.service.AssemblerState;
import a2geek.asm.api.service.ExpressionService;
import a2geek.asm.api.util.pattern.QMatch;
import a2geek.asm.api.util.pattern.QPattern;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container for information regarding the CPU addressing mode.
 * 
 * @author Rob
 */
@XmlType(name = "AddressModeDefinitionType")
public class AddressModeDefinition extends AddressMode {
	public static final String MATCH_NAME = "[:expr:]";
	public static final String MATCH_REGEX = "[0-9_:a-z-/'\\+\\*\\.\\$><&]";
	public static final Pattern NULL_PATTERN = Pattern.compile("", Pattern.CASE_INSENSITIVE);
	public static final String NULL_FORMAT = "";
	
	@XmlElement(name = "title")
	private String title;
	
	@XmlElement(name = "description")
	private String description;
	
	@XmlElement(name = "mnemonic-format")
	private String mnemonicFormat = NULL_FORMAT;
	@XmlTransient
	private Pattern mnemonicPattern;
	
	@XmlElement(name = "format")
	private String format = NULL_FORMAT;

	@XmlElement(name = "pattern")
	private String pattern;
	@XmlTransient
	private QPattern qpattern;

	@XmlElement(name = "constraint")
	private String constraint;

	@XmlElementWrapper(name = "code", required = true)
	@XmlElement(name = "byte", required = true)
	private List<ByteCode> byteCode = new ArrayList<>();

	/**
	 * Test the arguments to see if they match the RE Pattern.
	 * If so, execute the match constraint (if present).
	 */
	public boolean matches(String args) throws AssemblerException {
		QMatch argMatch = getQPattern().match(args);
		AssemblerState state = AssemblerState.get();
		if (argMatch.isMatched()) {
			if (getConstraint() == null) return true;		// a match
			// need to do a bit more work...
			QMatch formatMatch = getQPattern().match(getFormat());
			if (!formatMatch.isMatched() || argMatch.getSize() != formatMatch.getSize()) {
				throw new AssemblerException("Expression of '"
						+ args + "' and format of '" + getFormat() + "' did not resolve to the "
						+ "same number of matches (" + argMatch.getSize() + ","
						+ formatMatch.getSize() + ").");
			}
			Map<String,Long> variables = new HashMap<>(state.getVariables());
			// ... mainly pull all variables out of the assembly line and place into the variable Map
			for (int i=1; i<=argMatch.getSize(); i++) {
				variables.put(formatMatch.getResult(i-1),
					(Long)ExpressionService.evaluate(argMatch.getResult(i-1), variables));
			}
			return (Long)ExpressionService.evaluate(getConstraint(), variables) == 1;
		}
		return false;
	}
	public boolean hasConstraint() {
		return constraint != null && !constraint.isEmpty();
	}
	public List<ByteCode> getByteCode() {
		return byteCode;
	}
	/** Compute likely byte-code size.  This varies with expressions that have been added. */
	public int getByteCodeSize() {
		String firstMnemonicMatch = null;
		int size = 0;
		for (ByteCode bc : getByteCode()) {
			if (bc.getMnemonicMatch() == null) {
				size++;
			} else {
				if (firstMnemonicMatch == null) {
					firstMnemonicMatch = bc.getMnemonicMatch();
				}
				if (firstMnemonicMatch.equals(bc.getMnemonicMatch())) {
					size++;
				}
			}
		}
		return size;
	}
	public String getPattern() {
		return pattern;
	}
	public QPattern getQPattern() {
		if (qpattern == null) {
			qpattern = QPattern.build(pattern);
		}
		return qpattern;
	}

	public String getDescription() {
		return description;
	}
	public String getFormat() {
		if (format == null) {
			this.format = NULL_FORMAT;
		}
		return format;
	}
	public String getMnemonicFormat() {
		if (mnemonicFormat == null) {
			this.mnemonicFormat = NULL_FORMAT;
		}
		return mnemonicFormat;
	}
	public Pattern getMnemonicPattern() {
		if (mnemonicPattern == null) {
			mnemonicPattern = Pattern.compile(getMnemonicFormat(), Pattern.CASE_INSENSITIVE);
		}
		return mnemonicPattern;
	}
	public String getTitle() {
		return title;
	}
	public String getConstraint() {
		return constraint;
	}
	
	public String toString() {
		return getId() + ":" + format + ":" + title;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AddressModeDefinition am)) return false;
        return getId().equals(am.getId());
	}
	
	/**
	 * Test the mnemonic against the mnemonic format, if any.
	 */
	public boolean testMnemonic(String mnemonic) {
		if (getMnemonicPattern() != null) {
			return getMnemonicPattern().matcher(mnemonic).matches();
		}
		return false;
	}
	/**
	 * Determine the unmodified mnemonic text for this address mode.
	 * If this is not an address mode with a mnemonic modifier (e.g., OR[1234]),
	 * a null is returned as well as if the mnemonic simply doesn't match. 
	 */
	public String getBaseMnemonic(String text) {
		if (testMnemonic(text)) {
			Matcher matcher = getMnemonicPattern().matcher(text);
			matcher.matches();	// builds group match information
			StringBuilder nonPatternText = new StringBuilder();
			int pos = 0;
			for (int g=1; g<=matcher.groupCount(); g++) {
				if (matcher.start(g) > 1) {
					nonPatternText.append(text, pos, matcher.start(g));
				}
				pos = matcher.end(g);
			}
			if (pos >= text.length()) {
				nonPatternText.append(text.substring(pos));
			}
			return nonPatternText.toString();
		}
		return null;
	}
	/**
	 * Identify the specific portion of the mnemonic string that matched.
	 * The is the RE grouping expression and is used in the byte code generation
	 * to dynamically alter the generated byte code.
	 */
	public String getMnemonicMatch(String text) {
		if (testMnemonic(text)) {
			Matcher matcher = getMnemonicPattern().matcher(text);
			matcher.matches();
			if (matcher.groupCount() == 1) {
				return matcher.group(1);
			}
		}
		return null;
	}

	/** For unit tests */
	void setPattern(String pattern) {
		this.pattern = pattern;
	}
	/** For unit tests */
	void setFormat(String format) {
		this.format = format;
	}
	/** For unit tests */
	void setMnemonicFormat(String mnemonicFormat) {
		this.mnemonicFormat = mnemonicFormat;
	}
}

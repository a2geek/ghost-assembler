package com.webcodepro.asm.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import com.webcodepro.asm.assembler.LineParts;

/**
 * Provide services surrounding assembly of an entire program file.
 * @author Rob
 */
public class AssemblerService {
	private static Map<String,Directive> directives = new HashMap<String,Directive>();
	
	static {
		ServiceLoader<Directive> directiveLoader = ServiceLoader.load(Directive.class);
		for (Directive directive : directiveLoader) {
			directives.put(directive.getOpcodeMnemonic().toLowerCase(), directive);
		}
	}
	
	/** Make the list of known directives available. */
	public static Collection<Directive> getDirectives() {
		return directives.values();
	}

	/**
	 * Assemble the source code from a string (useful for integrations) into a byte array.
	 */
	public static byte[] assemble(PrintWriter pw, String source) throws IOException, AssemblerException {
		AssemblerState.init(source);
		return assemble(pw);
	}
	/**
	 * Assemble the source code from a file into a byte array.
	 */
	public static byte[] assemble(PrintWriter pw, File file) throws IOException, AssemblerException {
		AssemblerState.init(file);
		return assemble(pw);
	}
	private static byte[] assemble(PrintWriter pw) throws IOException, AssemblerException {
		determineLabelLocations();	// Pass #1
		AssemblerState.get().reset();
		assembleFile(pw);			// Pass #2

		return AssemblerState.get().getOutput().toByteArray();
	}

	/**
	 * Perform the first pass of the assembler.  That is, run through and assign
	 * the appropriate memory location for each declared label.
	 */
	protected static void determineLabelLocations() throws IOException, AssemblerException {
		AssemblerState state = AssemblerState.get();
		state.setIdentifyLabels(true);	// gives some leniency when a label hasn't been defined
		while (true) {
			String line = state.getReader().readLine();
			if (line == null) break;
			LineParts parts = LineParserService.parseLine(line);
			if (state.isActive() && parts.getLabel() != null) {
				if (parts.isGlobalLabel()) {
					if (state.containsGlobalVariable(parts.getLabel())) {
						throw new AssemblerException("Unable to redefine global label '" +
								parts.getLabel() + "'.");
					}
					state.addGlobalVariable_PC(parts.getLabel());
					state.nextLocalScope();
				} else {	// Assume a local label
					if (state.containsLocalVariable(parts.getLabel())) {
						throw new AssemblerException("Unable to redefine local label '" +
								parts.getLabel() + "' without a '.reset locals' directive.");
					}
					state.addLocalVariable_PC(parts.getLabel());
				}
			}
			if (isDirective(parts)) {
				processDirective(parts);
			} else if (state.isActive() && parts.getOpcode() != null) {
				state.incrementPC(LineAssemblerService.size(parts));
			}
		}
		state.reset();
	}

	/**
	 * Perform the second pass of the assembler.  This pass actually generates
	 * the byte-level code that is the ultimate output.
	 */
	protected static void assembleFile(PrintWriter pw) throws IOException, AssemblerException {
		AssemblerState state = AssemblerState.get();
		while (true) {
			String line = state.getReader().readLine();
			if (line == null) break;
			LineParts parts = LineParserService.parseLine(line);
			long startPosition = state.getOutput().size();
			if (state.isActive() && parts.isGlobalLabel()) {
				state.nextLocalScope();
			}
			if (isDirective(parts)) {
				processDirective(parts);
			} else if (state.isActive() && parts.getOpcode() != null) {
				state.incrementPC(LineAssemblerService.assemble(parts));
			}
			display(pw, startPosition, line, state);
		}
		state.getReader().close();
	}

	/**
	 * Identify assembler directives.
	 */
	protected static boolean isDirective(LineParts parts) {
		String opcode = parts.getOpcode();
		return opcode != null && (opcode.startsWith(".") || "=".equals(opcode));
	}

	/**
	 * Process assembler directives.  Updates AssemblerState.address.
	 */
	protected static void processDirective(LineParts parts) throws AssemblerException {
		AssemblerState state = AssemblerState.get();
		if (state.isActive()) {
			// While directives are moved out, this makes both words blend together
			String directiveKey = parts.getOpcode();
			if (directiveKey != null) directiveKey = directiveKey.toLowerCase();
			if (directives.containsKey(directiveKey)) {
				Directive directive = directives.get(directiveKey);
				directive.process(parts);
			} else {
				AssemblerException.toss(
						"Unknown assembler directive '%s' on line #%d!",
						parts.getOpcode(), state.getReader().getLineNumber());
			}
		} else {		// Line is not active - we are in a failed .ifdef or .ifndef:
			if (".endif".equals(parts.getOpcode())) {
				state.setActive(true);
			}
		}
	}
	/**
	 * Separate comma-separated values keeping strings intact.
	 * (Couldn't figure out how to do with the the <code>StringTokenizer</code>,
	 * <code>StreamTokenizer</code>, or a regular expression and <code>Pattern</code>.)
	 */
	public static String[] parseCommas(String string) {
		List<String> list = new ArrayList<String>();
		StringBuffer work = new StringBuffer();
		char inQuote = 0;
		for (int i=0; i<string.length(); i++) {
			char ch = string.charAt(i);
			if (inQuote != 0) {
				work.append(ch);
				if (ch == inQuote) inQuote = 0;
			} else if (ch == '\'' || ch == '"') {
				work.append(ch);
				inQuote = ch;
			} else if (ch == ',') {
				list.add(work.toString());
				work.setLength(0);
			} else {
				work.append(ch);
			}
		}
		if (work.length() > 0) list.add(work.toString());
		String[] answer = new String[list.size()];
		return list.toArray(answer);
	}
	
	/**
	 * Display the output.
	 */
	protected static void display(PrintWriter pw, long startPosition, String originalLine, AssemblerState state) {
		long bytesWritten = state.getOutput().size() - startPosition;
		long startingAddress = state.getPC() - bytesWritten;
		pw.printf("%04X: ", startingAddress);
		int maxHexNumbers = 3;
		while (startPosition < state.getOutput().size() && maxHexNumbers > 0) {
			pw.printf("%02X ", state.getOutput().byteAt((int)startPosition));
			startPosition++;
			maxHexNumbers--;
			startingAddress++;
		}
		while (maxHexNumbers-- > 0) pw.printf("   ");
		pw.printf("%4d  %s\n", state.getReader().getLineNumber(), originalLine);
		// If we have more than 3 bytes of data, print them 8 per line...
		while (startPosition < state.getOutput().size()) {
			pw.printf("%04X: ", startingAddress);
			maxHexNumbers = 8;
			while (startPosition < state.getOutput().size() && maxHexNumbers > 0) {
				pw.printf("%02X ", state.getOutput().byteAt((int)startPosition));
				startPosition++;
				maxHexNumbers--;
				startingAddress++;
			}
			pw.printf("\n");
		}
	}
}

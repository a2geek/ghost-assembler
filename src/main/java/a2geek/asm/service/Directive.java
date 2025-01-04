package a2geek.asm.service;

import a2geek.asm.assembler.LineParts;

import java.io.IOException;

/**
 * Common interface for all Assembler directives.
 * This should allow all directives to be defined and discovered
 * automatically and simplify the AssemblerService object.
 * Additionally, the Directive itself will identify components
 * for documentation.
 * 
 * @author Rob
 * @see AssemblerService
 */
public interface Directive {
	/** 
	 * Answer with the specific opcode mnemonic requesting this particular 
	 * directive.  Usually this is a "." directive, but it may be something 
	 * like equals ("=").
	 */
    String getOpcodeMnemonic();
	/**
	 * Process this directive using the given line details, updating
	 * the AssemblerState as needed. 
	 */
    void process(LineParts parts) throws AssemblerException;
	/**
	 * Get details regarding the Directive's documentation.
	 * Expecting most Directive's to pull documentation from a file, an IOException
	 * may be thrown.
	 */
    DirectiveDocumentation getDocumentation() throws IOException;
}

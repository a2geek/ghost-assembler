package a2geek.asm.api.service;

import java.io.IOException;

/**
 * The DirectiveDocumentation contains all pertinent information regarding
 * documentation for a directive.  Each "get" method is documented with
 * additional details.
 * 
 * @author Rob
 */
public class DirectiveDocumentation implements Comparable<DirectiveDocumentation> {
	private final String mnemonic;
	private final String name;
	private final String fileName;
	private final String group;
	private final int groupOrder;
	
	public DirectiveDocumentation(String mnemonic, String name, String fileName) throws IOException {
		this(mnemonic, name, fileName, null, 0);
	}
	public DirectiveDocumentation(String mnemonic, String name, String fileName, String group, int groupOrder) {
		this.mnemonic = mnemonic;
		this.name = name;
		this.fileName = fileName;
		this.group = group;
		this.groupOrder = groupOrder;
	}
	
	/**
	 * This is the actual mnemonic code used for this directive. 
	 */
	public String getMnemonic() {
		return mnemonic;
	}
	/**
	 * This is the name of the directive to be published in generated documentation.
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * Retrieve the help template for this directive.  A null may be returned
	 * to fill in the description with a "TBD" until something can be written.
	 */
	public String getFileName() {
		return this.fileName;
	}
	/**
	 * Some directives are associated in their usage.  The documentation group
	 * allows them to be grouped locally in the generated documentation.
	 * (For example "if" and "else" and "end if" logically fit together.)
	 * This is the mnemonic code for the related documentation, indicating
	 * the primary directive.
	 * If group does not apply, a null should be used.
	 */
	public String getGroup() {
		return this.group;
	}

	/** Sort by mnemonic, unless in a group and then use the group key followed by the mnemonic. */
	private String getSortKey() {
		if (group == null) return mnemonic;
		return String.format("%s:%03d:%s", group, groupOrder, mnemonic);
	}
	/** Natural sorting for the DirectiveDocument is determined by the getSortKey method. */
	@Override
	public int compareTo(DirectiveDocumentation o) {
		return getSortKey().compareTo(o.getSortKey());
	}
}

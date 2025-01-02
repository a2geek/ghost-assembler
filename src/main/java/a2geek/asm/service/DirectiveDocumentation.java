package a2geek.asm.service;

import a2geek.asm.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * The DirectiveDocumentation contains all pertinent information regarding
 * documentation for a directive.  Each "get" method is documented with
 * additional details.
 * 
 * @author Rob
 */
public class DirectiveDocumentation implements Comparable<DirectiveDocumentation> {
	private String mnemonic;
	private String name;
	private String text;
	private String group;
	private int groupOrder;
	
	public DirectiveDocumentation(String mnemonic, String name, InputStream inputStream, String group, int groupOrder) throws IOException {
		this(mnemonic, name, IOUtils.readAsString(inputStream), group, groupOrder);
	}
	public DirectiveDocumentation(String mnemonic, String name, InputStream inputStream) throws IOException {
		this(mnemonic, name, IOUtils.readAsString(inputStream), null, 0);
	}
	public DirectiveDocumentation(String mnemonic, String name, String text, String group, int groupOrder) {
		this.mnemonic = mnemonic;
		this.name = name;
		this.text = text;
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
	 * Retrieve the descriptive text for a directive.  A null may be returned
	 * to fill in the description with a "TBD" until something can be written.
	 * Note that the typical response will just be a 
	 * <code>getClass().getResourceAsStream()</code>.
	 */
	public String getText() {
		return this.text;
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

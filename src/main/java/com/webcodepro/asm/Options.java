package com.webcodepro.asm;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.List;

@Parameters(separators = " =")
public class Options {
	/** Output binary filename. */
	@Parameter(names = "-o", description = "Write output to given file")
	public String outputName;
	
	/** If assembly listing is to be written to a file, this is the filename; else null indicates to not save listing. */
	@Parameter(names = { "-L", "-listing" }, description = "Write assembly listing to given file")
	public String assemblyListingName;
	
	/** Flag to display version number. */
	@Parameter(names = { "-v", "--version" }, description = "Display version of Assembler")
	public boolean showVersion;
	
	/** Flag indicating help has been requested. */
	@Parameter(names = { "-h", "-?", "--help" }, description = "Display help and exit")
	public boolean showHelp;
	
	/** Flag indicating directives should be displayed. */
	@Parameter(names = "--list-directives", description = "List all recognized directives and exit")
	public boolean showDirectives;
	
	/** Flag indicating known CPUs should be displayed. */
	@Parameter(names = "--list-cpus", description = "List all built-in CPU definitions and exit")
	public boolean showCPUs;
	
	/** Flag indicating CPU documentation should be generated. */
	@Parameter(names = "--document-cpus", description = "Generate CPU documentation, use 'ALL' for everything")
	public String documentCPUs;
	
	/** Flag indicating directory to write to. */
	@Parameter(names = "--directory", description = "Write output to this directory (only CPU documentation at this time)")
	public String directory;
	
	@Parameter(description = "Files")
	public List<String> files = new ArrayList<String>();
	
	public String getOutputName(String filename) {
		if (outputName == null) {
			return filename.substring(0, filename.lastIndexOf('.')) + ".bin";
		}
		return outputName;
	}
	
	public boolean isDocumentAllCpus() {
		return "ALL".equals(documentCPUs);
	}
}

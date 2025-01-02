package a2geek.asm;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.ArrayList;
import java.util.List;

@Command(name = "ghost-assembler", description = "An extendable and embeddable Java-based assembler.")
public class Options {
	/** Output binary filename. */
	@Option(names = "-o", description = "Write output to given file")
	public String outputName;
	
	/** If assembly listing is to be written to a file, this is the filename; else null indicates to not save listing. */
	@Option(names = { "-L", "--listing" }, description = "Write assembly listing to given file")
	public String assemblyListingName;
	
	/** Flag to display version number. */
	@Option(names = { "-v", "--version" }, description = "Display version of Assembler")
	public boolean showVersion;
	
	/** Flag indicating help has been requested. */
	@Option(names = { "-h", "-?", "--help" }, description = "Display help and exit")
	public boolean showHelp;
	
	/** Flag indicating directives should be displayed. */
	@Option(names = "--list-directives", description = "List all recognized directives and exit")
	public boolean showDirectives;
	
	/** Flag indicating known CPUs should be displayed. */
	@Option(names = "--list-cpus", description = "List all built-in CPU definitions and exit")
	public boolean showCPUs;
	
	/** Flag indicating CPU documentation should be generated. */
	@Option(names = "--document-cpus", description = "Generate CPU documentation, use 'ALL' for everything")
	public String documentCPUs;
	
	/** Flag indicating directory to write to. */
	@Option(names = "--directory", description = "Write output to this directory (only CPU documentation at this time)")
	public String directory;
	
	@Parameters(description = "Files")
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

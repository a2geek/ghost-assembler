package a2geek.asm;

import a2geek.asm.service.AssemblerException;
import a2geek.asm.service.AssemblerService;
import a2geek.asm.service.DefinitionService;
import a2geek.asm.service.Directive;
import a2geek.asm.site.GenerateCpuDocumentation;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Command-line interface for the Assembler.
 * 
 * @author Rob
 */
public class Main {
	private static Options options = new Options();

	/** Get Assembler version number. */
	public static String getVersion() {
		Package pkg = Main.class.getPackage();
		var version = pkg.getImplementationVersion();
		if (version == null) version = "unknown";
		return version;
	}
	
	/**
	 * A basic command-line parser for the Assembler. 
	 */
	public static void main(String[] args) {
		var options = new Options();
		var cmd = new CommandLine(options);
		try {
			cmd.parseArgs(args);
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			cmd.usage(System.err);
			System.exit(1);
		}
		if (options.showHelp) {
			cmd.usage(System.out);
			System.exit(0);
		}
		if (options.showVersion) {
			System.out.printf("Assembler version %s\n", getVersion());
		}
		if (options.showCPUs) {
			showCPUs();
		}
		if (options.showDirectives) {
			showDirectives();
		}
		if (options.documentCPUs != null) {
			documentCPUs();
		}
		if (options.files == null || options.files.isEmpty()) {
			System.out.printf("Please include a file name to assemble!\n");
			cmd.usage(System.err);
			System.exit(2);
		}
		for (String file : options.files) {
			assemble(file);
		}
	}

	/** Display directive list. */
	private static void showDirectives() {
		System.out.printf("Directives currently available:\n");
		for (Directive d : AssemblerService.getDirectives()) {
			System.out.printf("\t%s\n", d.getOpcodeMnemonic());
		}
		System.out.printf("** END **\n");
		System.exit(0);
	}
	
	private static void showCPUs() {
		try {
			System.out.printf("List of defined CPUs:\n");
			for (String f : DefinitionService.getCpus()) {
				System.out.printf("\t%s\n", f);
			}
			System.out.printf("** END **\n");
			System.exit(0);
		} catch (AssemblerException e) {
			System.out.printf("Unable to load CPU listing.  Error: %s\n", e.getMessage());
			System.exit(4);
		}
	}
	
	private static void documentCPUs() {
		try {
			List<String> cpus;
			if (options.isDocumentAllCpus()) {
				cpus = DefinitionService.getCpus();
			} else {
				cpus = new ArrayList<String>();
				cpus.add(options.documentCPUs);
			}
			GenerateCpuDocumentation.generate(cpus.toArray(new String[0]), options.directory);
			System.exit(0);
		} catch (AssemblerException e) {
			System.err.printf("Unable to generate CPU documentation.\n");
			e.printStackTrace(System.err);
			System.exit(5);
		}
	}
	
	/**
	 * Perform the actual assembly.
	 */
	private static void assemble(String filename) {
		try {
			Date startTime = new Date();
			
			File assemblyFile = new File(filename);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			byte[] code = AssemblerService.assemble(pw, assemblyFile);
			pw.close();
			
			if (options.assemblyListingName != null) {
				String assembledCode = sw.toString();
				writeTextFile(options.assemblyListingName, assembledCode);
			}
			
			String outputName = options.getOutputName(filename);
			FileOutputStream output = new FileOutputStream(outputName);
			output.write(code);
			output.close();
			
			Date endTime = new Date();
			long length = endTime.getTime() - startTime.getTime();
			System.out.printf("Assembled %s in %s seconds.  Binary file written was %d bytes.\n",
					outputName, length/1000, code.length);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Given some text, write that text to given filename.  This method assumes that the text
	 * is already formatted.
	 */
	private static void writeTextFile(String filename, String text) {
		try {
			PrintWriter pw = new PrintWriter(new File(filename));
			pw.println(text);
			pw.close();
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}
}

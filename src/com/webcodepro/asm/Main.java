package com.webcodepro.asm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.webcodepro.asm.service.AssemblerException;
import com.webcodepro.asm.service.AssemblerService;
import com.webcodepro.asm.service.DefinitionService;
import com.webcodepro.asm.service.Directive;
import com.webcodepro.asm.site.GenerateCpuDocumentation;

/**
 * Command-line interface for the Assembler.
 * 
 * @author Rob
 */
public class Main {
	private static String version;
	private static Options options = new Options();
	private static JCommander cmd;
	
	/**
	 * Initialize application.
	 */
	private static void initialize() {
		initializeVersion();
		initializeJCommander();
	}

	/** Set the command-line version information. */
	private static void initializeVersion() {
		Package pkg = Main.class.getPackage();
		version = pkg.getImplementationVersion();
		if (version == null) version = "unknown";
	}
	
	/** Setup JCommander. */
	private static void initializeJCommander() {
		cmd = new JCommander(options);
		cmd.setProgramName("Assembler");
	}
	
	/** Get Assembler version number. */
	public static String getVersion() {
		initializeVersion();
		return version;
	}
	
	/**
	 * A basic command-line parser for the Assembler. 
	 */
	public static void main(String[] args) {
		initialize();
		try {
			cmd.parse(args);
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			cmd.usage();
			System.exit(1);
		}
		if (options.showHelp) {
			cmd.usage();
			System.exit(0);
		}
		if (options.showVersion) {
			System.out.printf("Assembler version %s\n", version);
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
			cmd.usage();
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
			GenerateCpuDocumentation.generate(cpus.toArray(new String[0]), options.directory, cmd);
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
			System.out.printf("Assembled %s in %s seconds.  Binary file written was %d bytes.", 
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

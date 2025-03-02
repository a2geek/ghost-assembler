package a2geek.asm.cli;

import a2geek.asm.api.service.AssemblerService;
import a2geek.asm.api.service.AssemblerState;
import a2geek.asm.api.service.DefinitionService;
import a2geek.asm.api.service.Directive;
import a2geek.asm.api.util.AssemblerException;
import a2geek.asm.api.util.Sources;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Command-line interface for the Assembler.
 * 
 * @author Rob
 */
public class Main {
	private static final Options options = new Options();

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
			System.out.println("Please include a file name to assemble!");
			cmd.usage(System.err);
			System.exit(2);
		}
		for (String file : options.files) {
			assemble(file);
		}
	}

	/** Display directive list. */
	private static void showDirectives() {
		System.out.println("Directives currently available:");
		for (Directive d : AssemblerService.getDirectives()) {
			System.out.printf("\t%s\n", d.getOpcodeMnemonic());
		}
		System.out.println("** END **");
		System.exit(0);
	}
	
	private static void showCPUs() {
		System.out.println("List of defined CPUs:");
		for (String f : DefinitionService.getCpus()) {
			System.out.printf("\t%s\n", f);
		}
		System.out.println("** END **");
		System.exit(0);
	}
	
	private static void documentCPUs() {
		try {
			List<String> cpus;
			if (options.isDocumentAllCpus()) {
				cpus = DefinitionService.getCpus();
			} else {
				cpus = new ArrayList<>();
				cpus.add(options.documentCPUs);
			}
			GenerateCpuDocumentation.generate(cpus.toArray(new String[0]), options.directory);
			System.exit(0);
		} catch (AssemblerException e) {
			System.err.println("Unable to generate CPU documentation.");
			e.printStackTrace(System.err);
			System.exit(5);
		}
	}
	
	/**
	 * Perform the actual assembly.
	 */
	private static void assemble(String filename) {
		AssemblerState state = null;
		try {
			Date startTime = new Date();
			
			File assemblyFile = new File(filename);
			state = AssemblerService.assemble(Sources.get(assemblyFile));
			byte[] code = state.getOutput().toByteArray();

			String outputName = options.getOutputName(filename);
			FileOutputStream output = new FileOutputStream(outputName);
			output.write(code);
			output.close();

			Date endTime = new Date();
			long length = endTime.getTime() - startTime.getTime();
			System.out.printf("Assembled %s in %s seconds.  Binary file written was %d bytes.\n",
					outputName, length/1000, code.length);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			AssemblerState.get().getLog().forEach(log -> {
				switch (log.type()) {
					case ERROR -> System.out.printf("      | %s: %s\n", log.type(), log.message());
					case SOURCE -> System.out.printf("%05d | %s\n", log.lineNumber(), log.message());
				}
			});
			System.exit(1);
		} finally {
			if (options.assemblyListingName != null && state != null) {
				writeListingFile(options.assemblyListingName, state);
			}
		}
	}
	
	/**
	 * Given some text, write that text to given filename.  This method assumes that the text
	 * is already formatted.
	 */
	private static void writeListingFile(String filename, AssemblerState state) {
		try {
			PrintWriter pw = new PrintWriter(filename);
			state.getLog().forEach(log -> {
				switch (log.type()) {
					case SOURCE -> pw.println(log.message());
					case ERROR -> pw.printf("%s: %s\n", log.type(), log.message());
				}
			});
			pw.close();
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}
}

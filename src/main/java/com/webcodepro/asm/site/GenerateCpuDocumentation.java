package com.webcodepro.asm.site;

import com.beust.jcommander.JCommander;
import com.webcodepro.asm.Main;
import com.webcodepro.asm.definition.CpuDefinition;
import com.webcodepro.asm.io.IOUtils;
import com.webcodepro.asm.service.*;
import com.webcodepro.asm.service.DefinitionService.ValidationType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
//import com.webcodepro.xmltemplateengine.OutputDocument;
//import com.webcodepro.xmltemplateengine.OutputException;
//import com.webcodepro.xmltemplateengine.OutputHandler2;
//import com.webcodepro.xmltemplateengine.ThreadState;

/**
 * A command-line tool used to generate site documentation for the maven-generated site.
 * Note that an XML transformation is not sufficient since a CPU definition may inherit from
 * other CPU definitions.  Since the application handles such details, this application was
 * written and takes advantage of the built-in inheritance logic.
 * 
 * @author Rob
 */
public class GenerateCpuDocumentation {
	private static String directory;
	
	/** Generate documentation site. */
	public static void generate(String[] cpus, String directory, JCommander cmd) throws AssemblerException {
		try {
			GenerateCpuDocumentation.directory = directory;
			copy("theme.css");
			
			Map<String,Object> vars = new HashMap<String,Object>();
			vars.put("version", Main.getVersion());
			vars.put("cpus", cpus);
			vars.put("parameters", cmd.getParameters());
			
			List<DirectiveDocumentation> directives = new ArrayList<DirectiveDocumentation>();
			for (Directive directive : AssemblerService.getDirectives()) {
				directives.add(directive.getDocumentation());
			}
			Collections.sort(directives);
			vars.put("directives", directives);

			for (String cpu : cpus) {
				CpuDefinition def = DefinitionService.load(String.format("<%s>", cpu), ValidationType.NONE);
				vars.put("cpu", def);
				vars.put("grid", new CpuGrid(def));
//				process("cpu.html", String.format("%s.html",cpu), vars);
				vars.remove("cpu");
				vars.remove("grid");
			}

//			process("overview.html", vars);
//			process("index.html", vars);
//			process("menu.html", vars);
//			process("usage.html", vars);
//			process("directives.html", vars);
		} catch (IOException e) {
			throw new AssemblerException(e);
//		} catch (OutputException e) {
//			throw new AssemblerException(e);
		}
	}
	
	/** Helper method that generates the same output filename as input filename. */
//	private static void process(String filename, Map<String,Object> vars) throws OutputException, IOException {
//		process(filename, filename, vars);
//	}
	
	/** Given an XHTML template and variables, process through XmlTemplateEngine to build resulting document. */
//	private static void process(String sourceFilename, String targetFilename, Map<String,Object> vars) throws OutputException, IOException {
//		System.out.printf("Generating %s -> '%s' ...\n", sourceFilename, targetFilename);
//		InputStream inputStream = null;
//		Writer writer = null;
//		try {
//			ThreadState.current().functions.put("not", new NotFunction());
//			inputStream = GenerateCpuDocumentation.class.getResourceAsStream(sourceFilename);
//			InputSource inputSource = new InputSource(inputStream);
//			EntityResolver entityResolver = new AsmEntityResolver();
//			OutputDocument doc = parse(inputSource, entityResolver);
//			writer = new FileWriter(getFileLocation(targetFilename));
//			doc.generate(writer, vars);
//		} finally {
//			IOUtils.closeQuietly(writer);
//			IOUtils.closeQuietly(inputStream);
//		}
//	}
	
	/** 
	 * Build XHTML output document.  This routine supersedes that from XmlTemplateEngine to 
	 * handle EntityResolver correctly. 
	 */
//	public static OutputDocument parse(InputSource inputSource, EntityResolver resolver) throws OutputException {
//		try {
//			XMLReader reader = XMLReaderFactory.createXMLReader();
//			if (resolver != null) reader.setEntityResolver(resolver);
//			OutputHandler2 handler2 = new OutputHandler2();
//			reader.setContentHandler(handler2);
//			reader.parse(inputSource);
//			return handler2.getDocument();
//		} catch (Throwable t) {
//			throw new OutputException(t);
//		}
//	}
	
	/** Copy a file into the destination directory. */
	private static void copy(String filename) throws IOException {
		System.out.printf("Copying '%s' ...\n", filename);
		InputStream is = GenerateCpuDocumentation.class.getResourceAsStream(filename);
		FileOutputStream os = new FileOutputStream(getFileLocation(filename));
		IOUtils.copy(is, os);
	}
	
	/** Standardize location where files are pulled from. */
	private static String getFileLocation(String filename) {
		if (directory == null) return filename;
		return String.format("%s%s%s", directory, File.separator, filename);
	}
}

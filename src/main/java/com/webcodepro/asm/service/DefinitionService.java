package com.webcodepro.asm.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.webcodepro.asm.definition.AddressMode;
import com.webcodepro.asm.definition.AddressModeDefinition;
import com.webcodepro.asm.definition.CpuDefinition;
import com.webcodepro.asm.definition.Operation;
import com.webcodepro.asm.io.IOUtils;

/**
 * Provides services to load the CpuDefinition objects.
 * 
 * @author Rob
 */
public class DefinitionService {
	private static final String SCHEMA_LOC = "/schemas/cpu-definition-2.0.xsd";
	private static final String DEFINITION_LOC = "definitions";
	private static List<String> cpus;

	/**
	 * Load a CPU definition from the given filename.
	 */
	public static CpuDefinition load(String filename, ValidationType validation) throws IOException {
		boolean systemDef = filename.startsWith("<");  // located in the definition package
		if (filename.startsWith("\"")) filename = filename.substring(1);
		if (filename.endsWith("\"")) filename = filename.substring(0, filename.length()-1);
		if (filename.startsWith("<")) filename = filename.substring(1);
		if (filename.endsWith(">")) filename = filename.substring(0, filename.length()-1);
		if (!filename.endsWith(".xml")) filename = filename + ".xml";

		InputStream inputStream = null;
		if (systemDef) {
			filename = buildFilename(filename);
			URL url = DefinitionService.class.getResource(filename);
			if (url != null) inputStream = url.openStream();
		} else {
			inputStream = new FileInputStream(filename);
		}
		return load(inputStream, validation);
	}
	/**
	 * Load a CPU definition from the given InputStream.
	 */
	public static CpuDefinition load(InputStream inputStream, ValidationType validation) throws IOException {
		try {
			JAXBContext jc = JAXBContext.newInstance(CpuDefinition.class);
			Unmarshaller u = jc.createUnmarshaller();
			
			if (validation == ValidationType.VALIDATE) {
				SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
				InputStream schemaInputStream = CpuDefinition.class.getResourceAsStream(SCHEMA_LOC);
				Schema schema = sf.newSchema(new StreamSource(schemaInputStream));
				u.setSchema(schema);
			}
			
			JAXBElement<CpuDefinition> doc = u.unmarshal(new StreamSource(inputStream), CpuDefinition.class);
			CpuDefinition def = doc.getValue();
			if (def.getInheritFrom() != null) {
				// Assume we inherit from a system-defined CPU
				CpuDefinition parent = load(String.format("<%s>", def.getInheritFrom()), validation);
				def.inherit(parent);
			}
			return def;
		} catch (JAXBException | AssemblerException | SAXException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
    }
	
	/**
	 * Do some simple validation on the structure and definition of the CPU.
	 */
	public static boolean validate(CpuDefinition cpu, List<String> issues) {
		if (issues == null) issues = new ArrayList<String>();
		// Check RE against the FORMAT given:
		for (AddressMode addressMode : cpu.getAddressModes()) {
			if (addressMode instanceof AddressModeDefinition defn) {
                Pattern pattern = defn.getRegexPattern();
				if (pattern == null) {
					issues.add(String.format("RE pattern (%s) did not compile for '%s'.", 
							defn.getRegex(), defn.getId()));
				}
				Matcher matcher = pattern.matcher(defn.getFormat());
				if (!matcher.matches()) {
					issues.add(String.format("RE pattern (%s) and parameter format (%s) do not match for '%s'.",
							defn.getRegex(), defn.getFormat(), defn.getId()));
				}
			} else {
				issues.add(String.format("The address-mode-reference with id of '%s' does not have a concrete definition", 
						addressMode.getId()));
			}
		}
		// Confirm every Operation has a mnemonic and at least one addressing-mode;
		for (Operation operation : cpu.getOperations()) {
			if (operation.getMnemonic() == null || operation.getMnemonic().isEmpty()) {
				issues.add("Mnemonic is required by all Operations!");
			}
			if (operation.getAddressingModes().isEmpty()) {
				issues.add(String.format("Operation '%s' requires at least one addressing-mode!", 
						operation.getMnemonic()));
			}
		}
		return issues.isEmpty();
	}
	/**
	 * Detect built-in CPU definitions.
	 * If we are NOT part of a JAR file, simply process the directory.
	 * If we ARE part of a JAR file, we need to read the automatically built cpus.txt file (see build.xml). 
	 */
	public static List<String> getCpus() throws AssemblerException {
		if (cpus == null) {
			loadFromFileSystem();
		}
		if (cpus == null) {
			loadFromJarFile();
		}
		return cpus;
	}
	
	private static void loadFromFileSystem() throws AssemblerException {
		// TODO make this dynamic - needs to work from IDE as well as the Boot JAR
		final String[] files = { "MOS6502.xml", "RCVM2.xml", "SWEET16.xml", "WDC65C02.xml" };
		setCpus(files);
	}
	private static void loadFromJarFile() throws AssemblerException {
		InputStream is = DefinitionService.class.getResourceAsStream(buildFilename("cpu.txt"));
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		try {
			while (true) {
				int bytes = is.read(buffer);
				if (bytes == -1) break;
				os.write(buffer, 0, bytes);
			}
		} catch (IOException e) {
			throw new AssemblerException("Unable to load CPU definition from JAR file", e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		}
		String s = os.toString();
		setCpus(s.split(";"));
		
	}
	private static void setCpus(String[] files) {
		cpus = new ArrayList<String>();
		for (String file : files) {
			cpus.add(file.substring(0, file.lastIndexOf(".")));
		}

	}
	
	private static String buildFilename(String filename) {
		return String.format("/%s/%s", DEFINITION_LOC, filename);
	}
	
	public enum ValidationType {
		NONE,
		VALIDATE
	}
}

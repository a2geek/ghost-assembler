package a2geek.asm.cli;

import a2geek.asm.api.definition.CpuDefinition;
import a2geek.asm.api.definition.Operation;
import a2geek.asm.api.definition.OperationAddressing;
import a2geek.asm.api.service.AssemblerException;
import a2geek.asm.api.service.AssemblerService;
import a2geek.asm.api.service.DefinitionService;
import a2geek.asm.api.service.DefinitionService.ValidationType;
import a2geek.asm.api.service.ExpressionService;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.attributes.AttributeResolver;
import io.pebbletemplates.pebble.error.LoaderException;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.*;
import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.pebble.operator.BinaryOperator;
import io.pebbletemplates.pebble.operator.UnaryOperator;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.tokenParser.TokenParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A command-line tool used to generate CPU documentation based on the XML definition.
 * Note that an XML transformation is not sufficient since a CPU definition may inherit from
 * other CPU definitions.  Since the application handles such details, this application was
 * written and takes advantage of the built-in inheritance logic.
 * 
 * @author Rob
 */
public class GenerateCpuDocumentation {
	private static String directory;
	
	/** Generate documentation site. */
	public static void generate(String[] cpus, String directory) throws AssemblerException {
		try {
			if (directory == null || directory.isEmpty()) {
				directory = ".";
			}
			GenerateCpuDocumentation.directory = directory;
			for (String cpu : cpus) {
				System.out.println(cpu);
				CpuDefinition def = DefinitionService.load(String.format("<%s>", cpu), ValidationType.NONE);
				process(def);
			}
			generateDirective();
		} catch (IOException e) {
			throw new AssemblerException(e);
		}
	}

	/** Generate a Markdown document describing the CPU. */
	private static void process(CpuDefinition cpu) throws IOException {
		PebbleEngine engine = new PebbleEngine.Builder()
				.loader(new CustomClasspathLoader())
				.extension(new CustomExtension())
				.build();
		PebbleTemplate template = engine.getTemplate("/templates/cpu.peb");

		Map<String,Object> context = new HashMap<>();
		context.put("cpu", cpu);
		context.put("grid", buildGrid(cpu));

		StringWriter sw = new StringWriter();
		template.evaluate(sw, context);

		Path path = Path.of(directory, String.format("%s.md", cpu.getName()));
		Files.writeString(path, sw.toString());
	}

	private record GridDetail(Operation op, OperationAddressing opAddr) {}
	private static GridDetail[] buildGrid(CpuDefinition cpu) {
		GridDetail[] ops = new GridDetail[256];
		for (var op : cpu.getOperations()) {
			// NOTE: Doesn't do justice to CPUs like SWEET16 which embed a register in the opcode itself
			for (var opAddr : op.getAddressingModes()) {
                try {
                    var value = ExpressionService.evaluate(opAddr.getOpcode());
					if (value instanceof Number number) {
						int i = number.intValue();
						if (i < 0 || i > 255) {
							System.err.printf("CPU: %s - opcode out of range (%02x)\n", cpu.getName(), i);
						} else if (ops[i] != null) {
							System.err.printf("CPU: %s - opcode value already used (%02x)\n", cpu.getName(), i);
						} else {
							ops[i] = new GridDetail(op, opAddr);
						}
					}
                } catch (AssemblerException e) {
                    System.err.printf("CPU: %s - unable to evaluate opcode value ('%s') - %s\n",
							cpu.getName(), opAddr.getOpcode(), e.getMessage());
                }
			}
		}
		return ops;
	}

	private static void generateDirective() throws IOException {
		PebbleEngine engine = new PebbleEngine.Builder()
				.loader(new CustomClasspathLoader())
				.extension(new CustomExtension())
				.strictVariables(true)
				.build();
		PebbleTemplate template = engine.getTemplate("/templates/directives.peb");

		Map<String,Object> context = new HashMap<>();
		context.put("directives", AssemblerService.directives.values());

		StringWriter sw = new StringWriter();
		template.evaluate(sw, context);

		Path path = Path.of(directory, "directives.md");
		Files.writeString(path, sw.toString());
	}

	/** The built-in Pebble "ClasspathLoader" does not find the templates when developing in the IDE. This fixes it. */
	private static class CustomClasspathLoader implements Loader<String> {
		@Override
		public Reader getReader(String cacheKey) {
			var is = getClass().getResourceAsStream(cacheKey);
			if (is == null) {
				throw new LoaderException(null, String.format("Unable to find template: '%s'", cacheKey));
			}
			return new InputStreamReader(is);
		}

		@Override
		public void setCharset(java.lang.String charset) {

		}

		@Override
		public void setPrefix(java.lang.String prefix) {

		}

		@Override
		public void setSuffix(java.lang.String suffix) {

		}

		@Override
		public java.lang.String resolveRelativePath(String relativePath, String anchorPath) {
			Path relative = Path.of(relativePath);
			Path anchor = Path.of(anchorPath);
			return anchor.resolveSibling(relative).toString();
		}

		@Override
		public String createCacheKey(String templateName) {
			return templateName;
		}

		@Override
		public boolean resourceExists(String templateName) {
			return false;
		}
	}

	private static class CustomExtension implements Extension {
		@Override
		public Map<String, Filter> getFilters() {
			return Map.of(
				"fixText", new FixTextFilter(),
				"hexDigit", new HexDigitFilter()
			);
		}

		@Override
		public Map<String, Test> getTests() {
			return Map.of();
		}

		@Override
		public Map<String, Function> getFunctions() {
			return Map.of();
		}

		@Override
		public List<TokenParser> getTokenParsers() {
			return List.of();
		}

		@Override
		public List<BinaryOperator> getBinaryOperators() {
			return List.of();
		}

		@Override
		public List<UnaryOperator> getUnaryOperators() {
			return List.of();
		}

		@Override
		public Map<String, Object> getGlobalVariables() {
			return Map.of();
		}

		@Override
		public List<NodeVisitorFactory> getNodeVisitors() {
			return List.of();
		}

		@Override
		public List<AttributeResolver> getAttributeResolver() {
			return List.of();
		}
	}

	/** Ensure that this chunk of text is just one string without line breaks and no extra whitespace. */
	private static class FixTextFilter implements Filter {
		private static final String NEWLINES_ARG = "newlines";
		@Override
		public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
			if (input == null) return "";
			if (check(args.get(NEWLINES_ARG), "yes", "keep", "preserve")) {
				return input.toString().lines().reduce("", (a, b) -> a + "\n" + b.trim());
			}
			else {
				return input.toString().lines().reduce("", (a, b) -> a + b.trim());
			}
		}

		private boolean check(Object arg, String... matches) {
			if (arg instanceof String s) {
				for (var match : matches) {
					if (match.equalsIgnoreCase(s)) return true;
				}
			}
			return false;
		}

		@Override
		public List<String> getArgumentNames() {
			return List.of(NEWLINES_ARG);
		}
	}

	/** Format a number as a single hex digit. */
	private static class HexDigitFilter implements Filter {
		@Override
		public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
			if (input instanceof Number number) {
				return String.format("%x", (number.intValue() & 0xf));
			} else if (input instanceof String string) {
				try {
					int num = Integer.parseInt(string, 10);
					return String.format("%x", (num & 0xf));
				} catch (NumberFormatException e) {
					return string;	// not a number, just pass it through
				}
			}
			return input;
		}

		@Override
		public List<String> getArgumentNames() {
			return List.of();
		}
	}
}

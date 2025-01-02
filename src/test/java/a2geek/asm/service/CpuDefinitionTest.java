package a2geek.asm.service;

import a2geek.asm.definition.CpuDefinition;
import a2geek.asm.service.DefinitionService.ValidationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test all defined CPU definitions against the schema
 * and validate against the logical validations supplied
 * by the DefinitionService.
 * 
 * @author Rob
 */
public class CpuDefinitionTest {
	/** For every known CPU, build a test case for validation. */
	public static List<String> testCases() throws AssemblerException {
		return DefinitionService.getCpus();
	}
	
	/** For a specific CPU, load with XML validation and then run logical validation on result. */
	@ParameterizedTest
	@MethodSource("testCases")
	public void validateCpu(final String cpuName) throws IOException {
		CpuDefinition cpu = DefinitionService.load(String.format("<%s>", cpuName), ValidationType.VALIDATE);
		List<String> issues = new ArrayList<String>();
		DefinitionService.validate(cpu, issues);
		if (!issues.isEmpty()) {
			System.err.printf("CPU definition %s issues found --\n", cpuName);
			for (String issue : issues) {
				System.err.printf("* %s\n", issue);
			}
			System.err.printf("** END %s **\n", cpuName);
		}
		Assertions.assertTrue(issues.isEmpty());
	}
}

package com.webcodepro.asm.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.webcodepro.asm.definition.CpuDefinition;
import com.webcodepro.asm.service.DefinitionService.ValidationType;

/**
 * Test all defined CPU definitions against the schema
 * and validate against the logical validations supplied
 * by the DefinitionService.
 * 
 * @author Rob
 */
@RunWith(Parameterized.class)
public class CpuDefinitionTest {
	private String cpuName;
	
	public CpuDefinitionTest(String cpuName) {
		this.cpuName = cpuName;
	}
	
	/** For every known CPU, build a test case for validation. */
	@Parameters
	public static Collection<String[]> buildTestCases() throws AssemblerException {
		List<String[]> list = new ArrayList<String[]>();
		for (String cpu : DefinitionService.getCpus()) {
			list.add(new String[] { cpu });
		}
		return list;
	}
	
	/** For a specific CPU, load with XML validation and then run logical validation on result. */
	@Test
	public void validateCpu() throws IOException {
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
		Assert.assertTrue(issues.isEmpty());
	}
}

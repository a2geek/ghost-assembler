package com.webcodepro.asm.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.webcodepro.asm.io.IOUtils;
import com.webcodepro.junit.AsmAssert;
import com.webcodepro.junit.TestDetailLoader;
import com.webcodepro.junit.TestDetailLoader.TestDetail;

/**
 * Discovers and executes all 'TestDetail-*.asm' files.
 * Note that this test expects to be run in a file system and not from a JAR file.
 * 
 * @author Rob
 */
@RunWith(Parameterized.class)
public class TestDetailRunner {
	private String file;
	
	public TestDetailRunner(String file) {
		this.file = file;
	}
	
	/** For every TestDetail file found, build a test case for validation. */
	@Parameters
	public static Collection<String[]> buildTestCases() throws AssemblerException {
		String dirname = AsmAssert.location("");
		File dir = new File(dirname);
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches("TestDetail-.*\\.asm");
			}
		});
		
		List<String[]> list = new ArrayList<String[]>();
		for (File file : files) {
			list.add(new String[] { file.getName() });
		}
		return list;
	}

	/** For a specific TestDetail, load, assemble and validate result. */
	@Test
	public void validateTestDetail() throws IOException, AssemblerException {
		TestDetail detail = TestDetailLoader.load(this.file);
		boolean success = false;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			byte[] code = AssemblerService.assemble(pw, detail.source);
			AsmAssert.assertEquals(detail.expected, code);
			success = true;
		} finally {
			IOUtils.closeQuietly(pw);
			if (!success) {
				System.out.println("----------------- " + this.file + " ----------------------");
				System.out.print(sw.toString());
				System.out.println("---------------------------------------------------------");
			}
		}
	}
}

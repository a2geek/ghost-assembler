package com.webcodepro.asm.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.webcodepro.asm.io.IOUtils;
import com.webcodepro.junit.AsmAssert;
import com.webcodepro.junit.TestDetailLoader;
import com.webcodepro.junit.TestDetailLoader.TestDetail;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Discovers and executes all 'TestDetail-*.asm' files.
 * Note that this test expects to be run in a file system and not from a JAR file.
 * 
 * @author Rob
 */
public class TestDetailRunner {
	/** For every TestDetail file found, build a test case for validation. */
	public static List<String> testCases() throws AssemblerException {
		String dirname = AsmAssert.location("");
		File dir = new File(dirname);
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches("TestDetail-.*\\.asm");
			}
		});
		
		List<String> list = new ArrayList<>();
		for (File file : files) {
			list.add(file.getName());
		}
		return list;
	}

	/** For a specific TestDetail, load, assemble and validate result. */
	@ParameterizedTest
	@MethodSource("testCases")
	public void validateTestDetail(final String fileName) throws IOException, AssemblerException {
		TestDetail detail = TestDetailLoader.load(fileName);
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
				System.out.println("----------------- " + fileName + " ----------------------");
				System.out.print(sw.toString());
				System.out.println("---------------------------------------------------------");
			}
		}
	}
}

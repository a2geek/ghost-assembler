package a2geek.asm.service;

import a2geek.asm.io.IOUtils;
import a2geek.junit.AsmAssert;
import a2geek.junit.TestDetailLoader;
import a2geek.junit.TestDetailLoader.TestDetail;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Discovers and executes all 'TestDetail-*.asm' files.
 * Note that this test expects to be run in a file system and not from a JAR file.
 * 
 * @author Rob
 */
@Disabled("preserving just in case concept is useful")
public class TestDetailRunner {
	/** For every TestDetail file found, build a test case for validation. */
	public static List<String> testCases() {
		String dirname = AsmAssert.location("");
		File dir = new File(dirname);
		File[] files = dir.listFiles((dir1, name) -> name.matches("TestDetail-.*\\.asm"));
		
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
				System.out.print(sw);
				System.out.println("---------------------------------------------------------");
			}
		}
	}
}

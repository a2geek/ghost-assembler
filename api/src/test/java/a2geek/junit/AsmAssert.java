package a2geek.junit;

import a2geek.asm.api.service.AssemblerService;
import a2geek.asm.api.service.AssemblerState;
import a2geek.asm.api.util.AssemblerException;
import a2geek.asm.api.util.Sources;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provides additional JUnit TestCase assert methods.
 * 
 * @author Rob
 */
public abstract class AsmAssert {
	/** Compare two byte arrays. */
	public static void assertEquals(byte[] expected, byte[] actual) {
		if ((expected == null && actual != null) || (expected != null && actual == null)) Assertions.fail();
		if (expected.length != actual.length) {
			Assertions.fail("Resulting lengths did not match!  " + expected.length + " <> " + actual.length);
		}
		for (int i=0; i<expected.length; i++) {
			Assertions.assertEquals(expected[i], actual[i]);
		}
	}
	/** Compare two long arrays. */
	public static void assertEquals(long[] expected, long[] actual) {
		if ((expected == null && actual != null) || (expected != null && actual == null)) Assertions.fail();
		if (expected.length != actual.length) Assertions.fail("Resulting lengths did not match!");
		for (int i=0; i<expected.length; i++) {
			Assertions.assertEquals(expected[i], actual[i]);
		}
	}
	/** Compare two String arrays. */
	public static void assertEquals(String[] expected, String[] actual) {
		if ((expected == null && actual != null) || (expected != null && actual == null)) Assertions.fail();
		if (expected.length != actual.length) Assertions.fail("Resulting lengths did not match!");
		for (int i=0; i<expected.length; i++) {
			Assertions.assertEquals(expected[i], actual[i]);
		}
	}
	
	/** Place file in working directory. */
	public static String location(String filename) {
		return "src/test/resources/" + filename;
	}
	
	/**
	 * Assemble and test the resulting code.
	 * IF an error occurs, we dump out the code and the filename.
	 */
	public static void assemble(String filename, byte[] expected) throws IOException, AssemblerException {
		boolean success = false;
		AssemblerState state = null;
		try {
			state = AssemblerService.assemble(Sources.get(new File(location(filename))));
			assertEquals(expected, state.getOutput().toByteArray());
			success = true;
		} finally {
			if (!success) {
				System.out.println("----------------- " + filename + " ----------------------");
				if (state == null) {
					System.out.println(Files.readString(Path.of(location(filename))));
				}
				else {
					state.getLog().forEach(System.out::println);
				}
				System.out.println("---------------------------------------------------------");
			}
		}
	}
}

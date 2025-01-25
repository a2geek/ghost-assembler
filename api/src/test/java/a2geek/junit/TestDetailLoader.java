package a2geek.junit;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class loads test cases that are stored with the expected results.  The file is separated by a vertical 
 * pipe ("|") character.  On the left are the expected bytes and on the right is the source code.  Generally,
 * the format works best (for human consumption) if the bytes are aligned with the code that generate them,
 * but there is no enforcement of this assumption.
 * 
 * @author Rob
 */
public abstract class TestDetailLoader {
	/**
	 * Load test case from the given file name.
	 */
	public static TestDetail load(String name) throws IOException {
		String filename = AsmAssert.location(name);
		try (
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
		) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			StringBuilder sourceCode = new StringBuilder();
			splitDetails(bufferedReader, outputStream, sourceCode);
			TestDetail detail = new TestDetail();
			detail.expected = outputStream.toByteArray();
			detail.source = sourceCode.toString();
			return detail;
		}
	}
	/** Read through the input file and parse it into a series of bytes and the associated source code. */
	private static void splitDetails(BufferedReader reader, ByteArrayOutputStream os, StringBuilder code) throws IOException {
		while (true) {
			String line = reader.readLine();
			if (line == null) break;
			boolean split = false;
			StringBuilder sb = new StringBuilder();
			for (char ch : line.toCharArray()) {
				if (!split) {
					if (ch == '|') {
						if (!sb.isEmpty()) throw new RuntimeException("Expecting 'sb' to be empty");
						split = true;
						continue;
					}
					if (!Character.isWhitespace(ch)) {
						sb.append(ch);
					}
					if (sb.length() == 2) {
						int b = Integer.parseInt(sb.toString(), 16);
						os.write(b);
						sb.setLength(0);
					}
				} else {
					code.append(ch);
				}
			}
			code.append("\n");
		}
	}

	/** A simple class to encapsulate a tests details. */
	public static class TestDetail {
		public byte[] expected;
		public String source;
	}
}

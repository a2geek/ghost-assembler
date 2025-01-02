package a2geek.asm.io;

import java.io.*;

/** Common IO utility methods. */
public abstract class IOUtils {
	/** Close any Closeable interface without throwing any type of exception. */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/** Read an InputStream into a String. Closes InputStream. */
	public static String readAsString(InputStream inputStream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		copy(inputStream, outputStream);
		return outputStream.toString();
	}
	
	/** Copy an InputStream to an OutputStream.  Closes both InputStream and OutputStream. */
	public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
		try {
			byte[] buffer = new byte[2048];
			while (true) {
				int bytesRead = inputStream.read(buffer);
				if (bytesRead == -1) break;
				outputStream.write(buffer, 0, bytesRead);
			}
		} finally {
			closeQuietly(inputStream);
			closeQuietly(outputStream);
		}
	}
}

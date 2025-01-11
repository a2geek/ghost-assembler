package a2geek.asm.api.io;

import java.io.ByteArrayOutputStream;

/**
 * Extend the basic Java ByteArrayOutputStream implementation to add
 * a few useful convenience methods.
 * 
 * @author Rob
 */
public class AssemblerByteArrayOutputStream extends ByteArrayOutputStream {
	public byte byteAt(int i) {
		return this.buf[i];
	}
}

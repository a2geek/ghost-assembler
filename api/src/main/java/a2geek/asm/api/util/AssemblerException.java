package a2geek.asm.api.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Serial;

/**
 * Assembler exception.
 * 
 * @author Rob
 */
public class AssemblerException extends Exception {
	@Serial
    private static final long serialVersionUID = 1070715884398627168L;

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
     */
    public AssemblerException() {
    	super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
    public AssemblerException(String message) {
    	super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public AssemblerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public AssemblerException(Throwable cause) {
        super(cause);
    }

    /**
     * Helper constructor for a formatted message in the AssemblerException.
     */
    public AssemblerException(String fmt, Object... args) {
        super(String.format(fmt, args));
    }
}

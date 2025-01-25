package a2geek.asm.api.service.directive;

import a2geek.asm.api.directive.AbstractDataDirective;
import a2geek.asm.api.service.AssemblerState;
import a2geek.asm.api.util.AssemblerException;
import a2geek.asm.api.util.Sources;
import a2geek.junit.AsmAssert;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test the common data generation routine.
 * 
 * @author Rob
 */
public class AbstractDataDirectiveTest {
	/**
	 * Test the string handling of process expression.
	 */
	@Test
	public void testProcessExpression_String() throws AssemblerException, IOException {
		// Without high bit
		AssemblerState.init(Sources.get("; empty"));
		AbstractDataDirective.processExpression("\"Hello\"", 0x00, false);
		AsmAssert.assertEquals(new byte[] { 'H', 'e', 'l', 'l', 'o' }, AssemblerState.get().getOutput().toByteArray());
		// With high bit
		AssemblerState.init(Sources.get("; empty"));
		AbstractDataDirective.processExpression("\"Hello\"", 0x80, false);
		byte h = (byte)0x80;
		AsmAssert.assertEquals(new byte[] { (byte)('H'|h), (byte)('e'|h), (byte)('l'|h), (byte)('l'|h), (byte)('o'|h) }, 
					 AssemblerState.get().getOutput().toByteArray());
		// With length
		AssemblerState.init(Sources.get("; empty"));
		AbstractDataDirective.processExpression("\"Hello\"", 0x00, true);
		AsmAssert.assertEquals(new byte[] { 5, 'H', 'e', 'l', 'l', 'o' }, AssemblerState.get().getOutput().toByteArray());
	}
}

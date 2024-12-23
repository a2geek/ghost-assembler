package com.webcodepro.asm.site;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

/**
 * Lookup required resources locally.  The W3C now blocks applications from dynamically 
 * loading DTDs from their site in real-time.  This means an application needs to get a copy
 * of the DTD and hand it off to SAX as SAX requires them.
 * 
 * @author Rob
 */
public class AsmEntityResolver implements EntityResolver2 {
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		return getInputSource(systemId);
	}

	@Override
	public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
		return null;
	}

	@Override
	public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
		return getInputSource(systemId);
	}
	
	private InputSource getInputSource(String path) throws IOException {
		String filename = path.substring(path.lastIndexOf("/")+1);
		InputStream is = getClass().getResourceAsStream(String.format("/META-INF/w3c/%s", filename));
		if (is == null) {
			throw new IOException(String.format(
					"Unable to locate filename of '%s' from path of '%s'", 
					filename, path));
		}
		return new InputSource(is);
	}
}

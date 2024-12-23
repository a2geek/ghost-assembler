package com.webcodepro.asm.site;

import java.util.List;

import com.webcodepro.xmltemplateengine.function.Function;

/**
 * Handle the "not(...)" function in templates.
 * 
 * @author Rob
 */
public class NotFunction implements Function {
	@Override
	public Object execute(List<Object> arguments) {
		if (arguments == null || arguments.size() != 1) {
			throw new RuntimeException("not function expects 1 argument");
		}
		Object o = arguments.get(0);
		if (o instanceof Boolean) {
			return Boolean.TRUE.equals(o) ? Boolean.FALSE : Boolean.TRUE;
		}
		throw new RuntimeException("not function requires a boolean argument");
	}

}

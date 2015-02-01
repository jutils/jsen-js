package com.jsen.javascript.injectors;

import javax.script.Bindings;
import javax.script.ScriptContext;

import com.jsen.core.ScriptContextInjector;
import com.jsen.javascript.url.URL;

/**
 * Implementation of the injector which adds the URL object into the script context.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class URLInjector extends ScriptContextInjector {
	public URLInjector() {
		super(ALL_SCRIPT_ENGINE_FACTORIES);
	}
	
	@Override
	public boolean inject(ScriptContext context) {
		URL url = new URL();
		
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("URL", url);
		
		return true;
	}
}

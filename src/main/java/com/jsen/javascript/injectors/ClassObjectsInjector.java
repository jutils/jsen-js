/**
 * ClassObjectsInjector.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.jsen.javascript.injectors;

import java.util.ArrayList;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;

import com.jsen.annotation.ScriptAnnotation;
import com.jsen.javascript.JavaScriptInjector;
import com.jsen.javascript.java.ClassScriptable;
import com.jsen.url.URL;
import com.jsen.url.URLUtils;

/**
 * Injector that injects script visible class instances into engine scope.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ClassObjectsInjector extends JavaScriptInjector {
	
	@Override
	public boolean inject(ScriptContext context) {
		List<Class<?>> classesToImport = new ArrayList<Class<?>>();
		
		// FIXME: Use automatic script classes resolving by overriding class loader, or something similar...
		classesToImport.add(URL.class);
		classesToImport.add(URLUtils.class);
		
		for (Class<?> clazz : classesToImport) {
			injectClass(context, clazz);
		}
		
		return true;
	}
	
	private void injectClass(ScriptContext context, Class<?> clazz) {
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		String name = ScriptAnnotation.extractClassName(clazz);
		bindings.put(name, new ClassScriptable(clazz));
	}
}

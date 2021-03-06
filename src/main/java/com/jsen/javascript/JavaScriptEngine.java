/**
 * JavaScriptEngine.java
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

package com.jsen.javascript;

import java.io.Reader;
import java.io.StringReader;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Wrapper;

import com.jsen.core.AbstractScriptEngineFactory;
import com.jsen.core.GlobalObjectScriptEngine;
import com.jsen.core.GlobalObjectScriptSettings;
import com.jsen.core.annotation.ScriptAnnotationClassMembersResolverFactory;
import com.jsen.core.reflect.ClassMembersResolverFactory;
import com.jsen.core.reflect.DefaultShutter;
import com.jsen.javascript.java.ObjectScriptable;
import com.jsen.javascript.java.ObjectTopLevel;

/**
 * JavaScript engine for the browser. It implements the Window object into 
 * top level scope and wraps this scope by ScriptContextScriptable above 
 * which runs the scripts.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class JavaScriptEngine<GlobalObject> extends GlobalObjectScriptEngine<GlobalObject> implements Invocable, Compilable {

	public static final String JAVASCRIPT_LANGUAGE = "text/javascript";
	
	/*static {
		ContextFactory globalFactory = new JavaScriptContextFactory();
		ContextFactory.initGlobal(globalFactory);
	}*/

	protected ContextFactory contextFactory;
	protected TopLevel topLevel;
	protected Scriptable runtimeScope;
	
	/**
	 * Constructs window JavaScript engine for the given settings and that was constructed using passed factory.
	 * 
	 * @param factory Script engine factory that created this browser engine.
	 * @param scriptSettings Script settings that might be used for initialization of this script engine.
	 */
	public JavaScriptEngine(AbstractScriptEngineFactory factory, GlobalObjectScriptSettings<GlobalObject> scriptSettings) {
		this(factory, scriptSettings, null);
	}
	
	/**
	 * Constructs window JavaScript engine for the given settings and that was constructed using passed factory.
	 * 
	 * @param factory Script engine factory that created this browser engine.
	 * @param scriptSettings Script settings that might be used for initialization of this script engine.
	 * @param contextFactory Context factory to be used for this script engine.
	 */
	public JavaScriptEngine(AbstractScriptEngineFactory factory, GlobalObjectScriptSettings<GlobalObject> scriptSettings, ContextFactory contextFactory) {
		super(factory, scriptSettings, (scriptSettings != null)? scriptSettings.getGlobalObject() : null);
		
		this.contextFactory = (contextFactory != null)? contextFactory : new JavaScriptContextFactory(this);

		this.topLevel = initializeTopLevel();
	}
		
	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		Object ret = null;

		Context cx = enterContext();
		try {
			Scriptable executionScope = getExecutionScope(context);
			String filename = getFilenameFromReader(reader);
			ret = cx.evaluateReader(executionScope, reader, filename, 1,  null);
		} catch (Exception ex) {
			throwWrappedScriptException(ex);
		} finally {
			exitContext();
		}

		return unwrap(ret);
	}

	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		return eval(new StringReader(script) , context);
	}

	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}

	/**
	 * Enters new context.
	 * 
	 * @return New entered context.
	 */
	public Context enterContext() {
		return contextFactory.enterContext();
	}
	
	/**
	 * Exits opened context for this thread.
	 */
	public void exitContext() {
		Context.exit();
	}
	
	@Override
	public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
		//throw new UnsupportedOperationException("getInterface() is not implemented yet!");
		return invoke(thiz, name, args);
	}

	@Override
	public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
		//throw new UnsupportedOperationException("getInterface() is not implemented yet!");
		return invoke(null, name, args);
	}

	@Override
	public <T> T getInterface(Class<T> clasz) {
		throw new UnsupportedOperationException("getInterface() is not implemented yet!");
	}

	@Override
	public <T> T getInterface(Object thiz, Class<T> clasz) {
		throw new UnsupportedOperationException("getInterface() is not implemented yet!");
	}
	
	@Override
	public CompiledScript compile(String script) throws ScriptException {
		return compile(new StringReader(script));
	}

	@Override
	public CompiledScript compile(Reader script) throws ScriptException {
		CompiledScript compiledScript = null;
		
		Context cx = enterContext();

		try {
			String filename = getFilenameFromReader(script);
			Script rhinoScript = cx.compileReader(script, filename, 1, null);
			compiledScript = new CompiledJavaScript(this, rhinoScript);
		} catch (Exception e) {
			throwWrappedScriptException(e);
		} finally {
			exitContext();
		}
		
		return compiledScript;
	}
	
	/**
	 * Wraps given exception and throws it as ScriptException.
	 * 
	 * @param ex Exception to be wrapped.
	 * @throws ScriptException Thrown always by this method.
	 */
	public static void throwWrappedScriptException(Exception ex) throws ScriptException {
		if ( ex instanceof RhinoException) {
			RhinoException rhinoException = (RhinoException)ex;
			int line = rhinoException.lineNumber();
			int column = rhinoException.columnNumber();
			
			String message;
			if (ex instanceof JavaScriptException) {
				message = String.valueOf(((JavaScriptException)ex).getValue());
			} else {
				message = ex.toString();
			}
			
			ScriptException scriptException = new ScriptException(message, rhinoException.sourceName(), line, column);
			scriptException.initCause(ex);
			throw scriptException;
		} else {
			throw new ScriptException(ex);
		} 
	}
	
	/**
	 * Converts JavaScript object to Java object, e.g. HostedJavaObject into wrapped Java object.
	 * 
	 * @param jsObj JavaScript object to be converted.
	 * @return Converted Java object.
	 */
	public static Object jsToJava(Object jsObj) {
		if (jsObj instanceof Wrapper) {
			Wrapper njb = (Wrapper) jsObj;

			if (njb instanceof NativeJavaClass) {
				return njb;
			}

			Object obj = njb.unwrap();
			if (obj instanceof Number || obj instanceof String ||
				obj instanceof Boolean || obj instanceof Character) {
				return njb;
			} else {
				return obj;
			}
		} else {
			return jsObj;
		}
	}
	
	/**
	 * Converts Java object into JavaScript object.
	 * 
	 * @param object Object to be converted.
	 * @param scope Scope to be used as parent scope.
	 * @return New converted JavaScript object.
	 */
	public static Object javaToJS(Object object, Scriptable scope) {
		return Context.javaToJS(object, scope);
	}
	
	/**
	 * Returns top level scope for the passed scope.
	 * 
	 * @param scope Scope for which should be returned the top level scope.
	 * @return Top level scope for the passed scope.
	 */
	public static ObjectTopLevel getObjectTopLevel(Scriptable scope) {
		Scriptable parentScope = scope;
		while ((parentScope = scope.getParentScope()) != null) {
			scope = parentScope;
		}

		do  {
			if (scope instanceof ObjectTopLevel) {
				return (ObjectTopLevel)scope;
			}
		} while ((scope = scope.getPrototype()) != null);
		
		return null;
	}
	
	/**
	 * Converts array of Java objects into array of JavaScript objects.
	 * 
	 * @param args Arguments to be converted.
	 * @param scope Top scope object
	 * @return Array of converted JavaScript objects.
	 */
	public static Object[] javaToJS(Object[] args, Scriptable scope) {
		Object[] wrapped = new Object[args.length];
		
		for (int i = 0; i < wrapped.length; i++) {
			wrapped[i] = javaToJS(args[i], scope);
		}
		
		return wrapped;
	}
	
	@Override
	protected ClassMembersResolverFactory initializeClassMembersResolverFactory() {
		DefaultShutter explicitGrantShutter = new DefaultShutter();

		ClassMembersResolverFactory factory = new ScriptAnnotationClassMembersResolverFactory(this, explicitGrantShutter);
		return factory;
	}
	
	/**
	 * Initializes global top level scope.
	 * 
	 * @return New top level scope.
	 */
	protected TopLevel initializeTopLevel() {
		@SuppressWarnings("unchecked")
		Object object = (scriptSettings != null)? ((GlobalObjectScriptSettings<GlobalObject>)scriptSettings).getGlobalObject() : null;
		
		if (object == null) {
			TopLevel topLevel = new TopLevel();
			
			Context cx = enterContext();
			try {
				cx.initStandardObjects(topLevel, true);
			} finally {
				exitContext();
			}
			
			return topLevel;
		}

		return new ObjectTopLevel(object, this);
	}
	
	/**
	 * Returns scope for running the scripts.
	 * 
	 * @param context Script context to be included into top level scope.
	 * @return New scope constructed from the top level scope and wrapped script context scope.
	 */
	protected Scriptable getExecutionScope(ScriptContext context) {		
		if (runtimeScope == null) {
			runtimeScope = new ScriptContextScriptable(context);
			
			runtimeScope.setPrototype(topLevel);
			runtimeScope.setParentScope(null);
		}
		
		return runtimeScope;
	}
	
	/**
	 * Unwraps passed value from JavaScript wrapper interface.
	 * 
	 * @param value Value to be unwrapped.
	 * @return Unwrapped value.
	 */
	protected Object unwrap(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof Wrapper) {
			value = ((Wrapper)value).unwrap();
		}

		return value;
	}

	private Object invoke(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
		Object ret = null;
		Context cx = enterContext();
		try {
			Scriptable executionScope = getExecutionScope(context);
			Scriptable functionScope = null;
			
			if (thiz == null) {
				functionScope = executionScope;
			} else {
				if (!(thiz instanceof Scriptable)) {
					thiz = Context.toObject(thiz, topLevel);
				}
				functionScope = (Scriptable) thiz;
			}

			Function function = null;
			
			if (name != null && !name.isEmpty()) {
				Object objectProperty = ObjectScriptable.getProperty(functionScope, name);
				if (!(objectProperty instanceof Function)) {
					throw new NoSuchMethodException("Function not found!");
				}

				function = (Function) objectProperty;
			} else if (thiz instanceof Function) {
				function = (Function)thiz;
			} else {
				throw new NoSuchMethodException("Passed function name is empty and passed thiz object is not function!");
			}
	 
			Scriptable parentScope = function.getParentScope();
			if (parentScope == null) {
				parentScope = functionScope;
			}
			
			Object[] callArgs = javaToJS(args, topLevel);
			ret = function.call(cx, parentScope, functionScope, callArgs);
		} catch (Exception ex) {
			throwWrappedScriptException(ex);
		} finally {
			exitContext();
		}
		
		return unwrap(ret);
	}
	
	private String getFilenameFromReader(Reader reader) {
		String filename = "<inline script>";
		/*if (reader instanceof ResourceReader) {
			ResourceReader resourceScript = (ResourceReader)reader;
			URL url = resourceScript.getURL();
			
			if (url != null) {
				filename = url.toExternalForm();
			}
		}*/
		
		return filename;
	}

}

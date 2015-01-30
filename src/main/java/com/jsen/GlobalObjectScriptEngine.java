/**
 * GlobalObjectScriptEngine.java
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

package com.jsen;

/**
 * Abstract class representing JSR 223 compliant base class  
 * for all script engines that have set global object.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class GlobalObjectScriptEngine<GlobalObject> extends AbstractScriptEngine {

	protected Object globalObject;
	
	/**
	 * Constructs global object script engine for the given settings and that was constructed using passed factory.
	 * 
	 * @param factory Script engine factory that created this browser engine.
	 * @param scriptSettings Script settings that might be used for initialization of this script engine.
	 * @param globalObject Global object that should be implemented.
	 */
	public GlobalObjectScriptEngine(AbstractScriptEngineFactory factory, GlobalObjectScriptSettings<GlobalObject> scriptSettings, Object globalObject) {
		super(factory, scriptSettings);
		
		this.globalObject = globalObject;
	}

}

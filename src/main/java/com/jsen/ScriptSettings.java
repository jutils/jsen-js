/**
 * ScriptSettings.java
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents class for creating settings which is passed into scripts and script engines.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#script-settings-object">Script settings object</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class ScriptSettings {
	protected Map<String, AbstractScriptEngine> scriptEngines;
	
	public ScriptSettings() {
		scriptEngines = new HashMap<String, AbstractScriptEngine>();
	}
	
	/**
	 * Returns all supported execution environments.
	 * 
	 * @return All supported execution environments.
	 */
	public Collection<AbstractScriptEngineFactory> getExecutionEnviroments() {
		ScriptEngineManager scriptManager = ScriptEngineManager.getInstance();
		
		return scriptManager.getAllMimeContentFactories();
	}
		
	/**
	 * Returns supported execution environment for given language.
	 * 
	 * @param language Script language
	 * @return Execution environment for given language
	 */
	public AbstractScriptEngine getExecutionEnviroment(String language) {		
		AbstractScriptEngine scriptEngine = scriptEngines.get(language);
		
		if (scriptEngine == null) {
			ScriptEngineManager scriptManager = ScriptEngineManager.getInstance();
			scriptEngine = scriptManager.getContent(language, this);
			scriptEngines.put(language, scriptEngine);
		}

		return scriptEngine;
	}
}

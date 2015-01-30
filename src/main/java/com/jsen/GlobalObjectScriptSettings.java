/**
 * GlobalObjectScriptSettings.java
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
 * Represents class for creating script settings that have Window as a global object.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#script-settings-for-browsing-contexts">Script settings for browsing contexts</a>
 */
public class GlobalObjectScriptSettings<GlobalObject> extends ScriptSettings {

	private GlobalObject _globalObject;
		
	public GlobalObjectScriptSettings(GlobalObject globalObject) {
		_globalObject = globalObject;
	}

	public GlobalObject getGlobalObject() {
		return _globalObject;
	}

}

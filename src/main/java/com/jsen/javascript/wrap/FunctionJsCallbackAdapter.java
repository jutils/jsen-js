/**
 * FunctionEventListenerAdapter.java
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

package com.jsen.javascript.wrap;

import com.jsen.javascript.JsCallback;
import com.jsen.javascript.java.HostedJavaMethod;

import org.mozilla.javascript.Function;

public class FunctionJsCallbackAdapter implements JsCallback {
	protected Function function;
	
	/**
	 * Constructs new adapter for given function.
	 * 
	 * @param function Function to be adapted.
	 */
	public FunctionJsCallbackAdapter(Function function) {
		this.function = function;
	}
	
	@Override
	public void call(final Object ...args) {	
		HostedJavaMethod.call(function, args);
	}

	/**
	 * Returns adapted function.
	 * 
	 * @return Adapted function.
	 */
	public Function getFunction() {
		return function;
	}
	
	@Override
	public String toString() {
		return HostedJavaMethod.getFunctionName(function);
	}
}

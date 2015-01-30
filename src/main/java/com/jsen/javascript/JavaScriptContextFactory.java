/**
 * JavaScriptContextFactory.java
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

import com.jsen.adapter.AdapterRegistry;
import com.jsen.javascript.wrap.AdaptedList;
import com.jsen.javascript.wrap.AdapterWrapFactoryDecorator;
import com.jsen.javascript.wrap.CollectionsWrapFactoryDecorator;
import com.jsen.javascript.wrap.DefaultWrapFactoryDecorator;
import com.jsen.javascript.wrap.ErrorAdapter;
import com.jsen.javascript.wrap.ListAdapter;
import com.jsen.javascript.wrap.WrapFactoryDecorator;
import com.jsen.reflect.ClassMembersResolverFactory;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * Context factory that is used for all JavaScript engines.
 * This context should be associated with the JavaScriptEngine 
 * that is unique for one thread, because there should be one context per one thread.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class JavaScriptContextFactory extends ContextFactory {
	
	protected JavaScriptEngine<?> scriptEngine;
	protected ClassMembersResolverFactory membersResolverFactory;
	protected AdapterRegistry adapterRegistry;
	
	/*public JavaScriptContextFactory() {
		this(new WindowJavaScriptEngine(null, null));
	}*/
	
	/**
	 * Constructs new JavaScript context with the associated script engine.
	 * 
	 * @param scriptEngine Script engine to be associated with this context factory.
	 */
	public JavaScriptContextFactory(JavaScriptEngine<?> scriptEngine) {
		this.scriptEngine = scriptEngine;
		this.membersResolverFactory = scriptEngine.getClassMembersResolverFactory();
		this.adapterRegistry = new AdapterRegistry();

		this.adapterRegistry.registerAdapter(ErrorAdapter.class);
		this.adapterRegistry.registerAdapter(ListAdapter.class);
	}
	
	@Override
	public boolean hasFeature(Context cx, int feature) {
		if (feature == Context.FEATURE_E4X) {
			return false;
		} else {
			return super.hasFeature(cx, feature);
		}
	}
	
	@Override
	protected Context makeContext() {
		Context cx = super.makeContext();
		
		WrapFactoryDecorator wrapFactoryDecorator = new DefaultWrapFactoryDecorator(membersResolverFactory);
		wrapFactoryDecorator = new CollectionsWrapFactoryDecorator(membersResolverFactory, wrapFactoryDecorator);
		wrapFactoryDecorator = new AdapterWrapFactoryDecorator(adapterRegistry, wrapFactoryDecorator);

		cx.setWrapFactory(wrapFactoryDecorator);
		
		return cx;
	}
}

/**
 * CollectionsWrapFactoryDecorator.java
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

import java.util.List;
import java.util.Map;

import com.jsen.javascript.java.ArrayWrapper;
import com.jsen.javascript.java.HostedJavaCollection;
import com.jsen.core.reflect.ClassMembersResolverFactory;
import com.jsen.core.reflect.DefaultObjectMembers;
import com.jsen.core.reflect.ObjectGetter;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * Wrap factory decorator that wraps Java native collections and makes 
 * accessible their items via JavaScript indexed properties or mapped properties.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class CollectionsWrapFactoryDecorator extends WrapFactoryDecorator {
	
	protected ClassMembersResolverFactory membersResolverFactory;
	
	/**
	 * Constructs leaf collections wrap factory decorator. 
	 */
	public CollectionsWrapFactoryDecorator() {}
	
	/**
	 * Constructs new wrap factory decorator.
	 * 
	 * @param decorator Decorator the be added as a child decorator and chained.
	 */
	public CollectionsWrapFactoryDecorator(ClassMembersResolverFactory membersResolverFactory, WrapFactoryDecorator decorator) {
		super(decorator);
		
		this.membersResolverFactory = membersResolverFactory;
	}
	
	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		final Class<?> type = javaObject.getClass();
		boolean isCollection = List.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type) || ObjectGetter.class.isAssignableFrom(type);
        
		if (isCollection && List.class.isAssignableFrom(type)) {
			DefaultObjectMembers objectMembers = DefaultObjectMembers.getObjectMembers(new ArrayWrapper(javaObject), membersResolverFactory);
        	return new HostedJavaCollection(scope, objectMembers);
        } else if (isCollection) {
			DefaultObjectMembers objectMembers = DefaultObjectMembers.getObjectMembers(javaObject, membersResolverFactory);
        	return new HostedJavaCollection(scope, objectMembers);
        }
        
        return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
	}
}

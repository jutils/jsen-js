package com.jsen.javascript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Undefined;

import com.jsen.core.reflect.ClassField;
import com.jsen.core.reflect.ClassMember;
import com.jsen.core.reflect.ClassMembersResolverFactory;
import com.jsen.core.reflect.DefaultObjectMembers;
import com.jsen.core.reflect.ObjectGetter;

public class ObjectTreeNodeModel implements Comparable<ObjectTreeNodeModel>{

	private static final int DEFAULT_RECURSION = 2;
	private boolean childrenLoaded;
	private ClassMembersResolverFactory membersResolverFactory;
	private String fieldName;
	private Class<?> fieldType;
	private Object fieldValue;
	private Exception exception;
	private String fieldTypeStr;
	private String fieldValueStr;

	private int position;
	private List<ObjectTreeNodeModel> childs;
	
	public ObjectTreeNodeModel(Object rootObject,
			ClassMembersResolverFactory membersResolverFactory, int recursion) {
		this(membersResolverFactory, "(root)", rootObject.getClass(),
				rootObject, null, recursion);
	}
	
	/**
	 * Constructs tree node with the default name "(root)" containing members of
	 * the passed object resolved with class members resolver factory.
	 *
	 * @param rootObject
	 *            Object of which members should be resolved and put into tree
	 *            node.
	 * @param membersResolverFactory
	 *            Class member resolver factory used for resolvinf members of
	 *            the passed object.
	 */
	public ObjectTreeNodeModel(Object rootObject,
			ClassMembersResolverFactory membersResolverFactory) {
		this(rootObject, membersResolverFactory, DEFAULT_RECURSION);
	}

	/**
	 * Constructs tree node containing members of the passed object resolved
	 * with class members resolver factory.
	 *
	 * @param nodeName
	 *            Name of this constructed node.
	 * @param rootObject
	 *            Object of which members should be resolved and put into tree
	 *            node.
	 * @param membersResolverFactory
	 *            Class member resolver factory used for resolvinf members of
	 *            the passed object.
	 */
	public ObjectTreeNodeModel(String nodeName, Object rootObject,
			ClassMembersResolverFactory membersResolverFactory) {
		this(nodeName, rootObject, membersResolverFactory, null);
	}

	/**
	 * Constructs tree node from the passed object and exception which might
	 * have occurred while retrieving value of the passed object. It also
	 * creates children nodes which represents the members of the passed object
	 * resolved with class members resolver factory.
	 *
	 * @param nodeName
	 *            Name of this constructed node.
	 * @param rootObject
	 *            Object of which members should be resolved and put into tree
	 *            node.
	 * @param membersResolverFactory
	 *            Class member resolver factory used for resolvinf members of
	 *            the passed object.
	 * @param exception
	 *            Exception which is passed if there was some exception while
	 *            retrieving the objet value
	 */
	public ObjectTreeNodeModel(String nodeName, Object rootObject,
			ClassMembersResolverFactory membersResolverFactory,
			Exception exception) {
		this(membersResolverFactory, nodeName, rootObject.getClass(),
				rootObject, exception, DEFAULT_RECURSION);
		childrenLoaded = true;
	}

	private ObjectTreeNodeModel(
			ClassMembersResolverFactory membersResolverFactory,
			String fieldName, Class<?> fieldType, Object fieldValue,
			Exception exception, int recursion) {
		this.membersResolverFactory = membersResolverFactory;
		this.fieldName = fieldName;
		this.exception = exception;
		this.childs = new ArrayList<ObjectTreeNodeModel>();
		setNewFieldValue(fieldType, fieldValue, exception, recursion);
	}

	public String getFieldName() {
		return fieldName;
	}

	public Class<?> getFieldType() {
		return fieldType;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	/**
	 * Sets new value for this node.
	 *
	 * @param fieldType
	 *            Type of member which will represent this node.
	 * @param fieldValue
	 *            Value of the member which will represent this node.
	 * @param exception
	 *            Exception which might have occurred which retrieving passed
	 *            object.
	 */
	public void setNewFieldValue(Class<?> fieldType, Object fieldValue,
			Exception exception) {
		setNewFieldValue(fieldType, fieldValue, exception, DEFAULT_RECURSION);
	}

	private void setNewFieldValue(Class<?> fieldType, Object fieldValue,
			Exception exception, int recursion) {
		this.fieldType = fieldType;
		this.fieldValue = fieldValue;
		this.fieldTypeStr = (fieldType != null) ? fieldType.getSimpleName()
				: null;
		this.fieldTypeStr = (fieldType == null && fieldValue != null) ? fieldValue
				.getClass().getSimpleName() : this.fieldTypeStr;
		if (fieldValue instanceof Function) {
			if (fieldValue instanceof BaseFunction) {
				BaseFunction baseFunction = (BaseFunction) fieldValue;
				this.fieldValueStr = baseFunction.getFunctionName() + "()";
			} else {
				this.fieldValueStr = "f()";
			}
		} else if (fieldValue instanceof String) {
			this.fieldValueStr = "\"" + (String) fieldValue + "\"";
		} else if (fieldTypeStr != null) {
			try {
				this.fieldValueStr = (fieldValue != null) ? fieldValue
						.toString() : "null";
			} catch (Exception e) {
				// e.printStackTrace();
				this.fieldValueStr = "(exceptiono occured)";
			}
		}
		constructObjectTree(this, fieldValue, recursion);
	}

	/**
	 * Visits this node. This method should be called when this node is walked
	 * inside tree. It ensures generating of the nested child tree nodes.
	 */
	public void visit() {
		if (getChildCount() > 0 && !childrenLoaded) {
			childrenLoaded = true;
			ObjectTreeNodeModel child = (ObjectTreeNodeModel) getFirstChild();
			do {
				constructObjectTree(child, child.fieldValue, 1);
				child = (ObjectTreeNodeModel) child.getNextSibling();
			} while (child != null);
		}
	}

	private void constructObjectTree(ObjectTreeNodeModel parentNode,
			Object object, int recursion) {
		if (recursion < 1 || object == null) {
			return;
		} else {
			recursion--;
		}
		DefaultObjectMembers objectMembers = DefaultObjectMembers
				.getObjectMembers(object, membersResolverFactory);
		Set<Entry<String, Set<ClassMember<?>>>> members = objectMembers
				.getNamedMemberEtrySet();
		List<ObjectTreeNodeModel> childrenList = new ArrayList<ObjectTreeNodeModel>();
		for (Entry<String, Set<ClassMember<?>>> member : members) {
			String fieldName = member.getKey();
			Set<ClassMember<?>> memberSet = member.getValue();
			if (memberSet.size() == 1) {
				ClassMember<?> classMember = memberSet.iterator().next();
				if (classMember instanceof ClassField) {
					ClassField classField = (ClassField) classMember;
					Class<?> fieldType = classField.getFieldType();
					Object fieldValue = null;
					Exception exception = null;
					try {
						fieldValue = classField.get(object);
					} catch (Exception e) {
						// e.printStackTrace();
						exception = e;
					}
					ObjectTreeNodeModel node = new ObjectTreeNodeModel(
							membersResolverFactory, fieldName, fieldType,
							fieldValue, exception, recursion);
					childrenList.add(node);
				}
			}
		}
		if (object instanceof ObjectGetter) {
			ObjectGetter objectWithGetter = (ObjectGetter) object;
			Collection<Object> keys = objectWithGetter.getKeys();
			for (Object key : keys) {
				Object fieldValue = null;
				Exception exception = null;
				try {
					fieldValue = objectWithGetter.get(key);
				} catch (Exception e) {
					// e.printStackTrace();
					exception = e;
				}
				Class<?> fieldType = fieldValue.getClass();
				ObjectTreeNodeModel node = new ObjectTreeNodeModel(
						membersResolverFactory, "[" + key.toString() + "]",
						fieldType, fieldValue, exception, recursion);
				childrenList.add(node);
			}
		}
		Collections.sort(childrenList);
		for (ObjectTreeNodeModel child : childrenList) {
			parentNode.add(child);
		}
	}

	@Override
	public String toString() {
		if (exception != null) {
			return fieldName + " - (exception occured)";
		} else if (fieldTypeStr == null || fieldValue == Undefined.instance) {
			return fieldName + " - (undefined property)";
		} else {
			return fieldName + ": " + fieldTypeStr + " = " + fieldValueStr;
		}
	}

	@Override
	public int compareTo(ObjectTreeNodeModel o) {
		return fieldName.compareTo(o.fieldName);
	}
	
	public void add(ObjectTreeNodeModel node) {
		position = childs.size();
		childs.add(node);
	}
	
	public int getChildCount() {		
		return childs.size();
	}
	
	public ObjectTreeNodeModel getFirstChild() {
		return childs.get(0);
	}
	
	public ObjectTreeNodeModel getNextSibling() {
		if (position + 1 < childs.size()) {
			return childs.get(position + 1);
		} else {
			return null;
		}
		
	}
}

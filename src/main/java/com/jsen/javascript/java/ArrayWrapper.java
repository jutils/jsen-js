package com.jsen.javascript.java;

import java.util.List;

import org.mozilla.javascript.Undefined;

import com.jsen.core.Wrapper;
import com.jsen.core.annotation.ScriptClass;
import com.jsen.core.annotation.ScriptFunction;
import com.jsen.core.annotation.ScriptGetter;
import com.jsen.javascript.JsCallback;

@ScriptClass
public class ArrayWrapper implements Wrapper<Object> {
	Object javaObject;
	
	public ArrayWrapper(Object javaObject) {
		this.javaObject = javaObject;
	}
	
	@Override
	public Object unwrap() {
		return javaObject;
	}
	
	@ScriptFunction(name="forEach")
	public void forEach(JsCallback callback) {
		if (javaObject instanceof List<?>) {
			List<?> list = (List<?>)javaObject;
			for (Object o : list) {
				callback.call(o);
			}
		}
	}
	
	@ScriptFunction(name="indexOf")
	public Object indexOf(Object o) {
		if (javaObject instanceof List<?>) {
			List<?> list = (List<?>)javaObject;
			return list.indexOf(o);
		} else {
			return Undefined.instance;
		}
	}
	
	@ScriptGetter(field="length")
	public Object getLength() {
		if (javaObject instanceof List<?>) {
			return ((List<?>)javaObject).size();
		} else if (javaObject instanceof Object[]) {
			return ((Object[])javaObject).length;
		} else {
			return Undefined.instance;
		}
	}
}

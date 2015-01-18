package com.fernet;

import java.lang.reflect.Method;
import java.util.Map;

import javax.ws.rs.Path;

import com.google.common.collect.Maps;

class Registry {
	private final Map<String, Method> pathToMethod;

	public Registry(Class<?>... serviceClasses) {
		pathToMethod = Maps.newHashMap();
		for (Class<?> serviceClass : serviceClasses) {
			for (Method method : serviceClass.getMethods()) {
				Path path = method.getAnnotation(Path.class);
				if (path != null) {
					pathToMethod.put(path.value(), method);
				}
			}
		}
	}

	public Method getMethodForPath(String path) {
		return pathToMethod.get(path);
	}
}

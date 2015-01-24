package com.fernet;

import java.lang.reflect.Method;
import java.util.Map;

public interface MethodResolver {
	Method resolveMethod(HttpMethod httpMethod, String path);

	String[] resolveParameters(Method method, String path,
			Map<String, String[]> reqParams, String body);
}

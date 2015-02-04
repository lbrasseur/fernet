package com.fernet;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface MethodResolver {
	Method resolveMethod(HttpMethod httpMethod, String path);

	String[] resolveParameters(Method method, String path,
			Map<String, String[]> reqParams, String body);

	String resolveRequestMimeType(Method method,
			HttpServletRequest request);

	String resolveResponseMimeType(Method method,
			HttpServletRequest request);
}

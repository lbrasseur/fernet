package com.fernet;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

class MethodResolver {
	private final Collection<MethodDefinition> pathToMethod;

	public MethodResolver(Class<?>... serviceClasses) {
		requireNonNull(serviceClasses);
		pathToMethod = Sets.newHashSet();
		for (Class<?> serviceClass : serviceClasses) {
			for (Method method : serviceClass.getMethods()) {
				Path path = method.getAnnotation(Path.class);
				Object httpMethod = firstNonNull(
						method.getAnnotation(GET.class),
						method.getAnnotation(POST.class),
						method.getAnnotation(PUT.class),
						method.getAnnotation(DELETE.class),
						method.getAnnotation(HEAD.class));
				if (path != null && httpMethod != null) {
					pathToMethod.add(new MethodDefinition(method, HttpMethod
							.fromAnnotation(httpMethod), Pattern
							.compile(pathToRegex(path.value()))));
				}
			}
		}
	}

	public Method resolveMethod(final HttpMethod httpMethod, final String path) {
		requireNonNull(httpMethod);
		requireNonNull(path);
		MethodDefinition methodDefinition = Iterables.find(pathToMethod,
				new Predicate<MethodDefinition>() {
					@Override
					public boolean apply(MethodDefinition methodDefinition) {
						return methodDefinition.httpMethod == httpMethod
								&& methodDefinition.pathPattern.matcher(path)
										.matches();
					}
				}, null);
		return methodDefinition != null ? methodDefinition.method : null;
	}

	private String pathToRegex(String path) {
		return path.replaceAll("\\{.*?\\}", "(.*?)");
	}

	private Object firstNonNull(Object... values) {
		for (Object value : values) {
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	private static class MethodDefinition {

		private final Method method;
		private final HttpMethod httpMethod;
		private final Pattern pathPattern;

		private MethodDefinition(Method method, HttpMethod httpMethod,
				Pattern pathPattern) {
			this.method = method;
			this.httpMethod = httpMethod;
			this.pathPattern = pathPattern;
		}

		@Override
		public int hashCode() {
			return Objects.hash(method, httpMethod, pathPattern);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			MethodDefinition other = (MethodDefinition) obj;
			return Objects.equals(method, other.method)
					&& Objects.equals(httpMethod, other.httpMethod)
					&& Objects.equals(pathPattern, other.pathPattern);
		}
	}
}

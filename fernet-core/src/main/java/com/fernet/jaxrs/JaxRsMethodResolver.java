package com.fernet.jaxrs;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.fernet.HttpMethod;
import com.fernet.MethodResolver;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class JaxRsMethodResolver implements MethodResolver {
	private final Map<Method, MethodDefinition> methodDefinitions;

	public JaxRsMethodResolver(Class<?>... serviceClasses) {
		requireNonNull(serviceClasses);
		methodDefinitions = Maps.newHashMap();
		for (Class<?> serviceClass : serviceClasses) {
			for (Method method : serviceClass.getMethods()) {
				Path path = method.getAnnotation(Path.class);
				Annotation httpMethod = firstAnnotation(method,
						ImmutableList.of(GET.class, POST.class, PUT.class,
								DELETE.class, HEAD.class));
				if (path != null && httpMethod != null) {
					// TODO: check the @PathParam values to early detect errors
					methodDefinitions
							.put(method, new MethodDefinition(method,
									httpMethodFromAnnotation(httpMethod),
									Pattern.compile(pathToRegex(path.value()))));
				}
			}
		}
	}

	@Override
	public Method resolveMethod(final HttpMethod httpMethod, final String path) {
		requireNonNull(httpMethod);
		requireNonNull(path);
		MethodDefinition methodDefinition = Iterables.find(
				methodDefinitions.values(), new Predicate<MethodDefinition>() {
					@Override
					public boolean apply(MethodDefinition methodDefinition) {
						return methodDefinition.httpMethod == httpMethod
								&& methodDefinition.pathPattern.matcher(path)
										.matches();
					}
				}, null);
		return methodDefinition != null ? methodDefinition.method : null;
	}

	@Override
	public String[] resolveParameters(Method method, String path,
			Map<String, String[]> reqParams, String body) {
		String[] values = new String[method.getParameterTypes().length];
		for (int n = 0; n < values.length; n++) {
			Annotation[] annotations = method.getParameterAnnotations()[n];

			QueryParam queryParam = findAnnotation(QueryParam.class,
					annotations);
			if (queryParam != null) {
				String[] valueArray = reqParams.get(queryParam.value());
				if (valueArray != null) {
					// TODO: Which solution should be implemented for repeated
					// parameters? Taking first value for now.
					values[n] = valueArray[0];
				} else {
					DefaultValue defaultValue = findAnnotation(
							DefaultValue.class, annotations);
					values[n] = defaultValue != null ? defaultValue.value()
							: "";
				}
			} else {
				PathParam pathParan = findAnnotation(PathParam.class,
						annotations);
				if (pathParan != null) {
					String pathParamStr = "{" + pathParan.value() + "}";
					values[n] = "";
					Pattern pathPattern = methodDefinitions.get(method).pathPattern;
					Matcher annotationMatcher = pathPattern.matcher(method
							.getAnnotation(Path.class).value());
					Matcher requestMatcher = pathPattern.matcher(path);
					annotationMatcher.find();
					requestMatcher.find();
					for (int g = 1; g <= annotationMatcher.groupCount(); g++) {
						if (annotationMatcher.group(g).equals(pathParamStr)) {
							values[n] = requestMatcher.group(g);
						}
					}
				} else {
					values[n] = body;
				}
			}
		}
		return values;
	}

	@Override
	public String resolveRequestMimeType(HttpMethod httpMethod,
			HttpServletRequest request) {
		// TODO Read @Consumes annotation
		// Set a parameter for default mime type
		return parseMimeType(request.getContentType());
	}

	@Override
	public String resolveResponseMimeType(HttpMethod httpMethod,
			HttpServletRequest request) {
		// TODO Read @Produces annotation
		// Set a parameter for default mime type
		return parseMimeType(request.getHeader("Accept"));
	}
	
	private String parseMimeType(String contentType) {
		int pos = contentType.indexOf(';');
		return pos == 0 ? contentType : contentType.substring(0, pos).trim();
	}

	@SuppressWarnings("unchecked")
	private <T extends Annotation> T findAnnotation(Class<T> annotationClass,
			Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotationClass.isAssignableFrom(annotation.getClass())) {
				return (T) annotation;
			}
		}
		return null;
	}

	private String pathToRegex(String path) {
		return "^" + path.replaceAll("\\{.+?\\}", "(.+?)") + "$";
	}

	private Annotation firstAnnotation(Method method,
			Iterable<Class<? extends Annotation>> annotationClasses) {
		for (Class<? extends Annotation> annotationClass : annotationClasses) {
			Annotation annotation = method.getAnnotation(annotationClass);
			if (annotation != null) {
				return annotation;
			}
		}
		return null;
	}

	private HttpMethod httpMethodFromAnnotation(Annotation annotation) {
		if (annotation instanceof POST) {
			return HttpMethod.POST;
		} else if (annotation instanceof PUT) {
			return HttpMethod.PUT;
		} else if (annotation instanceof DELETE) {
			return HttpMethod.DELETE;
		} else if (annotation instanceof HEAD) {
			return HttpMethod.HEAD;
		} else {
			return HttpMethod.GET;
		}
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

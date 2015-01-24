package com.fernet;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.CharStreams;

public class RestFilter implements Filter {
	private final MethodResolver methodResolver;
	private final ServiceProvider serviceProvider;
	private final Serializer serializer;
	private final Authorizer authorizer;

	@Inject
	public RestFilter(MethodResolver methodResolver,
			ServiceProvider serviceProvider, Serializer serializer,
			Authorizer authorizer) {
		this.methodResolver = requireNonNull(methodResolver);
		this.serviceProvider = requireNonNull(serviceProvider);
		this.serializer = requireNonNull(serializer);
		this.authorizer = requireNonNull(authorizer);
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		try {
			final HttpServletRequest req = (HttpServletRequest) servletRequest;
			HttpServletResponse resp = (HttpServletResponse) servletResponse;

			HttpMethod httpMethod = HttpMethod.valueOf(req.getMethod());
			String path = req.getRequestURI().substring(
					req.getContextPath().length());

			Method method = methodResolver.resolveMethod(httpMethod, path);
			if (method != null) {
				if (!authorizer.isAuthorized(req)) {
					resp.sendError(HttpURLConnection.HTTP_FORBIDDEN);
					return;
				}

				String body = CharStreams.toString(new InputStreamReader(req
						.getInputStream()));
				String[] stringArgs = methodResolver.resolveParameters(method,
						path, req.getParameterMap(), body);
				checkState(stringArgs.length == method.getParameterCount(),
						String.format(
								"Resolved argument count does not match: %s. Expected: %s",
								stringArgs.length, method.getParameterCount()));
				Object[] args = new Object[stringArgs.length];
				for (int n = 0; n < stringArgs.length; n++) {
					args[n] = serializer.fromString(stringArgs[n],
							method.getParameterTypes()[n]);
				}

				Object service = serviceProvider.getService(method
						.getDeclaringClass());
				Object response = method.invoke(service, args);

				resp.getWriter().write(serializer.toString(response));
			} else {
				filterChain.doFilter(servletRequest, servletResponse);
			}
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
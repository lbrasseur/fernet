package com.fernet;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class RestFilter implements Filter {
	private final MethodResolver methodResolver;
	private final ServiceProvider serviceProvider;
	private final Gson gson;

	@Inject
	public RestFilter(MethodResolver methodResolver,
			ServiceProvider serviceProvider,
			Gson gson) {
		this.methodResolver = checkNotNull(methodResolver);
		this.serviceProvider = checkNotNull(serviceProvider);
		this.gson = checkNotNull(gson);
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;

		HttpMethod httpMethod = HttpMethod.valueOf(req.getMethod());
		String path = req.getRequestURI().substring(
				req.getContextPath().length());

		Method method = methodResolver.resolveMethod(httpMethod, path);
		if (method != null) {
			Object service = serviceProvider.getService(method.getDeclaringClass());

			try (Reader contentReader = new InputStreamReader(
					req.getInputStream())) {
				Object requestDto = gson.fromJson(contentReader,
						method.getParameterTypes()[0]);
				Object responseDto = method.invoke(service, requestDto);
				resp.getWriter().write(gson.toJson(responseDto));
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new ServletException(e);
			}
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
package com.fernet;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.inject.Injector;

public class RestServlet extends HttpServlet {
	private static final long serialVersionUID = 2456676953647740932L;
	private final Registry registry;
	private final Injector injector;
	private final Gson gson;

	@Inject
	public RestServlet(Registry registry, Injector injector, Gson gson) {
		this.registry = checkNotNull(registry);
		this.injector = checkNotNull(injector);
		this.gson = checkNotNull(gson);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = req.getRequestURI().substring(req.getContextPath().length());
		Method method = registry.getMethodForPath(path);
		Object service = injector.getInstance(method.getDeclaringClass());
		
		try (Reader contentReader = new InputStreamReader(req.getInputStream())) {
			Object requestDto = gson.fromJson(contentReader,
					method.getParameterTypes()[0]);
			Object responseDto = method.invoke(service, requestDto);
			resp.getWriter().write(gson.toJson(responseDto));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			Throwables.propagate(e);
		}
	}
}
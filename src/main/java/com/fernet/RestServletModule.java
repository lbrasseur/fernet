package com.fernet;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;

import javax.ws.rs.Path;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class RestServletModule extends ServletModule {
	private Class<?> serviceClasses[];

	public RestServletModule(Class<?>... serviceClasses) {
		this.serviceClasses = checkNotNull(serviceClasses);
	}

	@Override
	protected void configureServlets() {
		for (Class<?> serviceClass : serviceClasses) {
			for (Method method : serviceClass.getMethods()) {
				Path path = method.getAnnotation(Path.class);
				if (path != null) {
					serve(path.value()).with(RestServlet.class);
				}
			}
		}

		bind(RestServlet.class).in(Singleton.class);
	}

	@Provides
	@Singleton
	public Registry getServiceRegistry() {
		return new Registry(serviceClasses);
	}
}
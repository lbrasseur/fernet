package com.fernet.guice;

import static java.util.Objects.requireNonNull;

import com.fernet.MethodResolver;
import com.fernet.RestFilter;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class RestServletModule extends ServletModule {
	private Class<?> serviceClasses[];

	public RestServletModule(Class<?>... serviceClasses) {
		this.serviceClasses = requireNonNull(serviceClasses);
	}

	@Override
	protected void configureServlets() {
		filter("*").through(RestFilter.class);
		bind(RestFilter.class).in(Singleton.class);
	}

	@Provides
	@Singleton
	public MethodResolver getMethodResolver() {
		return new MethodResolver(serviceClasses);
	}
}
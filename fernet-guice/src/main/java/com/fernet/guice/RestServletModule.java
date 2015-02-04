package com.fernet.guice;

import static java.util.Objects.requireNonNull;

import com.fernet.Authorizer;
import com.fernet.MethodResolver;
import com.fernet.RestFilter;
import com.fernet.Serializer;
import com.fernet.ServiceProvider;
import com.fernet.gson.GsonSerializer;
import com.fernet.jaxrs.JaxRsMethodResolver;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.servlet.ServletModule;
import com.google.inject.util.Providers;

public class RestServletModule extends ServletModule {
	private Class<?> serviceClasses[];

	public RestServletModule(Class<?>... serviceClasses) {
		this.serviceClasses = requireNonNull(serviceClasses);
	}

	@Override
	protected void configureServlets() {
		filter("*").through(RestFilter.class);
		bind(RestFilter.class).in(Singleton.class);
		bind(ServiceProvider.class).to(InjectorServiceProvider.class);
		bind(Serializer.class).to(GsonSerializer.class);
		bind(Authorizer.class).toProvider(Providers.<Authorizer> of(null));
		MapBinder<String, Serializer> mapbinder = MapBinder.newMapBinder(
				binder(), String.class, Serializer.class);
		mapbinder.addBinding("application/json").to(GsonSerializer.class);
	}

	@Provides
	@Singleton
	public MethodResolver getMethodResolver() {
		return new JaxRsMethodResolver(serviceClasses);
	}
}
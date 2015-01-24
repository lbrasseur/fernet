package com.fernet.guice;

import static java.util.Objects.requireNonNull;

import javax.inject.Inject;

import com.fernet.ServiceProvider;
import com.google.inject.Injector;

class InjectorServiceProvider implements ServiceProvider {
	private final Injector injector;
	
	@Inject
	public InjectorServiceProvider(Injector injector) {
		this.injector = requireNonNull(injector);
	}

	@Override
	public <T> T getService(Class<T> serviceClass) {
		return injector.getInstance(serviceClass);
	}
}

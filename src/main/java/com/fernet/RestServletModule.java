package com.fernet;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;

public class RestServletModule extends ServletModule {
	@Override
	protected void configureServlets() {
		serve("/*").with(Key.get(new TypeLiteral<RestServlet<String>>() {
		}));
	}
}
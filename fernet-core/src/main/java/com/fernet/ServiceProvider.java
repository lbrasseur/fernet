package com.fernet;

public interface ServiceProvider {
	<T> T getService(Class<T> serviceClass);
}

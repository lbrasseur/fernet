package com.fernet.gson;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import javax.inject.Inject;

import com.fernet.Serializer;
import com.google.common.base.Throwables;
import com.google.gson.Gson;

public class GsonSerializer implements Serializer {
	private final Gson gson;

	@Inject
	public GsonSerializer(Gson gson) {
		this.gson = requireNonNull(gson);
	}

	@Override
	public <T> T fromString(String data, Class<T> type) {
		requireNonNull(data);
		requireNonNull(type);
		try {
			if (isPrimitive(type)) {
				return type.getConstructor(String.class).newInstance(data);
			} else {
				return gson.fromJson(data, type);
			}
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw Throwables.propagate(e);
		}
	}
	@Override
	public String toString(Object data) {
		requireNonNull(data);
		if (isPrimitive(data.getClass())) {
			return data.toString();
		} else {
			return gson.toJson(data);
		}
	}
	private boolean isPrimitive(Class<?> type) {
		return String.class.isAssignableFrom(type)
				|| Boolean.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type)
				|| Number.class.isAssignableFrom(type)
				|| Double.class.isAssignableFrom(type);
	}
}

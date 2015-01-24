package com.fernet.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.inject.Inject;

import com.fernet.Serializer;
import com.google.common.base.Throwables;
import com.google.gson.Gson;

public class GsonSerializer implements Serializer {
	private final Gson gson;

	@Inject
	public GsonSerializer(Gson gson) {
		this.gson = checkNotNull(gson);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromString(String data, Class<T> type) {
		try {
			// TODO: Primitive parsing should match gson format.
			// Also, primitive list is incomplete.
			if (String.class.isAssignableFrom(type)) {
				return (T) data;
			} else if (Boolean.class.isAssignableFrom(type)) {
				return (T) Boolean.valueOf(data);
			} else if (Date.class.isAssignableFrom(type)) {
				return (T) DateFormat.getDateInstance().parse(data);
			} else if (Integer.class.isAssignableFrom(type)) {
				return (T) Integer.valueOf(data);
			} else if (Double.class.isAssignableFrom(type)) {
				return (T) Double.valueOf(data);
			} else {
				return gson.fromJson(data, type);
			}
		} catch (ParseException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public String toString(Object data) {
		// TODO: Primitive parsing should match gson format.
		// Also, primitive list is incomplete.
		if (data instanceof String || data instanceof Boolean
				|| data instanceof Date || data instanceof Integer
				|| data instanceof Double) {
			return data.toString();
		} else {
			return gson.toJson(data);
		}
	}
}

package com.fernet.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import com.fernet.Serializer;
import com.google.gson.Gson;

public class GsonSerializer implements Serializer {
	private final Gson gson;

	@Inject
	public GsonSerializer(Gson gson) {
		this.gson = checkNotNull(gson);
	}

	@Override
	public <T> T fromString(String data, Class<T> type) {
		return gson.fromJson(data, type);
	}

	@Override
	public String toString(Object data) {
		return gson.toJson(data);
	}
}

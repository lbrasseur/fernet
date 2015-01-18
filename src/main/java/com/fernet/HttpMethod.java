package com.fernet;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

public enum HttpMethod {
	GET, POST, PUT, DELETE, HEAD;

	static HttpMethod fromAnnotation(Object annotation) {
		if (annotation instanceof GET) {
			return GET;
		} else if (annotation instanceof POST) {
			return POST;
		} else if (annotation instanceof PUT) {
			return PUT;
		} else if (annotation instanceof DELETE) {
			return DELETE;
		} else if (annotation instanceof HEAD) {
			return HEAD;
		} else {
			throw new IllegalArgumentException("Invalid annotation: "
					+ annotation);
		}
	}
}
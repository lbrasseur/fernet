package com.fernet;

import javax.servlet.http.HttpServletRequest;

public interface Authorizer {
	boolean isAuthorized(HttpServletRequest req);
}

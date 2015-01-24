package com.fernet;

import javax.servlet.http.HttpServletRequest;

public class AlwaysTrueAuthorizer implements
		Authorizer {

	@Override
	public boolean isAuthorized(HttpServletRequest req) {
		return true;
	}
}

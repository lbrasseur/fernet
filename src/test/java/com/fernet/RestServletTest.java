package com.fernet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for {@link RestServlet}.
 */
public class RestServletTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public RestServletTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(RestServletTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testRestServlet() {
		assertTrue(true);
	}
}

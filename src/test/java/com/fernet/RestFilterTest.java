package com.fernet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for {@link RestFilter}.
 */
public class RestFilterTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public RestFilterTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(RestFilterTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testRestServlet() {
		assertTrue(true);
	}
}

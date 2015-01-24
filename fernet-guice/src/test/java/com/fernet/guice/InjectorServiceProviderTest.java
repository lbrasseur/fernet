package com.fernet.guice;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for {@link InjectorServiceProvider}.
 */
public class InjectorServiceProviderTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public InjectorServiceProviderTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(InjectorServiceProviderTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testRestServlet() {
		assertTrue(true);
	}
}

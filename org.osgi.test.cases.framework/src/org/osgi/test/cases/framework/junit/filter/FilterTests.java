/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.framework.junit.filter;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Radio Systems AB
 */
public class FilterTests extends DefaultTestBundleControl {
	private Bundle						tb1;

	protected void setUp() throws Exception {
		tb1 = installBundle("filter.tb1.jar");
		tb1.start();
	}

	protected void tearDown() throws Exception {
		tb1.uninstall();
	}
	
	private void testFilter(String filter, String logtext, String[] ids)
			throws Exception {
		ServiceReference< ? >[] refs = getContext().getServiceReferences(
				"org.osgi.test.cases.framework.filter.tb1.TestService", filter);
		logRefList(logtext, refs, ids);
	}

	/**
	 * Tests a null filter.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m1() throws Exception {
		testFilter(null, "Testing null filter", new String[]{"a1", "b1", "b2", "c1", "c2", "c25", "d25"});
	}

	/**
	 * Tests a syntactically invalid expression.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m2() throws Exception {
		try {
			getContext()
					.getServiceReferences(
							"org.osgi.test.cases.framework.filter.tb1.TestService",
							"(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(name=ServiceA))))))))))))))))))))))))");
			fail("The filter expression was wrong");
		} catch (InvalidSyntaxException ise) {
			pass("Exception thrown");
		}
	}

	/**
	 * Tests case insensitivity in attribute names.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m3() throws Exception {
		testFilter("(nAMe=ServiceA)", "Testing case insensitive", new String[] {"a1"});
	}

	/**
	 * Tests basic filter functionality.
	 * 
	 * @requirement framework.filter.m4
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m4() throws Exception {
		testFilter(
				"(  \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F |  (   \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F  name 	  =ServiceA)(  \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F  name=ServiceB)   \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F  )",
				"Testing spaces in filter operation", new String[] {"a1", "b1", "b2"});
	}

	/**
	 * Tests String types.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m5() throws Exception {
		testFilter("(name=ServiceA)", "Testing type String", new String[]{"a1"});
	}

	/**
	 * Tests numeric types.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m6() throws Exception {
		testFilter("(Integer=3)", "Testing type Integer", new String[]{"c25"});
		testFilter("(Long=3)", "Testing type Long", new String[]{"c25"});
		testFilter("(Byte=3)", "Testing type Byte", new String[]{"c25"});
		testFilter("(Short=3)", "Testing type Short", new String[]{"c25"});
	}

	/**
	 * Test that a value is found in an array of values.
	 * 
	 * @requirement framework.filter.m9
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m9() throws Exception {
		testFilter("(compatible=1.5)", "Testing array filter", new String[]{"c2"});
	}

	/**
	 * Test that a value is found in a Vector of values.
	 * 
	 * @requirement framework.filter.m10
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m10() throws Exception {
		testFilter("(compatible=2.1)", "Testing Vector filter", new String[]{"c25"});
	}

	/**
	 * Tests the approximate (~=) operation. Case and white space differences
	 * should match.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s1() throws Exception {
		testFilter("(name~=servicea)", "Testing approximate equality (case)", new String[]{"a1"});
		testFilter("(description~=Service A)",
				"Testing approximate equality (white space)", new String[]{"a1"});
	}

	/**
	 * Filter by an object that implements java.lang.Comparable
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s2() throws Exception {
		// Always finds the service object when filtering by ObjectA
		testFilter("(ObjectA=0)",
				"Testing object that implements java.lang.Comparable", new String[]{"d25"});
	}

	/**
	 * Testing = operator with an object that does not implement
	 * <code>java.lang.Comparable</code> but has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s3() throws Exception {
		testFilter("(ObjectB=4)",
				"Testing = operator with object that does not implement java.lang.Comparable", new String[]{"d25"});
	}

	/**
	 * Testing <= operator with an object that does not implement
	 * <code>java.lang.Comparable</code> but has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s4() throws Exception {
		testFilter("(ObjectB<=4)",
				"Testing <= operator with object that does not implement java.lang.Comparable", new String[]{"d25"});
	}

	/**
	 * Testing >= operator with an object that does not implement
	 * <code>java.lang.Comparable</code> but has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s5() throws Exception {
		testFilter("(ObjectB>=4)",
				"Testing >= operator with object that does not implement java.lang.Comparable", new String[]{"d25"});
	}

	/**
	 * Testing ~= operator with an object that does not implement
	 * <code>java.lang.Comparable</code> but has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s6() throws Exception {
		testFilter("(ObjectB~=4)",
				"Testing ~= operator with object that does not implement java.lang.Comparable", new String[]{"d25"});
	}

	/**
	 * Testing < operator with an object that does not implement
	 * <code>java.lang.Comparable</code> but has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s7() throws Exception {
		String msg;
		msg = "Testing < operator with object that does not implement java.lang.Comparable";
		try {
			testFilter("(ObjectB<=4)", msg, new String[]{"d25"});
			pass("Invalid syntax??");
		} catch (InvalidSyntaxException ise) {
			fail("Objects are not required to implement Comparable");
		}
	}

	/**
	 * Testing with an object that neither implements
	 * <code>java.lang.Comparable</code> nor has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s9() throws Exception {
		testFilter(
				"(ObjectC=4)",
				"Testing object that does not have a public constructor with a single java.lang.String argument", new String[]{});
	}

	/**
	 * Help function used to sort and log an array of service references.
	 */
	private void logRefList(String prefix, ServiceReference< ? >[] refs,
			String[] ids) {
		
		if ( refs == null && ids.length ==0 )
			return;
		
		Set<String> set = new TreeSet<>();
		set.addAll( Arrays.asList(ids));
		
		for ( int i=0; i<refs.length; i++ ) {
			String id = (String) refs[i].getProperty("id");
			assertNotNull("Expecting an id for " + refs[i].getProperty("name") + ":" +refs[i].getProperty("version"),id);
			assertTrue( "Did not expect " + id, set.contains(id));
			set.remove(id);
		}
		assertTrue( "Remaining ids left in expected set: " + set, set.size() == 0);
		
		/*
		StringBuffer logString = new StringBuffer();
		if (refs != null) {
			Vector services = new Vector();
			logString.append(refs.length + " services: { ");
			// The order of the services isn't specified, so we'd better sort
			// them.
			outer: for (int i = 0; i < refs.length; i++) {
				String descr = refs[i].getProperty("name") + " "
						+ refs[i].getProperty("version");
				for (i = 0; i < services.size(); i++) {
					if (((String) services.elementAt(i)).compareTo(descr) >= 0) {
						services.insertElementAt(descr, i);
						continue outer;
					}
				}
				services.addElement(descr);
			}
			boolean comma = false;
			for (Enumeration e = services.elements(); e.hasMoreElements();) {
				if (comma)
					logString.append(", ");
				logString.append(e.nextElement().toString());
				comma = true;
			}
			logString.append(" }");
		} else
			logString.append("No services.");
		log(prefix + logString.toString());
		*/
	}
}

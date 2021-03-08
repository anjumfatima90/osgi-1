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
package org.osgi.test.cases.framework.junit.div;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.namespace.HostNamespace;
import org.osgi.framework.namespace.NativeNamespace;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Namespace;
import org.osgi.test.cases.framework.div.tb6.BundleClass;
import org.osgi.test.support.FrameworkEventCollector;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 *
 * @author Ericsson Radio Systems AB
 */
public class DivTests extends DefaultTestBundleControl {
	public static final String	basePath	= "/org/osgi/test/cases/framework/div/";
	public static final String	basePkg		= "org.osgi.test.cases.framework.div.";

	public static final Collection<Map<String, Object>> MATCH_NATIVE_ATTRIBUTES;
	static {
		Collection<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
		Map<String, Object> commonAttrs = new HashMap<String, Object>();
		commonAttrs.put("org.osgi.test.cases.framework.div.tb12", "abc");
		commonAttrs.put("org.osgi.test.cases.framework.div.tb15", "abc");
		commonAttrs.put("org.osgi.test.cases.framework.div.tb16", "xyz");
		commonAttrs.put(NativeNamespace.CAPABILITY_LANGUAGE_ATTRIBUTE, "en");
		commonAttrs.put(NativeNamespace.CAPABILITY_OSVERSION_ATTRIBUTE,
				new Version(1, 0, 0));

		Map<String, Object> linuxX86 = new HashMap<String, Object>(
				commonAttrs);
		linuxX86.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "linux");
		linuxX86.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE, "X86");
		temp.add(Collections.unmodifiableMap(linuxX86));

		Map<String, Object> linuxX86_64 = new HashMap<String, Object>(
				commonAttrs);
		linuxX86_64.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "linux");
		linuxX86_64.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE,
				"X86-64");
		temp.add(Collections.unmodifiableMap(linuxX86_64));

		Map<String, Object> qnxX86 = new HashMap<String, Object>(commonAttrs);
		qnxX86.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "qnx");
		qnxX86.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE, "X86");
		temp.add(Collections.unmodifiableMap(qnxX86));

		Map<String, Object> qnxPPC = new HashMap<String, Object>(commonAttrs);
		qnxPPC.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "qnx");
		qnxPPC.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE, "powerPC");
		temp.add(Collections.unmodifiableMap(qnxPPC));

		Map<String, Object> qnxARMLE = new HashMap<String, Object>(
				commonAttrs);
		qnxARMLE.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "qnx");
		qnxARMLE.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE, "ARM_le");
		temp.add(Collections.unmodifiableMap(qnxARMLE));

		Map<String, Object> winX86 = new HashMap<String, Object>(commonAttrs);
		winX86.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "win32");
		winX86.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE, "X86");
		temp.add(Collections.unmodifiableMap(winX86));

		Map<String, Object> solarisSparc = new HashMap<String, Object>(
				commonAttrs);
		solarisSparc
				.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "solaris");
		solarisSparc.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE,
				"Sparc");
		temp.add(Collections.unmodifiableMap(solarisSparc));

		Map<String, Object> sunOSSparc = new HashMap<String, Object>(
				commonAttrs);
		sunOSSparc.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "sunOS");
		sunOSSparc.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE, "Sparc");
		temp.add(Collections.unmodifiableMap(sunOSSparc));

		Map<String, Object> winCE = new HashMap<String, Object>(commonAttrs);
		winCE.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "windows CE");
		winCE.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE, "ARM_le");
		temp.add(Collections.unmodifiableMap(winCE));

		Map<String, Object> macX86 = new HashMap<String, Object>(commonAttrs);
		macX86.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "Mac OS X");
		macX86.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE, "X86");
		temp.add(Collections.unmodifiableMap(macX86));

		Map<String, Object> macX86_64 = new HashMap<String, Object>(
				commonAttrs);
		macX86_64.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "Mac OS X");
		macX86_64.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE, "X86-64");
		temp.add(Collections.unmodifiableMap(macX86_64));

		Map<String, Object> macPPC = new HashMap<String, Object>(commonAttrs);
		macPPC.put(NativeNamespace.CAPABILITY_OSNAME_ATTRIBUTE, "Mac OS X");
		macPPC.put(NativeNamespace.CAPABILITY_PROCESSOR_ATTRIBUTE, "PPC");
		temp.add(Collections.unmodifiableMap(macPPC));

		MATCH_NATIVE_ATTRIBUTES = Collections.unmodifiableCollection(temp);
	}

	/**
	 * Logs the manifest headers.
	 */
	public void testManifestHeaders() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb1.jar");
		try {
			tb.start();
			Dictionary<String,String> h = tb.getHeaders("");
			assertEquals("numeric first char", h.get("5-"));
			assertEquals(basePkg + "tb1.CheckManifest", h
					.get("bundle-activator"));
			assertEquals("should contain the bundle category", h
					.get("bundle-category"));
			assertEquals("., foo/bar/dummy.jar", h.get("bundle-classpath"));
			assertEquals("info@ericsson.com", h.get("bundle-contactaddress"));
			assertEquals("should contain the bundle copyright", h
					.get("bundle-copyright"));
			assertEquals("Contains the manifest checked by the test case.", h
					.get("bundle-description"));
			assertEquals("http://www.ericsson.com", h.get("bundle-docurl"));
			assertEquals(basePkg + "tb1", h.get("bundle-name"));
			assertEquals("www.ericsson.se", h.get("bundle-updatelocation"));
			assertEquals("Ericsson Radio Systems AB", h.get("bundle-vendor"));
			assertEquals("Improper value for bundle manifest version 2", h
					.get("bundle-version"));
			assertEquals("12                          34", h.get("continue"));
			assertEquals(
					"org.osgi.dummy1;                         version=\"0.0\", org.osgi.dummy2,org.osgi.dummy3;version=\"19.67.34\"",
					h.get("export-package"));
			assertEquals(
					"should contain the exported services, not used by framework",
					h.get("export-service"));
			assertEquals(
					"This bundle is defined by developer and should be ignored by framework",
					h.get("fakeheader"));
			assertEquals(
					"should contain the imported services, not used by framework",
					h.get("import-service"));
			assertEquals("1.0", h.get("manifest-version"));
			assertEquals(
					"xxxxxxxxx xxxxxxxxx xxxxxxxxx xxxxxxxxx xxxxxxxxx xxxxxxEND",
					h.get("max-length"));
			assertEquals("\u00d0\u00de", h.get("unicode-test"));

			tb.stop();
		}
		catch (BundleException e) {
			fail("Exception in manifest headers", e);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests empty manifest headers.
	 */
	public void testMissingManifestHeaders() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb5.jar");
		try {
			tb.start();
			Dictionary<String,String> h = tb.getHeaders();
			assertEquals(1, h.size());
			assertEquals("1.0", h.get("manifest-version"));
			tb.stop();
		}
		catch (BundleException e) {
			fail("Exception in testing missing manifest headers", e);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests extended classpath
	 */
	public void testBundleClassPath() {
		// instantiate an object from tbcinner.jar
		BundleClass dummy = null;
		try {
			dummy = new BundleClass();
		}
		catch (Throwable e) {
			fail(
					"We didn't manage to instansiate a class from the extended bundle class path",
					e);
		}
		assertEquals("different class loader", dummy.getClass()
				.getClassLoader(), this.getClass().getClassLoader());
	}

	/**
	 * Tests that location remains the same after an update
	 */
	public void testBundleLocation() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb5.jar");
		try {
			String originalLocation = tb.getLocation();
			long originalLastModified = tb.getLastModified();
			Sleep.sleep(250);
			tb.update();
			assertEquals("bundle location changed after update.",
					originalLocation, tb.getLocation());
			assertFalse("bundle last modified did not change",
					originalLastModified == tb.getLastModified());
		}
		finally {
			tb.uninstall();
		}
	}

	private void assertNativeNamespace(Bundle b, boolean isOptional,
			boolean isResolved) {
		BundleRevision revision = b.adapt(BundleRevision.class);
		assertNotNull("Bundle has no revision", revision);
		List<BundleRequirement> nativeRequirements = revision
				.getDeclaredRequirements(NativeNamespace.NATIVE_NAMESPACE);
		assertEquals("Unexpected number of native requirements", 1,
				nativeRequirements.size());
		BundleRequirement nativeRequirement = nativeRequirements.get(0);

		// check the optional directive
		String optionalDirective = nativeRequirement.getDirectives().get(
				Namespace.REQUIREMENT_RESOLUTION_DIRECTIVE);
		if (optionalDirective == null) {
			optionalDirective = Namespace.RESOLUTION_MANDATORY;
		}
		assertEquals("Wrong resolution type",
				isOptional ? Namespace.RESOLUTION_OPTIONAL
						: Namespace.RESOLUTION_MANDATORY, optionalDirective);
		// check for a valid filter directive
		String filterDirective = nativeRequirement.getDirectives().get(
				Namespace.REQUIREMENT_FILTER_DIRECTIVE);
		assertNotNull("Null filter directive: " + nativeRequirement,
				filterDirective);
		Filter filter = null;
		try {
			filter = getContext().createFilter(filterDirective);
		} catch (InvalidSyntaxException e) {
			fail("Bad filter: " + nativeRequirement, e);
		}

		// check that the filter matches as expected
		for (Map<String, ?> matchingMap : MATCH_NATIVE_ATTRIBUTES) {
			if (isResolved) {
				assertTrue("filter does not match map: " + filter + ".matches("
						+ matchingMap + ")", filter.matches(matchingMap));
			} else {
				assertFalse("filter does match map: " + filter + ".matches("
						+ matchingMap + ")", filter.matches(matchingMap));
			}
		}

		if (isResolved) {
			BundleWiring wiring = b.adapt(BundleWiring.class);
			assertNotNull("Bundle has no wiring: " + b, wiring);
			if ((revision.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0) {
				List<BundleWire> hostWires = wiring
						.getRequiredWires(HostNamespace.HOST_NAMESPACE);
				assertEquals("Wrong number of hosts.", 1, hostWires.size());
				wiring = hostWires.get(0).getProviderWiring();
			}
			List<BundleWire> nativeWires = wiring
					.getRequiredWires(NativeNamespace.NATIVE_NAMESPACE);
			assertEquals("Unexpected number of native wires: " + b,
					isOptional ? 0 : 1,
					nativeWires.size());
			BundleWire nativeWire = nativeWires.get(0);
			assertEquals("Found wrong requirement in native wire.",
					nativeRequirement, nativeWire.getRequirement());

			// make sure we are wired to a capability from the system bundle
			assertEquals("Native provider is not the system bundle", 0,
					nativeWire.getProvider().getBundle().getBundleId());

			// this is a bit of a strange test, but it ensures the
			// capability that is wired to has the arbitrary matching
			// attributes configured into the framework
			Map<String, ?> wiredCapAttrs = nativeWire.getCapability()
					.getAttributes();
			assertTrue("filter does not match the wired to capability attrs: "
					+ filter + ".matches(" + wiredCapAttrs + ")",
					filter.matches(wiredCapAttrs));
		}
	}

	/**
	 * Tests basic native code invocation.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCode() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb2.jar");
		try {
			tb.start();
			assertNativeNamespace(tb, false, true);
		}
		catch (BundleException be) {
			fail("Native code not installed. " + reportProcessorOS(), be);
		}
		finally {
			tb.uninstall();
		}
	}


	/**
	 * Tests to add a FrameworkListener.
	 */
	public void testFrameworkListener() throws Exception {
		FrameworkEventCollector fec = new FrameworkEventCollector(
				FrameworkEvent.ERROR);
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb3.jar");
		getContext().addFrameworkListener(fec);
		tb.start();
		tb.uninstall();
		List<FrameworkEvent> result = fec.getList(1,
				10000 * OSGiTestCaseProperties
				.getScaling());
		getContext().removeFrameworkListener(fec);
		assertEquals("No FrameworkEvent received", 1, result.size());
		FrameworkEvent fe = result.get(0);
		assertEquals("No FrameworkEvent received", tb, fe.getBundle());
		Throwable t = fe.getThrowable();
		assertNotNull(t);
		assertTrue(t instanceof BundleException);
	}

	/**
	 * Tests the file system.
	 */
	public void testFileAccess() throws Exception {
		File file = getContext().getDataFile("testfile");
		if (file == null) {
			log("Framework lacks filesystem support, no error.");
			return;
		}
		PrintWriter out = new PrintWriter(new FileWriter(file));
		out.println("Line 1");
		out.println("Line 2");
		out.println("Line 3");
		out.close();
		BufferedReader in = new BufferedReader(new FileReader(file));
		assertEquals("Line 1", in.readLine());
		assertEquals("Line 2", in.readLine());
		assertEquals("Line 3", in.readLine());
		in.close();
		assertTrue(file.delete());
		try {
			in = new BufferedReader(new FileReader(file));
			fail("File was not gone, Error!");
		}
		catch (FileNotFoundException fnfe) {
			// expected
		}
	}

	public void testBundleZero() {
		// Bundle(0).update is tested in permission/tc5
		try {
			getContext().getBundle(0).start();
		}
		catch (BundleException e) {
			fail("bundle(0).start threw Exception", e);
		}
		try {
			getContext().getBundle(0).uninstall();
			fail("bundle(0).uninstall returned without Exception");
		}
		catch (BundleException e) {
			// expected
		}
	}

	public void testEERequirement() throws Exception {
		@SuppressWarnings("deprecation")
		final String ee = getContext()
				.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
		log("EE: " + ee);
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb7a.jar");
		try {
			tb.start();
		}
		catch (BundleException e) {
			fail("Required Execution Environment is available", e);
		}
		finally {
			tb.uninstall();
		}

		tb = getContext().installBundle(getWebServer() + "div.tb7b.jar");
		try {
			tb.start();
			fail("Required Execution Environment is not available");
		}
		catch (BundleException e) {
            // expecting exception, but bundle should not have resolved
            assertEquals("Bundle should not be resolved!", Bundle.INSTALLED, tb.getState());
		}
		finally {
			tb.uninstall();
		}

	}

	/**
	 * Tests native code selection filter. The bundle should be loaded even if
	 * no native code clause matches the selection filter, since there's an
	 * optional clause present (*).
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFilterOptional() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb12.jar");
		try {
			tb.start();
			assertNativeNamespace(tb, true, false);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests native code selection filter. The bundle should NOT be loaded if no
	 * native code clause matches the selection filter, since there's no
	 * optional clause present (*).
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFilterNoOptional() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb15.jar");
		try {
			tb.start();
			fail("Bundle should not start!");
		}
		catch (BundleException be) {
            // expecting exception, but bundle should not have resolved
            assertEquals("Bundle should not be resolved!", Bundle.INSTALLED, tb.getState());
			assertNativeNamespace(tb, false, false);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests native code selection filter. The bundle should only be loaded if
	 * at least one native code clause matches the selection filter, since
	 * there's no optional clause present (*). This test also checks if the new
	 * osname alias (win32) matches properly (OSGi R4).
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFilterAlias() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb16.jar");
		try {
			tb.start();
			assertNativeNamespace(tb, false, true);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests native code from a fragment bundle. The native code should be
	 * loaded from a fragment bundle of the host bundle.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFragment() throws Exception {
		Bundle tbFragment = getContext().installBundle(
				getWebServer() + "div.tb18.jar");
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb17.jar");
		try {
			tb.start();
			assertNativeNamespace(tbFragment, false, true);
		}
		finally {
			tb.uninstall();
			tbFragment.uninstall();
		}
	}

	/**
	 * Tests native code language filter. The bundle should NOT be loaded if no
	 * native code clause matches the os language, since there's no optional
	 * clause present (*).
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeLanguage() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb19.jar");
		try {
			tb.start();
			fail("Error: Bundle should NOT be loaded: language should not match");
		}
		catch (BundleException be) {
            // expecting exception, but bundle should not have resolved
            assertEquals("Bundle should not be resolved!", Bundle.INSTALLED, tb.getState());
			assertNativeNamespace(tb, false, false);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests native code language filter. The bundle should be loaded since all
	 * valid languages are included in the filter.
	 *
	 * @see http://ftp.ics.uci.edu/pub/ietf/http/related/iso639.txt for valid
	 *      language codes.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeLanguageSuccess() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb20.jar");
		try {
			tb.start();
			assertNativeNamespace(tb, false, true);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests native code os version. The bundle should NOT be loaded if no
	 * native code clause matches the os version range, since there's no
	 * optional clause present (*).
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeVersion() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb21.jar");
		try {
			tb.start();
			fail("Error: Bundle should NOT be loaded: os version out of range");
		}
		catch (BundleException be) {
            // expecting exception, but bundle should not have resolved
            assertEquals("Bundle should not be resolved!", Bundle.INSTALLED, tb.getState());
			assertNativeNamespace(tb, false, false);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests successful native code os version. The bundle should be loaded
	 * since the version range should contain all valid os versions.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeVersionSuccess() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb22.jar");
		try {
			tb.start();
			assertNativeNamespace(tb, false, true);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests that a bundle can require the osgi.native namespace directly in a
	 * Require-Capability header.
	 * 
	 * @throws BundleException
	 */
	public void testNativeCodeNamespaceRequirement() throws BundleException {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb26.jar");
		try {
			tb.start();
			assertNativeNamespace(tb, false, true);
		} finally {
			tb.uninstall();
		}
	}

	public void testNativeCodeNamespaceCapability() throws BundleException {
		Bundle tb = null;
		try {
			tb = getContext().installBundle(getWebServer() + "div.tb27.jar");
			fail("Expected to fail installation of bundle that declares a native capability.");
		} catch (BundleException e) {
			// expected, check the error type
			assertEquals("Wrong error code.", BundleException.MANIFEST_ERROR,
					e.getType());
		} finally {
			if (tb != null) {
				tb.uninstall();
			}
		}
	}

	public void testExecutionEnvironmentNamespaceCapability()
			throws BundleException {
		Bundle tb = null;
		try {
			tb = getContext().installBundle(getWebServer() + "div.tb28.jar");
			fail("Expected to fail installation of bundle that declares an execution environment capability.");
		} catch (BundleException e) {
			// expected, check the error type
			assertEquals("Wrong error code.", BundleException.MANIFEST_ERROR,
					e.getType());
		} finally {
			if (tb != null) {
				tb.uninstall();
			}
		}
	}

	public void testBundleEventConstants() {
		assertConstant(Integer.valueOf(0x00000001), "INSTALLED", BundleEvent.class);
		assertConstant(Integer.valueOf(0x00000002), "STARTED", BundleEvent.class);
		assertConstant(Integer.valueOf(0x00000004), "STOPPED", BundleEvent.class);
		assertConstant(Integer.valueOf(0x00000008), "UPDATED", BundleEvent.class);
		assertConstant(Integer.valueOf(0x00000010), "UNINSTALLED",
				BundleEvent.class);
		assertConstant(Integer.valueOf(0x00000020), "RESOLVED", BundleEvent.class);
		assertConstant(Integer.valueOf(0x00000040), "UNRESOLVED", BundleEvent.class);
		assertConstant(Integer.valueOf(0x00000080), "STARTING", BundleEvent.class);
		assertConstant(Integer.valueOf(0x00000100), "STOPPING", BundleEvent.class);
		assertConstant(Integer.valueOf(0x00000200), "LAZY_ACTIVATION",
				BundleEvent.class);
	}

	public void testFrameworkEventConstants() {
		assertConstant(Integer.valueOf(0x00000001), "STARTED", FrameworkEvent.class);
		assertConstant(Integer.valueOf(0x00000002), "ERROR", FrameworkEvent.class);
		assertConstant(Integer.valueOf(0x00000004), "PACKAGES_REFRESHED",
				FrameworkEvent.class);
		assertConstant(Integer.valueOf(0x00000008), "STARTLEVEL_CHANGED",
				FrameworkEvent.class);
		assertConstant(Integer.valueOf(0x00000010), "WARNING", FrameworkEvent.class);
		assertConstant(Integer.valueOf(0x00000020), "INFO", FrameworkEvent.class);
		assertConstant(Integer.valueOf(0x00000040), "STOPPED", FrameworkEvent.class);
		assertConstant(Integer.valueOf(0x00000080), "STOPPED_UPDATE",
				FrameworkEvent.class);
		assertConstant(Integer.valueOf(0x00000200), "WAIT_TIMEDOUT",
				FrameworkEvent.class);
	}

	public void testBundleGetEntry() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		try {

			URL url = bundle.getEntry(basePath + "tb10/Foo.class");
			assertNotNull(
					"Testing the method invocation with an existing entry", url);

			url = bundle.getEntry(basePath + "tb10/Nonexistent");
			assertNull(
					"Testing the method invocation with an nonexistent entries",
					url);

			bundle.uninstall();

			try {
				bundle.getEntry(basePath + "tb10/Nonexistent");
				fail("Testing the method invocation with an uninstalled bundle");
			}
			catch (IllegalStateException ex) {
				// This is an expected exception and can be ignored
				bundle = null;
			}
		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	public void testBundleGetEntryPaths() throws Exception {
		String[] expectedEntryPaths = {
				"org/osgi/test/cases/framework/div/tb10/Activator.class",
				"org/osgi/test/cases/framework/div/tb10/Bar.class",
				"org/osgi/test/cases/framework/div/tb10/Foo.class",
				"org/osgi/test/cases/framework/div/tb10/TestService.class",
				"org/osgi/test/cases/framework/div/tb10/TestServiceImpl.class"};

		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		try {
			Enumeration<String> enumeration = bundle
					.getEntryPaths(basePath + "tb10");
			assertNotNull("Check if some resource is returned", enumeration);

			int count = 0;
			while (enumeration.hasMoreElements()) {
				String entryPath = enumeration.nextElement();

				for (int i = 0; i < expectedEntryPaths.length; i++) {
					if (entryPath.equals(expectedEntryPaths[i])) {
						count++;
					}
				}
			}

			assertEquals("Checking the returned entries",
					expectedEntryPaths.length, count);

			enumeration = bundle.getEntryPaths(basePath + "tb10/nonexistent");
			assertNull(
					"Testing the method invocation with nonexistent entries",
					enumeration);

			bundle.uninstall();

			try {
				bundle.getEntryPaths(basePath + "tb10/incorrect");
				fail("Testing the method invocation with an uninstalled bundle");
			}
			catch (IllegalStateException ex) {
				// Ignore this exception
				bundle = null;
			}

		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	public void testBundleGetResource() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		Bundle fragment = getContext().installBundle(
				getWebServer() + "div.tb13.jar");

		try {
			URL url = bundle.getResource(basePath + "tb10/Foo.class");
			assertNotNull(
					"Testing the method invocation with an existing resource (using a absolute path)",
					url);
			url = bundle.getResource(basePath + "tb10/Nonexistent");
			assertNull(
					"Testing the method invocation with a nonexistent resource",
					url);

			url = fragment.getResource(basePath + "tb13/Foo.class");
			assertNull(
					"A fragment bundle cannot return a resource using the method getResource()",
					url);

			bundle.uninstall();

			try {
				bundle
						.getResource("/org/osgi/test/cases/framework/div/tb10/Foo.class");
				fail("Testing  the method invocation after uninstall the bundle");
			}
			catch (IllegalStateException ex) {
				// This is an expected exception and can be ignored
				bundle = null;
			}

		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
			if (fragment != null) {
				fragment.uninstall();
			}
		}
	}

	public void testBundleGetResourcesResolved() {
		doTestBundleGetResources(true);
	}

	public void testBundleGetResourcesUnresolved() {
		doTestBundleGetResources(false);
	}

	private void doTestBundleGetResources(boolean resolved) {
		Bundle tb25 = null;
		try {
			tb25 = install(resolved ? "div.tb25.resolved.jar" : "div.tb25.unresolved.jar");
		} catch (Exception e) {
			fail("Unexpected error installing test bundle.", e);
		}
		try {
			// sanity check for the root resources
			URL rootEntry = tb25.getEntry("resources/root.txt");
			assertNotNull("root.txt not found", rootEntry);
			assertEquals("Wrong resource", "root.txt", getValue(rootEntry));
			rootEntry = tb25.getEntry("resources/all.txt");
			assertEquals("Wrong resource", "root.all.txt", getValue(rootEntry));
			assertNotNull("root.all.txt not found", rootEntry);
			// Bundle-ClassPath of div.tb25 does not specify '.'
			// the root resources must not be found.
			URL resource = tb25.getResource("resources/root.txt");
			assertNull("Found unexpected resource.", resource);

			// 'a' resources must be found first for duplicate resources
			resource = tb25.getResource("resources/all.txt");
			assertNotNull("Did not find resource.", resource);
			assertEquals("Wrong resource", "a.all.txt", getValue(resource));

			// test non shadowed resources
			resource = tb25.getResource("resources/a.txt");
			assertNotNull("Did not find resource.", resource);
			resource = tb25.getResource("resources/b.txt");
			assertNotNull("Did not find resource.", resource);

			// test get resources for shadowed resource
			// again the root resource must not be found
			Enumeration<URL> resources = null;
			try {
				resources = tb25.getResources("resources/all.txt");
			} catch (IOException e) {
				fail("Unexpected io exception.", e);
			}
			assertNotNull("Did not find resources.", resources);
			// there are only two resources from 'a' and 'b' in that order.
			try {
				resource = resources.nextElement();
				assertNotNull("Did not find resource.", resource);
				assertEquals("Wrong resource", "a.all.txt", getValue(resource));
				resource = resources.nextElement();
				assertNotNull("Did not find resource.", resource);
				assertEquals("Wrong resource", "b.all.txt", getValue(resource));
				assertFalse("Expecting no more resources.", resources.hasMoreElements());
			} catch (NoSuchElementException e) {
				fail("Wrong number of elements.", e);
			}
			// after all the getResource calls the bundle must be RESOLVED or INSTALLED depending on resolved param
			assertEquals("Wrong state for bundle.", resolved ? Bundle.RESOLVED : Bundle.INSTALLED, tb25.getState());
		} finally {
			try {
				tb25.uninstall();
			} catch (BundleException e) {
				// ignore
			}
		}
	}

	private String getValue(URL url) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			return reader.readLine();
		} catch (IOException e) {
			return null;
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					// ignore
				}
		}
	}

	public void testBundleGetResources() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		Bundle fragment = getContext().installBundle(
				getWebServer() + "div.tb13.jar");

		try {
			Enumeration<URL> enumeration = bundle.getResources(basePath
					+ "tb10/Foo.class");
			assertNotNull(
					"Testing the method invocation with an existing resource (using a absolute path)",
					enumeration);
			enumeration = bundle.getResources(basePath + "tb10/Nonexistent");
			assertNull(
					"Testing the method invocation with a nonexistent resource",
					enumeration);

			enumeration = fragment.getResources(basePath + "tb13/Foo.class");
			assertNull(
					"A fragment bundle cannot return a resource using the method getResource()",
					enumeration);

			bundle.uninstall();

			try {
				bundle.getResources(basePath + "tb10/Foo.class");
				fail("Testing  the method invocation after uninstall the bundle");
			}
			catch (IllegalStateException ex) {
				// This is an expected exception and can be ignored
				bundle = null;
			}

		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
			if (fragment != null) {
				fragment.uninstall();
			}
		}
	}

	public void testBundleGetSymbolicName1() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		try {
			String bsn = bundle.getSymbolicName();
			assertEquals(
					"Testing the method getSymbolicName() with a symbolic name in the manifest",
					basePkg + "tb10", bsn);

			bundle.uninstall();
			bsn = bundle.getSymbolicName();
			bundle = null;
			assertEquals(
					"Testing the method getSymbolicName() with a symbolic name in the manifest",
					basePkg + "tb10", bsn);

		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	public void testBundleGetSymbolicName2() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb11.jar");
		try {
			String bsn = bundle.getSymbolicName();
			assertNull(
					"Testing the method getSymbolicName() without a symbolic name in the manifest",
					bsn);

			bundle.uninstall();
			bsn = bundle.getSymbolicName();
			bundle = null;

			assertNull(
					"Testing the method getSymbolicName() after uninstall the bundle (without a symbolic name in the manifest)",
					bsn);
		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	public void testBundleGetBundleContext1() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		try {
			assertNull("BundleContext for installed bundle must be null",
					bundle.getBundleContext());

			bundle.start();
			assertNotNull("BundleContext for started bundle must not be null",
					bundle.getBundleContext());

			assertEquals("Bundle id via BundleContext must equal original id",
					bundle.getBundleId(), bundle.getBundleContext().getBundle()
							.getBundleId());

			bundle.stop();
			assertNull("BundleContext for stopped bundle must be null", bundle
					.getBundleContext());
		}
		finally {
			bundle.uninstall();
			assertNull("BundleContext for uninstalled bundle must be null",
					bundle.getBundleContext());
		}

		bundle = getContext().getBundle(0);

		assertNotNull("BundleContext for system bundle must not be null",
				bundle.getBundleContext());

		assertEquals("Bundle id via BundleContext must equal zero", 0L, bundle
				.getBundleContext().getBundle().getBundleId());

		assertEquals(
				"BundleContext for test case should match context passed to activator",
				getContext(), getContext().getBundle().getBundleContext());
	}

	public void testBundleGetBundleContext2() throws Exception {
		Bundle host, fragment;

		fragment = getContext().installBundle(getWebServer() + "div.tb18.jar");
		host = getContext().installBundle(getWebServer() + "div.tb17.jar");

		try {
			host.start(); // resolve the bundles
			assertNotNull("BundleContext for host bundle must not be null",
					host.getBundleContext());
			assertNull("BundleContext for fragment bundle must be null",
					fragment.getBundleContext());

			host.stop();
			assertNull("BundleContext for stopped host bundle must be null",
					host.getBundleContext());
			assertNull("BundleContext for fragment bundle must be null",
					fragment.getBundleContext());
		}
		finally {
			fragment.uninstall();
			host.uninstall();
		}
	}

	public void testBundleLoadClass1() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		try {
			bundle.start();
			Class< ? > clazz = null;
			try {
				clazz = bundle.loadClass(basePkg + "tb10.Foo");
			}
			catch (ClassNotFoundException ex) {
				fail(
						"Testing the method loadClass() with an installed bundle and a existing class",
						ex);
			}

			ServiceReference< ? > sr = getContext()
					.getServiceReference(
					basePkg + "tb10.TestService");

			Object service = getContext().getService(sr);
			ClassLoader classLoader = (ClassLoader) service.getClass()
					.getMethod("getClassLoader", (Class[]) null).invoke(service, (Object[]) null);
			assertEquals(
					"Expecting the ClassLoader of the class and the bundle to be the same",
					clazz.getClassLoader(), classLoader);

			try {
				clazz = bundle
						.loadClass(basePkg + "tb10.NonExistent");
				fail("Testing the method loadClass() with an installed bundle and a nonexistent class");
			}
			catch (ClassNotFoundException ex) {
				// This is an expected exception and can be ignored
			}

			bundle.uninstall();
			try {
				bundle.loadClass(basePkg + "tb10.Foo");
				fail("Testing the method after uninstall the bundle");
			}
			catch (IllegalStateException ex) {
				// This is an expected exception and can be ignored
				bundle = null;
			}
		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	public void testBundleLoadClass2() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb13.jar");
		try {
			try {
				bundle.loadClass(basePkg + "tb13.Foo");
				fail("Testing the method loadClass() with a fragment bundle");
			}
			catch (ClassNotFoundException ex) {
				// expected
			}

			try {
				bundle.loadClass(basePkg + "tb13.Nonexistent");
				fail("Testing the method loadClass() with a fragment bundle");
			}
			catch (ClassNotFoundException ex) {
				// expected
			}
		}
		finally {
			bundle.uninstall();
		}
	}

	/**
	 * Tests service registration.
	 */
	public void testBundleContextRegisterService() throws Exception {
		Bundle tb24a = getContext().installBundle(
				getWebServer() + "div.tb24a.jar");
		Bundle tb24b = getContext().installBundle(
				getWebServer() + "div.tb24b.jar");
		Bundle tb24c = getContext().installBundle(
				getWebServer() + "div.tb24c.jar");

		tb24a.start();
		tb24b.start();

		try {
			tb24c.start();
			tb24c.stop();
		}
		catch (BundleException ex) {
			fail("A bundle can register a service when the package is shared");
		}
		finally {
			tb24b.stop();
			tb24a.stop();

			tb24c.uninstall();
			tb24b.uninstall();
			tb24a.uninstall();
		}
	}

	private String reportProcessorOS() {
		String os = getContext().getProperty("org.osgi.framework.os.name");
		String proc = getContext().getProperty("org.osgi.framework.processor");
		StringBuffer sb = new StringBuffer();
		sb.append("Current osname=\"").append(os).append("\" processor=\"")
				.append(proc);
		sb
				.append("\". For allowed constants see https://docs.osgi.org/reference/");
		return sb.toString();
	}

}

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
package org.osgi.test.cases.framework.junit.classloading;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.BiFunction;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;
import org.osgi.test.support.wiring.Wiring;

/**
 * This class contains tests related with the framework class loading policies.
 *
 * @author left
 * @author $Id$
 */
public class ClassLoadingTests extends DefaultTestBundleControl {

	protected void tearDown() {
		Wiring.synchronousRefreshBundles(getContext());
	}

	// Service Registry --------------------------

	/**
	 * BundleContext.getServiceReference returns the highest ranked service that
	 * BundleContext.getServiceReferences returns.
	 *
	 * This test case will check the behavior of method getServiceReference()
	 * when more than one service is installed.
	 *
	 * @spec BundleContext.getServiceReference(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testBundleContextGetReference001() throws Exception {
		Bundle tb1;
		Bundle tb1a;
		Bundle tb1b;
		Bundle tb4a;

		tb1 = installBundle("classloading.tb1.jar");
		tb1.start();
		tb1a = installBundle("classloading.tb1a.jar");
		tb1a.start();
		tb1b = installBundle("classloading.tb1b.jar");
		tb1b.start();

		tb4a = getContext().installBundle(
				getWebServer() + "classloading.tb4a.jar");
		try {
			trace("Checking the service reference returned by BundleContext.getServiceReference()");
			tb4a.start();
			tb4a.stop();
		}
		catch (BundleException ex) {
			if (ex.getCause() != null) {
				fail(ex.getCause().getMessage());
			}
			else {
				fail(ex.getMessage());
			}
		}
		finally {
			tb4a.uninstall();

			tb1b.stop();
			tb1b.uninstall();
			tb1a.stop();
			tb1a.uninstall();
			tb1.stop();
			tb1.uninstall();
		}
	}

	/**
	 * If getServiceReferences returns no service references, then
	 * getServiceReference returns null.
	 *
	 * This test case will check the behavior of method getServiceReference()
	 * without any installed services.
	 *
	 * @spec BundleContext.getServiceReference(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testBundleContextGetReference002() throws Exception {
		Bundle tb4b;

		tb4b = getContext().installBundle(
				getWebServer() + "classloading.tb4b.jar");
		try {
			trace("Checking the behavior of BundleContext.getServiceReference() when BundleContext.getServiceReferences() returns no references");
			tb4b.start();
			tb4b.stop();
		}
		catch (BundleException ex) {
			if (ex.getCause() != null) {
				fail(ex.getCause().getMessage());
			}
			else {
				fail(ex.getMessage());
			}
		}
		finally {
			tb4b.uninstall();
		}
	}

	/**
	 * Some bundles may wish to get all service references available in the
	 * framework regardless of what version of a package they are wired to. A
	 * new method is added (BundleContext.getAllServiceReferences(String,
	 * String)) to allow a bundle to get all ServiceReference objects.
	 * getAllServiceReferences returns all the ServiceReference objects without
	 * performing any class tests.
	 *
	 * The test bundle will importing the version 3.0.0 of the interface
	 * SomeService and check the behavior of method getServiceReferences() when
	 * two services using the interface version 1.0.0 are installed. The method
	 * getAllServiceReferences() must returns all services independent of the
	 * interface wired to the test bundle.
	 *
	 * @spec BundleContext.getAllServiceReferences(String, String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testBundleContextGetAllServiceReferences001() throws Exception {
		Bundle tb1;
		Bundle tb1a;
		Bundle tb1b;
		Bundle tb3;
		Bundle tb4c;

		tb1 = installBundle("classloading.tb1.jar");
		tb1.start();
		tb1a = installBundle("classloading.tb1a.jar");
		tb1a.start();
		tb1b = installBundle("classloading.tb1b.jar");
		tb1b.start();
		tb3 = installBundle("classloading.tb3.jar");
		tb3.start();

		tb4c = getContext().installBundle(
				getWebServer() + "classloading.tb4c.jar");
		try {
			trace("Checking if the method getAllServiceReferences returns the number of services as expected");
			tb4c.start();
			tb4c.stop();
		}
		catch (BundleException ex) {
			if (ex.getCause() != null) {
				fail(ex.getCause().getMessage());
			}
			else {
				fail(ex.getMessage());
			}
		}
		finally {
			tb4c.uninstall();

			tb3.stop();
			tb3.uninstall();
			tb1b.stop();
			tb1b.uninstall();
			tb1a.stop();
			tb1a.uninstall();
			tb1.stop();
			tb1.uninstall();
		}
	}

	/**
	 * This test case will check the behavior of method isAssignableTo() when
	 * the wired package of the getter bundle (the bundle requesting the
	 * service) is the same of the registrant bundle.
	 *
	 * @spec ServiceReference.isAssignableTo(Bundle, String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testBundleContextIsAssignableTo001() throws Exception {
		Bundle tb1;
		Bundle tb1a;
		Bundle tb4d;

		tb1 = installBundle("classloading.tb1.jar");
		tb1.start();
		tb1a = installBundle("classloading.tb1a.jar");
		tb1a.start();

		tb4d = getContext().installBundle(
				getWebServer() + "classloading.tb4d.jar");
		try {
			trace("Checking if the method isAssignableTo returns true when the package source of the getter bundle is the same of the registrant bundle");
			tb4d.start();
			tb4d.stop();
		}
		catch (BundleException ex) {
			if (ex.getCause() != null) {
				fail(ex.getCause().getMessage());
			}
			else {
				fail(ex.getMessage());
			}
		}
		finally {
			tb4d.uninstall();

			tb1a.stop();
			tb1a.uninstall();
			tb1.stop();
			tb1.uninstall();
		}
	}

	/**
	 * This test case will check the behavior of method isAssignableTo() when
	 * the wired package of the getter bundle (the bundle requesting the
	 * service) is not the same of the registrant bundle.
	 *
	 * @spec ServiceReference.isAssignableTo(Bundle, String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testBundleContextIsAssignableTo002() throws Exception {
		Bundle tb1;
		Bundle tb1a;
		Bundle tb3;
		Bundle tb4e;

		tb1 = installBundle("classloading.tb1.jar");
		tb1.start();
		tb1a = installBundle("classloading.tb1a.jar");
		tb1a.start();
		tb3 = installBundle("classloading.tb3.jar");
		tb3.start();

		tb4e = getContext().installBundle(
				getWebServer() + "classloading.tb4e.jar");
		try {
			trace("Checking if the method isAssignableTo returns false when the package source of the getter bundle is not the same of the registrant bundle");
			tb4e.start();
			tb4e.stop();
		}
		catch (BundleException ex) {
			if (ex.getCause() != null) {
				fail(ex.getCause().getMessage());
			}
			else {
				fail(ex.getMessage());
			}
		}
		finally {
			tb4e.uninstall();

			tb3.stop();
			tb3.uninstall();
			tb1a.stop();
			tb1a.uninstall();
			tb1.stop();
			tb1.uninstall();
		}
	}

	private static final String	SOME_SERVICE_CLASS	= "org.osgi.test.cases.framework.classloading.exports.service.SomeService";
	private static final String	SOME_SERVICE_IMPL	= "org.osgi.test.cases.framework.classloading.exports.service.SimpleSomeServiceImpl";

	private enum IMPL_BUNDLE {
		VERSION1, VERSION2, REGISTRANT, USER
	}

	/**
	 * User bundle has no package source; should see other's service
	 * 
	 * @throws Exception
	 */
	public void testBundleContextIsAssignableTo003() throws Exception {
		BiFunction<Bundle,Object,ServiceReference< ? >> register = (b, s) -> {
			return b.getBundleContext()
					.registerService(SOME_SERVICE_CLASS, s, null)
					.getReference();
		};
		doTestBundleContextIsAssibnableTo( //
				"classloading.tb18c.jar", //
				"classloading.tb18d.jar", //
				IMPL_BUNDLE.VERSION1, //
				true, //
				register);
	}

	/**
	 * Tests two bundles have private packages; should not see eachother's
	 * service
	 * 
	 * @throws Exception
	 */
	public void testBundleContextIsAssignableTo004() throws Exception {
		BiFunction<Bundle,Object,ServiceReference< ? >> register = (b, s) -> {
			return b.getBundleContext()
					.registerService(SOME_SERVICE_CLASS, s, null)
					.getReference();
		};
		doTestBundleContextIsAssibnableTo( //
				"classloading.tb18a.jar", //
				"classloading.tb18b.jar", //
				IMPL_BUNDLE.REGISTRANT, //
				false, //
				register);
	}

	/**
	 * Test user has package source, registrant does not, a factory from another
	 * bundle is used; should see the service
	 * 
	 * @throws Exception
	 */
	public void testBundleContextIsAssignableTo005() throws Exception {
		BiFunction<Bundle,Object,ServiceReference< ? >> register = (b, s) -> {
			return b.getBundleContext()
					.registerService(SOME_SERVICE_CLASS,
							new ServiceFactory<Object>() {

								@Override
								public Object getService(Bundle bundle,
										ServiceRegistration<Object> registration) {
									return s;
								}

								@Override
								public void ungetService(Bundle bundle,
										ServiceRegistration<Object> registration,
										Object service) {
								}

							}, null)
					.getReference();
		};
		doTestBundleContextIsAssibnableTo( //
				"classloading.tb18d.jar", //
				"classloading.tb18c.jar", //
				IMPL_BUNDLE.VERSION1, //
				true, //
				register);
	}

	/**
	 * Test user has package source, registrant does not, register an object
	 * that has a different package source; should not see service
	 * 
	 * @throws Exception
	 */
	public void testBundleContextIsAssignableTo006() throws Exception {
		BiFunction<Bundle,Object,ServiceReference< ? >> register = (b, s) -> {
			return b.getBundleContext()
					.registerService(SOME_SERVICE_CLASS, s, null)
					.getReference();
		};
		doTestBundleContextIsAssibnableTo( //
				"classloading.tb18d.jar", //
				"classloading.tb18c.jar", //
				IMPL_BUNDLE.VERSION2, //
				false, //
				register);
	}

	/**
	 * Test user has package source, registrant does not, register an object
	 * that has same package source as user; should see service
	 * 
	 * @throws Exception
	 */
	public void testBundleContextIsAssignableTo007() throws Exception {
		BiFunction<Bundle,Object,ServiceReference< ? >> register = (b, s) -> {
			return b.getBundleContext()
					.registerService(SOME_SERVICE_CLASS, s, null)
					.getReference();
		};
		doTestBundleContextIsAssibnableTo( //
				"classloading.tb18d.jar", //
				"classloading.tb18c.jar", //
				IMPL_BUNDLE.VERSION1, //
				true, //
				register);
	}

	private void doTestBundleContextIsAssibnableTo(String registrantLoc,
			String userLoc, IMPL_BUNDLE implBundle, boolean expected,
			BiFunction<Bundle,Object,ServiceReference< ? >> register)
			throws Exception {
		List<Bundle> bundles = new ArrayList<>();

		try {
			Bundle exporter1;
			Bundle exporter2;
			Bundle registrant;
			Bundle user;
			bundles.add(exporter1 = installBundle("classloading.tb1.jar"));
			bundles.add(exporter2 = installBundle("classloading.tb2.jar"));
			bundles.add(registrant = installBundle(registrantLoc));
			bundles.add(user = installBundle(userLoc));
			bundles.forEach(b -> {
				try {
					b.start();
				} catch (BundleException e) {
					sneakyThrow(e);
				}
			});
			Class< ? > serviceImpl;
			switch (implBundle) {
				case VERSION1 :
					serviceImpl = exporter1.loadClass(SOME_SERVICE_IMPL);
					break;
				case VERSION2 :
					serviceImpl = exporter2.loadClass(SOME_SERVICE_IMPL);
					break;
				case REGISTRANT :
					serviceImpl = registrant.loadClass(SOME_SERVICE_IMPL);
					break;
				case USER :
					serviceImpl = user.loadClass(SOME_SERVICE_IMPL);
				default :
					throw new RuntimeException("unknown");
			}
			ServiceReference< ? > ref = register.apply(registrant,
					serviceImpl.getConstructor().newInstance());
			assertEquals("Wrong result from isAssignableTo.", expected,
					ref.isAssignableTo(user,
							"org.osgi.test.cases.framework.classloading.exports.service.SomeService"));

		} finally {
			bundles.forEach(b -> {
				try {
					b.uninstall();
				} catch (BundleException e) {
					sneakyThrow(e);
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
		throw (E) e;
	}

	/**
	 * The service registry must deal with multiple versions of a particular
	 * interface being published. It should record the module which registered
	 * each interface and use this information to match against a module later
	 * attempting to find the interface. A find should be satisfied with the
	 * version of the service interface imported by the module attempting to
	 * find the interface.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testServiceRegistryWithMultipleServices001() throws Exception {
		Bundle tb1;
		Bundle tb1a;
		Bundle tb2;
		Bundle tb2a;
		Bundle tb4f;
		Bundle tb4g;

		tb1 = installBundle("classloading.tb1.jar");
		tb1.start();
		tb2 = installBundle("classloading.tb2.jar");
		tb2.start();

		try {
			trace("Checking if the correct service is imported");
			tb4f = getContext().installBundle(
					getWebServer() + "classloading.tb4f.jar");
			tb1a = installBundle("classloading.tb1a.jar");
			tb1a.start();
			tb4f.start();
			tb4f.stop();
			tb1a.stop();
			tb1a.uninstall();
			tb4f.uninstall();

			trace("Checking if the correct service is imported");
			tb4g = getContext().installBundle(
					getWebServer() + "classloading.tb4g.jar");
			tb2a = installBundle("classloading.tb2a.jar");
			tb2a.start();
			tb4g.start();
			tb4g.stop();
			tb2a.stop();
			tb2a.uninstall();
			tb4g.uninstall();
		}
		catch (BundleException ex) {
			if (ex.getCause() != null) {
				fail(ex.getCause().getMessage());
			}
			else {
				fail(ex.getMessage());
			}
		}
		finally {
			tb2.stop();
			tb2.uninstall();
			tb1.stop();
			tb1.uninstall();
		}
	}

	// Service Events ----------------------------

	/**
	 * Service events must only be delivered to event listeners which can
	 * validly cast the event.
	 *
	 * Tests the case which the event cannot be delivered.
	 *
	 * @spec ServiceListener.serviceChanged(ServiceEvent)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testServiceListener001() throws Exception {
		Bundle tb1;
		Bundle tb1a;
		Bundle tb2;
		Bundle tb5;
		Bundle tb5a;
		Method method;
		Object service;
		ServiceReference< ? > serviceReference;

		tb1 = installBundle("classloading.tb1.jar");
		tb1.start();
		tb1a = installBundle("classloading.tb1a.jar");
		tb2 = installBundle("classloading.tb2.jar");
		tb2.start();
		tb5 = installBundle("classloading.tb5.jar");
		tb5.start();

		tb5a = getContext().installBundle(
				getWebServer() + "classloading.tb5a.jar");
		try {
			tb5a.start();

			tb1a.start();
			tb1a.stop();

			try {
				Sleep.sleep(1000);
			}
			catch (InterruptedException ex) {
				// Ignore this exception
			}

			// Check if the some event is delivered
			serviceReference = getContext()
					.getServiceReference(
							"org.osgi.test.cases.framework.classloading.exports.listener.ServiceListenerTester");
			service = getContext().getService(serviceReference);
			method = service.getClass().getMethod("getServiceEventDelivered",
					(Class[]) null);
			assertNull(
					"Checking if an event is delivered for a bundle which imports a non-assignable service interface",
					method.invoke(service, (Object[]) null));

			getContext().ungetService(serviceReference);

			tb5a.stop();
		}
		finally {
			tb5a.uninstall();

			tb5.start();
			tb5.uninstall();
			tb1a.uninstall();
			tb2.stop();
			tb2.uninstall();
			tb1.stop();
			tb1.uninstall();
		}
	}

	/**
	 * Service events must only be delivered to event listeners which can
	 * validly cast the event.
	 *
	 * Tests the case which the event must be delivered.
	 *
	 * @spec ServiceListener.serviceChanged(ServiceEvent)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testServiceListener002() throws Exception {
		Bundle tb1;
		Bundle tb1a;
		Bundle tb5;
		Bundle tb5b;
		Method method;
		Object service;
		ServiceReference< ? > serviceReference;

		tb1 = installBundle("classloading.tb1.jar");
		tb1.start();
		tb1a = installBundle("classloading.tb1a.jar");
		tb5 = installBundle("classloading.tb5.jar");
		tb5.start();

		tb5b = getContext().installBundle(
				getWebServer() + "classloading.tb5b.jar");
		try {
			tb5b.start();

			tb1a.start();
			tb1a.stop();

			try {
				Sleep.sleep(1000);
			}
			catch (InterruptedException ex) {
				// Ignore this exception
			}

			// Check if the some event is delivered
			serviceReference = getContext()
					.getServiceReference(
							"org.osgi.test.cases.framework.classloading.exports.listener.ServiceListenerTester");
			service = getContext().getService(serviceReference);
			method = service.getClass().getMethod("getServiceEventDelivered",
					(Class[]) null);
			assertNotNull(
					"Checking if an event is delivered for a bundle which imports an assignable service interface",
					method.invoke(service, (Object[]) null));

			getContext().ungetService(serviceReference);

			tb5b.stop();
		}
		finally {
			tb5b.uninstall();

			tb5.start();
			tb5.uninstall();
			tb1a.uninstall();
			tb1.stop();
			tb1.uninstall();
		}
	}

	/**
	 * Some bundles may wish to listen to all service events regardless of what
	 * version of a package they are wired to. A new type of ServiceListener is
	 * added (AllServiceListener) to allow a bundle to listen to all service
	 * events. When an AllServiceLisetener is used the framework does not do
	 * class checks on the ServiceReference for the ServiceEvent before
	 * delivering it to the AllServiceListener.
	 *
	 * @spec AllServiceListener.serviceChanged(ServiceEvent)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testAllServiceListener001() throws Exception {
		Bundle tb1;
		Bundle tb1a;
		Bundle tb2;
		Bundle tb2a;
		Bundle tb5;
		Bundle tb5c;
		Method method;
		Object service;
		ServiceReference< ? > serviceReference;

		tb1 = installBundle("classloading.tb1.jar");
		tb1.start();
		tb1a = installBundle("classloading.tb1a.jar");
		tb2 = installBundle("classloading.tb2.jar");
		tb2.start();
		tb2a = installBundle("classloading.tb2a.jar");
		tb5 = installBundle("classloading.tb5.jar");
		tb5.start();

		tb5c = getContext().installBundle(
				getWebServer() + "classloading.tb5c.jar");
		try {
			tb5c.start();

			// This bundle install a service which is assignable to the imported
			// interface
			tb1a.start();
			tb1a.stop();

			try {
				Sleep.sleep(1000);
			}
			catch (InterruptedException ex) {
				// Ignore this exception
			}

			// Check if the some event is delivered
			serviceReference = getContext()
					.getServiceReference(
							"org.osgi.test.cases.framework.classloading.exports.listener.AllServiceListenerTester");
			service = getContext().getService(serviceReference);
			method = service.getClass().getMethod("getServiceEventDelivered",
					(Class[]) null);
			assertNotNull(
					"Checking if an event is delivered for a bundle which imports a assignable service interface",
					method.invoke(service, (Object[]) null));

			getContext().ungetService(serviceReference);

			// This bundle install a service which is not assignable to the
			// imported interface
			tb2a.start();
			tb2a.stop();

			try {
				Sleep.sleep(1000);
			}
			catch (InterruptedException ex) {
				// Ignore this exception
			}

			// Check if the some event is delivered
			serviceReference = getContext()
					.getServiceReference(
							"org.osgi.test.cases.framework.classloading.exports.listener.AllServiceListenerTester");
			service = getContext().getService(serviceReference);
			method = service.getClass().getMethod("getServiceEventDelivered",
					(Class[]) null);
			assertNotNull(
					"Checking if an event is delivered for a bundle which imports a non-assignable service interface",
					method.invoke(service, (Object[]) null));

			getContext().ungetService(serviceReference);

			tb5c.stop();
		}
		finally {
			tb5c.uninstall();

			tb5.uninstall();
			tb2a.uninstall();
			tb2.uninstall();
			tb1a.uninstall();
			tb1.uninstall();
		}
	}

	/**
	 * Some bundles may wish to listen to all service events regardless of what
	 * version of a package they are wired to. A new type of ServiceListener is
	 * added (AllServiceListener) to allow a bundle to listen to all service
	 * events. When an AllServiceLisetener is used the framework does not do
	 * class checks on the ServiceReference for the ServiceEvent before
	 * delivering it to the AllServiceListener.
	 *
	 * This test uses an AllServiceListener which is also an
	 * UnfilteredServiceListener. The listener is added with a filter that can
	 * never match (!(objectClass=*)).
	 *
	 * @spec AllServiceListener.serviceChanged(ServiceEvent)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testUnfilteredAllServiceListener001() throws Exception {
		Bundle tb1;
		Bundle tb1a;
		Bundle tb2;
		Bundle tb2a;
		Bundle tb5;
		Bundle tb5d;
		Method method;
		Object service;
		ServiceReference< ? > serviceReference;

		tb1 = installBundle("classloading.tb1.jar");
		tb1.start();
		tb1a = installBundle("classloading.tb1a.jar");
		tb2 = installBundle("classloading.tb2.jar");
		tb2.start();
		tb2a = installBundle("classloading.tb2a.jar");
		tb5 = installBundle("classloading.tb5.jar");
		tb5.start();

		tb5d = installBundle("classloading.tb5d.jar");
		try {
			tb5d.start();

			// This bundle install a service which is assignable to the imported
			// interface
			tb1a.start();
			tb1a.stop();

			try {
				Sleep.sleep(1000);
			}
			catch (InterruptedException ex) {
				// Ignore this exception
			}

			// Check if the some event is delivered
			serviceReference = getContext()
					.getServiceReference(
							"org.osgi.test.cases.framework.classloading.exports.listener.AllServiceListenerTester");
			service = getContext().getService(serviceReference);
			method = service.getClass().getMethod("getServiceEventDelivered",
					(Class[]) null);
			assertNotNull(
					"Checking if an event is delivered for a bundle which imports a assignable service interface",
					method.invoke(service, (Object[]) null));

			getContext().ungetService(serviceReference);

			// This bundle install a service which is not assignable to the
			// imported interface
			tb2a.start();
			tb2a.stop();

			try {
				Sleep.sleep(1000);
			}
			catch (InterruptedException ex) {
				// Ignore this exception
			}

			// Check if the some event is delivered
			serviceReference = getContext()
					.getServiceReference(
							"org.osgi.test.cases.framework.classloading.exports.listener.AllServiceListenerTester");
			service = getContext().getService(serviceReference);
			method = service.getClass().getMethod("getServiceEventDelivered",
					(Class[]) null);
			assertNotNull(
					"Checking if an event is delivered for a bundle which imports a non-assignable service interface",
					method.invoke(service, (Object[]) null));

			getContext().ungetService(serviceReference);

			tb5d.stop();
		}
		finally {
			tb5d.uninstall();

			tb5.uninstall();
			tb2a.uninstall();
			tb2.uninstall();
			tb1a.uninstall();
			tb1.uninstall();
		}
	}

	

	// Permission Checking -----------------------

	/**
	 * Since Java SE 9 added support for the Java Platform Module System to Java
	 * SE, The set of java.* provided by the Java platform is no longer constant
	 * for a specific version of the Java Platform. It depends on the set of
	 * modules that are enabled for the runtime instance. The OSGi framework
	 * specification is updated to support requirements on java.* packages.
	 * Explicitly, this means, a bundle can now declare imports for java.*
	 * packages. But, exporting java.* packages is not allowed by a bundle and
	 * doing so will result in an error.
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testJavaPackageExplicityExportImport001() {
		Bundle tb7a;
		Bundle tb7b;

		try {
			trace("Installing a bundle which declares imports for java.*");
			tb7a = getContext().installBundle(
					getWebServer() + "classloading.tb7a.jar");
			tb7a.uninstall();
		}
		catch (BundleException ex) {
			fail("Should not have thrown the exception: " + ex.toString());
		}

		try {
			trace("Installing a bundle which declares exports for java.*");
			tb7b = getContext().installBundle(
					getWebServer() + "classloading.tb7b.jar");
			tb7b.uninstall();
			fail("A bundle which declares exports for java.* packages should fail to install");
		}
		catch (BundleException ex) {
			assertEquals("It should throw a bundle exception of type manifest error", BundleException.MANIFEST_ERROR, ex.getType());
		}
	}

	/**
	 * All other packages accessible via the system class path must be hidden
	 * from executing bundles.
	 *
	 * @ Bundle.start()
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testHiddenPackages001() throws Exception {
		Bundle tb7c;
		String bootPackages;

		tb7c = getContext().installBundle(
				getWebServer() + "classloading.tb7c.jar");
		try {
			try {
				ClassLoader.getSystemClassLoader().loadClass(
						"javax.xml.parsers.SAXParser");
			}
			catch (ClassNotFoundException ex) {
				fail("A class required by this test cannot be loaded");
			}

			bootPackages = getProperty(Constants.FRAMEWORK_BOOTDELEGATION, "");
			if (bootPackages != null) {
				StringTokenizer packages = new StringTokenizer(bootPackages, ",");
				while(packages.hasMoreTokens()) {
					String pkg = packages.nextToken().trim();
					if (pkg.equals("*") || pkg.indexOf("org.*") != -1 || pkg.indexOf("org.omg.*") != -1 || pkg.indexOf("org.omg.CORBA") != -1) {
						fail("This test does not run with the org.omg.CORBA package available in '" + Constants.FRAMEWORK_BOOTDELEGATION + "'");
					}
				}
			}

			trace("Installing a bundle which uses a package accessible using the system class loader");
			try {
				tb7c.start();
				tb7c.stop();
				fail("All packages (except java.*) loaded by system class loader must be hidden from executing bundles");
			} catch (BundleException be) {
				// expected
			}
		}
		finally {
			tb7c.uninstall();
		}
	}

	// Class Loading Search Order ----------------

	/**
	 * When an R4 bundle's class loader attempts to load a class or find a
	 * resource, the search must be performed in a specified order.
	 *
	 * If the class or resource is in a java.* package, the request is delegated
	 * to the parent class loader; otherwise the search continues with the next
	 * step. If the request is delegated to the parent class loader and the
	 * class or resource is not found, then the search terminates and the
	 * request fails.
	 *
	 * If the class or resource is a package included in the boot delegation
	 * list, the request is delegated to the parent classloader.
	 *
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testClassLoadingSearchOrder001() throws Exception {
		Bundle tb8a;
		Bundle tb8b;
		Bundle tb8c;
		Bundle tb8d;
		Class< ? > clazz;

		tb8a = installBundle("classloading.tb8a.jar");
		tb8b = installBundle("classloading.tb8b.jar");
		tb8c = installBundle("classloading.tb8c.jar");

		try {
			tb8d = installBundle("classloading.tb8d.jar");

			// The loading class must be loaded by the parent class loader
			clazz = tb8d.loadClass("java.lang.Math");
			assertTrue("Checking the number of class methods", clazz
					.getMethods().length != 0);

			// Loading a class which does not exist when loaded by the parent
			// class loader
			try {
				clazz = tb8d.loadClass("java.lang.SomeClass");
				fail("The classes belongs to package java.lang must be loaded only with the parent class loader");
			}
			catch (ClassNotFoundException ex) {
				// Expected exception
			}

			tb8d.uninstall();
		}
		catch (BundleException ex) {
			if (ex.getCause() == null) {
				fail(ex.getMessage());
			}
			else {
				fail(ex.getCause().getMessage());
			}
		}
		finally {

			tb8c.uninstall();
			tb8b.uninstall();
			tb8a.uninstall();
		}
	}

	/**
	 * If the requested class or resource is in a package that is imported using
	 * Import-Package or that is dynamically imported, then the request is
	 * delegated to the exporting bundle's class loader ; otherwise the search
	 * continues with the next step. If the request is delegated to an exporting
	 * class loader and the class or resource is not found, then the search
	 * terminates and the request fails.
	 *
	 * If the requested class or resource is in a package imported by this
	 * module, the request is delegated to the exporting module's class loader.
	 * If the class or resource is not found, then the search terminates and the
	 * request has failed. The search terminates in this case since imported
	 * classes are not treated as split.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testClassLoadingSearchOrder002() throws Exception {
		Bundle tb8a;
		Bundle tb8b;
		Bundle tb8c;
		Bundle tb8d;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		tb8a = installBundle("classloading.tb8a.jar");
		tb8b = installBundle("classloading.tb8b.jar");
		tb8c = installBundle("classloading.tb8c.jar");

		try {
			tb8d = installBundle("classloading.tb8d.jar");

			// This resource must be loaded using the importing bundle
			url = tb8d.getResource("resources/tb8a-resource.txt");

			// Read the resource content to check the loading bundle
			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the bundle name", "tb8a",
						reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

			// This resource cannot be found when loaded with the class
			// loader of the exporting bundle
			url = tb8d.getResource("resources/tb8b-resource.txt");
			assertNull("Checking if the resource is incorrectly found", url);

			tb8d.uninstall();
		}
		catch (BundleException ex) {
			if (ex.getCause() == null) {
				fail(ex.getMessage());
			}
			else {
				fail(ex.getCause().getMessage());
			}
		}
		finally {
			tb8c.uninstall();
			tb8b.uninstall();
			tb8a.uninstall();
		}
	}

	/**
	 * If the requested class or resource is in a package that is imported from
	 * one or more other bundles using Require-Bundle, the request is delegated
	 * to the class loaders of the other bundles, in the order in which they are
	 * specified in this bundle's manifest. If the class or resource is not
	 * found, then the search continues with the next step.
	 *
	 * The bundle's own internal class path is searched. If the class or
	 * resource is not found, then the search continues with the next step
	 * unless the requested class or resource is in a package that is imported
	 * from one or more other bundles using Require-Bundle, in which case the
	 * search terminates and the request fails.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testClassLoadingSearchOrder003() throws Exception {
		Bundle tb8b;
		Bundle tb8c;
		Bundle tb8e;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		tb8b = installBundle("classloading.tb8b.jar");
		tb8c = installBundle("classloading.tb8c.jar");

		tb8e = installBundle("classloading.tb8e.jar");
		try {
			// This resource must be loaded using the required bundle class
			// loader
			url = tb8e.getResource("resources/tb8b-resource.txt");

			// Read the resource content to check the loading bundle
			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the bundle name", "tb8b",
						reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

			// This resource must be loaded using the bundle class loader
			url = tb8e.getResource("resources/tb8e-resource.txt");

			// Read the resource content to check the loading bundle
			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the bundle name", "tb8e",
						reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

			// This resource cannot be found
			url = tb8c.getResource("resources/nonexistent-resource.txt");
			assertNull("Checking if the resource is incorrectly found", url);
		}
		finally {
			tb8e.uninstall();
			tb8c.uninstall();
			tb8b.uninstall();
		}
	}

	/**
	 * Teste if the bundle's own internal class path is searched.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testClassLoadingSearchOrder004() throws Exception {
		Bundle tb8b;
		Bundle tb8c;
		Bundle tb8e;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		tb8b = installBundle("classloading.tb8b.jar");
		tb8c = installBundle("classloading.tb8c.jar");

		tb8e = installBundle("classloading.tb8e.jar");
		try {
			// This resource must be loaded using the bundle class loader
			url = tb8e.getResource("resources/tb8e-resource.txt");

			// Read the resource content to check the loading bundle
			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the bundle name", "tb8e",
						reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

			// This resource cannot be found
			url = tb8c.getResource("resources/nonexistent-resource.txt");
			assertNull("Checking if the resource is incorrectly found", url);
		}
		finally {
			tb8e.uninstall();
			tb8c.uninstall();
			tb8b.uninstall();
		}
	}

	/**
	 * If the requested class or resource is in a package that is imported using
	 * DynamicImport-Package, then a dynamic import of the package is now
	 * attempted, otherwise the search terminates and the request fails. If the
	 * dynamic import of the package is established, the request is delegated to
	 * the exporting bundle's class loader. If the request is delegated to an
	 * exporting class loader and the class or resource is not found, then the
	 * search terminates and the request fails.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testClassLoadingSearchOrder005() throws Exception {
		Bundle tb8c;
		Bundle tb8f;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		tb8c = installBundle("classloading.tb8c.jar");

		tb8f = installBundle("classloading.tb8f.jar");
		try {
			// This resource must be loaded using the dynamic importing bundle
			url = tb8f.getResource("resources/tb8c-resource.txt");

			// Read the resource content to check the loading bundle
			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the bundle name", "tb8c",
						reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

			// This resource cannot be found
			url = tb8f.getResource("resources/nonexistent-resource.txt");
			assertNull("Checking if the resource is incorrectly found", url);
		}
		finally {
			tb8f.uninstall();
			tb8c.uninstall();
		}
	}

	// RFC 0079 - 5.1.5.2 Class Loading Search Order for R3 Compatibility -----

	/**
	 * Since previous versions of the specification were incomplete and/or
	 * contradictory regarding the visibility of packages provided via the
	 * system class path, it is possible that legacy bundles exist that violate
	 * the rules defined above. To smooth this transition with legacy bundles, a
	 * framework must modify its behavior slightly for R3 or lower bundles. For
	 * such legacy bundles, the following class/resource search order is
	 * acceptable:
	 *
	 * Delegate the request to the parent class loader and if the class or
	 * resource is found, return this result. If the class or resource is in a
	 * java.* package and it was not found by the parent class loader, then the
	 * request fails.
	 *
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testClassLoadingSearchOrder006() throws Exception {
		Bundle tb9a;
		Bundle tb9b;
		Class< ? > clazz;

		tb9a = installBundle("classloading.tb9a.jar");

		tb9b = installBundle("classloading.tb9b.jar");
		try {
			// The loading class must be loaded by the parent class loader
			clazz = tb9b.loadClass("java.lang.Math");
			assertTrue("Checking the number of class methods", clazz
					.getMethods().length != 0);

			// Loading a class which does not exist when loaded by the parent
			// class loader
			try {
				clazz = tb9b.loadClass("java.lang.SomeClass");
				fail("The classes belongs to package java.lang must be loaded only with the parent class loader");
			}
			catch (ClassNotFoundException ex) {
				// Expected exception
			}
		}
		finally {
			tb9b.uninstall();
			tb9a.uninstall();
		}
	}

	/**
	 * If the requested class or resource is in a package imported, either
	 * statically or dynamically, by this bundle, then the request is delegated
	 * to the exporting bundle's class loader ; otherwise the search continues
	 * with the next step. If the request is delegated to an exporting bundle's
	 * class loader and the class or resource is not found, then the search
	 * terminates and the request fails since imported packages are not treated
	 * as split.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testClassLoadingSearchOrder007() throws Exception {
		Bundle tb9a;
		Bundle tb9c;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		tb9a = installBundle("classloading.tb9a.jar");

		tb9c = installBundle("classloading.tb9c.jar");
		try {
			// This resource must be loaded using the importing bundle class
			// loader
			url = tb9c.getResource("resources/tb9a-resource.txt");

			// Read the resource content to check the loading bundle
			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the bundle name", "tb9a",
						reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

			// This resource cannot be found
			url = tb9c.getResource("resources/nonexistent-resource.txt");
			assertNull("Checking if the resource is incorrectly found", url);
		}
		finally {
			tb9c.uninstall();
			tb9a.uninstall();
		}
	}

	/**
	 * The bundle's own internal class path is searched. If the class or
	 * resource is not found, then the search terminates and the request fails.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testClassLoadingSearchOrder008() throws Exception {
		Bundle tb9d;
		URL url;

		tb9d = installBundle("classloading.tb9d.jar");
		try {
			// This resource must be loaded using the bundle class path
			url = tb9d.getResource("resources/tb9d-resource.txt");
			assertNotNull("Checking if the resource is correctly found", url);

			// This resource cannot be found
			url = tb9d.getResource("resources/nonexistent-resource.txt");
			assertNull("Checking if the resource is incorrectly found", url);
		}
		finally {
			tb9d.uninstall();
		}
	}

	// Bundles and Modules -----------------------------------

	/**
	 * Bundles are related to modules as follows: a given bundle archive defines
	 * a module; installing a bundle creates a module; updating a bundle may
	 * create another module based on the updated bundle archive and so there
	 * may be a one-many relationship between the bundle object and the modules
	 * created for the bundle. However, a bundle archive will not create more
	 * than one module unless it is modified.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.update(InputStream)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testBundleModule001() throws Exception {
		long bundleId;
		Bundle tb1;
		InputStream inputStream;
		URL url;

		// Install the bundle
		tb1 = installBundle("classloading.tb1.jar");
		bundleId = tb1.getBundleId();

		try {
			// Update the bundle with the same archive
			url = new URL(getWebServer() + "classloading.tb1.jar");
			inputStream = url.openStream();
			tb1.update(inputStream);
			inputStream.close();

			// Bundle id must not change after update
			assertTrue("Bundle id must not change after update",
					bundleId == tb1.getBundleId());
		}
		finally {
			tb1.uninstall();
		}

		tb1 = installBundle("classloading.tb10a.jar");

		try {
			// Bundle id must not change after install the new bundle
			assertTrue(
					"Bundle id must not change after install the new bundle with the same symbolic name and different version",
					bundleId != tb1.getBundleId());
		}
		finally {
			tb1.uninstall();
		}
	}

	// Bundle Symbolic Name ---------------------------------

	/**
	 * Version 2 bundle manifests must specify the bundle symbolic name.
	 *
	 * @spec BundleContext.start(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testBundleSymbolicName001() throws Exception {
		Bundle tb11a;

		try {
			tb11a = installBundle("classloading.tb11a.jar");
			tb11a.uninstall();
			fail("A version 2 bundle manifests must specify the bundle symbolic name");
		}
		catch (BundleException ex) {
			// Ignore this exception
		}
	}

	// Singleton Bundles -----------------------------------

	/**
	 * At most one version of a particular singleton bundle may be resolved in
	 * the framework. When multiple versions of a singleton bundle with the same
	 * symbolic name are installed, the framework implementation must select at
	 * most one of the singleton bundles to resolve.
	 *
	 * Only bundles specified as singleton bundles are treated as such. In
	 * particular, singleton bundles do not affect the resolution of non-
	 * singleton bundles with the same symbolic name.
	 *
	 * @spec Bundle.start()
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testSingletonBundle001() throws Exception {
		Bundle tb11b;
		Bundle tb11c;
		Bundle tb11d;
		Bundle tb11e;

		tb11b = getContext().installBundle(
				getWebServer() + "classloading.tb11b.jar");
		tb11c = getContext().installBundle(
				getWebServer() + "classloading.tb11c.jar");
		tb11d = getContext().installBundle(
				getWebServer() + "classloading.tb11d.jar");
		tb11e = getContext().installBundle(
				getWebServer() + "classloading.tb11e.jar");

		try {
			Wiring.resolveBundles(getContext(), tb11b, tb11c, tb11d, tb11e);
			if ((tb11b.getState() & Bundle.RESOLVED) != 0)
				tb11b.start();
			if ((tb11c.getState() & Bundle.RESOLVED) != 0)
				tb11c.start();
			if ((tb11d.getState() & Bundle.RESOLVED) != 0)
				tb11d.start();
			if ((tb11e.getState() & Bundle.RESOLVED) != 0)
				tb11e.start();

			assertTrue(
					"Only one singleton bundle may be resolved at the same time",
					(tb11b.getState() == Bundle.ACTIVE && tb11c.getState() == Bundle.INSTALLED) ||
					(tb11c.getState() == Bundle.ACTIVE && tb11b.getState() == Bundle.INSTALLED));
			assertTrue(
					"More than one non-singleton bundle may be resolved at the same time",
					tb11d.getState() == Bundle.ACTIVE
							&& tb11e.getState() == Bundle.ACTIVE);
		}
		finally {
			tb11e.uninstall();
			tb11d.uninstall();
			tb11c.uninstall();
			tb11b.uninstall();
		}
	}

	// Installing Bundles ---------------------------------

	/**
	 * If an attempt is made to install a bundle which has the same bundle
	 * symbolic name and bundle version as an already installed bundle, then a
	 * BundleException must be thrown and the install must fail
	 *
	 * @spec BundleContext.installBundle(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testBundleInstall001() throws Exception {
		Bundle tb12a;
		Bundle tb12b;

		tb12a = getContext().installBundle(
				getWebServer() + "classloading.tb12a.jar");

		try {
			trace("Installing a bundle with the same symbolic name and version of other installed bundle");
			tb12b = getContext().installBundle(
					getWebServer() + "classloading.tb12b.jar");
			tb12b.uninstall();
			fail("A bundle with the same symbolic name and version of other cannot be installed");
		}
		catch (BundleException ex) {
			// Ignore this exception
		}
		finally {
			tb12a.uninstall();
		}
	}

	// Exporting Packages -----------------------------------

	/**
	 * If multiple packages need to be exported with identical parameters, the
	 * syntax permits a list of packages, separated by semicolons, to be
	 * specified before the parameters. The same package may be exported more
	 * than once with different parameters.
	 *
	 * @spec Bundle.start()
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageExport001() throws Exception {
		Bundle tb13a;
		Bundle tb13o;
		Bundle tb13p;

		try {
			trace("Installing a bundle with correct export syntax");
			tb13a = installBundle("classloading.tb13a.jar");

			tb13o = installBundle("classloading.tb13o.jar");
			tb13p = installBundle("classloading.tb13p.jar");

			tb13p.uninstall();
			tb13o.uninstall();
			tb13a.uninstall();
		}
		catch (BundleException ex) {
			fail("A bundle with correct export syntax cannot be installed");
		}
	}

	/**
	 * Tests the attribute uses.
	 *
	 * uses - a string specifying a list of packages upon which the package(s)
	 * being exported depend. The default is that the list is empty which means
	 * the package(s) being exported do not depend on any other packages.
	 *
	 * If an import is resolved to a module which exports the same package more
	 * than once, the first export statement which matches the import is
	 * selected and only the dependencies specified (via "uses") for that export
	 * statement apply to the importer.
	 *
	 * @spec Bundle.getResource(String)
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageExport002() throws Exception {
		Bundle tb8a;
		Bundle tb8b;
		Bundle tb13b;
		Bundle tb13c;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		tb8a = getContext().installBundle(
				getWebServer() + "classloading.tb8a.jar");
		tb8b = getContext().installBundle(
				getWebServer() + "classloading.tb8b.jar");
		tb8a.start();
		tb8b.start();

		tb13b = getContext().installBundle(
				getWebServer() + "classloading.tb13b.jar");
		tb13c = getContext().installBundle(
				getWebServer() + "classloading.tb13c.jar");

		try {
			tb13b.start();
			tb13c.start();

			trace("Loading a class from the imported package");
			try {
				tb13c
						.loadClass("org.osgi.test.cases.framework.classloading.exports.service.SomeService");
			}
			catch (ClassNotFoundException ex) {
				fail("The class belongs to an imported package cannot be found");
			}

			// Read the resource content to check the loading bundle
			trace("Loading a resource and checking its loading bundle");
			url = tb13c.getResource("/resources/tb8a-resource.txt");

			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the loading bundle name",
						"tb8b", reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

			tb13c.stop();
			tb13b.stop();
		}
		finally {
			tb13c.uninstall();
			tb13b.uninstall();

			tb8b.stop();
			tb8a.stop();
			tb8b.uninstall();
			tb8a.uninstall();
		}
	}

	/**
	 * Packages in the list which are neither exported nor imported by the
	 * current module are ignored and do not create dependencies.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageExport003() throws Exception {
		Bundle tb8a;
		Bundle tb8b;
		Bundle tb13c;
		Bundle tb13d;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		tb8a = getContext().installBundle(
				getWebServer() + "classloading.tb8a.jar");
		tb8b = getContext().installBundle(
				getWebServer() + "classloading.tb8b.jar");
		tb8a.start();
		tb8b.start();

		tb13d = getContext().installBundle(
				getWebServer() + "classloading.tb13d.jar");
		tb13c = getContext().installBundle(
				getWebServer() + "classloading.tb13c.jar");

		try {
			tb13d.start();
			tb13c.start();

			// Read the resource content to check the loading bundle
			trace("Loading a resource and checking its loading bundle");
			url = tb13c.getResource("/resources/tb8a-resource.txt");

			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the loading bundle name",
						"tb8a", reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

			tb13d.stop();
			tb13c.stop();
		}
		finally {
			tb13d.uninstall();
			tb13c.uninstall();

			tb8b.stop();
			tb8a.stop();
			tb8b.uninstall();
			tb8a.uninstall();
		}

		// Repeat the test changing the order of package exporters

		tb8b = getContext().installBundle(
				getWebServer() + "classloading.tb8b.jar");
		tb8a = getContext().installBundle(
				getWebServer() + "classloading.tb8a.jar");
		tb8b.start();
		tb8a.start();

		tb13d = getContext().installBundle(
				getWebServer() + "classloading.tb13d.jar");
		tb13c = getContext().installBundle(
				getWebServer() + "classloading.tb13c.jar");

		try {
			tb13d.start();
			tb13c.start();

			// Read the resource content to check the loading bundle
			trace("Loading a resource and checking its loading bundle");
			url = tb13c.getResource("/resources/tb8a-resource.txt");

			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the loading bundle name",
						"tb8b", reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

			tb13d.stop();
			tb13c.stop();
		}
		finally {
			tb13d.uninstall();
			tb13c.uninstall();

			tb8a.stop();
			tb8b.stop();
			tb8a.uninstall();
			tb8b.uninstall();
		}
	}

	/**
	 * Test the attributes uses.
	 *
	 * uses - The framework forms the transitive closure of the dependency
	 * relationship. For example, if a bundle exports package p, q, and r and
	 * declares that P uses Q and Q uses R, then the framework will behave as if
	 * the bundle also declared that P uses R.
	 *
	 * @spec Bundle.getResource(String)
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageExport004() throws Exception {
		Bundle tb8a;
		Bundle tb8b;
		Bundle tb13e;
		Bundle tb13f;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		tb8a = getContext().installBundle(
				getWebServer() + "classloading.tb8a.jar");
		tb8b = getContext().installBundle(
				getWebServer() + "classloading.tb8b.jar");
		tb8a.start();
		tb8b.start();

		tb13e = getContext().installBundle(
				getWebServer() + "classloading.tb13e.jar");
		tb13f = getContext().installBundle(
				getWebServer() + "classloading.tb13f.jar");

		try {
			tb13e.start();
			tb13f.start();

			trace("Loading a class from the imported package");
			try {
				tb13f
						.loadClass("org.osgi.test.cases.framework.classloading.exports.security.SomePermission");
			}
			catch (ClassNotFoundException ex) {
				fail("The class belongs to an imported package cannot be found");
			}

			// Read the resource content to check the loading bundle
			trace("Loading a resource and checking its loading bundle");
			url = tb13f.getResource("/resources/tb8a-resource.txt");

			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the loading bundle name",
						"tb8b", reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

			tb13f.stop();
			tb13e.stop();
		}
		finally {
			tb13f.uninstall();
			tb13e.uninstall();

			tb8b.stop();
			tb8a.stop();
			tb8b.uninstall();
			tb8a.uninstall();
		}
	}

	/**
	 * Tests the attribute mandatory when the bundle import does matches the
	 * required attributes.
	 *
	 * mandatory - a string with no default value. The list specifies names of
	 * matching attributes which must be specified by matching Import-Package
	 * statements.
	 *
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageExport005() throws Exception {
		Bundle tb13g;
		Bundle tb13h;

		tb13g = installBundle("classloading.tb13g.jar");
		tb13h = installBundle("classloading.tb13h.jar");

		try {
			trace("Loading a class from the imported package");
			try {
				tb13h
						.loadClass("org.osgi.test.cases.framework.classloading.exports.listener.ServiceListenerTester");
			}
			catch (ClassNotFoundException ex) {
				fail("The class belongs to an imported package must be found");
			}
		}
		finally {
			tb13h.stop();
			tb13g.stop();

			tb13h.uninstall();
			tb13g.uninstall();
		}
	}

	/**
	 * Tests the attribute mandatory when the bundle import does not matches the
	 * required attributes.
	 *
	 * mandatory - a string with no default value. The list specifies names of
	 * matching attributes which must be specified by matching Import-Package
	 * statements.
	 *
	 * @spec Bundle.start()
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageExport006() throws Exception {
		Bundle tb13g;
		Bundle tb13i;

		tb13g = installBundle("classloading.tb13g.jar");
		tb13i = getContext().installBundle(
				getWebServer() + "classloading.tb13i.jar");

		try {
			trace("Starting a bundle which incorrectly imports a package");
			tb13i.start();
			fail("The bundle cannot start without the mandatory attributes required by the exported package");
		}
		catch (BundleException ex) {
			// expected
		}
		finally {
			tb13g.stop();

			tb13i.uninstall();
			tb13g.uninstall();
		}
	}

	/**
	 * Tests the attributes include and exclude.
	 *
	 * include/exclude - a list of classes of the specified packages which must
	 * be allowed to be exported in the case of include or prevented from being
	 * exported in the case of exclude. This acts as a class load time filter
	 * and does not affect module resolution in any way.
	 *
	 * Filtering works as follows. The name of a class being loaded is checked
	 * against the include list and the exclude list. If it matches an entry in
	 * the include list but does not match any entry in the exclude list, then
	 * loading proceeds normally. In all other cases, loading fails and a
	 * ClassNotFound exception is thrown. Note that the ordering of includes and
	 * excludes is not significant.
	 *
	 * Note that the syntax permits zero or more wildcards to occur at arbitrary
	 * positions.
	 *
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageExport007() throws Exception {
		Bundle tb13j;
		Bundle tb13o;

		tb13j = installBundle("classloading.tb13j.jar");
		tb13o = installBundle("classloading.tb13o.jar");

		try {
			trace("Loading a class which matchs the include list");
			try {
				tb13o
						.loadClass("org.osgi.test.cases.framework.classloading.exports.listener.ServiceListenerTester");
			}
			catch (ClassNotFoundException ex) {
				fail("The class belongs the exported package must be found");
			}

			trace("Loading a class which matchs the exclude list");
			try {
				tb13o
						.loadClass("org.osgi.test.cases.framework.classloading.exports.listener.AllServiceListenerTester");
				fail("The class belongs the exported package must not be found");
			}
			catch (ClassNotFoundException ex) {
				// expected
			}
		}
		finally {
			tb13o.uninstall();
			tb13j.uninstall();
		}
	}

	/**
	 * Tests the attributes version and specification-version.
	 *
	 * version - the version of the named packages with syntax. The default
	 * value is "0.0.0".
	 *
	 * specification-version - this is a deprecated synonym for the version
	 * attribute with the sole exception that, if both specification- version
	 * and version are specified (for the same package(s)), they must have
	 * identical values. If both are specified but with different values, the
	 * bundle fails to install.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageExport008() throws Exception {
		Bundle tb13k;
		Bundle tb13l;

		trace("Installing a bundle with an exported package having the same value for version and specification-version attributes");
		try {
			tb13k = getContext().installBundle(
					getWebServer() + "classloading.tb13k.jar");
			tb13k.uninstall();
		}
		catch (BundleException ex) {
			fail("A bundle with an exported package having the same value for version and specification-version attributes must be installed");
		}

		try {
			trace("Installing a bundle with an exported package having different values for version and specification-version attributes");
			tb13l = getContext().installBundle(
					getWebServer() + "classloading.tb13l.jar");
			tb13l.uninstall();
			fail("A bundle with an exported package having different values for version and specification-version attributes must fail to install");
		}
		catch (BundleException ex) {
			// expected
		}
	}

	/**
	 * Test the attributes bundle-symbolic-name and bundle-version.
	 *
	 * bundle-symbolic-name - the bundle symbolic name of the exporting bundle
	 * is implicitly associated with the export statement. The export statement
	 * must not specify an explicit bundle symbolic name.
	 *
	 * bundle-version - the bundle version of the exporting bundle is implicitly
	 * associated with the export statement. The export statement must not
	 * specify an explicit bundle version.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageExport009() throws Exception {
		Bundle tb13m;

		try {
			trace("Installing a bundle with an exported package which incorrectly specify bundle-symbolic-name and bundle-version");
			tb13m = getContext().installBundle(
					getWebServer() + "classloading.tb13m.jar");
			tb13m.uninstall();
			fail("A bundle with an exported package which incorrectly specify bundle-symbolic-name and bundle-version must fail to install");
		}
		catch (BundleException ex) {
			// expected
		}
	}

	// Importing Packages -----------------------------------

	/**
	 * If multiple packages need to be imported with identical parameters, the
	 * syntax permits a list of packages, separated by semicolons, to be
	 * specified before the parameters.
	 *
	 * @spec Bundle.getResource(String)
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageImport001() throws Exception {
		Bundle tb8a;
		Bundle tb15a;
		Bundle tb15b;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		tb8a = installBundle("classloading.tb8a.jar");
		tb15a = installBundle("classloading.tb15a.jar");

		try {
			// Install a bundle which import packages
			tb15b = getContext().installBundle(
					getWebServer() + "classloading.tb15b.jar");

			// Check if packages are correctly imported
			trace("Loading a class from the imported package");
			try {
				tb15b
						.loadClass("org.osgi.test.cases.framework.classloading.exports.service.SomeService");
			}
			catch (ClassNotFoundException ex) {
				fail("A class from imported class cannot be found");
			}

			// Read the resource content to check the loading bundle
			trace("Loading a resource and checking its loading bundle");
			url = tb15b.getResource("/resources/tb8a-resource.txt");

			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the loading bundle name",
						"tb15a",
						reader.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

			tb15b.uninstall();
		}
		finally {
			tb15a.uninstall();
			tb8a.uninstall();
		}
	}

	/**
	 * Test the attribute resolution with value mandatory.
	 *
	 * resolution - a string taking one of the values "mandatory" or "optional".
	 * The default value is "mandatory".
	 *
	 * "mandatory" indicates that the import must be resolved when the importing
	 * module is resolved. If such an import cannot be resolved and the
	 * importing module does not also export the specified packages, the module
	 * fails to resolve.
	 *
	 * @spec Bundle.start()
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageImport002() throws Exception {
		Bundle tb1;
		Bundle tb15c;

		// Try to install when the imported package is not available
		tb15c = getContext().installBundle(
				getWebServer() + "classloading.tb15c.jar");

		try {
			trace("Starting a bundle with an import package that is not available");
			tb15c.start();
			fail("The bundle must not be resolved, since the imported package is not available");
		}
		catch (BundleException ex) {
			// expected
		}
		finally {
			tb15c.uninstall();
		}

		// Try to install when the imported package is available
		tb1 = installBundle("classloading.tb1.jar");

		try {
			// Install a bundle which import package
			tb15c = getContext().installBundle(
					getWebServer() + "classloading.tb15c.jar");

			try {
				trace("Starting a bundle with an import package that is available");
				tb15c.start();
			}
			catch (BundleException ex) {
				fail("The bundle must be resolved, since the imported package is available");
			}

			// Check if packages are correctly imported
			trace("Loading a class from the imported package");
			try {
				tb15c
						.loadClass("org.osgi.test.cases.framework.classloading.exports.service.SomeService");
			}
			catch (ClassNotFoundException ex) {
				fail("A class from imported class cannot be found");
			}

			tb15c.uninstall();
		}
		finally {
			tb1.uninstall();
		}
	}

	/**
	 * Test the attibute resolution with value "optional".
	 *
	 * "optional" indicates that the import is optional and the importing module
	 * may be resolved without the import being resolved. If the import is not
	 * resolved when the module is resolved, the import may not be resolved
	 * before the module is re-resolved.
	 *
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageImport003() throws Exception {
		Bundle tb1;
		Bundle tb15d;

		// Try to install when the imported package is not available
		tb15d = getContext().installBundle(
				getWebServer() + "classloading.tb15d.jar");

		try {
			try {
				trace("Starting a bundle with an import package that is not available");
				tb15d.start();
			}
			catch (BundleException ex) {
				fail("The bundle must be resolved, even though the imported package is not available");
			}

			// Now, install a bundle with the imported package
			tb1 = installBundle("classloading.tb1.jar");

			Wiring.synchronousRefreshBundles(getContext(), tb15d);

			try {
				Sleep.sleep(5000);
			} catch (InterruptedException ex) {
				// Ignore this exception
			}

			// Check if the package is available
			try {
				tb15d
						.loadClass("org.osgi.test.cases.framework.classloading.exports.service.SomeService");
			}
			catch (ClassNotFoundException ex) {
				fail("The package must be available after resolve the bundle");
			}
			finally {
				tb1.uninstall();
			}
		}
		finally {
			tb15d.stop();
			tb15d.uninstall();
		}
	}

	/**
	 * Tests the attributes version and specification-version.
	 *
	 * version - a version range to select the (re-)exporter's implementation
	 * version. The default value is "0.0.0".
	 *
	 * specification-version - this is a deprecated synonym for the version
	 * attribute with the sole exception that, if both specification- version
	 * and version are specified (for the same package(s)), they must have
	 * identical values. If both are specified but with different values, the
	 * bundle fails to install.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageImport004() throws Exception {
		Bundle tb1;
		Bundle tb15e;
		Bundle tb15f;

		tb1 = installBundle("classloading.tb1.jar");

		try {
			trace("Installing a bundle with an import package having the same value for version and specification-version attributes");
			try {
				tb15e = getContext()
						.installBundle(
						getWebServer() + "classloading.tb15e.jar");
				tb15e.uninstall();
			}
			catch (BundleException ex) {
				fail("A bundle with an imported package having the same value for version and specification-version attributes must be installed");
			}

			try {
				trace("Installing a bundle with an import package having different values for version and specification-version attributes");
				tb15f = getContext()
						.installBundle(
						getWebServer() + "classloading.tb15f.jar");
				tb15f.uninstall();
				fail("A bundle with an imported package having different values for version and specification-version attributes must fail to install");
			}
			catch (BundleException ex) {
				// expected
			}
		}
		finally {
			tb1.uninstall();
		}
	}

	/**
	 * Tests the attributes bundle-symbolic-name and bundle-version.
	 *
	 * bundle-symbolic-name - the bundle symbolic name of the (re-)exporting
	 * bundle.
	 *
	 * bundle-version - a version range to select the bundle version of the
	 * (re-)exporting bundle. The default value is "0.0.0".
	 *
	 * @spec Bundle.start()
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageImport005() throws Exception {
		Bundle tb1;
		Bundle tb15g;

		tb1 = installBundle("classloading.tb1.jar");
		tb15g = getContext().installBundle(
				getWebServer() + "classloading.tb15g.jar");

		try {
			trace("Starting a bundle which imports a package specifying the bundle name and version");
			tb15g.start();
			tb15g.stop();
		}
		catch (BundleException ex) {
			fail("A bundle which imports a package specifying the bundle name and version must be started");
		}
		finally {
			tb15g.uninstall();
			tb1.uninstall();
		}
	}

	/**
	 * A module which imports a given package more than once is in error and
	 * fails to resolve.
	 *
	 * @spec Bundle.start()
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageImport006() throws Exception {
		Bundle tb1;
		Bundle tb15h;

		tb1 = installBundle("classloading.tb1.jar");
		tb15h = null;

		try {
			tb15h = getContext().installBundle(
					getWebServer() + "classloading.tb15h.jar");

			trace("Starting a bundle which imports a given package more than once");
			tb15h.start();
			tb15h.stop();
			fail("A bundle which imports a package more than once must not be resolved");
		}
		catch (BundleException ex) {
			// expected
		}
		finally {
			if (tb15h != null) {
				tb15h.uninstall();
			}
			tb1.uninstall();
		}
	}

	/**
	 * If an import matches exports in more than one module, the module with
	 * lowest bundle identifier is selected.
	 *
	 * @spec Bundle.getBundleId()
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageImport007() throws Exception {
		Bundle tb8a;
		Bundle tb8b;
		Bundle tb15i;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		tb8a = installBundle("classloading.tb8a.jar");
		tb8b = installBundle("classloading.tb8b.jar");
		tb15i = installBundle("classloading.tb15i.jar");

		try {
			// Check that tb8a has the lowest bundle id
			if (tb8a.getBundleId() > tb8b.getBundleId()) {
				fail("This test requires that the id of first installed bundle greatest than the id of others bundles");
			}

			// Read the resource content to check the loading bundle
			trace("Loading a resource and checking its loading bundle");
			url = tb15i.getResource("/resources/tb8a-resource.txt");

			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the loading bundle name",
						"tb8a", reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}
		}
		finally {
			tb15i.uninstall();
			tb8b.uninstall();
			tb8a.uninstall();
		}
	}

	// Dynamically Importing Packages -----------------------

	/**
	 * Dynamic imports are matched to exports (to form package wirings) during
	 * class loading and do not affect module resolution.
	 *
	 * @spec Bundle.start()
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageDynamicImport001() throws Exception {
		Bundle tb17a;

		tb17a = getContext().installBundle(
				getWebServer() + "classloading.tb17a.jar");

		try {
			trace("Starting a bundle importing dynamically an exported package");
			tb17a.start();
			tb17a.stop();
		}
		catch (BundleException ex) {
			fail("When using dynamic imports, a bundle must be started even tough the imported package is not available");
		}
		finally {
			tb17a.uninstall();
		}
	}

	/**
	 * Packages may be named explicitly or by using wild-carded expressions such
	 * as "org.foo.*" and "*".
	 *
	 * If multiple packages need to be dynamically imported with identical
	 * parameters, the syntax permits a list of packages, separated by
	 * semicolons, to be specified before the parameters.
	 *
	 * Test the attribute bundle-version.
	 *
	 * bundle-version - a version range to select the bundle version of the (re-
	 * )exporting bundle. The default value is "0.0.0".
	 *
	 * @spec Bundle.getResource(String)
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageDynamicImport002() throws Exception {
		Bundle tb1;
		Bundle tb8a;
		Bundle tb17b;
		URL url;

		tb1 = installBundle("classloading.tb1.jar");
		tb8a = installBundle("classloading.tb8a.jar");
		tb17b = installBundle("classloading.tb17b.jar");

		try {
			trace("Loading resources from a dynamic imported package");

			try {
				tb17b
						.loadClass("org.osgi.test.cases.framework.classloading.exports.service.SomeService");
			}
			catch (ClassNotFoundException ex) {
				fail("Cannot load a class from a dynamic imported package");
			}

			url = tb17b.getResource("resources/tb8a-resource.txt");
			if (url == null) {
				fail("Cannot load a resource from a dynamic imported package");
			}
		}
		finally {
			tb17b.uninstall();
			tb8a.uninstall();
			tb1.uninstall();
		}
	}

	/**
	 * Dynamic imports are searched in the order in which they are specified.
	 * The order is particularly important when wildcarded package names are
	 * used as the order will then determine the order in which matching occurs. *
	 * Test the attribute bundle-symbolic-name.
	 *
	 * bundle-symbolic-name - the bundle symbolic name of the (re-)exporting
	 * bundle.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageDynamicImport003() throws Exception {
		Bundle tb8a;
		Bundle tb8b;
		Bundle tb17c;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		tb8a = installBundle("classloading.tb8a.jar");
		tb8b = installBundle("classloading.tb8b.jar");
		tb17c = installBundle("classloading.tb17c.jar");

		try {
			// Read the resource content to check the loading bundle
			trace("Loading a resource and checking its loading bundle");
			url = tb17c.getResource("/resources/tb8a-resource.txt");

			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the loading bundle name",
						"tb8b", reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}
		}
		finally {
			tb17c.uninstall();
			tb8b.uninstall();
			tb8a.uninstall();
		}
	}

	/**
	 * Dynamic imports may not be used when the package is exported.
	 *
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageDynamicImport004() throws Exception {
		Bundle tb1;
		Bundle tb17i;
		Bundle tb17j;

		tb1 = installBundle("classloading.tb1.jar");
		tb17i = installBundle("classloading.tb17i.jar");
		tb17j = installBundle("classloading.tb17j.jar");

		try {
			try {
				tb17i
						.loadClass("org.osgi.test.cases.framework.classloading.exports.service.SomeService");
				fail("bundle does not export package");
			}
			catch (ClassNotFoundException ex) {
				// expected
			}
			try {
				tb17j
						.loadClass("org.osgi.test.cases.framework.classloading.exports.service.SomeService");
				fail("The dynamic import must not be propagated");
			}
			catch (ClassNotFoundException ex) {
				// expected
			}
		}
		finally {
			tb17j.uninstall();
			tb17i.uninstall();
			tb1.uninstall();
		}
	}

	/**
	 * Test the attributes version and specification-version.
	 *
	 * version - a version range to select the exporter's implementation
	 * version. The default value is "0.0.0".
	 *
	 * specification-version - this is a deprecated synonym for the version
	 * attribute with the sole exception that, if both specification- version
	 * and version are specified (for the same package(s)), they must have
	 * identical values. If both are specified but with different values, the
	 * bundle fails to install.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageDynamicImport005() throws Exception {
		Bundle tb1;
		Bundle tb17d;
		Bundle tb17e;

		tb1 = installBundle("classloading.tb1.jar");

		tb17d = null;
		try {
			trace("Installing a bundle with a dynamic imported package having the same value for version and specification-version attributes");
			tb17d = installBundle("classloading.tb17d.jar");

			trace("Loading a class from a dynamic imported package");
			tb17d
					.loadClass("org.osgi.test.cases.framework.classloading.exports.service.SomeService");
		}
		catch (BundleException ex) {
			fail("A bundle with a dynamic imported package having the same value for version and specification-version attributes must be installed");
		}
		catch (ClassNotFoundException ex) {
			fail("The class must be found when belongs a dynamic imported package");
		}
		finally {
			if ((tb17d != null) && (tb17d.getState() != Bundle.UNINSTALLED)) {
				tb17d.uninstall();
			}

			tb1.uninstall();
		}

		try {
			trace("Installing a bundle with a dynamic imported package having different values for version and specification-version attributes");
			tb17e = getContext().installBundle(
					getWebServer() + "classloading.tb17e.jar");
			tb17e.uninstall();
			fail("A bundle with a dynamic imported package having different values for version and specification-version attributes must fail to install");
		}
		catch (BundleException ex) {
			// expected
		}
	}

	/**
	 * In order for a DynamicImport-Package to be resolved to an export
	 * statement, any arbitrary attributes specified must match the attributes
	 * of the export statement. However, a match is not prevented if the export
	 * statement includes attributes which are not specified by the
	 * corresponding DynamicImport-Package statement, unless one or more of
	 * those attributes are exported as required (using the "mandatory"
	 * directive).
	 *
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageDynamicImport006() throws Exception {
		Bundle tb13g;
		Bundle tb17g;

		tb13g = installBundle("classloading.tb13g.jar");
		tb17g = installBundle("classloading.tb17g.jar");

		try {
			trace("Loading a class from a dynamic imported package");
			try {
				tb17g
						.loadClass("org.osgi.test.cases.framework.classloading.exports.listener.ServiceListenerTester");
			}
			catch (ClassNotFoundException ex) {
				fail("The class belongs to a dynamic imported package must be found");
			}
		}
		finally {
			tb17g.stop();
			tb13g.stop();

			tb17g.uninstall();
			tb13g.uninstall();
		}
	}

	/**
	 * In order for a DynamicImport-Package to be resolved to an export
	 * statement, any arbitrary attributes specified must match the attributes
	 * of the export statement. However, a match is not prevented if the export
	 * statement includes attributes which are not specified by the
	 * corresponding DynamicImport-Package statement, unless one or more of
	 * those attributes are exported as required (using the "mandatory"
	 * directive).
	 *
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPackageDynamicImport007() throws Exception {
		Bundle tb13g;
		Bundle tb17h;

		tb13g = installBundle("classloading.tb13g.jar");
		tb17h = installBundle("classloading.tb17h.jar");

		try {
			trace("Loading a class from a dynamic imported package");
			try {
				tb17h
						.loadClass("org.osgi.test.cases.framework.classloading.exports.listener.ServiceListenerTester");
				fail("The class belongs to a dynamic imported package must not be found");
			}
			catch (ClassNotFoundException ex) {
				// expected
			}
		}
		finally {
			tb17h.uninstall();
			tb13g.uninstall();
		}

	}

	// Requiring a Bundle -------------------------------------
	// Require Bundle Cycles --------------------------------

	/**
	 * Test that the Require-Bundle manifest header imports all the exported
	 * packages of one or more named bundles. A given package may be exported by
	 * more than one of the named bundles in which case it is treated as a split
	 * package and the bundles are searched in the order in which they were
	 * specified after which the requiring bundle is searched for any additional
	 * classes of the package.
	 *
	 * Tests that multiple bundles may export the same package. Bundles which
	 * export the same package that are involved in a require bundle cycle can
	 * lead to lookup cycles when searching for classes and resources from the
	 * package.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if any failure occurs or any assert fails
	 */
	public void testRequiredBundle001() throws Exception {
		Bundle tb16b = getContext().installBundle(
				getWebServer() + "classloading.tb16b.jar");
		Bundle tb16c = getContext().installBundle(
				getWebServer() + "classloading.tb16c.jar");
		Bundle tb16a = getContext().installBundle(
				getWebServer() + "classloading.tb16a.jar");
		tb16b.start();
		tb16c.start();
		tb16a.start();
		URL url = tb16a.getResource("test/package/resources/resource.txt");
		try {
			assertNotNull("Expecting to find resource", url);

			BufferedReader bufr = new BufferedReader(new InputStreamReader(url
					.openStream()));
			String line = bufr.readLine();
			bufr.close();
			assertEquals("Expecting to get resource from classloading.tb16c",
					"tb16c", line);

			// Test that own resources are available to requiring bundle
			url = tb16a
					.getResource("test/package/resources/tb16a-resource.txt");
			assertNotNull("Expecting to find the resource in requiring bundle",
					url);
		}
		finally {
			tb16a.stop();
			tb16b.stop();
			tb16c.stop();
			uninstallBundle(tb16a);
			uninstallBundle(tb16b);
			uninstallBundle(tb16c);
		}
	}

	/**
	 * When searching for classes in a required bundle, the framework should
	 * only search the packages (re- ) exported by the required bundle. Any
	 * other packages contained in the required bundle must not be searched.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if any failure occurs or any assert fails
	 */
	public void testRequiredBundle002() throws Exception {
		Bundle tb16b = getContext().installBundle(
				getWebServer() + "classloading.tb16b.jar");
		Bundle tb16c = getContext().installBundle(
				getWebServer() + "classloading.tb16c.jar");
		Bundle tb16a = getContext().installBundle(
				getWebServer() + "classloading.tb16a.jar");
		URL url = tb16a
				.getResource("org/osgi/test/cases/framework/classloading/tb16b/TestClass.class");
		try {
			assertNull("Not expecting to find the resource in required bundle",
					url);
		}
		finally {
			uninstallBundle(tb16a);
			uninstallBundle(tb16b);
			uninstallBundle(tb16c);
		}
	}

	/**
	 * Test visibility directive with value of "private" which indicates that
	 * any packages that are exported by the required bundle are not made
	 * visible on the export signature of the requiring bundle.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if any failure occurs or any assert fails
	 */
	public void testRequiredBundle004() throws Exception {
		Bundle tb16b = getContext().installBundle(
				getWebServer() + "classloading.tb16b.jar");
		Bundle tb16a = getContext().installBundle(
				getWebServer() + "classloading.tb16k.jar");
		Bundle tb16i = getContext().installBundle(
				getWebServer() + "classloading.tb16i.jar");
		tb16a.start();
		tb16b.start();
		tb16i.start();
		try {
			URL url = tb16i
					.getResource("test/package/resources/tb16b-resource.txt");
			assertNull("Not expecting to find the resource tb16b-resource.txt",
					url);
		}
		finally {
			tb16a.stop();
			tb16b.stop();
			tb16i.stop();
			uninstallBundle(tb16a);
			uninstallBundle(tb16b);
			uninstallBundle(tb16i);
		}
	}

	/**
	 * Test that when the resolution directive has a value of "optional" the the
	 * requiring module may be resolved without the required module being
	 * resolved. If the required bundle is not resolved when the requiring
	 * bundle is resolved, the requiring bundle is not affected if the required
	 * bundle may subsequently be resolved until such time as the requiring
	 * bundle is re-resolved.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if any failure occurs or any assert fails
	 */
	public void testRequiredBundle005() throws Exception {
		// Install and start requiring bundle
		Bundle tb16e = getContext().installBundle(
				getWebServer() + "classloading.tb16e.jar");
		tb16e.start(); // this will cause requiring bundle to be resolved

		// Install and start required bundle
		Bundle tb16b = getContext().installBundle(
				getWebServer() + "classloading.tb16b.jar");
		tb16b.start(); // this will cause required bundle to be resolved
		try {
			// The resource in required bundle is not expected
			URL url = tb16e.getResource("test/package/resources/resource.txt");
			assertNull("Not expecting to find resource from required bundle",
					url);

		}
		finally {
			tb16b.stop();
			uninstallBundle(tb16b);
			tb16e.stop();
			uninstallBundle(tb16e);
		}
	}

	/**
	 * Test bundle-version attribute allows a version-range to select the bundle
	 * version of the required bundle.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if any failure occurs or any assert fails
	 */
	public void testRequiredBundle006() throws Exception {
		// Install and start required bundle
		Bundle tb16b = getContext().installBundle(
				getWebServer() + "classloading.tb16b.jar");
		tb16b.start(); // this will cause required bundle to be resolved

		// Install and start requiring bundle
		Bundle tb16f = getContext().installBundle(
				getWebServer() + "classloading.tb16f.jar");
		tb16f.start(); // this will cause requiring bundle to be resolved

		try {
			// The resource in required bundle is not expected
			URL url = tb16f.getResource("test/package/resources/resource.txt");
			assertNull("Not expecting to find resource from required bundle",
					url);

		}
		finally {
			tb16b.stop();
			uninstallBundle(tb16b);
			tb16f.stop();
			uninstallBundle(tb16f);
		}
	}

	/**
	 * A bundle may both import packages (via Import-Package) and require one or
	 * more bundles (via Require-Bundle), but if a package is imported via
	 * Import-Package it is not also imported via Require-Bundle: Import-
	 * Package takes priority over Require-Bundle and packages which are
	 * exported by a required bundle and imported via Import-Package are not
	 * treated as split
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if any failure occurs or any assert fails
	 */
	public void testRequiredBundle007() throws Exception {
		Bundle tb16b = getContext().installBundle(
				getWebServer() + "classloading.tb16b.jar");
		tb16b.start();
		Bundle tb16g = getContext().installBundle(
				getWebServer() + "classloading.tb16g.jar");
		tb16g.start();
		URL url = tb16g.getResource("test/package/resources/resource.txt");
		try {
			assertNotNull("Expecting to find resource", url);
			BufferedReader bufr = new BufferedReader(new InputStreamReader(url
					.openStream()));
			String line = bufr.readLine();
			bufr.close();
			assertEquals("Expecting to get resource from classloading.tb16b",
					"tb16b", line);
			url = tb16g
					.getResource("test/package/resources/tb16g-resource.txt");
			assertNull(
					"Not expecting to find the resource in requiring bundle",
					url);
		}
		finally {
			tb16g.stop();
			tb16b.stop();
			uninstallBundle(tb16g);
			uninstallBundle(tb16b);
		}
	}

	/**
	 * Tests that if the required bundle exports the same package more than
	 * once, the first export statement is used and only the dependencies
	 * specified (via "uses") for that export statement apply to the requiring
	 * bundle.
	 *
	 * @spec Bundle.getResource(String)
	 * @spec Bundle.loadClass(String)
	 * @throws Exception if any failure occurs or any assert fails
	 */
	public void testRequiredBundle008() throws Exception {
		Bundle tb8a;
		Bundle tb8b;
		Bundle tb13b;
		Bundle tb16h;
		BufferedReader reader;
		InputStream inputStream;
		URL url;

		// start bundles that export different versions of the same package
		tb8a = getContext().installBundle(
				getWebServer() + "classloading.tb8a.jar");
		tb8b = getContext().installBundle(
				getWebServer() + "classloading.tb8b.jar");
		tb8a.start();
		tb8b.start();

		// start required bundle that exports the package used by the
		// requiring bundle
		tb13b = getContext().installBundle(
				getWebServer() + "classloading.tb13b.jar");
		tb13b.start();

		// start requiring bundle
		tb16h = getContext().installBundle(
				getWebServer() + "classloading.tb16h.jar");
		tb16h.start();

		try {

			trace("Loading a class from the imported package");
			try {
				tb16h
						.loadClass("org.osgi.test.cases.framework.classloading.exports.service.SomeService");
			}
			catch (ClassNotFoundException ex) {
				fail("The class belongs to an imported package cannot be found");
			}

			// Read the resource content to check the loading bundle
			trace("Loading a resource and checking its loading bundle");
			url = tb16h.getResource("/resources/tb8a-resource.txt");

			inputStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				assertEquals("Checking the loading bundle name",
						"tb8b", reader
						.readLine());
			}
			finally {
				reader.close();
				inputStream.close();
			}

		}
		finally {
			tb16h.stop();
			tb13b.stop();
			tb16h.uninstall();
			tb13b.uninstall();

			tb8b.stop();
			tb8a.stop();
			tb8b.uninstall();
			tb8a.uninstall();
		}
	}

	/**
	 * Test visibility directive with value of "reexport" which indicates that
	 * any packages that are exported by the required bundle are re-exported by
	 * the requiring bundle. Any arbitrary matching attributes with which they
	 * were exported by the required bundle are deleted.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception If any exception occurs or an assert fails
	 */
	public void testRequiredBundle009() throws Exception {
		Bundle tb16b = getContext().installBundle(
				getWebServer() + "classloading.tb16b.jar");
		Bundle tb16c = getContext().installBundle(
				getWebServer() + "classloading.tb16c.jar");
		Bundle tb16a = getContext().installBundle(
				getWebServer() + "classloading.tb16a.jar");
		Bundle tb16i = getContext().installBundle(
				getWebServer() + "classloading.tb16i.jar");
		tb16a.start();
		tb16b.start();
		tb16c.start();
		tb16i.start();
		try {
			// test if resources from package re-exported by tb16a is available
			// in tb16i due to the visibility directive being set to "reexport"
			URL url = tb16i
					.getResource("test/package/resources/tb16a-resource.txt");
			assertNotNull("Expecting to find the resource tb16a-resource.txt",
					url);
			url = tb16i
					.getResource("test/package/resources/tb16b-resource.txt");
			assertNotNull("Expecting to find the resource tb16b-resource.txt",
					url);
		}
		finally {
			tb16a.stop();
			tb16b.stop();
			tb16c.stop();
			tb16i.stop();
			uninstallBundle(tb16a);
			uninstallBundle(tb16b);
			uninstallBundle(tb16c);
			uninstallBundle(tb16i);
		}
	}

	/**
	 * Tests that multiple bundles may export the same package. Bundles which
	 * re-export the same package that are involved in a require bundle cycle
	 * can lead to lookup cycles when searching for classes and resources from
	 * the package.
	 *
	 * Tests that the re-export statement does not inherit matching attributes
	 * or the "mandatoryness" of matching attributes. If these are not specified
	 * on the re-export statement, they are either unspecified or take default
	 * values where defaults are defined.
	 *
	 * @spec Bundle.getResource(String)
	 * @throws Exception if any failure occurs or any assert fails
	 */
	public void testRequiredBundle010() throws Exception {
		Bundle tb16b = getContext().installBundle(
				getWebServer() + "classloading.tb16b.jar");
		Bundle tb16c = getContext().installBundle(
				getWebServer() + "classloading.tb16c.jar");
		Bundle tb16a = getContext().installBundle(
				getWebServer() + "classloading.tb16j.jar");
		tb16b.start();
		tb16c.start();
		tb16a.start();
		URL url = tb16a.getResource("test/package/resources/resource.txt");
		try {
			assertNotNull("Expecting to find resource", url);

			BufferedReader bufr = new BufferedReader(new InputStreamReader(url
					.openStream()));
			String line = bufr.readLine();
			bufr.close();
			assertEquals("Expecting to get resource from classloading.tb16c",
					"tb16c", line);
		}
		finally {
			tb16a.stop();
			tb16b.stop();
			tb16c.stop();
			uninstallBundle(tb16a);
			uninstallBundle(tb16b);
			uninstallBundle(tb16c);
		}
	}

	// Installing Modules -------------------------------------

	/**
	 * Test that any duplicate attribute or duplicate directive causes a R4
	 * bundle to fail to install.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testInstallingModules001() throws Exception {
		try {
			Bundle tb14b = getContext().installBundle(
					getWebServer() + "classloading.tb14b.jar");
			tb14b.uninstall();
			fail("Expecting bundle install to fail because the manifest has a duplicate directive.");
		}
		catch (BundleException e) {
			// expected
		}
	}

	/**
	 * Test that mutiple imports of a given package causes a R4 bundle to fail
	 * to install.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testInstallingModules002() throws Exception {
		try {
			Bundle tb14c = getContext().installBundle(
					getWebServer() + "classloading.tb14c.jar");
			tb14c.uninstall();
			fail("Expecting bundle install to fail because a package has been imported multiple times.");
		}
		catch (BundleException e) {
			// expected
		}
	}

	/**
	 * Test that any syntactic error (e.g. improperly formatted version or
	 * bundle symbolic name, unrecognized directive, unrecognized directive
	 * value, etc.) causes a R4 bundle to fail to install.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testInstallingModules003() throws Exception {

		try {
			Bundle tb14f = getContext().installBundle(
					getWebServer() + "classloading.tb14f.jar");
			tb14f.uninstall();
			fail("Expecting bundle install to fail because of errors in manifest.");
		}
		catch (BundleException e) {
			// expected
		}
		catch (Exception e) {
			fail("Unexpected exception " + e.getClass().getName() + " - "
					+ e.getMessage());
		}

	}

	/**
	 * Test that specification-version and version specified together (for the
	 * same package(s)) causes a R4 bundle to fail to install. For example:
	 * Import-Package p;specification-version=1;version=2 would fail to install,
	 * but: Import-Package p;specification-version=1, q;version=2 would not be
	 * an error.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testInstallingModules004() throws Exception {

		try {
			Bundle tb14g = getContext().installBundle(
					getWebServer() + "classloading.tb14g.jar");
			tb14g.uninstall();
			fail("Expecting bundle install to fail because of error in Import-Package specification.");
		}
		catch (BundleException e) {
			e.printStackTrace();
		}

	}

}

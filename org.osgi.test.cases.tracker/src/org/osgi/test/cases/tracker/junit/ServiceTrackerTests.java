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

package org.osgi.test.cases.tracker.junit;

import java.util.Hashtable;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.Semaphore;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.tracker.service.TestService1;
import org.osgi.test.cases.tracker.service.TestService2;
import org.osgi.test.cases.tracker.service.TestService3;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ServiceTrackerTests extends DefaultTestBundleControl {

	public void testOpenClose() throws Exception {
		// 2.23.1 Testcase1 (tc1), tracking a service
		// Tb1 contains service: testservice1
		// Install tb1
		Bundle tb = installBundle("tb1.jar");

		// Creates ServiceTracker object with ServiceReference to testservice1
		ServiceReference<TestService1> sr = getContext()
				.getServiceReference(TestService1.class);
		ServiceTracker<TestService1, TestService1> st = new ServiceTracker<TestService1, TestService1>(
				getContext(), sr, null);
		st.open();

		try {
			// Call ServiceTracker.size()
			// Should reply 1
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 1",
					1, st.size());

			// Call ServiceTracker.getServiceReferences()
			ServiceReference<TestService1>[] srs = st.getServiceReferences();
			assertNotNull(
					"ServiceReference for the tracked service can be reached at this time: true",
					srs);
			assertEquals(
					"ServiceReference for the tracked service can be reached at this time: 1",
					1, srs.length);
			// Call ServiceTracker.getService()
			TestService1 ss = st.getService();
			// Call ServiceTracker.getServices()
			Object[] sss = st.getServices();
			// Call ServiceTracker.getService(ServiceReference)
			TestService1 ssr = st.getService(sr);
			// All should be equal and testservice1
			assertSame(
					"Tracked services can be reached at this time and are equal in the different methods",
					ss, sss[0]);
			assertSame(
					"Tracked services can be reached at this time and are equal in the different methods",
					ss, ssr);

			// Call ServiceTracker.close()
			st.close();

			// Call ServiceTracker.getServiceReferences()
			srs = st.getServiceReferences();
			assertNull(
					"No ServiceReferences for tracked services can be reached at this time",
					srs);
			// Call ServiceTracker.getService()
			ss = st.getService();
			// Call ServiceTracker.getServices()
			sss = st.getServices();
			// Call ServiceTracker.getService(ServiceReference)
			ssr = st.getService(sr);
			// All should be null
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ss);
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					sss);
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ssr);

			// Call ServiceTracker.size()
			// Should reply 0
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 0 ",
					0, st.size());

			st.open();

			// Call ServiceTracker.size()
			// Should reply 1
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 1",
					1, st.size());

			uninstallBundle(tb);
			tb = null;
			// Call ServiceTracker.getServiceReferences()
			srs = st.getServiceReferences();
			assertNull(
					"No ServiceReferences for tracked services can be reached at this time: true",
					srs);
			// Call ServiceTracker.getService()
			ss = st.getService();
			// Call ServiceTracker.getServices()
			sss = st.getServices();
			// Call ServiceTracker.getService(ServiceReference)
			ssr = st.getService(sr);
			// Should reply with null
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ss);
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					sss);
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ssr);

			// Call ServiceTracker.size()
			// Should reply 0
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 0 ",
					0, st.size());
		}
		finally {
			st.close();
			if (tb != null) {
				uninstallBundle(tb);
			}
		}
	}

	// add testing for isEmpty and getServices(T[])
	public void testOpenClose2() throws Exception {
		TestService1 dummy = new TestService1() {};
		// 2.23.1 Testcase1 (tc1), tracking a service
		// Tb1 contains service: testservice1
		// Install tb1
		Bundle tb = installBundle("tb1.jar");

		// Creates ServiceTracker object with ServiceReference to testservice1
		ServiceReference<TestService1> sr = getContext()
				.getServiceReference(TestService1.class);
		ServiceTracker<TestService1, TestService1> st = new ServiceTracker<TestService1, TestService1>(
				getContext(), sr, null);
		st.open();

		try {
			// Call ServiceTracker.size()
			// Should reply 1
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 1",
					1, st.size());
			// Call ServiceTracker.isEmpty()
			// Should reply false
			assertFalse("The ServiceTracker is empty", st.isEmpty());

			// Call ServiceTracker.getServiceReferences()
			ServiceReference<TestService1>[] srs = st.getServiceReferences();
			assertNotNull(
					"ServiceReference for the tracked service can be reached at this time: true",
					srs);
			assertEquals(
					"ServiceReference for the tracked service can be reached at this time: 1",
					1, srs.length);
			// Call ServiceTracker.getService()
			TestService1 ss = st.getService();
			assertNotNull(ss);
			// Call ServiceTracker.getServices()
			Object[] sss = st.getServices();
			assertNotNull(sss);
			// Call ServiceTracker.getServices(T[])
			TestService1[] ssst = new TestService1[st.size()];
			TestService1[] ssstr = st.getServices(ssst);
			assertSame("different array returned",ssst, ssstr);
			ssst = new TestService1[0];
			ssstr = st.getServices(ssst);
			assertNotSame("same array returned", ssst, ssstr);
			assertEquals("wrong size array", st.size(), ssstr.length);
			ssst = new TestService1[st.size() + 10];
			ssst[st.size()] = dummy;
			ssstr = st.getServices(ssst);
			assertSame("different array returned", ssst, ssstr);
			assertNull("no null after last element", ssstr[st.size()]);
			// Call ServiceTracker.getService(ServiceReference)
			TestService1 ssr = st.getService(sr);
			// All should be equal and testservice1
			assertSame(
					"Tracked services can be reached at this time and are equal in the different methods",
					ss, sss[0]);
			assertSame(
					"Tracked services can be reached at this time and are equal in the different methods",
					ss, ssstr[0]);
			assertSame(
					"Tracked services can be reached at this time and are equal in the different methods",
					ss, ssr);

			// Call ServiceTracker.close()
			st.close();

			// Call ServiceTracker.getServiceReferences()
			srs = st.getServiceReferences();
			assertNull(
					"No ServiceReferences for tracked services can be reached at this time",
					srs);
			// Call ServiceTracker.getService()
			ss = st.getService();
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ss);
			// Call ServiceTracker.getServices()
			sss = st.getServices();
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					sss);
			// Call ServiceTracker.getServices(T[])
			ssst = new TestService1[st.size() + 10];
			ssst[st.size()] = dummy;
			ssstr = st.getServices(ssst);
			assertSame("different array returned", ssst, ssstr);
			assertNull("no null after last element", ssstr[st.size()]);
			ssst = new TestService1[st.size()];
			ssstr = st.getServices(ssst);
			assertSame("different array returned", ssst, ssstr);
			assertEquals(
					"No Services for tracked services can be reached at this time: true",
					0, ssstr.length);
			// Call ServiceTracker.getService(ServiceReference)
			ssr = st.getService(sr);
			// All should be null
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ssr);

			// Call ServiceTracker.size()
			// Should reply 0
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 0 ",
					0, st.size());
			// Should reply true
			assertTrue(
					"The number of Services being tracked by ServiceTracker is: 0 ",
					st.isEmpty());

			st.open();

			// Call ServiceTracker.size()
			// Should reply 1
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 1",
					1, st.size());
			// Call ServiceTracker.isEmpty()
			// Should reply false
			assertFalse("The ServiceTracker is empty", st.isEmpty());

			uninstallBundle(tb);
			tb = null;
			// Call ServiceTracker.getServiceReferences()
			srs = st.getServiceReferences();
			assertNull(
					"No ServiceReferences for tracked services can be reached at this time: true",
					srs);
			// Call ServiceTracker.getService()
			ss = st.getService();
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ss);
			// Call ServiceTracker.getServices()
			sss = st.getServices();
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					sss);
			// Call ServiceTracker.getServices(T[])
			ssst = new TestService1[st.size() + 10];
			ssst[st.size()] = dummy;
			ssstr = st.getServices(ssst);
			assertSame("different array returned", ssst, ssstr);
			assertNull("no null after last element", ssstr[st.size()]);
			ssst = new TestService1[st.size()];
			ssstr = st.getServices(ssst);
			assertSame("different array returned", ssst, ssstr);
			assertEquals(
					"No Services for tracked services can be reached at this time: true",
					0, ssstr.length);
			// Call ServiceTracker.getService(ServiceReference)
			ssr = st.getService(sr);
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ssr);

			// Call ServiceTracker.size()
			// Should reply 0
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 0 ",
					0, st.size());
			// Should reply true
			assertTrue(
					"The number of Services being tracked by ServiceTracker is: 0 ",
					st.isEmpty());
		}
		finally {
			st.close();
			if (tb != null) {
				uninstallBundle(tb);
			}
		}
	}

	public void testWaitForService() throws Exception {

		BundleContext context = getContext();

		// 2.23.2 Testcase2 (tc2), waitforService
		// Tb1 contains service: testservice1
		// Tb2 contains service: testservice2
		// Tb3 contains service: testservice3
		// Install tb1, tb2 and tb3
		Bundle b1 = installBundle("tb1.jar", false);
		Bundle b2 = installBundle("tb2.jar", false);
		Bundle b3 = installBundle("tb3.jar", false);

		// Creates ServiceTracker1 object with testservice1
		// Call ServiceTracker.open()
		ServiceTracker<TestService1, TestService1> st1 = new ServiceTracker<TestService1, TestService1>(
				context, TestService1.NAME, null);
		st1.open();
		// Creates ServiceTracker2 object with testservice2
		// Call ServiceTracker.open()
		ServiceTracker<TestService2, TestService2> st2 = new ServiceTracker<TestService2, TestService2>(
				context, TestService2.NAME, null);
		st2.open();
		// Creates ServiceTracker3 object with testservice3
		// Call ServiceTracker.open()
		ServiceTracker<TestService3, TestService3> st3 = new ServiceTracker<TestService3, TestService3>(
				context, TestService3.NAME, null);
		st3.open();

		try {
			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 0",
					0, st1.size());
			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 2 is: 0",
					0, st2.size());
			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 3 is: 0",
					0, st3.size());

			Semaphore s1 = new Semaphore(0);
			BundleStarter t1 = new BundleStarter(b1, s1);
			t1.start();
			s1.release();
			TestService1 tt1 = st1.waitForService(0);
			assertNotNull("Returned an object in ServiceTracker 1?:  true", tt1);
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 1",
					1, st1.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 2 is: 0",
					0, st2.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 3 is: 0",
					0, st3.size());

			Semaphore s2 = new Semaphore(0);
			BundleStarter t2 = new BundleStarter(b2, s2);
			t2.start();
			TestService2 tt2 = st2.waitForService(1000);
			assertNull("Returned an object in ServiceTracker 2?: false", tt2);
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 1",
					1, st1.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 2 is: 0",
					0, st2.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 3 is: 0",
					0, st3.size());
			s2.release();

			Semaphore s3 = new Semaphore(1);
			BundleStarter t3 = new BundleStarter(b3, s3);
			t3.start();

			// wait for threads to complete
			st2.waitForService(5000);
			st3.waitForService(5000);

			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 1",
					1, st1.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 2 is: 1",
					1, st2.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 3 is: 1",
					1, st3.size());
		}
		finally {
			// Call ServiceTracker.close()
			st1.close();
			st2.close();
			st3.close();

			b3.uninstall();
			b2.uninstall();
			b1.uninstall();
		}
	}

	private class BundleStarter extends Thread {
		private final Semaphore	semaphore;
		private final Bundle	bundle;

		BundleStarter(Bundle bundle, Semaphore semaphore) {
			this.bundle = bundle;
			this.semaphore = semaphore;
		}

		public void run() {
			try {
				semaphore.acquire();
				bundle.start();
			}
			catch (Exception e) {
				log(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void testCustomizer() throws Exception {
		BundleContext context = getContext();
		Bundle tb;
		// 2.23.3 Testcase3 (tc3), ServiceTrackerCustomizer
		// Tb1 contains service: testservice1
		// Implement ServiceTrackerCustomizer

		// Create ServiceTracker object with testservice1
		// Call ServiceTracker.open()
		final boolean[] customizerCalled = new boolean[] {false, false, false};
		ServiceTracker<TestService1, TestService1> st = new ServiceTracker<TestService1, TestService1>(
				context, TestService1.class.getName(),
				new ServiceTrackerCustomizer<TestService1, TestService1>() {
					public TestService1 addingService(
							ServiceReference<TestService1> reference) {
						synchronized (customizerCalled) {
							customizerCalled[0] = true;
						}
						TestService1 obj = getContext().getService(reference);
						return obj;

					}

					public void modifiedService(
							ServiceReference<TestService1> reference,
							TestService1 service) {
						synchronized (customizerCalled) {
							customizerCalled[1] = true;
						}
					}

					public void removedService(
							ServiceReference<TestService1> reference,
							TestService1 service) {
						synchronized (customizerCalled) {
							customizerCalled[2] = true;
						}
						getContext().ungetService(reference);
					}
				});
		st.open();
		try {
			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 0",
					0, st.size());
			// Install tb1
			tb = installBundle("tb1.jar");
			try {
				synchronized (customizerCalled) {
					assertTrue("addingService not called", customizerCalled[0]);
				}
				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 1",
						1, st.size());
				// Addingservice should do something

			}
			finally {
				// Uninstall tb1
				uninstallBundle(tb);
			}
			// RemovedService should do something
			synchronized (customizerCalled) {
				assertTrue("removedService not called", customizerCalled[2]);
			}
		}
		finally {
			// Call ServiceTracker.close()
			st.close();
		}

	}

	public void testRemove() throws Exception {
		BundleContext context = getContext();

		// 2.23.4 Testcase4 (tc4), tracking a classname
		// Tb1 contains service: testservice1, testservice2, testservice3
		// Tb2 contains service: testservice1
		// Tb3 contains service: testservice1
		// Tb4 uses testservice2
		// Creates ServiceTracker object with classname testservice1
		// Call ServiceTracker.open()
		Filter f = context.createFilter("(name=TestService1)");
		ServiceTracker<TestService1, TestService1> st = new ServiceTracker<TestService1, TestService1>(
				context, f, null);
		st.open();

		// Install tb1, tb2, tb3 and tb4
		Bundle tb1 = installBundle("tb1.jar");
		Bundle tb2 = installBundle("tb2.jar");
		Bundle tb3 = installBundle("tb3.jar");
		Bundle tb4 = installBundle("tb4.jar");

		try {
			// Call ServiceTracker.getServiceReferences()
			ServiceReference<TestService1>[] srs = st.getServiceReferences();
			assertNotNull("one bundle registered TestService1", srs);
			assertEquals("one bundle registered TestService1", 1, srs.length);
			assertEquals("tb1 registered TestService1", tb1.getBundleId(),
					srs[0].getBundle().getBundleId());
			// Call ServiceTracker.getServices()
			Object[] os = st.getServices();
			assertNotNull("one registered TestService1", os);
			assertEquals("one registered TestService1", 1, os.length);
			assertTrue("instanceof TestService1", os[0] instanceof TestService1);

			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 1",
					1, st.size());

			ServiceReference<TestService1> sr = context
					.getServiceReference(TestService1.class);
			st.remove(sr);
			// Call ServiceTracker.getServiceReferences()
			// Should find tb1, tb3
			srs = st.getServiceReferences();
			assertNull("no TestService1", srs);

			// Call ServiceTracker.getServices()
			// Should find testservice1
			os = st.getServices();
			assertNull("no TestService1", os);

			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 0",
					0, st.size());
		}
		finally {
			// Call ServiceTracker.close()
			st.close();

			uninstallBundle(tb4);
			uninstallBundle(tb3);
			uninstallBundle(tb2);
			uninstallBundle(tb1);
		}
	}

	public void testFilterWithPropertyChanges() throws Exception {
		BundleContext context = getContext();
		// 2.23.5 Testcase5 (tc5), filter match
		// Tb1 contains service: testservice1
		// Call BundleContext.BundleContext.createFilter(String) (testservice1)

		Filter f = context.createFilter("(name=TestService1)");
		// Creates ServiceTracker object with Filter
		ServiceTracker<TestService1, TestService1> st = new ServiceTracker<TestService1, TestService1>(
				context, f, null);
		// Call ServiceTracker.open()
		st.open();
		try {
			// Call ServiceTracker.size()
			// Should reply 0
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 0",
					0, st.size());
			// Install tb1
			Bundle tb1 = installBundle("tb1.jar");
			try {
				// Call ServiceTracker.size()
				// Should reply 1
				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 1",
						1, st.size());
				// Call ServiceTracker.getServiceReferences()
				// Should find all
				// Call ServiceTracker.getServiceReferences()
				// Should find tb1, tb3
				ServiceReference<TestService1>[] srs = st
						.getServiceReferences();

				assertNotNull(
						"There were no ServiceReferences in this ServiceTracker.",
						srs);
				for (int i = 0; i < srs.length; i++) {
					assertEquals(
							"The ServiceReferences contains: TestService1",
							"TestService1", srs[i].getProperty("name"));
				}

				// Change property for TestService1 so that the filter doesn't
				// match
				// The only way to change property is to have the
				// ServiceRegistration,
				// i.e. reg a new TestService1
				Hashtable<String, Object> ts1Props = new Hashtable<String, Object>();
				ts1Props.put("name", "TestService1");
				ts1Props.put("version", Float.valueOf(1.0f));
				ts1Props.put("compatible", Float.valueOf(1.0f));
				ts1Props.put("description", "TestService 1 in tbc");

				ServiceRegistration<TestService1> tsr1 = context
						.registerService(TestService1.class, new TestService1() {
							// empty
						}, ts1Props);

				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 2",
						2, st.size());
				ts1Props.put("name", "TestService1a");
				tsr1.setProperties(ts1Props);
				// Check that the servicetracker doesn't find the TestService
				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 1",
						1, st.size());
				// Change property for tb1 so that the filter match again
				ts1Props.put("name", "TestService1");
				tsr1.setProperties(ts1Props);
				// Check that the servicetracker find TestService
				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 2",
						2, st.size());

				tsr1.unregister();
				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 1",
						1, st.size());
			}
			finally {
				uninstallBundle(tb1);
			}
		}
		finally {
			// Call ServiceTracker.close()
			st.close();
		}
	}

	public void testTrackingCount() throws Exception {
		BundleContext context = getContext();
		ServiceTracker<TestService3,TestService3> st = new ServiceTracker<>(
				context, TestService3.class, null);
		assertEquals("ServiceTracker.getTrackingCount() == -1", -1, st
				.getTrackingCount());
		st.open();
		try {
			// Should be 0
			assertEquals("ServiceTracker.getTrackingCount() == 0", 0, st
					.getTrackingCount());

			ServiceRegistration<TestService3> sr = context
					.registerService(TestService3.class, new TestService3() {
						// empty
					}, null);
			// Should be 1
			assertEquals("ServiceTracker.getTrackingCount() == 1", 1, +st
					.getTrackingCount());

			sr.unregister();
			// Should be 2
			assertEquals("ServiceTracker.getTrackingCount() == 2", 2, st
					.getTrackingCount());
		}
		finally {
			st.close();
		}
		assertEquals("ServiceTracker.getTrackingCount() == -1", -1, st
				.getTrackingCount());
	}

	public void testServiceTracker01() {
		// simple ServiceTracker test
		Runnable runIt = new Service();
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put(getName(), Boolean.TRUE);
		ServiceRegistration<Runnable> reg = getContext()
				.registerService(Runnable.class, runIt, props);
		ServiceTracker<Runnable, ServiceReference<Runnable>> testTracker = null;
		try {
			final boolean[] results = new boolean[] {false, false, false};
			ServiceTrackerCustomizer<Runnable, ServiceReference<Runnable>> testCustomizer = new ServiceTrackerCustomizer<Runnable, ServiceReference<Runnable>>() {
				public ServiceReference<Runnable> addingService(
						ServiceReference<Runnable> reference) {
					results[0] = true;
					return reference;
				}

				public void modifiedService(
						ServiceReference<Runnable> reference,
						ServiceReference<Runnable> service) {
					results[1] = true;
				}

				public void removedService(
						ServiceReference<Runnable> reference,
						ServiceReference<Runnable> service) {
					results[2] = true;
				}
			};
			try {
				testTracker = new ServiceTracker<Runnable, ServiceReference<Runnable>>(
						getContext(),
						FrameworkUtil
								.createFilter("(&(objectClass=java.lang.Runnable)("
										+ getName() + "=true))"),
						testCustomizer);
			}
			catch (InvalidSyntaxException e) {
				fail("filter error", e);
			}
			testTracker.open();
			assertTrue("Did not call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to still match
			props.put("testChangeProp", Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertTrue("Did not call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to no longer match
			props.put(getName(), Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertTrue("Did not call removedService", results[2]);
			clearResults(results);

			// change props to no longer match
			props.put("testChangeProp", Boolean.TRUE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props back to match
			props.put(getName(), Boolean.TRUE);
			reg.setProperties(props);
			assertTrue("Did not call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

		}
		finally {
			if (reg != null)
				reg.unregister();
			if (testTracker != null)
				testTracker.close();
		}
	}

	public void testServiceTracker02() {
		// simple ServiceTracker test
		Runnable runIt = new Service();
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put(getName(), Boolean.FALSE);
		ServiceRegistration<Runnable> reg = getContext()
				.registerService(Runnable.class, runIt, props);
		ServiceTracker<Runnable, ServiceReference<Runnable>> testTracker = null;
		try {
			final boolean[] results = new boolean[] {false, false, false};
			ServiceTrackerCustomizer<Runnable, ServiceReference<Runnable>> testCustomizer = new ServiceTrackerCustomizer<Runnable, ServiceReference<Runnable>>() {
				public ServiceReference<Runnable> addingService(
						ServiceReference<Runnable> reference) {
					results[0] = true;
					return reference;
				}

				public void modifiedService(
						ServiceReference<Runnable> reference,
						ServiceReference<Runnable> service) {
					results[1] = true;
				}

				public void removedService(
						ServiceReference<Runnable> reference,
						ServiceReference<Runnable> service) {
					results[2] = true;
				}
			};
			try {
				testTracker = new ServiceTracker<Runnable, ServiceReference<Runnable>>(
						getContext(),
						FrameworkUtil
								.createFilter("(&(objectClass=java.lang.Runnable)("
										+ getName() + "=true))"),
						testCustomizer);
			}
			catch (InvalidSyntaxException e) {
				fail("filter error", e);
			}
			testTracker.open();
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to match
			props.put(getName(), Boolean.TRUE);
			reg.setProperties(props);
			assertTrue("Did not call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to still match
			props.put("testChangeProp", Boolean.TRUE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertTrue("Did not call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to no longer match
			props.put(getName(), Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertTrue("Did not call removedService", results[2]);
			clearResults(results);

			// change props to no longer match
			props.put("testChangeProp", Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

		}
		finally {
			if (reg != null)
				reg.unregister();
			if (testTracker != null)
				testTracker.close();
		}
	}

	public void testServiceTracker03() {
		// simple ServiceTracker test
		Runnable runIt = new Service();
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put(getName(), Boolean.TRUE);
		ServiceRegistration<Runnable> reg = getContext()
				.registerService(
				Runnable.class, runIt, props);
		ServiceTracker<Runnable, ServiceReference<Runnable>> testTracker = null;
		try {
			final boolean[] results = new boolean[] {false, false, false};
			ServiceTrackerCustomizer<Runnable, ServiceReference<Runnable>> testCustomizer = new ServiceTrackerCustomizer<Runnable, ServiceReference<Runnable>>() {
				public ServiceReference<Runnable> addingService(
						ServiceReference<Runnable> reference) {
					results[0] = true;
					return reference;
				}

				public void modifiedService(
						ServiceReference<Runnable> reference,
						ServiceReference<Runnable> service) {
					results[1] = true;
				}

				public void removedService(
						ServiceReference<Runnable> reference,
						ServiceReference<Runnable> service) {
					results[2] = true;
				}
			};
			try {
				testTracker = new ServiceTracker<Runnable, ServiceReference<Runnable>>(
						getContext(),
						FrameworkUtil
						.createFilter("(&(objectclass=java.lang.Runnable)("
								+ getName().toLowerCase() + "=true))"),
						testCustomizer);
			}
			catch (InvalidSyntaxException e) {
				fail("filter error", e);
			}
			testTracker.open();
			assertTrue("Did not call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to not match
			props.put(getName(), Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertTrue("Did not call removedService", results[2]);
			clearResults(results);

			// change props to match
			props.put(getName(), Boolean.TRUE);
			reg.setProperties(props);
			assertTrue("Did not call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to still match
			props.put("testChangeProp", Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertTrue("Did not call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);
		}
		finally {
			if (reg != null)
				reg.unregister();
			if (testTracker != null)
				testTracker.close();
		}
	}

	public void testModifiedRanking() {
		Runnable runIt = new Service();
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put(getName(), Boolean.TRUE);
		props.put(Constants.SERVICE_RANKING, Integer.valueOf(15));
		ServiceRegistration<Runnable> reg1 = getContext()
				.registerService(
				Runnable.class, runIt, props);
		props.put(Constants.SERVICE_RANKING, Integer.valueOf(10));
		ServiceRegistration<Runnable> reg2 = getContext()
				.registerService(
				Runnable.class, runIt, props);
		ServiceTracker<Runnable, Runnable> testTracker = null;
		try {
			try {
				testTracker = new ServiceTracker<Runnable, Runnable>(
						getContext(),
						FrameworkUtil
						.createFilter("(&(objectclass=java.lang.Runnable)("
								+ getName().toLowerCase() + "=true))"), null);
			}
			catch (InvalidSyntaxException e) {
				fail("filter error", e);
			}
			testTracker.open();
			assertEquals("wrong service reference", reg1.getReference(),
					testTracker.getServiceReference());

			props.put(Constants.SERVICE_RANKING, Integer.valueOf(20));
			reg2.setProperties(props);
			assertEquals("wrong service reference", reg2.getReference(),
					testTracker.getServiceReference());
		}
		finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (testTracker != null)
				testTracker.close();
		}
	}

	public void testMap() {
		Service runIt = new Service();
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put(getName(), Boolean.TRUE);
		props.put(Constants.SERVICE_RANKING, Integer.valueOf(15));
		ServiceRegistration<Service> reg1 = getContext().registerService(
				Service.class, runIt, props);
		props.put(Constants.SERVICE_RANKING, Integer.valueOf(10));
		ServiceRegistration<Service> reg2 = getContext().registerService(
				Service.class, runIt, props);
		ServiceTracker<Service, Service> testTracker = null;
		try {
			testTracker = new ServiceTracker<Service, Service>(getContext(),
					Service.class, null);
			Map<ServiceReference<Service>, Service> map = testTracker
					.getTracked();
			assertEquals("wrong size", testTracker.size(), map.size());
			testTracker.open();

			SortedMap<ServiceReference<Service>, Service> sortedMap = testTracker
					.getTracked();
			assertEquals("wrong service reference", reg1.getReference(),
					sortedMap.firstKey());
			assertEquals("wrong service reference", reg2.getReference(),
					sortedMap.lastKey());
			assertEquals("wrong size", testTracker.size(), sortedMap.size());

			props.put(Constants.SERVICE_RANKING, Integer.valueOf(20));
			reg2.setProperties(props);

			sortedMap = testTracker.getTracked();
			assertEquals("wrong service reference", reg2.getReference(),
					sortedMap.firstKey());
			assertEquals("wrong service reference", reg1.getReference(),
					sortedMap.lastKey());
			assertEquals("wrong size", testTracker.size(), sortedMap.size());
		}
		finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (testTracker != null)
				testTracker.close();
		}
	}

	private void clearResults(boolean[] results) {
		for (int i = 0; i < results.length; i++)
			results[i] = false;
	}

	/**
     * Similarly, a new type of SeviceTracker is added (AllServiceTracker). The
     * AllServiceTracker allows a bundle to track all services regardless of
     * what version of a package they are wired to. The AllServiceTracker is
     * identical in function to the ServiceTracker API except it will register
     * an AllServiceListener to track ServiceReference objects for a bundle.
     *
     * @spec ServiceTracker.open(boolean)
     * @throws Exception if there is any problem or an assert fails
     */
    public void testAllServiceTracker01() throws Exception {
    	Bundle tb1;
        Bundle tb5;
		ServiceTracker<TestService1,TestService1> serviceTracker;

        tb1 = installBundle("tb1.jar");
        tb5 = installBundle("tb5.jar");

		serviceTracker = new ServiceTracker<>(
    			getContext(),
				TestService1.class,
    			null);
    	serviceTracker.open(true);

    	try {
            assertEquals(
    				"ServiceTracker must track services which are not class loader accessibile",
                    2, serviceTracker.size());
            serviceTracker.close();

            serviceTracker.open(false);

            assertEquals(
                    "ServiceTracker must track services which are not class loader accessibile",
                    1, serviceTracker.size());

    	}
    	finally {
            serviceTracker.close();
            tb5.uninstall();
    		tb1.uninstall();
    	}
    }

    /**
	 * Make sure different ServiceTrackers in same bundle get same service
	 * objects for a prototype scoped service.
	 * 
	 * @throws Exception
	 */
	public void testPrototypeService() throws Exception {
		Bundle tb6 = installBundle("tb6.jar");

		ServiceTracker<TestService1, TestService1> serviceTracker1 = new ServiceTracker<>(getContext(),
				TestService1.class, null);
		ServiceTracker<TestService1, TestService1> serviceTracker2 = new ServiceTracker<>(getContext(),
				TestService1.class, null);
		serviceTracker1.open();
		serviceTracker2.open();

		try {
			assertEquals("wrong size", 1, serviceTracker1.size());
			assertEquals("wrong size", 1, serviceTracker2.size());
			TestService1 s1 = serviceTracker1.getService();
			TestService1 s2 = serviceTracker2.getService();
			assertSame("not same service", s1, s2);
		} finally {
			serviceTracker1.close();
			serviceTracker2.close();
			tb6.uninstall();
		}
	}

	static class Service implements Runnable {
		public void run() {
			// nothing
		}
	};
}

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
package org.osgi.test.cases.framework.junit.lifecycle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * 
 * The testPermissions test is currently removed because of unknown state. It
 * uses tb10 but this seems to be removed from the upper makefile.
 * 
 * @author Ericsson Radio Systems AB
 */
public class TestBundleControl extends DefaultTestBundleControl implements
		BundleListener, ServiceListener {
	private Vector<Object> _eventQueue = new Vector<>(16);

	/**
	 * Tests normal Install/Uninstall/start/stop of a bundle.
	 */
	public void testNormalInstallStartStopUninstall() throws Exception {
		Bundle tb = installBundle("lifecycle.tb1.jar", false);
		assertTrue("Wring bundle state: " + tb.getState(),
				tb.getState() == Bundle.INSTALLED
						|| tb.getState() == Bundle.RESOLVED);
		try {
			tb.start();
		}
		catch (BundleException e) {
			fail("Failed to start " + tb.getSymbolicName(), e);
		}
		assertEquals("Wrong state for " + tb.getSymbolicName(), Bundle.ACTIVE,
				tb.getState());
		try {
			tb.stop();
		}
		catch (BundleException e) {
			fail("Failed to stop " + tb.getSymbolicName(), e);
		}
		assertEquals("Wrong state for " + tb.getSymbolicName(),
				Bundle.RESOLVED, tb.getState());
		try {
			tb.uninstall();
		}
		catch (BundleException e) {
			fail("Failed to uninstall " + tb.getSymbolicName(), e);
		}
		assertEquals("Wrong state for " + tb.getSymbolicName(),
				Bundle.UNINSTALLED, tb.getState());
	}

	/**
	 * Tests Start/Stop with exception.
	 */
	public void testStartStopWithException() throws Exception {
		Bundle tb2a, tb2b;
		tb2a = installBundle("lifecycle.tb2a.jar", false);
		try {
			tb2a.start();
			fail("Testing start with exception: No exception thrown, Error!");
		}
		catch (BundleException be) {
			assertEquals("Wrong state for " + tb2a.getSymbolicName(),
					Bundle.RESOLVED, tb2a.getState());
		}
		try {
			tb2a.uninstall();
		}
		catch (BundleException e) {
			fail("Failed to uninstall " + tb2a.getSymbolicName(), e);
		}
		tb2a = null;

		tb2b = installBundle("lifecycle.tb2b.jar", false);
		try {
			tb2b.start();
		}
		catch (BundleException e) {
			fail("Failed to start " + tb2b.getSymbolicName(), e);
		}
		assertEquals("Wrong state for " + tb2b.getSymbolicName(),
				Bundle.ACTIVE, tb2b.getState());
		try {
			tb2b.stop();
			fail("Testing stop with exception: No exception thrown, Error!");
		}
		catch (BundleException be) {
			assertEquals("Wrong state for " + tb2b.getSymbolicName(),
					Bundle.RESOLVED, tb2b.getState());
		}
		try {
			tb2b.uninstall();
		}
		catch (BundleException e) {
			fail("Failed to uninstall " + tb2b.getSymbolicName(), e);
		}
		tb2b = null;
	}

	/**
	 * Tests unavailable URL.
	 */
	public void testUnavailableURL() throws Exception {
		try {
			getContext().installBundle("NoSuchBundle.jar");
			fail("No exception thrown, Error!");
		}
		catch (BundleException be) {
			// do nothing; pass
		}
	}

	/**
	 * Tests halfway broken inputstream during install.
	 */
	public void testBrokenStreamInstall() throws Exception {
		byte b[] = new byte[500];
		try {
			URL url = getContext().getBundle().getEntry("lifecycle.tb4.jar");
			assertNotNull("lifecycle.tb4.jar is null", url);
			InputStream in = url.openStream();
			// Read 500 bytes of the file
			for (int i = 0; i < 500; i += in.read(b, i, 500 - i)) {
				// empty
			}
			// Now, make an InputStream from those first 500 bytes.
			ByteArrayInputStream bin = new ByteArrayInputStream(b);
			try {
				getContext().installBundle(url.toExternalForm(), bin);
				fail("No exception thrown, Error!");
			}
			catch (BundleException be) {
				// do nothing; pass
			}
		}
		catch (IOException ioe) {
			fail("Failed to read content", ioe);
		}
	}

	/**
	 * Tests halfway broken inputstream during update.
	 */
	public void testBrokenStreamUpdate() throws Exception {
		Bundle tb = installBundle("lifecycle.tb6a.jar", false);
		try {
			tb.start();
			long Id = tb.getBundleId();
			String bsn = tb.getSymbolicName();
			Version version = tb.getVersion();

			URL url = getContext().getBundle().getEntry("lifecycle.tb6b.jar");
			assertNotNull("lifecycle.tb6b.jar is null", url);
			InputStream in = url.openStream();

			// Read 500 bytes of the file
			byte b[] = new byte[500];
			for (int i = 0; i < 500; i += in.read(b, i, 500 - i)) {
				// empty
			}
			// Now, make an InputStream from those first 500 bytes.
			ByteArrayInputStream bin = new ByteArrayInputStream(b);
			try {
				tb.update(bin);
				fail("No exception thrown, Error!");
			} catch (BundleException be) {
				// do nothing; pass
			}

			// Make sure it is still the same bundle and active
			assertEquals("Bundle is not active.", Bundle.ACTIVE, tb.getState());
			assertEquals("Wrong bundle id", Id, tb.getBundleId());
			assertEquals("Wrong symbolic name.", bsn, tb.getSymbolicName());
			assertEquals("Wrong version.", version, tb.getVersion());
		} catch (IOException ioe) {
			fail("Failed to read content", ioe);
		} finally {
			tb.uninstall();
		}
	}

	/**
	 * Used by the Event test below to receive bundle events.
	 */
	public synchronized void bundleChanged(BundleEvent be) {
		// TODO should really test for RESOLVED/UNRESOLVED. Filtering for now
		// until ...
		if ((be.getType() & (BundleEvent.RESOLVED | BundleEvent.UNRESOLVED)) != 0)
			return;
		// System.out.println("In " + _eventQueue.size() + " event : "
		// + event(be.getType()));
		_eventQueue.add(be);
		notify();
	}

	/**
	 * Used by the Event test below to receive service events.
	 */
	public void serviceChanged(ServiceEvent se) {
		_eventQueue.add(se);
	}

	/**
	 * Tests various events.
	 */
	public void testEvents() throws Exception {
		getContext().addBundleListener(this);
		getContext().addServiceListener(this);
		Bundle tb = installBundle("lifecycle.tb5.jar", false);
		syncBundle("Get install event", BundleEvent.INSTALLED, getContext()
				.getBundle());
		tb.start();
		syncService("EventTest", ServiceEvent.REGISTERED);
		syncBundle("Get started event", BundleEvent.STARTED, tb);
		tb.stop();
		syncService("EventTest", ServiceEvent.UNREGISTERING);
		syncBundle("Get stopped event", BundleEvent.STOPPED, tb);
		tb.uninstall();
		syncBundle("Get uninstalled event", BundleEvent.UNINSTALLED, tb);
		getContext().removeBundleListener(this);
		getContext().removeServiceListener(this);
		final boolean[] called = new boolean[] {false};
		ServiceListener sl = new ServiceListener() {
			public void serviceChanged(ServiceEvent se) {
				called[0] = true;
			}
		};
		getContext()
				.addServiceListener(sl,
						"(objectClass=org.osgi.test.cases.framework.lifecycle.tb5.EventTest)");
		tb = installBundle("lifecycle.tb5.jar", false);
		tb.start();
		tb.stop();
		tb.uninstall();
		getContext().removeServiceListener(sl);
		assertTrue("Listener not called", called[0]);

		called[0] = false;
		getContext().addServiceListener(sl, "(objectClass=not)");
		tb = installBundle("lifecycle.tb5.jar", false);
		tb.start();
		tb.stop();
		tb.uninstall();
		getContext().removeServiceListener(sl);
		assertFalse("Listener called", called[0]);
	}

	/**
	 * Tests service registrations.
	 */
	public void testServiceRegistrations() throws Exception {
		String clazzes[];
		ServiceRegistration< ? > sr;
		ServiceReference< ? > refs[];
		Hashtable<String,Object> props;
		boolean found;
		clazzes = new String[] {TestBundleControl.class.getName(),
				DefaultTestBundleControl.class.getName(),
				ServiceListener.class.getName()};
		props = new Hashtable<>();
		props.put("PropertyX", "TBC");
		sr = getContext().registerService(clazzes, this, props);
		refs = getContext().getServiceReferences(
				DefaultTestBundleControl.class.getName(), null);
		assertNotNull("No service references found", refs);
		found = false;
		for (int i = 0; i < refs.length; i++)
			if (refs[i].getProperty("PropertyX").equals("TBC"))
				found = true;
		assertTrue("Did not find PropertyX", found);
		sr.unregister();
		sr = null;
		clazzes = new String[] {BundleActivator.class.getName()};
		try {
			sr = getContext().registerService(clazzes, this, props);
			fail("No exception thrown, Error!");
		}
		catch (IllegalArgumentException iae) {
			// do nothing; pass
		}
	}

	/**
	 * Tests normal update.
	 */
	public void testNormalUpdate() throws Exception {
		Bundle tb;
		long Id;
		getContext().addBundleListener(this);
		tb = installBundle("lifecycle.tb6a.jar", false);
		syncBundle("Test update, get installed ", BundleEvent.INSTALLED,
				getContext().getBundle());
		tb.start();
		syncBundle("Test update, get started ", BundleEvent.STARTED, tb);
		Id = tb.getBundleId();
		URL url = getContext().getBundle().getEntry("lifecycle.tb6b.jar");
		assertNotNull("url is null", url);
		InputStream in = url.openStream();
		tb.update(in);
		syncBundle("Test update expect stopped before update",
				BundleEvent.STOPPED, tb);
		syncBundle("Test update, now get updated ", BundleEvent.UPDATED, tb);
		syncBundle("Test update, start again", BundleEvent.STARTED, tb);
		assertEquals("Wrong bundle id", Id, tb.getBundleId());
		tb.stop();
		syncBundle("Test update get stopped", BundleEvent.STOPPED, tb);
		tb.uninstall();
		syncBundle("Test update, get uninstalled ", BundleEvent.UNINSTALLED, tb);
		getContext().removeBundleListener(this);
	}

	/**
	 * Tests failed update with rollback.
	 */
	public void testRollbackUpdate() throws Exception {
		Bundle tb;
		tb = installBundle("lifecycle.tb7.jar");
		tb.start();
		try {
			tb.update();
			fail("No exception thrown, Error!");
		}
		catch (BundleException be) {
			// do nothing; pass
		}
		assertEquals("Wrong bundle state", Bundle.ACTIVE, tb.getState());
		tb.uninstall();
	}

	/**
	 * Tries to use a stopped BundleContext.
	 */
	public void testStoppedBundleContext() throws Exception {
		Bundle tb = installBundle("lifecycle.tb8.jar", false);
		try {
			tb.start();
			BundleContext otherContext = tb.getBundleContext();
			tb.stop();
			// By now, this bc should be illegal to use.
			try {
				otherContext.getServiceReference(TestBundleControl.class
						.getName());
				fail("BundleContext is still usable, ERROR!");

			}
			catch (IllegalStateException ise) {
				// do nothing; pass
			}
			tb.start();
			// The context should not become reusable after we restart the
			// bundle!
			try {
				otherContext.getServiceReference(TestBundleControl.class
						.getName());
				fail("BundleContext is still usable, ERROR!");
			}
			catch (IllegalStateException ise2) {
				// expected
			}
		}
		finally {
			tb.stop();
			tb.uninstall();
		}
	}

	/**
	 * Tests that getLocation returns the correct location.
	 */
	public void testGetLocation() throws Exception {
		// Use the same bundle as the "hang in stop" test, we won't start (or
		// stop) it anyway.
		URL url = getContext().getBundle().getEntry("lifecycle.tb9.jar");
		assertNotNull(url);
		String loc = url.toExternalForm();
		Bundle tb = getContext().installBundle(loc);
		try {
			assertEquals("Unexpected Location", loc, tb.getLocation());
		}
		finally {
			tb.uninstall();
		}
	}

	private synchronized void syncService(String test, int event) {
		if (_eventQueue.size() == 0)
			fail(test + ": Time out on event receive " + event);
		ServiceEvent se = (ServiceEvent) _eventQueue.remove(0);
		String service = (String) se.getServiceReference().getProperty(
				"Service-Name");
		assertEquals("Wrong event type", event, se.getType());
		assertEquals("Wrong service", test, service);
	}

	private synchronized void syncBundle(String test, int event, Bundle origin) {
		try {
			if (_eventQueue.size() == 0) {
				System.out.println("Waiting for event");
				wait(10000);
			}
			if (_eventQueue.size() == 0) {
				fail(test + ": Time out on event receive " + event(event));
			}
			BundleEvent be = (BundleEvent) _eventQueue.remove(0);
			assertEquals(test + ": invalid event", event(event),
					event(be.getType()));
			assertSame("wrong origin", origin, be.getOrigin());
		}
		catch (InterruptedException x) {/* cannot happen */
		}
	}

	private String event(int event) {
		switch (event) {
			case 0 :
				return "(NONE)";
			case BundleEvent.INSTALLED :
				return "INSTALLED(" + BundleEvent.INSTALLED + ")";
			case BundleEvent.STARTED :
				return "STARTED(" + BundleEvent.STARTED + ")";
			case BundleEvent.UPDATED :
				return "UPDATED(" + BundleEvent.UPDATED + ")";
			case BundleEvent.STOPPED :
				return "STOPPED(" + BundleEvent.STOPPED + ")";
			case BundleEvent.UNINSTALLED :
				return "UNINSTALLED(" + BundleEvent.UNINSTALLED + ")";
			case BundleEvent.RESOLVED :
				return "RESOLVED(" + BundleEvent.RESOLVED + ")";
			case BundleEvent.UNRESOLVED :
				return "UNRESOLVED(" + BundleEvent.UNRESOLVED + ")";
			case BundleEvent.STARTING :
				return "STARTING(" + BundleEvent.STARTING + ")";
			case BundleEvent.STOPPING :
				return "STOPPING(" + BundleEvent.STOPPING + ")";
			default :
				return "UNKNOWN(" + event + ")";
		}
	}
}

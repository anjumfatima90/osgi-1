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
package org.osgi.test.cases.event.junit;

import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.test.cases.event.service.TBCService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The <code>EventTestControl</code> is the bundle initially installed and
 * started by the EventTestCase when it is started. It performs the various
 * generic event mechanism tests and reports back to the EventTestCase.
 *
 * @author $Id$
 */
public class EventAdminTests extends DefaultTestBundleControl {

	EventAdmin eventAdmin;
	private Bundle tb1;
	private Bundle tb2;

	/**
	 * Prepare for each run. It is important that a test run is properly
	 * initialized and that each case can run standalone.
	 *
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
		log("#before each run");
		tb1 = installBundle("tb1.jar");
		tb1.start();
		tb2 = installBundle("tb2.jar");
		tb2.start();
		eventAdmin = getService(EventAdmin.class);
	}

	/**
	 * Clean up after a run. Notice that during debugging many times the
	 * tearDown is never reached.
	 *
	 * @throws Exception
	 */
	protected void tearDown() throws Exception {
		log("#after each run");
		tb1.stop();
		uninstallBundle(tb1);
		tb2.stop();
		uninstallBundle(tb2);
		ungetAllServices();
	}

	/**
	 * Tests if org.osgi.test.cases.event.tb1 and org.osgi.test.cases.event.tb2
	 * are succesfully installed and if their TBCService serivices are avilable.
	 * It is checked if there is exactly one EventAdmin registered service.
	 *
	 * Verify that the System bundle exists and exports the system services:
	 * PermissionAdmin.
	 *
	 * @specification org.osgi.framework
	 * @specificationSection system.bundle
	 */
	public void testInstallation() throws Exception {
		ServiceTracker<TBCService,TBCService> trackerProvider1 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = trackerProvider1.getService();
		assertNotNull("TBCService service in tb1 should be registered",
				tbcService1);
		trackerProvider1.close();

		ServiceTracker<TBCService,TBCService> trackerProvider2 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb2.Activator", null);
		trackerProvider2.open();
		TBCService tbcService2 = trackerProvider2.getService();
		assertNotNull("TBCService service in tb2 should be registered",
				tbcService2);
		trackerProvider2.close();

		ServiceReference< ? >[] eventAdminSRs = getContext()
				.getServiceReferences(
				EventAdmin.class.getName(), null);
		if (eventAdminSRs != null) {
			assertEquals(
					"There must be exactly one EventAdmin registered service ["
							+ EventAdmin.class.getName() + "]", 1,
					eventAdminSRs.length);
		}
	}

	/**
	 * Tests the notification for events after sending (if they match of the
	 * listeners).
	 */
	public void testSendEvent() { // TC4
		ServiceTracker<TBCService,TBCService> trackerProvider1 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = trackerProvider1.getService();

		ServiceTracker<TBCService,TBCService> trackerProvider2 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb2.Activator", null);
		trackerProvider2.open();
		TBCService tbcService2 = trackerProvider2.getService();

		String[] topics;
		topics = new String[] { "org/osgi/test/*", "org/osgi/newtest1/*",
				"org/osgi1/*", "org/Event1" };
		tbcService1.setProperties(topics, null);

		topics = new String[] { "org/osgi/test/*",
				"org/osgi/newtest1/newtest2/*", "org/osgi2/*" };
		tbcService2.setProperties(topics, null);

		String[] events = new String[] { "org/osgi/test/Event0", "org/Event1",
				"org/osgi1/Event2", "org/osgi1/test/Event3",
				"org/osgi/newtest1/Event4", "org/osgi/newtest2/Event5",
				"org/osgi2/test/Event6" };
		Boolean[] eventsMap1 = new Boolean[] { Boolean.TRUE, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE,
				Boolean.FALSE };
		Boolean[] eventsMap2 = new Boolean[] { Boolean.TRUE, Boolean.FALSE,
				Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,
				Boolean.TRUE };

		Event event;
		for (int i = 0; i < events.length; i++) {
			event = new Event(events[i],
					(Dictionary<String, ? >) new Hashtable<String,Object>());
			eventAdmin.sendEvent(event);
			pass("before 1");
			assertEvent(event, tb1, tbcService1, eventsMap1[i].booleanValue());
			pass("before 2");
			assertEvent(event, tb2, tbcService2, eventsMap2[i].booleanValue());
		}
		trackerProvider1.close();
		trackerProvider2.close();
	}

	/**
	 * Tests the notification for events after posting (if they match of the
	 * listeners).
	 */
	public void testPostEvent() { // TC5
		ServiceTracker<TBCService,TBCService> trackerProvider1 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = trackerProvider1.getService();

		ServiceTracker<TBCService,TBCService> trackerProvider2 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb2.Activator", null);
		trackerProvider2.open();
		TBCService tbcService2 = trackerProvider2.getService();

		String[] topics = new String[] { "test/*" };
		String[] delivery = new String[] {EventConstants.DELIVERY_ASYNC_ORDERED};
		tbcService1.setProperties(topics, null);
		tbcService2.setProperties(topics, delivery);

		Event[] events = new Event[10];
		for (int i = 0; i < events.length; i++) {
			events[i] = new Event("test/Event" + i,
					(Dictionary<String, ? >) new Hashtable<String,Object>());
		}

		for (int i = 0; i < events.length; i++) {
			eventAdmin.postEvent(events[i]);
		}
		// wait to ensure that events are received asynchronous
		try {
			Sleep.sleep(5000);
		} catch (InterruptedException e) {
		}

		List<Event> tbc1Events = tbcService1.getLastReceivedEvents();
		List<Event> tbc2Events = tbcService2.getLastReceivedEvents();
		String message = "Events should be recieved in the same order as they are post ";

		if (tbc1Events == null || tbc1Events.size() == 0) {
			fail("tbc1: No events recived");
		}
		if (tbc2Events == null || tbc2Events.size() == 0) {
			fail("tbc2: No events recived");
		}

		Event event;
		for (int i = 0; i < tbc1Events.size(); i++) {
			event = tbc1Events.get(i);
			if (event == null) {
				fail("tbc1: Event with topic [test/Event" + i
						+ "] not recieved");
			}
			assertEquals(message, "test/Event" + i, event.getTopic());
		}
		for (int i = 0; i < tbc2Events.size(); i++) {
			event = tbc2Events.get(i);
			if (event == null) {
				fail("tbc2: Event with topic [test/Event" + i
						+ "] not recieved");
			}
			assertEquals(message, "test/Event" + i, event.getTopic());
		}
		trackerProvider1.close();
		trackerProvider2.close();
	}

	/**
	 * Tests the notification for events after posting (if they match of the
	 * listeners).
	 */
	public void testPostEventUnordered() { // TC5
		ServiceTracker<TBCService,TBCService> trackerProvider1 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = trackerProvider1.getService();

		ServiceTracker<TBCService,TBCService> trackerProvider2 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb2.Activator", null);
		trackerProvider2.open();
		TBCService tbcService2 = trackerProvider2.getService();

		String[] topics = new String[] {"test/*"};
		String[] delivery = new String[] {EventConstants.DELIVERY_ASYNC_UNORDERED};
		tbcService1.setProperties(topics, delivery);
		tbcService2.setProperties(topics, delivery);

		Event[] events = new Event[10];
		for (int i = 0; i < events.length; i++) {
			events[i] = new Event("test/Event" + i,
					(Dictionary<String, ? >) new Hashtable<String,Object>());
		}

		for (int i = 0; i < events.length; i++) {
			eventAdmin.postEvent(events[i]);
		}
		// wait to ensure that events are received asynchronous
		try {
			Sleep.sleep(5000);
		}
		catch (InterruptedException e) {
			// ignored
		}

		List<Event> tbc1Events = tbcService1.getLastReceivedEvents();
		List<Event> tbc2Events = tbcService2.getLastReceivedEvents();

		if (tbc1Events == null || tbc1Events.size() == 0) {
			fail("tbc1: No events recived");
		}
		if (tbc2Events == null || tbc2Events.size() == 0) {
			fail("tbc2: No events recived");
		}

		for (int i = 0; i < events.length; i++) {
			if (tbc1Events.contains(events[i])) {
				pass("tbc1: Event with topic [test/Event" + i + "] recieved");
			}
			else {
				fail("tbc1: Event with topic [test/Event" + i
						+ "] not recieved");
			}
		}
		for (int i = 0; i < events.length; i++) {
			if (tbc2Events.contains(events[i])) {
				pass("tbc2: Event with topic [test/Event" + i + "] recieved");
			}
			else {
				fail("tbc2: Event with topic [test/Event" + i
						+ "] not recieved");
			}
		}
		trackerProvider1.close();
		trackerProvider2.close();
	}

	/**
	 * Tests the notification for events after posting simultaneously in 10
	 * threads (if they match of the listeners).
	 */
	public void testMultiThreadsPostEvent() { // TC7
		testMultiThreads(10, "postEvent");
	}

	/**
	 * This is used to start posting or sending simultaneously.
	 */
	class MultiThread extends Thread {
		private final Event		event;
		private final Object	lock;
		private final String	method;

		MultiThread(Event event, Object lock, String name, String method) {
			super(name);
			this.lock = lock;
			this.event = event;
			this.method = method;
		}

		public void run() {
			trace("MultiThread started on event: " + event);
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// ignored
				}
			}
			if ("postEvent".equals(method)) {
				eventAdmin.postEvent(event);
			} else {
				if ("sendEvent".equals(method)) {
					eventAdmin.sendEvent(event);
				}
				else {
					// unrecognized method
				}
			}
			trace("MultiThread " + method
					+ " event: " + event);
		}
	}

	/**
	 * Tests the notification for events after sending simultaneously in 10
	 * threads (if they match of the listeners).
	 */
	public void testMultiThreadsSendEvent() { // TC6
		testMultiThreads(10, "sendEvent");
	}

	/**
	 * Tests the notification for events after firing simultaneously in count
	 * threads (if they match of the listeners).
	 */
	private void testMultiThreads(final int count, final String method) {
		ServiceTracker<TBCService,TBCService> trackerProvider1 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = trackerProvider1.getService();

		ServiceTracker<TBCService,TBCService> trackerProvider2 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb2.Activator", null);
		trackerProvider2.open();
		TBCService tbcService2 = trackerProvider2.getService();

		String[] topics = new String[] {"test/*"};
		tbcService1.setProperties(topics,
				new String[] {EventConstants.DELIVERY_ASYNC_ORDERED});
		tbcService2.setProperties(topics,
				new String[] {EventConstants.DELIVERY_ASYNC_UNORDERED});

		Event[] events = new Event[count];
		for (int i = 0; i < events.length; i++) {
			events[i] = new Event("test/Event" + i,
					(Dictionary<String, ? >) new Hashtable<String,Object>());
		}

		MultiThread[] mpts = new MultiThread[events.length];

		Object lock = new Object();
		for (int i = 0; i < events.length; i++) {
			mpts[i] = new MultiThread(events[i], lock,
 "[MultiThread] - " + i,
					method);
			mpts[i].start();
		}

		// wait to ensure that all threads are started
		boolean allAlive;
		do {
			allAlive = true;
			for (int i = 0; i < mpts.length; i++) {
				if (!mpts[i].isAlive()) {
					allAlive = false;
					try {
						Sleep.sleep(100);
					}
					catch (InterruptedException e) {
						// ignored
					}
					break;
				}
			}
		} while (!allAlive);
		// add small sleep to ensure all threads go to wait on lock
		try {
			Sleep.sleep(3000);
		}
		catch (InterruptedException e) {
			// ignored
		}
		trace("All MultiThread started, notify all...");
		// here notify all threads to start posting events simultaneously
		synchronized (lock) {
			lock.notifyAll();
		}
		trace("Wait all MultiThread to deliver events");
		// wait to ensure that events are received asynchronous
		try {
			Sleep.sleep(5000);
		}
		catch (InterruptedException e) {
			// ignored
		}

		List<Event> tbc1Events = tbcService1.getLastReceivedEvents();
		List<Event> tbc2Events = tbcService2.getLastReceivedEvents();

		if (tbc1Events == null || tbc1Events.size() == 0) {
			fail("tbc1: No events received");
		}
		if (tbc2Events == null || tbc2Events.size() == 0) {
			fail("tbc2: No events received");
		}

		for (int i = 0; i < events.length; i++) {
			if (tbc1Events.contains(events[i])) {
				pass("tbc1: Event with topic [test/Event" + i + "] received");
			}
			else {
				fail("tbc1: Event with topic [test/Event" + i
						+ "] not received");
			}
		}
		for (int i = 0; i < events.length; i++) {
			if (tbc2Events.contains(events[i])) {
				pass("tbc2: Event with topic [test/Event" + i + "] received");
			}
			else {
				fail("tbc2: Event with topic [test/Event" + i
						+ "] not received");
			}
		}
		trackerProvider1.close();
		trackerProvider2.close();
	}

	private void assertEvent(Event eventPassed, Bundle bundle,
			TBCService tbcService, boolean recieved) {
		pass(">>>Passed event: " + eventPassed);
		// pass("Bundle's event handler topic: " +
		// arrayToString(tbcService.getTopics()));
		Event eventReceived = tbcService.getLastReceivedEvent();
		assertEquals("In [" + bundle.getSymbolicName() + "] received event ["
				+ eventReceived + "]", recieved, eventReceived != null);
		if (eventReceived != null) {
			String[] properties = eventReceived.getPropertyNames();
			String property;
			for (int i = 0; i < properties.length; i++) {
				property = properties[i];
				eventReceived.getProperty(property);
			}
		}
	}

	/**
	 * Tests support for type String as EventConstants.EVENT_TOPIC property value.
	 * An implementation passes this test if an event is received on the topic.
	 */
	public void testEventTopicsPropertyString() {
		ServiceTracker<TBCService,TBCService> trackerProvider1 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = trackerProvider1.getService();
		String topic = "org/osgi/test/Event";
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(EventConstants.EVENT_TOPIC, topic);
		tbcService1.setProperties(properties);
		Event event = new Event(topic, (Dictionary<String, ? >) null);
		eventAdmin.sendEvent(event);
		assertEvent(event, tb1, tbcService1, true);
		trackerProvider1.close();
	}

	/**
	 * Tests support for type String[] as EventConstants.EVENT_TOPIC property value.
	 * An implementation passes this test if an event is received on each topic.
	 */
	public void testEventTopicsPropertyStringArray() {
		ServiceTracker<TBCService,TBCService> trackerProvider1 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = trackerProvider1.getService();
		String[] topics = new String[] {
				"org/osgi/test/Event1",
				"org/osgi/test/Event2"
		};
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		tbcService1.setProperties(properties);
		for (int i = 0; i < topics.length; i++) {
			Event event = new Event(topics[i], (Dictionary<String, ? >) null);
			eventAdmin.sendEvent(event);
			assertEvent(event, tb1, tbcService1, true);
		}
		trackerProvider1.close();
	}

	/**
	 * Tests support for type Collection<String> as EventConstants.EVENT_TOPIC property value.
	 * An implementation passes this test if an event is received on each topic.
	 */
	public void testEventTopicsPropertyStringCollection() {
		ServiceTracker<TBCService,TBCService> trackerProvider1 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = trackerProvider1.getService();
		Collection<String> topics = Arrays.asList("org/osgi/test/Event1",
				"org/osgi/test/Event2",
				"org/osgi/test/Event3");
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		tbcService1.setProperties(properties);
		for (String topic : topics) {
			Event event = new Event(topic,
					(Dictionary<String, ? >) null);
			eventAdmin.sendEvent(event);
			assertEvent(event, tb1, tbcService1, true);
		}
		trackerProvider1.close();
	}

	/**
	 * Ensures EventAdmin does not deliver an event published on topic "a/b/c"
	 * to an EventHandler listening to topic a/b/c/*.
	 */
	public void testEventDeliveryForWildcardTopic1() {
		ServiceTracker<TBCService,TBCService> trackerProvider1 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = trackerProvider1.getService();
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(EventConstants.EVENT_TOPIC, "a/b/c/*");
		tbcService1.setProperties(properties);
		Event event = new Event("a/b/c", (Dictionary<String, ? >) null);
		eventAdmin.sendEvent(event);
		assertEvent(event, tb1, tbcService1, false);
		trackerProvider1.close();
	}

	/**
	 * Ensures EventAdmin delivers an event published to topic "a/b/c" or
	 * "a/b/c/d" to an EventHandler listening to topics "a/b/c" and "a/b/c/*".
	 */
	public void testEventDeliveryForWildcardTopic2() {
		ServiceTracker<TBCService,TBCService> trackerProvider1 = new ServiceTracker<>(
				getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = trackerProvider1.getService();
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(EventConstants.EVENT_TOPIC, new String[] {"a/b/c", "a/b/c/*"});
		tbcService1.setProperties(properties);
		Event event = new Event("a/b/c", (Dictionary<String, ? >) null);
		eventAdmin.sendEvent(event);
		assertEvent(event, tb1, tbcService1, true);
		event = new Event("a/b/c/d", (Dictionary<String, ? >) null);
		eventAdmin.sendEvent(event);
		assertEvent(event, tb1, tbcService1, true);
		trackerProvider1.close();
	}
}

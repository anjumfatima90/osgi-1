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
package org.osgi.test.cases.webcontainer.junit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.test.cases.webcontainer.util.EventFactory;
import org.osgi.test.cases.webcontainer.util.WebContainerTestBundleControl;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.sleep.Sleep;

/**
 * @version $Rev$ $Date$
 *
 *          EventTest to verify Events are published by the web extender
 *          bundle at certain situations defined by rfc 66
 */
public class EventTest extends WebContainerTestBundleControl {
    Bundle eventhandler;
    private final int WAITCOUNT = 10;
    private final String EXTENDER_BUNDLE = "extender.bundle";
    private final String EXTENDER_BUNDLE_ID = "extender.bundle.id";
    private final String EXTENDER_BUNDLE_VERSION = "extender.bundle.version";
    private final String EXTENDER_BUNDLE_SYMBOLICNAME = "extender.bundle.symbolicName";
    private final static String COLLISION = "collision";
    private final static String COLLISION_BUNDLES = "collision.bundles";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw1");
        this.options.put(Constants.BUNDLE_SYMBOLICNAME, "org.osgi.test.cases.webcontainer.tw1");

        // install the war file
        log("install war file: tw1.war at context path " + this.warContextPath);
        String loc = super.getWarURL("tw1.war", this.options);
        if (this.debug) {
            log("bundleName to be passed into installBundle is " + loc);
        }
        super.b = installBundle(loc, false);

        // verify event admin service is installed
        log("verify event admin service is installed.  The tests in this class require event admin service being installed.");
		ServiceReference<EventAdmin> sr = getContext()
				.getServiceReference(EventAdmin.class);
        assertNotNull("EventAdmin service is not available", sr);
        assertNotNull(getContext().getService(sr));

        log("install & start the test event handler");
        this.eventhandler = getContext().installBundle(
                getWebServer() + "eventHandler.jar");
        this.eventhandler.start();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        this.eventhandler.uninstall();
    }

    /**
     * this test tests a simple war start/stop with the correct events emitted
     * @throws Exception
     */
    public void testEvent001() throws Exception {
        // clear all events in the eventfactory
        //EventFactory.clearEvents();
        //assertEquals("factory should not have any event", 0, EventFactory.getEventSize());

        this.b.start();

        // expect emit of the following events:
        // org/osgi/service/web/DEPLOYING the web extender has spotted the web application bundle and is starting it.
        // org/osgi/service/web/DEPLOYED the web extender has finished starting the web application bundle. Formatted: Bullets and Numbering
        // wait a few seconds to make sure events are delivered.
        int count = 0;
        Event eventPrevious = null;
        Event eventCurrent = null;
        while(eventCurrent == null && count < WAITCOUNT) {
            eventCurrent = EventFactory.getEvent("org.osgi.test.cases.webcontainer.tw1", "org/osgi/service/web/DEPLOYED");
			Sleep.sleep(1000 * OSGiTestCaseProperties.getScaling());
            count++;
        }

        assertEquals("event factory size should be 2", 2, EventFactory.getEventSize());
        eventPrevious = EventFactory.getEvent("org.osgi.test.cases.webcontainer.tw1", "org/osgi/service/web/DEPLOYING");
        assertNotNull(eventPrevious);
        assertNotNull(eventCurrent);

        long startingTime = (Long)eventPrevious.getProperty(EventConstants.TIMESTAMP);
        long startedTime = (Long)eventCurrent.getProperty(EventConstants.TIMESTAMP);

        assertEquals("org/osgi/service/web/DEPLOYING", eventPrevious.getTopic());
        assertEquals("org.osgi.test.cases.webcontainer.tw1", (String)eventPrevious.getProperty(EventConstants.BUNDLE_SYMBOLICNAME));
        assertEquals(this.b.getBundleId(), eventPrevious.getProperty(EventConstants.BUNDLE_ID));
        assertEquals(this.b, eventPrevious.getProperty(EventConstants.BUNDLE));
        assertEquals(this.b.getVersion(), eventPrevious.getProperty(EventConstants.BUNDLE_VERSION));
        assertEquals(this.b.getHeaders().get("Web-ContextPath"), (String)eventPrevious.getProperty("context.path"));
        assertNotNull(startingTime);
        assertNotNull(eventPrevious.getProperty(EXTENDER_BUNDLE));
        assertNotNull(eventPrevious.getProperty(EXTENDER_BUNDLE_ID));
        assertNotNull(eventPrevious.getProperty(EXTENDER_BUNDLE_SYMBOLICNAME));
        assertNotNull(eventPrevious.getProperty(EXTENDER_BUNDLE_VERSION));

        assertEquals("org/osgi/service/web/DEPLOYED", eventCurrent.getTopic());
        assertEquals("org.osgi.test.cases.webcontainer.tw1", (String)eventCurrent.getProperty(EventConstants.BUNDLE_SYMBOLICNAME));
        assertEquals(this.b.getBundleId(), eventCurrent.getProperty(EventConstants.BUNDLE_ID));
        assertEquals(this.b, eventCurrent.getProperty(EventConstants.BUNDLE));
        assertEquals(this.b.getVersion(), eventCurrent.getProperty(EventConstants.BUNDLE_VERSION));
        assertEquals(this.b.getHeaders().get("Web-ContextPath"), (String)eventPrevious.getProperty("context.path"));
        assertNotNull(startedTime);
        assertNotNull(eventCurrent.getProperty(EXTENDER_BUNDLE));
        assertNotNull(eventCurrent.getProperty(EXTENDER_BUNDLE_ID));
        assertNotNull(eventCurrent.getProperty(EXTENDER_BUNDLE_SYMBOLICNAME));
        assertNotNull(eventCurrent.getProperty(EXTENDER_BUNDLE_VERSION));

        // the extender information should be the same

        assertTrue(startedTime >= startingTime);
        assertEquals(eventPrevious.getProperty(EXTENDER_BUNDLE), eventCurrent.getProperty(EXTENDER_BUNDLE));
        assertEquals(eventPrevious.getProperty(EXTENDER_BUNDLE_ID), eventCurrent.getProperty(EXTENDER_BUNDLE_ID));
        assertEquals(eventPrevious.getProperty(EXTENDER_BUNDLE_SYMBOLICNAME), eventCurrent.getProperty(EXTENDER_BUNDLE_SYMBOLICNAME));
        assertEquals(eventPrevious.getProperty(EXTENDER_BUNDLE_VERSION), eventCurrent.getProperty(EXTENDER_BUNDLE_VERSION));

        eventPrevious = null;
        eventCurrent = null;


        this.b.stop();
        // emit the following events:
        // org/osgi/service/web/UNDEPLOYING the web extender is stopping the web application bundle.
        // org/osgi/service/web/UNDEPLOYED a web extender has stopped the web application bundle.
        // wait a few seconds to make sure events are delivered.
        count = 0;
        while(eventCurrent == null && count < WAITCOUNT) {
        	eventCurrent = EventFactory.getEvent("org.osgi.test.cases.webcontainer.tw1", "org/osgi/service/web/UNDEPLOYED");
			Sleep.sleep(1000 * OSGiTestCaseProperties.getScaling());
            count++;
        }
        eventPrevious = EventFactory.getEvent("org.osgi.test.cases.webcontainer.tw1", "org/osgi/service/web/UNDEPLOYING");
        assertNotNull(eventPrevious);
        assertNotNull(eventCurrent);
        assertEquals("org/osgi/service/web/UNDEPLOYING", eventPrevious.getTopic());
        assertEquals("org/osgi/service/web/UNDEPLOYED", eventCurrent.getTopic());
        assertTrue((Long)eventPrevious.getProperty(EventConstants.TIMESTAMP) >= startedTime);
        assertTrue((Long)eventCurrent.getProperty(EventConstants.TIMESTAMP) >= (Long)eventPrevious.getProperty(EventConstants.TIMESTAMP));
    }

    /**
     * this test tests a war start failure with correct events emitted.
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	public void testEvent002() throws Exception {
        // start the bundle again and try deploy another bundle that cause a failure
        this.b.start();
        // wait a few seconds to make sure events are delivered.
        int count = 0;
        Event eventPrevious = null;
        Event eventCurrent = null;

        // clear all events in the eventfactory
        EventFactory.clearEvents();
        assertEquals("factory should not have any event", 0, EventFactory.getEventSize());

        Map<String, Object> options = new HashMap<String, Object>();
        options.put(WEB_CONTEXT_PATH, "/tw1");
        options.put(Constants.BUNDLE_SYMBOLICNAME, "org.osgi.test.cases.webcontainer.tw4");

        // install the war file that uses the same WebContextPath
        log("install war file: tw4.war at context path /tw1");
        Bundle b2 = installBundle(super.getWarURL("tw4.war", options), false);
        try {
            b2.start();
            // emit the following events:
            // org/osgi/service/web/DEPLOYING the web extender has spotted the web application bundle and is starting it.
            // org/osgi/service/web/FAILED - a web extender cannot start the bundle, this will be fired after a DEPLOYING
            // event has been fired if the bundle cannot be started for any reason.
            // wait a few seconds to make sure events are delivered.
            count = 0;
            while(eventCurrent == null && count < WAITCOUNT) {
                eventCurrent = EventFactory.getEvent("org.osgi.test.cases.webcontainer.tw4", "org/osgi/service/web/FAILED");
				Sleep.sleep(1000 * OSGiTestCaseProperties.getScaling());
                count++;
            }
            eventPrevious = EventFactory.getEvent("org.osgi.test.cases.webcontainer.tw4", "org/osgi/service/web/DEPLOYING");
            assertNotNull(eventPrevious);
            assertNotNull(eventCurrent);

            long startingTime = (Long)eventPrevious.getProperty(EventConstants.TIMESTAMP);
            long failedTime = (Long)eventCurrent.getProperty(EventConstants.TIMESTAMP);

            assertEquals("org/osgi/service/web/DEPLOYING", eventPrevious.getTopic());
            assertEquals("org.osgi.test.cases.webcontainer.tw4", (String)eventPrevious.getProperty(EventConstants.BUNDLE_SYMBOLICNAME));
            assertEquals(b2.getBundleId(), eventPrevious.getProperty(EventConstants.BUNDLE_ID));
            assertEquals(b2, eventPrevious.getProperty(EventConstants.BUNDLE));
            assertEquals(b2.getVersion(), eventPrevious.getProperty(EventConstants.BUNDLE_VERSION));
            assertEquals(b2.getHeaders().get("Web-ContextPath"), (String)eventPrevious.getProperty("context.path"));
            assertNotNull(startingTime);
            assertNotNull(eventPrevious.getProperty(EXTENDER_BUNDLE));
            assertNotNull(eventPrevious.getProperty(EXTENDER_BUNDLE_ID));
            assertNotNull(eventPrevious.getProperty(EXTENDER_BUNDLE_SYMBOLICNAME));
            assertNotNull(eventPrevious.getProperty(EXTENDER_BUNDLE_VERSION));

            assertEquals("org/osgi/service/web/FAILED", eventCurrent.getTopic());
            assertEquals("org.osgi.test.cases.webcontainer.tw4", (String)eventCurrent.getProperty(EventConstants.BUNDLE_SYMBOLICNAME));
            assertEquals(b2.getBundleId(), eventCurrent.getProperty(EventConstants.BUNDLE_ID));
            assertEquals(b2, eventCurrent.getProperty(EventConstants.BUNDLE));
            assertEquals(b2.getVersion(), eventCurrent.getProperty(EventConstants.BUNDLE_VERSION));
            assertEquals(b2.getHeaders().get("Web-ContextPath"), (String)eventPrevious.getProperty("context.path"));
            assertNotNull(failedTime);
            assertNotNull(eventCurrent.getProperty(EXTENDER_BUNDLE));
            assertNotNull(eventCurrent.getProperty(EXTENDER_BUNDLE_ID));
            assertNotNull(eventCurrent.getProperty(EXTENDER_BUNDLE_SYMBOLICNAME));
            assertNotNull(eventCurrent.getProperty(EXTENDER_BUNDLE_VERSION));
            // remove this checking as spec doesn't mandate the exception property to be set.
            //assertNotNull((Throwable)eventCurrent.getProperty(EventConstants.EXCEPTION));
            assertNotNull(eventCurrent.getProperty(COLLISION));
            assertEquals("/tw1", (String)eventCurrent.getProperty(COLLISION));
            assertNotNull(eventCurrent.getProperty(COLLISION_BUNDLES));
            assertTrue("check collision.bundles property contains " + b2.getBundleId(), contains((Collection<Long>)eventCurrent.getProperty(COLLISION_BUNDLES), b2.getBundleId()));
            assertTrue("check collision.bundles property contains " + this.b.getBundleId(), contains((Collection<Long>)eventCurrent.getProperty(COLLISION_BUNDLES), this.b.getBundleId()));

            // the extender information should be the same
            assertTrue(failedTime >= startingTime);
            assertEquals(eventPrevious.getProperty(EXTENDER_BUNDLE), eventCurrent.getProperty(EXTENDER_BUNDLE));
            assertEquals(eventPrevious.getProperty(EXTENDER_BUNDLE_ID), eventCurrent.getProperty(EXTENDER_BUNDLE_ID));
            assertEquals(eventPrevious.getProperty(EXTENDER_BUNDLE_SYMBOLICNAME), eventCurrent.getProperty(EXTENDER_BUNDLE_SYMBOLICNAME));
            assertEquals(eventPrevious.getProperty(EXTENDER_BUNDLE_VERSION), eventCurrent.getProperty(EXTENDER_BUNDLE_VERSION));
        } finally {
            b2.uninstall();
        }
    }

    /**
     * check to see if a list contains the particular bundleId
     * @param bundleIds
     * @param bundlId
     * @return
     */
    private boolean contains(Collection<Long> bundleIds, Long bundleId) {
        for (Long id : bundleIds) {
            if (id.longValue() == bundleId.longValue()) {
                return true;
            }
        }
        return false;
    }
}

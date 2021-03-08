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

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Apr 17, 2006  Luiz Felipe Guimaraes
 * 283           [MEGTCK][DMT] DMT API changes
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtEvent;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtEventListener;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

import junit.framework.TestCase;



/**
 * @author Luiz Felipe Guimaraes
 *
 * This Class Validates the implementation of <code>DmtEvent<code> constants,
 * according to MEG specification
 */
public class DmtEvent implements TestInterface {
	private DmtTestControl tbc;

	private ServiceRegistration<DmtEventListener>	listenerRegistration;

	public DmtEvent(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
		prepare();
		testDmtEvent001();
		testDmtEvent002();
		testDmtEvent003();
	}

    private void prepare() {
        tbc.setPermissions(
            new PermissionInfo[] {
            new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS),
            new PermissionInfo(DmtPrincipalPermission.class.getName(), DmtConstants.PRINCIPAL, "*") }
            );
    }

    private void addEventListener(int type, String uri, DmtEventListener listener) {
		Dictionary<String,Object> properties = new Hashtable<>();
    	properties.put(DmtEventListener.FILTER_EVENT, Integer.valueOf(type));
    	properties.put(DmtEventListener.FILTER_SUBTREE, uri);
		listenerRegistration = tbc.getContext()
				.registerService(DmtEventListener.class,
    					listener, properties);
	}

    private void removeEventListener(DmtEventListener listener) {
    	if (listenerRegistration != null) {
    		listenerRegistration.unregister();
    		listenerRegistration = null;
    	}
    }

	/**
	 * Asserts all the DmtEvent methods when the event is successfully sent
	 *
	 * @spec 117.13.5
	 */
	private void testDmtEvent001() {
		DmtSession session = null;
		DmtEventListenerImpl eventListener = new DmtEventListenerImpl();
		try {
			DefaultTestBundleControl.log("#testDmtEvent001");

			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_ATOMIC);

            synchronized (tbc) {
                tbc.wait(DmtConstants.WAITING_TIME);
            }
            addEventListener(DmtConstants.ALL_DMT_EVENTS, TestExecPluginActivator.ROOT,eventListener);

			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE,new DmtData("B"));
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.INEXISTENT_NODE,true);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.RENAMED_NODE_NAME);
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			TestCase.assertEquals("Asserts that if the session is atomic, no event is sent before commit.",0,eventListener.getCount());
			session.commit();
			synchronized (tbc) {
				tbc.wait(DmtConstants.WAITING_TIME);
			}
			// RFC-141: There is no pre-defined number and order of events anymore (see https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1794)
//			tbc.assertEquals("Asserts that the number of events are correct",5,eventListener.getCount());
//			tbc.assertTrue("Asserts that the order of the sent events is the expected.",eventListener.isOrdered());


//			org.osgi.service.dmt.DmtEvent[] dmtEvents = eventListener.getDmtEvents();
//			assertEvent(dmtEvents[0], session,org.osgi.service.dmt.DmtEvent.ADDED,new String[] { TestExecPluginActivator.INEXISTENT_NODE},null);
//			assertEvent(dmtEvents[1], session,org.osgi.service.dmt.DmtEvent.DELETED,new String[] { TestExecPluginActivator.INTERIOR_NODE},null);
//			assertEvent(dmtEvents[2], session,org.osgi.service.dmt.DmtEvent.REPLACED,new String[] { TestExecPluginActivator.LEAF_NODE},null);
//			assertEvent(dmtEvents[3], session,org.osgi.service.dmt.DmtEvent.RENAMED,new String[] { TestExecPluginActivator.INTERIOR_NODE},new String[] { TestExecPluginActivator.RENAMED_NODE} );
//			assertEvent(dmtEvents[4], session,org.osgi.service.dmt.DmtEvent.COPIED,new String[] { TestExecPluginActivator.INTERIOR_NODE},new String[] { TestExecPluginActivator.INEXISTENT_NODE } );

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			removeEventListener(eventListener);
			tbc.closeSession(session);
			//wait for the close session event to be processed, otherwise it would interfere with the next test
            synchronized (tbc) {
            	try {
					Sleep.sleep(DmtConstants.WAITING_TIME);
            	} catch (Exception e) {
				}
            }
		}

	}

	private void assertEvent(org.osgi.service.dmt.DmtEvent event,DmtSession session,int expectedType,String[] expectedNodes,String[] expectedNewNodes) {
		TestCase.assertEquals("Asserts that DmtEvent.getType() returns the correct event",
				expectedType,event.getType());
		TestCase.assertTrue("Asserts that DmtEvent.getSessionId() returns the session Id",
				event.getSessionId() == session.getSessionId());

		if (null==expectedNodes) {
			TestCase.assertNull("Asserts that DmtEvent.getNodes() returns null",
					event.getNodes());
		} else {
			TestCase.assertTrue("Asserts that DmtEvent.getNodes() returns all nodes expected in this event",
					event.getNodes().length==expectedNodes.length);
			for (int i = 0; i < expectedNodes.length; i++) {
				TestCase.assertTrue("Asserts that DmtEvent.getNodes() returns the expected nodes of this event",
						event.getNodes()[i].equals(expectedNodes[i]));
			}
		}
		if (null==expectedNewNodes) {
			TestCase.assertNull("Asserts that DmtEvent.getNewNodes() returns null",
					event.getNewNodes());
		} else {
			TestCase.assertTrue("Asserts that DmtEvent.getNewNodes() returns all nodes expected in this event",
					event.getNewNodes().length==expectedNewNodes.length);
			for (int i = 0; i < expectedNewNodes.length; i++) {
				TestCase.assertTrue("Asserts that DmtEvent.getNewNodes() returns the expected nodes of this event",
						event.getNewNodes()[i].equals(expectedNewNodes[i]));
			}
		}

	}


	/**
	 * Asserts that DmtEvent.getNodes() and DmtEvent.getNewNodes() must return null for
	 * DmtEvent.SESSION_OPENED and DmtEvent.SESSION_CLOSED
	 *
	 * @spec 117.13.5
	 */
	private void testDmtEvent002() {
		DmtSession session = null;
		DmtEventListenerImpl eventListener = new DmtEventListenerImpl();
		try {
			DefaultTestBundleControl.log("#testDmtEvent002");

			addEventListener(DmtConstants.ALL_DMT_EVENTS,
					TestExecPluginActivator.INTERIOR_NODE,eventListener);

			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_ATOMIC);
			session.close();
			synchronized (tbc) {
				tbc.wait(DmtConstants.WAITING_TIME);
			}
			TestCase.assertEquals("Asserts that the number of events are correct",2,eventListener.getCount());

			org.osgi.service.dmt.DmtEvent[] dmtEvents = eventListener.getDmtEvents();
			assertEvent(dmtEvents[0], session,org.osgi.service.dmt.DmtEvent.SESSION_OPENED,null,null);
			assertEvent(dmtEvents[1], session,org.osgi.service.dmt.DmtEvent.SESSION_CLOSED,null,null);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			removeEventListener(eventListener);
			tbc.closeSession(session);
		}

	}

	/**
	 * Asserts all the DmtEvent methods when the event is successfully sent before DmtSession.close()
	 * is called (in case of DmtSession.LOCK_TYPE_EXCLUSIVE)
	 *
	 * @spec 117.13.5
	 */
	private void testDmtEvent003() {
		DmtSession session = null;
		DmtEventListenerImpl eventLisneter = new DmtEventListenerImpl();
		try {
			DefaultTestBundleControl.log("#testDmtEvent003");

			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);

            synchronized (tbc) {
                tbc.wait(DmtConstants.WAITING_TIME);
            }
            addEventListener(DmtConstants.ALL_DMT_EVENTS,
					TestExecPluginActivator.ROOT,eventLisneter);

			session.setNodeValue(TestExecPluginActivator.LEAF_NODE,new DmtData("B"));
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);

			synchronized (tbc) {
				tbc.wait(DmtConstants.WAITING_TIME);
			}

			TestCase.assertEquals("Asserts that if the session is exclusive, events are sent before close.",2,eventLisneter.getCount());

			org.osgi.service.dmt.DmtEvent[] dmtEvents = eventLisneter.getDmtEvents();
			//The first one must be the DmtEvent.REPLACED and the second one DmtEvent.ADDED
			assertEvent(dmtEvents[0], session,org.osgi.service.dmt.DmtEvent.REPLACED,new String[] { TestExecPluginActivator.LEAF_NODE},null);
			assertEvent(dmtEvents[1], session,org.osgi.service.dmt.DmtEvent.ADDED,new String[] { TestExecPluginActivator.INEXISTENT_NODE},null);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			removeEventListener(eventLisneter);
			tbc.closeSession(session);
		}

	}



}


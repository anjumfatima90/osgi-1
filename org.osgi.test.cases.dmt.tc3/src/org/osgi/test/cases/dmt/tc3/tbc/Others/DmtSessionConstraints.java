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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * Jan 31, 2005 Andre Assad
 * 1            Implement MEG TCK
 * ===========  ==============================================================
 * Feb 22, 2005 Luiz Felipe Guimaraes
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.Others;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This test case validates the constraints according to MEG specification
 */
public class DmtSessionConstraints {

	private DmtTestControl tbc;

	public DmtSessionConstraints(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testConstraints001();
		testConstraints002();
		testConstraints003();
		testConstraints004();
		testConstraints005();
		testConstraints006();
		testConstraints007();
		testConstraints008();
		testConstraints009();
	}

	/**
	 * Tests if there can be any number of concurrent read only sessions within the same subtree.
     * 
     * @spec 117.3 The DMT Admin Service
	 */
	public void testConstraints001() {
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			DefaultTestBundleControl.log("#testConstraints001");
			session1 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			session2 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			DefaultTestBundleControl.pass("Two concurrent read only sessions were created.");
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * Tests if a read only session blocks the creation of an updating session 
     * (with a LOCK_TYPE_EXCLUSIVE) within the same subtree.
     * 
     * @spec 117.3 The DMT Admin Service
	 */

	public void testConstraints002() {
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			DefaultTestBundleControl.log("#testConstraints002");
			session1 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			session2 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			TestCase
					.fail("A read only session didn't block the creation of an updating session (with a LOCK_TYPE_EXCLUSIVE)");
		} catch (DmtException e) {
			TestCase.assertEquals("A read only session blocked the creation of an updating session (with a LOCK_TYPE_EXCLUSIVE)",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * Tests if a read only session blocks the creation of an updating session 
     * (with a LOCK_TYPE_ATOMIC) within the same subtree.
     * 
     * @spec 117.3 The DMT Admin Service
	 */
	public void testConstraints003() {
		DefaultTestBundleControl.log("#testConstraints003");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			session2 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestCase
					.fail("A read only session didn't block the creation of an updating session (with a LOCK_TYPE_ATOMIC)");
		} catch (DmtException e) {
			TestCase.assertEquals("A read only session blocked the creation of an updating session (with a LOCK_TYPE_ATOMIC)",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}


	/**
	 * Tests if a session (with the LOCK_TYPE_EXCLUSIVE) can not be shared 
     * with LOCK_TYPE_ATOMIC lock
     * 
     * @spec 117.3 The DMT Admin Service
	 */
	public void testConstraints004() {
		DefaultTestBundleControl.log("#testConstraints004");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session2 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestCase
					.fail("An EXCLUSIVE session could be shared with an ATOMIC session");
		} catch (DmtException e) {
			TestCase.assertEquals("An EXCLUSIVE session could NOT be shared with an ATOMIC session",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * Tests if a session (with the LOCK_TYPE_EXCLUSIVE) can not be 
     * shared with LOCK_TYPE_SHARED lock
     * 
     * @spec 117.3 The DMT Admin Service
	 */
	public void testConstraints005() {
		DefaultTestBundleControl.log("#testConstraints005");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session2 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			TestCase
					.fail("An EXCLUSIVE session could be shared with a SHARED session");
		} catch (DmtException e) {
			TestCase.assertEquals("An EXCLUSIVE session could NOT be shared with an SHARED session",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * Tests if a session (with the LOCK_TYPE_EXCLUSIVE) can not be shared 
     * with LOCK_TYPE_EXCLUSIVE lock
     * 
     * @spec 117.3 The DMT Admin Service
	 */
	public void testConstraints006() {
		DefaultTestBundleControl.log("#testConstraints006");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session2 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			TestCase
					.fail("An EXCLUSIVE session could be shared with an EXCLUSIVE session");
		} catch (DmtException e) {
			TestCase.assertEquals("An EXCLUSIVE session could NOT be shared with an EXCLUSIVE session",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * Test if concurrent updating sessions cannot be opened within the same plugin.
     * Both session have LOCK_TYPE_ATOMIC
     * 
     * 
     * @spec 117.3 The DMT Admin Service
	 */
	public void testConstraints007() {
		DefaultTestBundleControl.log("#testConstraints007");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.INTERIOR_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestCase
					.fail("An ATOMIC session could be shared with an ATOMIC session");
		} catch (DmtException e) {
			TestCase.assertEquals("An ATOMIC session could NOT be shared with an ATOMIC session",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * Test if concurrent updating sessions cannot be opened within the same plugin.
     * One session has LOCK_TYPE_ATOMIC and other has LOCK_TYPE_EXCLUSIVE
     * 
     * @spec 117.3 The DMT Admin Service
	 */

	public void testConstraints008() {
		DefaultTestBundleControl.log("#testConstraints008");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			TestCase
					.fail("An ATOMIC session could be shared with an EXCLUSIVE session");
		} catch (DmtException e) {
			TestCase.assertEquals("An ATOMIC session could NOT be shared with an EXCLUSIVE session",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * Test if concurrent updating sessions cannot be shared with a shared session.
     * 
     * @spec 117.3 The DMT Admin Service
	 */

	public void testConstraints009() {
		DefaultTestBundleControl.log("#testConstraints009");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);
			TestCase
					.fail("An ATOMIC session could be shared with a SHARED session");
		} catch (DmtException e) {
			TestCase.assertEquals("An ATOMIC session could NOT be shared with a SHARED session",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}
	
}

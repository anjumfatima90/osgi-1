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
 * Jan 25, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Luiz Felipe Guimaraes
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.NonAtomic.TestNonAtomicPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This Test Case Validates the implementation of <code>renameNode</code> method of DmtSession, 
 * according to MEG specification.
 */
public class RenameNode implements TestInterface {
	private DmtTestControl tbc;
	
	public RenameNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}
	@Override
	public void run() {
        prepare();
	    testRenameNode001();
		testRenameNode002();
		testRenameNode003();
		testRenameNode004();
		testRenameNode005();
//		testRenameNode006();  This is not valid for DMT Admin 2.0.
		testRenameNode007();
		testRenameNode008();
		testRenameNode009();
		testRenameNode010();
		testRenameNode011();
		testRenameNode012();
		testRenameNode013();
		testRenameNode014();
		testRenameNode015();
		testRenameNode016();
		testRenameNode017();
	}
	
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.renameNode(TestExecPluginActivator.INEXISTENT_NODE, "newName");
			DefaultTestBundleControl.failException("#",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is NODE_NOT_FOUND",DmtException.NODE_NOT_FOUND,e.getCode());
		} catch (Exception e) { 
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that renameNode is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode002");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.REPLACE );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_ATOMIC);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE, "newName" );
			DefaultTestBundleControl.pass("renameNode correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, TestExecPluginActivator.INTERIOR_NODE);            
		}

	}   
	/**
	 * This method asserts that renameNode is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode003");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.REPLACE));
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE, "newName" );
			DefaultTestBundleControl.pass("renameNode correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.closeSession(session);
		}
	} 	
	
	/**
	 * This method asserts that relative URI works as described in this method.
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode004");

			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.renameNode(TestExecPluginActivator.INTERIOR_NODE_NAME,
					TestExecPluginActivator.INEXISTENT_NODE_NAME);

			DefaultTestBundleControl.pass("A relative URI can be used with renameNode.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	
	/**
	 * This method asserts if DmtIllegalStateException is thrown if this method is called 
	 * when the session is LOCK_TYPE_SHARED
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode005");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE, "newName" );
			DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			DefaultTestBundleControl.pass("DmtIllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
            tbc.closeSession(session);
		}
	}
	
	/* 
	 * Note: This test is not valid for DMT Admin 2.0 
	 *       because there is no URI limit any more,
	 *       so this test is not executed.
	 * 
	 * This method asserts that DmtException.URI_TOO_LONG is thrown when  
	 * the newName is too long 
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	@SuppressWarnings("unused")
	private void testRenameNode006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode006");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			String newNameTooLong = DmtTestControl.getSegmentTooLong(null);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,newNameTooLong);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is URI_TOO_LONG",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * the newName is null
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode007");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,null);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * the newName is syntactically invalid 
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode008");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,"newName/");
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * the newName is syntactically invalid 
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode009() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode009");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,"newName\\");
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * the newName is syntactically invalid 
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode010() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode010");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,"/./newName");
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * the newName is syntactically invalid 
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode011");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,"/../newName");
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
	 * if the session is atomic and the plugin is read-only 
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode012() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode012");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.renameNode(TestReadOnlyPluginActivator.INTERIOR_NODE,"newName");
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is TRANSACTION_ERROR",
					DmtException.TRANSACTION_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
	 * if the session is atomic and the plugin does not support non-atomic writing
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode013() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode013");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.renameNode(TestNonAtomicPluginActivator.INTERIOR_NODE,"newName");
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is TRANSACTION_ERROR",
					DmtException.TRANSACTION_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin is read-only 
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode014() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode014");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.renameNode(TestReadOnlyPluginActivator.INTERIOR_NODE,"newName");
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin
	 * does not support non-atomic writing
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode015() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode015");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.renameNode(TestNonAtomicPluginActivator.INTERIOR_NODE,"newName");
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the source node is the root of the tree
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode016() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode016");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), ".",DmtConstants.ALL_ACTIONS));
			session.renameNode(".","newName");
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown
	 * if the node is the root of the session
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	private void testRenameNode017() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRenameNode017");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE, "newName" );
			
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);           
		}

	}
}

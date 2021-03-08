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
 * 21/JAN/2005  Andre Assad
 * CR 1         Implement MEG TCK
 * ===========  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.*;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.*;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getNodeAcl, setNodeAcl</code> methods of DmtSession, 
 * according to MEG specification
 */
public class GetSetNodeAcl implements TestInterface {

	private DmtTestControl tbc;

	public GetSetNodeAcl(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
		testGetSetNodeAcl001();
		testGetSetNodeAcl002();
		testGetSetNodeAcl003();
		testGetSetNodeAcl004();
		testGetSetNodeAcl005();
		testGetSetNodeAcl006();
		testGetSetNodeAcl007();
		testGetSetNodeAcl008();
		testGetSetNodeAcl009();
		testGetSetNodeAcl010();
		testGetSetNodeAcl011();
		testGetSetNodeAcl012();
		testGetSetNodeAcl013();
		testGetSetNodeAcl014();

	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that a Acl is correctly set for a given node in the tree.
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl001");
			Acl acl = new Acl(DmtConstants.ACLSTR);
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(DmtConstants.OSGi_LOG, acl);

			TestCase.assertEquals("Asserting node Acl", acl.toString(), session
					.getNodeAcl(DmtConstants.OSGi_LOG).toString());

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session, DmtConstants.OSGi_LOG);
		}

	}

	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl002");
			Acl acl = new Acl(DmtConstants.ACLSTR);

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.INEXISTENT_NODE, acl);
			DefaultTestBundleControl.failException("", DmtException.class);
			
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code was NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session, TestExecPluginActivator.INEXISTENT_NODE);
		}
	}

	

	/**
	 * This method asserts that setNodeAcl is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl003");

            tbc.openSessionAndSetNodeAcl(DmtConstants.OSGi_LOG, DmtConstants.PRINCIPAL, Acl.REPLACE | Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					DmtConstants.OSGi_LOG, DmtSession.LOCK_TYPE_EXCLUSIVE);
			Acl acl = new Acl(
                new String[] { DmtConstants.PRINCIPAL },
                new int[] { Acl.ADD });
            
			session.setNodeAcl(DmtConstants.OSGi_LOG, acl);
            session.close();
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestCase.assertEquals("Asserts that setNodeAcl really sets the Acl of a node",acl,session.getNodeAcl(DmtConstants.OSGi_LOG));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,DmtConstants.OSGi_LOG);
            
		}
	}

	/**
	 * This method asserts that setNodeAcl is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.REPLACE));

            Acl acl =  new Acl(
                new String[] { DmtConstants.PRINCIPAL },
                new int[] { Acl.ADD });
			session.setNodeAcl(DmtConstants.OSGi_LOG,acl);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
			
            TestCase.assertEquals("Asserts that setNodeAcl really sets the Acl of a node",acl,session.getNodeAcl(DmtConstants.OSGi_LOG));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, DmtConstants.OSGi_LOG);		
            
		}

	}
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeAcl(TestExecPluginActivator.INEXISTENT_NODE);

			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code was NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	

	/**
	 * This method asserts that getNodeAcl returns null if no acl is defined
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl006");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestCase.assertNull("Asserting that it returns null if no acl is defined", session
					.getNodeAcl(TestExecPluginActivator.INTERIOR_NODE));

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	
	/**
	 * This method asserts that getNodeAcl is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl007");

            tbc.openSessionAndSetNodeAcl(DmtConstants.OSGi_LOG, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					DmtConstants.OSGi_LOG, DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.getNodeAcl(DmtConstants.OSGi_LOG);

			DefaultTestBundleControl.pass("getNodeAcl correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,DmtConstants.OSGi_LOG);
            
		}
	}

	/**
	 * This method asserts that getNodeAcl is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl008");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));

			session.getNodeAcl(DmtConstants.OSGi_LOG);

			DefaultTestBundleControl.pass("getNodeAcl correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}

	}
	
	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl009() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl009");
			
		
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeAcl(TestExecPluginActivator.LEAF_RELATIVE);

			DefaultTestBundleControl.pass("A relative URI can be used with getNodeAcl.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl010() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl010");
			
		
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);
			Acl acl =  new Acl(DmtConstants.ACLSTR);
			session.setNodeAcl(TestExecPluginActivator.LEAF_RELATIVE,acl);
			
            TestCase.assertEquals("A relative URI can be used with setNodeAcl.",acl,session.getNodeAcl(TestExecPluginActivator.LEAF_NODE));
			
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
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl011");
			session = tbc.getDmtAdmin().getSession(
				TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_SHARED);
			session.setNodeAcl(TestExecPluginActivator.LEAF_RELATIVE, new Acl(DmtConstants.ACLSTR));
			DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			DefaultTestBundleControl.pass("DmtIllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown
	 * if the command attempts to set the ACL of the root node not to include 
	 * Add rights for all principals 
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl012() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl012");
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), ".",DmtConstants.ALL_ACTIONS));
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(".",new Acl("Add=www.cesar.org.br"));

			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code was COMMAND_NOT_ALLOWED.",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.closeSession(session);
		}
	}
	
	
	
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl013() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl013");
			
		
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeAcl("");


			DefaultTestBundleControl.pass("Asserts that an empty string as relative URI means the root " +
					"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl014() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeAcl014");
			
		
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);
			Acl acl =  new Acl(DmtConstants.ACLSTR);
			session.setNodeAcl("",acl);
			
            TestCase.assertEquals("Asserts that an empty string as relative URI means the root " +
					"URI the session was opened with",acl,session.getNodeAcl(TestExecPluginActivator.LEAF_NODE));

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
}

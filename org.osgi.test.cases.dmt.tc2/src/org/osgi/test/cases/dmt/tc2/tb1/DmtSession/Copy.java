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
 * Jan 24, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
 * Feb 17, 2005  Leonardo Barros
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.*;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.*;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPlugin;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.NonAtomic.TestNonAtomicPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This Test Case Validates the implementation of <code>copy</code> method of DmtSession, 
 * according to MEG specification
 */
public class Copy implements TestInterface {
	private DmtTestControl tbc;

	public Copy(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
        testCopy001();
		testCopy002();
		testCopy003();
		testCopy004();
		testCopy005();
		testCopy006();
		testCopy007();
		testCopy008();
		testCopy009();
		testCopy010();
		testCopy011();
		testCopy012();
		testCopy013();
		testCopy014();
		testCopy015();
		testCopy016();
		testCopy017();
		testCopy018();
		testCopy019();
		testCopy020();
		testCopy021();
        testCopy022();
        testCopy023();
        testCopy024();
        testCopy025();
        testCopy026();
        testCopy027();
        testCopy028();
	}

	private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }

	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown when it tries to copy 
     * from an ancestor of newNodeUri
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(DmtConstants.OSGi_ROOT,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	

	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown 
	 * if nodeUri points to a non-existing node
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy002");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INEXISTENT_NODE,
					TestExecPluginActivator.ROOT + "/other", true);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is NODE_NOT_FOUND",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that DmtException.NODE_ALREADY_EXISTS is thrown 
	 * if newNodeUri points to a node that already exists 
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy003");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INTERIOR_NODE2, false);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	
	/**
	 * This method asserts that the method is called if it has the right DmtPermission (local)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy004() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCopy004");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            tbc.setPermissions(new PermissionInfo[]{
                new PermissionInfo(DmtPermission.class.getName(),
                    TestExecPluginActivator.ROOT, DmtPermission.ADD),
                new PermissionInfo(DmtPermission.class.getName(),
                    TestExecPluginActivator.INTERIOR_NODE, DmtPermission.GET)});
            
            session.copy(TestExecPluginActivator.INTERIOR_NODE,
                TestExecPluginActivator.INEXISTENT_NODE, false);
            DefaultTestBundleControl.pass("A node could be copied with the right permission");
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.setPermissions(new PermissionInfo(
                DmtPermission.class.getName(),
                DmtConstants.ALL_NODES, DmtConstants.ALL_ACTIONS));

            tbc.cleanUp(session, null);
        }
    }

	/**
	 * This method asserts that the method is called if it has the right Acl (remote)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy005() {

		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy005");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET | Acl.ADD );
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.ROOT, DmtConstants.PRINCIPAL, Acl.ADD );
            
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));

			session = tbc.getDmtAdmin().getSession(
					DmtConstants.PRINCIPAL, DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, false);
			DefaultTestBundleControl
					.pass("This method asserts that the method is called if it has the right Acl");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
			tbc.cleanAcl(TestExecPluginActivator.ROOT);
            
		}

	}

	/**
	 * This method asserts that relative URI works as described in this method.
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy006");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.copy(TestExecPluginActivator.INTERIOR_NODE_NAME,
					TestExecPluginActivator.INEXISTENT_NODE_NAME, false);

			DefaultTestBundleControl.pass("A relative URI can be used with Copy.");
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
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy007");
			session = tbc.getDmtAdmin().getSession(
				TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_SHARED);

			session.copy(TestExecPluginActivator.INTERIOR_NODE_NAME,
				TestExecPluginActivator.INEXISTENT_NODE_NAME, false);
			
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
	 * This method asserts that DmtException.PERMISSION_DENIED is thrown if the session is 
	 * associated with a principal and the ACL of the copied node(s) does not allow the Get operation
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy008");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.EXEC );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));
			
			session = tbc.getDmtAdmin().getSession(
					DmtConstants.PRINCIPAL, DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
			tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
		}

	}
	
	/**
	 * This method asserts that DmtException.PERMISSION_DENIED is thrown if the session is 
	 * associated with a principal and the ACL of the parent of the target node does not allow 
	 * the Add operation for the associated principal 

	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy009() {
		
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy009");

			tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.ROOT, DmtConstants.PRINCIPAL, Acl.GET);
			//The copied node allows the Get operation			
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET);
            
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));

			session = tbc.getDmtAdmin().getSession(
					DmtConstants.PRINCIPAL, DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            tbc.cleanAcl(TestExecPluginActivator.ROOT);
            
		}

	}
	
	/**
	 * This method asserts that an SecurityException is thrown if the caller does not 
	 * have DmtPermission for the copied node(s) with the Get action present
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy010() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy010");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), TestExecPluginActivator.INEXISTENT_NODE,
					DmtPermission.ADD));
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			DefaultTestBundleControl.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			DefaultTestBundleControl.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.failExpectedOtherException(SecurityException.class, e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
	}
	
	/**
	 * This method asserts that an SecurityException is thrown if the caller does not 
	 * have DmtPermission for the parent of the target node with the Add action
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy011");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			//The copied node allows Get
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), TestExecPluginActivator.INTERIOR_NODE,
					DmtPermission.GET));
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			DefaultTestBundleControl.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			DefaultTestBundleControl.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.failExpectedOtherException(SecurityException.class, e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
		}
	}
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * newNodeUri is null 
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy012() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy012");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,null, true);
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
	 * This method asserts that an empty string as newNodeUri means the root 
	 * URI the session was opened with  (DmtException.NODE_ALREADY_EXISTS is thrown 
	 * because the root already exist)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy013() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy013");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INTERIOR_NODE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,"", true);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);;
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * newNodeUri is syntactically invalid (node name ends with the '/' character)
)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy014() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy014");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.INEXISTENT_NODE + "/", true);
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
	 * newNodeUri is syntactically invalid (node name ends with the '\' character)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy015() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy015");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.INEXISTENT_NODE + "\\", true);
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
	 * newNodeUri is syntactically invalid (URI contains the segment "." at 
	 * a position other than the beginning of the URI)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy016() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy016");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.ROOT + "/./"+ TestExecPluginActivator.INEXISTENT_NODE_NAME, true);
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
	 * newNodeUri is syntactically invalid (node name is ".." or the URI contains such a segment)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy017() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy017");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.ROOT + "/../"+ TestExecPluginActivator.INEXISTENT_NODE_NAME, true);
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
	 * This method asserts that DmtException.URI_TOO_LONG is thrown when  
	 * the length of nodeUri is too long 
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy018() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy018");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			// TODO (S. Druesedow) fix implementation because Uri length limits are removed (see bug 2144)
//			if (Uri.getMaxSegmentNameLength()!=Integer.MAX_VALUE) {
//			    String uriTooLong = DmtTestControl.getSegmentTooLong(TestExecPluginActivator.ROOT);
//			    session.copy(TestExecPluginActivator.INTERIOR_NODE,uriTooLong, true);
//			    tbc.failException("", DmtException.class);
//			} else {
//		        tbc.log("#There is no upper limit on the length of segment names, " +
//        			"DmtException.URI_TOO_LONG will not be tested in this case");
//			}
			
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
	 * This method asserts that DmtException.URI_TOO_LONG is thrown when  
	 * the segment number exceeds the limit
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy019() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy019");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			// TODO (S. Druesedow) fix implementation because Uri length limits are removed (see bug 2144)
//			if (Uri.getMaxUriSegments()!=Integer.MAX_VALUE) {
//			    String uriTooLong = DmtTestControl.getExcedingSegmentsUri(TestExecPluginActivator.ROOT);
//				session.copy(TestExecPluginActivator.INTERIOR_NODE,uriTooLong, true);
//				tbc.failException("", DmtException.class);
//				
//			} else {
//		        tbc.log("#There is no upper limit on the number of URI segments, " +
//        		"DmtException.URI_TOO_LONG will not be tested in this case");
//			}
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
	 * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
	 * if the session is atomic and the plugin is read-only
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy020() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy020");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.copy(TestReadOnlyPluginActivator.INTERIOR_NODE,
			    TestReadOnlyPluginActivator.INEXISTENT_NODE, true);

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
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy021() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy021");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.copy(TestNonAtomicPluginActivator.INTERIOR_NODE,
			    TestNonAtomicPluginActivator.INEXISTENT_NODE, true);

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
	 * This method asserts that no exception is thrown if the caller has DmtPermission for the target node 
	 * with the Replace action and the node has a title 
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy022() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy022");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
            tbc.setPermissions(new PermissionInfo[]{
                    new PermissionInfo(DmtPermission.class.getName(),
                        TestExecPluginActivator.ROOT, DmtPermission.ADD),
                    new PermissionInfo(DmtPermission.class.getName(),
                        TestExecPluginActivator.INTERIOR_NODE, DmtPermission.GET),
                    new PermissionInfo(DmtPermission.class.getName(),
                                TestExecPluginActivator.INEXISTENT_NODE, DmtPermission.REPLACE)});

            //When doing this, getNodeTitle returns a value intead of null
            TestExecPlugin.setDefaultNodeTitle(DmtConstants.TITLE);
            
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			DefaultTestBundleControl.pass("Copy method could called when the caller has DmtPermission for the " +
					"target node with the Replace action and the node has a title ");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            TestExecPlugin.setDefaultNodeTitle(null);
		}
	}
	
	/**
	 * This method asserts that an SecurityException is thrown if the caller does not 
	 * have DmtPermission for the target node with the Replace action and the
	 * node has a title (in this case is necessary Replace)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy023() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy023");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
            tbc.setPermissions(new PermissionInfo[]{
                    new PermissionInfo(DmtPermission.class.getName(),
                        TestExecPluginActivator.ROOT, DmtPermission.ADD),
                    new PermissionInfo(DmtPermission.class.getName(),
                        TestExecPluginActivator.INTERIOR_NODE, DmtPermission.GET)});

            //When doing this, getNodeTitle returns a value intead of null
            TestExecPlugin.setDefaultNodeTitle(DmtConstants.TITLE);
            
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			DefaultTestBundleControl.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			DefaultTestBundleControl.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.failExpectedOtherException(SecurityException.class, e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            TestExecPlugin.setDefaultNodeTitle(null);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if any of the implied retrieval or update operations are not allowed.
	 * In this test a leaf node is copied (DmtAdmin calls DmtSession.createLeafNode(String)
	 * that throws this DmtException because it is opened in a non-atomic session and 
	 * the underlying plugin is read-only)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy024() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy024");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestReadOnlyPluginActivator.LEAF_NODE,
					TestReadOnlyPluginActivator.INEXISTENT_LEAF_NODE, true);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if any of the implied retrieval or update operations are not allowed.
	 * In this test a leaf node is copied (DmtAdmin calls DmtSession.createLeafNode(String)
	 * that throws this DmtException because it is opened in a non-atomic session and 
	 * the underlying plugin does not support non-atomic writing)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy025() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy025");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestNonAtomicPluginActivator.LEAF_NODE,
					TestNonAtomicPluginActivator.INEXISTENT_LEAF_NODE, true);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if any of the implied retrieval or update operations are not allowed.
	 * In this test an interior node is copied (DmtAdmin calls DmtSession.createInteriorNode(String)
	 * that throws this DmtException because it is opened in a non-atomic session and 
	 * the underlying plugin is read-only)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy026() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy026");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestReadOnlyPluginActivator.INTERIOR_NODE,
					TestReadOnlyPluginActivator.INEXISTENT_NODE, true);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if any of the implied retrieval or update operations are not allowed.
	 * In this test an interior node is copied (DmtAdmin calls DmtSession.createInteriorNode(String)
	 * that throws this DmtException because it is opened in a non-atomic session and 
	 * the underlying plugin does not support non-atomic writing)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy027() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy027");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestNonAtomicPluginActivator.INTERIOR_NODE,
					TestNonAtomicPluginActivator.INEXISTENT_NODE, true);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with (it throws DmtException.COMMAND_FAILED 
	 * because they arent in the same session's subtree)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy028() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCopy028");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.copy("",
					TestExecPluginActivator.INEXISTENT_NODE, false);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is COMMAND_FAILED",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
}

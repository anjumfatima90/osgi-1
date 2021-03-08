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
 * Aug 18, 2005  Luiz Felipe Guimaraes
 * 23            Update test cases according to changes in the Acl API
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Constraints;

import java.lang.reflect.Modifier;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPlugin;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This class validates the constraints of Acl, according to MEG specification
 * 
 */
public class AclConstraints {
	private DmtTestControl tbc;

	public AclConstraints(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testAclConstraints001();
		testAclConstraints002();
		testAclConstraints003();
		testAclConstraints004();
		testAclConstraints005();
		testAclConstraints006();
		testAclConstraints007();
		testAclConstraints008();
        testAclConstraints009();
        testAclConstraints010();
        testAclConstraints011();
        testAclConstraints012();

	}

	/**
	 * This test asserts that white space between tokens is not allowed.
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints001() {

		try {
			DefaultTestBundleControl.log("#testAclConstraints001");
			
            new org.osgi.service.dmt.Acl("Add=test&Exec=test &Get=*");
			
            DefaultTestBundleControl.failException("",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			DefaultTestBundleControl.pass("White space between tokens of a Acl is not allowed.");			
		} catch (Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}
	
	/**
	 * This test asserts that if the root node ACL is not explicitly set, it should be set to Add=*&Get=*&Replace=*.
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testAclConstraints002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			String expectedRootAcl = "Add=*&Get=*&Replace=*";
			String rootAcl = session.getNodeAcl(".").toString();
			TestCase.assertEquals("This test asserts that if the root node ACL is not explicitly set, it should be set to Add=*&Get=*&Replace=*.",expectedRootAcl,rootAcl);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This test asserts that the root node of DMT must always have an ACL associated with it
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testAclConstraints003");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(".",null);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {	
			TestCase.assertEquals("Asserts that the root node of DMT must always have an ACL associated with it",
			    DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			try {
				session.setNodeAcl(".",new org.osgi.service.dmt.Acl("Add=*&Get=*&Replace=*"));
			} catch (Exception e) {
				tbc.failUnexpectedException(e);
			} finally {
				tbc.closeSession(session);
			}
		}
	}
	
	/**
	 * This test asserts that the root's ACL can be changed
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testAclConstraints004");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			String expectedRootAcl = "Add=*&Exec=*&Replace=*";
			session.setNodeAcl(".",new org.osgi.service.dmt.Acl(expectedRootAcl));
			String rootAcl = session.getNodeAcl(".").toString();
			TestCase.assertEquals("Asserts that the root's ACL can be changed.",expectedRootAcl,rootAcl);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			try {
				session.setNodeAcl(".",new org.osgi.service.dmt.Acl("Add=*&Get=*&Replace=*"));
			} catch (Exception e) {
				tbc.failUnexpectedException(e);
			} finally {
				tbc.closeSession(session);
			}
		}
	}
	
	/**
	 * The Dmt Admin service synchronizes the ACLs with any change 
	 * in the DMT that is made through its service interface.
	 * This test case we test if the deleteNode method deletes also the ACL of that node.
	 * 
	 * @spec 117.7.2 Ghost Acls
	 */
	private void testAclConstraints005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testAclConstraints005");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,
					new org.osgi.service.dmt.Acl("Replace=*"));

			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);

			TestCase.assertNull("This test asserts that the Dmt Admin service synchronizes the ACLs with any change "
							+ "in the DMT that is made through its service interface",
							session.getNodeAcl(TestExecPluginActivator.INTERIOR_NODE));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This test case we test if the rename method renames also the ACL of that node.
	 * 
	 * @spec 117.7.2 Ghost Acls
	 */
	
	private void testAclConstraints006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testAclConstraints006");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			String expectedRootAcl = "Add=*";
			session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,
					new org.osgi.service.dmt.Acl(expectedRootAcl));

			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.RENAMED_NODE_NAME);
			TestExecPlugin.setAllUriIsExistent(true);
			TestCase.assertNull("Asserts that the method rename deletes the ACL of the source.",session.getNodeAcl(TestExecPluginActivator.INTERIOR_NODE));
			
			TestCase.assertEquals("Asserts that the method rename moves the ACL from the source to the destiny.",
					expectedRootAcl,session.getNodeAcl(TestExecPluginActivator.RENAMED_NODE).toString());
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,TestExecPluginActivator.RENAMED_NODE);
			TestExecPlugin.setAllUriIsExistent(false);
		}
	}
	
	/**
	 * This test asserts that if a principal has Replace access to a node, 
	 * the principal is permitted to change the ACL of all its child nodes, 
	 * regardless of the ACLs that are set on the child nodes.
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testAclConstraints007");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.LEAF_NODE, DmtConstants.PRINCIPAL_2, Acl.GET );
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.REPLACE );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.LEAF_NODE,new org.osgi.service.dmt.Acl("Get=*"));
			
			DefaultTestBundleControl.pass("If a principal has Replace access to a node, the principal is permitted to change the ACL of all its child nodes");
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,TestExecPluginActivator.LEAF_NODE);
			tbc.cleanAcl(TestExecPluginActivator.INTERIOR_NODE);
			
		}
	}
	/**
	 * This test asserts that Replace access on a leaf node does not allow changing the ACL property itself.
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testAclConstraints008");
            //We need to set that a parent of the node does not have Replace else the acl of the root "." is gotten
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.DELETE );
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.LEAF_NODE, DmtConstants.PRINCIPAL, Acl.REPLACE );

            tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
            session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.LEAF_NODE,DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.setNodeAcl(TestExecPluginActivator.LEAF_NODE,new org.osgi.service.dmt.Acl("Get=*"));
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {	
			TestCase.assertEquals("Asserts that Replace access on a leaf node does not allow changing the ACL property itself.",
			    DmtException.PERMISSION_DENIED,e.getCode());
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,TestExecPluginActivator.LEAF_NODE);
            tbc.cleanAcl(TestExecPluginActivator.INTERIOR_NODE);
			
		}
	}
    
    /**
     * Asserts that ACLs must only be verified by the Dmt Admin service when the session 
     * has an associated principal.
     * 
     * @spec 117.7 Access Control Lists
     */
    private void testAclConstraints009() {
        DmtSession session = null;
        try {
			DefaultTestBundleControl.log("#testAclConstraints009");
            session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,
                    new org.osgi.service.dmt.Acl("Replace=*"));

            session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);

            DefaultTestBundleControl.pass("ACLs is only verified by the Dmt Admin service when the session has an associated principal.");
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.closeSession(session);
        }
    }
    
    /**
     * This method asserts that the copy methods does not copy the Acl.
     * It also tests that the copied nodes inherit the access rights 
     * from the parent of the destination node.
     * 
     * @spec 117.4.10 Copying Nodes
     */
    private void testAclConstraints010() {
        DmtSession session = null;
        try {
			DefaultTestBundleControl.log("#testAclConstraints010");
            tbc.cleanAcl(TestExecPluginActivator.INEXISTENT_NODE);
            
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            Acl aclParent = new Acl(new String[] { DmtConstants.PRINCIPAL },new int[] { Acl.REPLACE });
            session.setNodeAcl(TestExecPluginActivator.ROOT,
                    aclParent);
            
            session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,
                    new Acl(new String[] { DmtConstants.PRINCIPAL },
                            new int[] { Acl.EXEC }));
            TestExecPlugin.setAllUriIsExistent(false);
            session.copy(TestExecPluginActivator.INTERIOR_NODE,
                    TestExecPluginActivator.INEXISTENT_NODE, true);
            TestExecPlugin.setAllUriIsExistent(true);
            TestCase.assertTrue("Asserts that the copied nodes inherit the access rights from the parent of the destination node.",
                aclParent.equals(session.getEffectiveNodeAcl(TestExecPluginActivator.INEXISTENT_NODE)));
            
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session, TestExecPluginActivator.INTERIOR_NODE);
            tbc.cleanAcl(TestExecPluginActivator.ROOT);
            TestExecPlugin.setAllUriIsExistent(false);
        }
    }
    
    /**
     * This method asserts that when copy method is called if the calling principal does not 
     * have Replace rights for the parent, the destiny node must be set with an Acl having 
     * Add, Delete and Replace permissions
     * 
     * @spec 117.4.10 Copying Nodes
     */
    private void testAclConstraints011() {
        DmtSession session = null;
        try {
			DefaultTestBundleControl.log("#testAclConstraints011");
            
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET | Acl.ADD );
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.ROOT, DmtConstants.PRINCIPAL, Acl.ADD );

            Acl aclExpected = new Acl(new String[] { DmtConstants.PRINCIPAL },new int[] { Acl.ADD | Acl.DELETE | Acl.REPLACE });

            tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));
			
            session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL, ".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            session.copy(TestExecPluginActivator.INTERIOR_NODE,
                    TestExecPluginActivator.INEXISTENT_NODE, true);
            TestExecPlugin.setAllUriIsExistent(true);

            session.close();
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            TestCase.assertTrue("Asserts that if the calling principal does not have Replace rights for the parent, " +
            		"the destiny node must be set with an Acl having Add, Delete and Replace permissions.",
            		aclExpected.equals(session.getNodeAcl(TestExecPluginActivator.INEXISTENT_NODE)));
            
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session, TestExecPluginActivator.INTERIOR_NODE);
            tbc.cleanAcl(TestExecPluginActivator.ROOT);
            tbc.cleanAcl(TestExecPluginActivator.INEXISTENT_NODE);
            TestExecPlugin.setAllUriIsExistent(false);
        }
    }
    
    /**
     * Asserts that Acl is a public final class
     * 
     * @spec 117.12.2 Acl
     */
    private void testAclConstraints012() {
        try {
            int aclModifiers = Acl.class.getModifiers();
            TestCase.assertTrue("Asserts that Acl is a public final class", aclModifiers == (Modifier.FINAL | Modifier.PUBLIC));
            
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        }
    }
}

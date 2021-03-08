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
 * Jan 1, 2005   Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
 * Aug 25, 2005  Luiz Felipe Guimaraes
 * 173           [MEGTCK][DMT] Changes on interface names and plugins
 * ============  ==============================================================
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
 * This Test Case Validates the implementation of <code>createLeafNode<code> method of DmtSession, 
 * according to MEG specification
 */
public class CreateLeafNode implements TestInterface {
	private DmtTestControl tbc;
	private DmtData value = new DmtData(10);

	public CreateLeafNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
        testCreateLeafNode001();
		testCreateLeafNode002();
		testCreateLeafNode003();
		testCreateLeafNode004();
		testCreateLeafNode005();
		testCreateLeafNode006();
		testCreateLeafNode007();
		testCreateLeafNode008();
		testCreateLeafNode009();
		testCreateLeafNode010();
		testCreateLeafNode011();
		testCreateLeafNode012();
		testCreateLeafNode013();
		testCreateLeafNode014();
		testCreateLeafNode015();
		testCreateLeafNode016();
		testCreateLeafNode017();
		testCreateLeafNode018();
		testCreateLeafNode019();
		testCreateLeafNode020();
		testCreateLeafNode021();
		testCreateLeafNode022();
		testCreateLeafNode023();
		testCreateLeafNode024();
		testCreateLeafNode025();
		testCreateLeafNode026();
		testCreateLeafNode027();
		testCreateLeafNode028();
        testCreateLeafNode029();
        testCreateLeafNode030();
        testCreateLeafNode031();
        testCreateLeafNode032();
        testCreateLeafNode033();
        testCreateLeafNode034();
        testCreateLeafNode035();
        testCreateLeafNode036();
        testCreateLeafNode037();
        testCreateLeafNode038();
        testCreateLeafNode039();
        testCreateLeafNode040();
        testCreateLeafNode041();
        testCreateLeafNode042();
        
    }
        
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_ALREADY_EXISTS is thrown
	 * if nodeUri points to a node that already exists 
	 * 
	 * @spec DmtSession.createLeafNode(String)
	 */
	private void testCreateLeafNode001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestExecPluginActivator.LEAF_NODE);

			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}

	}

	/**
	 * This method asserts that DmtException.NODE_ALREADY_EXISTS is thrown  
	 * if nodeUri points to a node that already exists using the method with two parameters
	 *  
	 * @spec DmtSession.createLeafNode(String,DmtData)
	 */
	private void testCreateLeafNode002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createLeafNode(TestExecPluginActivator.LEAF_NODE,
					value);

			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that DmtException.NODE_ALREADY_EXISTS is thrown  
	 * if nodeUri points to a node that already exists using the method with three parameters
	 *  
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	private void testCreateLeafNode003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestExecPluginActivator.LEAF_NODE,
					value, DmtConstants.MIMETYPE);

			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that createLeafNode is executed when the right DmtPermission is set (Local) 
	 * using the method with one parameter.
	 * 
	 * @spec DmtSession.createLeafNode(String)
	 */
	private void testCreateLeafNode004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtConstants.ALL_NODES,
					DmtPermission.ADD));

			session
					.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE);

			DefaultTestBundleControl.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
		}
	}

	/**
	 * This method asserts that createLeafNode is executed when the right DmtPermission is set (Local)  
	 * using the method with two parameters.
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData)
	 */
	private void testCreateLeafNode005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtConstants.ALL_NODES,
					DmtPermission.ADD));

			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, value);

			DefaultTestBundleControl.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
	}

	/**
	 * This method asserts that createLeafNode is executed when the right DmtPermission is set (Local) 
	 * using the method with three parameters.
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	private void testCreateLeafNode006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode006");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtConstants.ALL_NODES,
					DmtPermission.ADD));

			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, value, DmtConstants.MIMETYPE);

			DefaultTestBundleControl.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
	}

	/**
	 * This method asserts that createLeafNode is executed when the right Acl is set (Remote)
	 * using the method with one parameter.
	 * 
	 * @spec DmtSession.createLeafNode(String)
	 */
	private void testCreateLeafNode007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode007");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.ROOT, new Acl(
					new String[] { DmtConstants.PRINCIPAL },
					new int[] { Acl.ADD | Acl.GET }));

			session.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE);

			DefaultTestBundleControl.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, TestExecPluginActivator.ROOT);
            
		}

	}

	/**
	 * This method asserts that createLeafNode is executed when the right Acl is set (Remote) 
	 * using the method with two parameters.
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData)
	 */
	private void testCreateLeafNode008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode008");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.ROOT, new Acl(
					new String[] { DmtConstants.PRINCIPAL },
					new int[] { Acl.ADD | Acl.GET }));
			session.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE,
					value);

			DefaultTestBundleControl.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, TestExecPluginActivator.ROOT);
            
		}

	}

	/**
	 * This method asserts that createLeafNode is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	private void testCreateLeafNode009() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode009");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.ROOT, new Acl(
					new String[] { DmtConstants.PRINCIPAL },
					new int[] { Acl.ADD | Acl.GET }));

			session.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, value, DmtConstants.MIMETYPE);

			DefaultTestBundleControl.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, TestExecPluginActivator.ROOT);
            
		}
	}

	/**
	 * This method asserts that relative URI works as described in this method using the method with one parameter.
	 * 
	 * @spec DmtSession.createLeafNode(String)
	 */
	private void testCreateLeafNode010() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode010");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE_NAME);

			DefaultTestBundleControl.pass("A relative URI can be used with CreateLeafNode.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that relative URI works as described in this method using the method with two parameters.
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData)
	 */
	private void testCreateLeafNode011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode011");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE_NAME,
					value);

			DefaultTestBundleControl.pass("A relative URI can be used with CreateLeafNode.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that relative URI works as described in this method 
	 * using the method with three parameters
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	private void testCreateLeafNode012() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode012");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE_NAME,
					value, DmtConstants.MIMETYPE);

			DefaultTestBundleControl.pass("A relative URI can be used with CreateLeafNode.");
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
	 * @spec DmtSession.createLeafNode(String)
	 */
	private void testCreateLeafNode013() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode013");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
			session.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE);
			DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			DefaultTestBundleControl.pass("DmtIllegalStateException correctly thrown");
		} catch (Exception e) {
			DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if DmtIllegalStateException is thrown if this method is called 
	 * when the session is LOCK_TYPE_SHARED
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData)
	 */
	private void testCreateLeafNode014() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode014");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
			session.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE,value);
			DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			DefaultTestBundleControl.pass("DmtIllegalStateException correctly thrown");
		} catch (Exception e) {
			DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts if DmtIllegalStateException is thrown if this method is called 
	 * when the session is LOCK_TYPE_SHARED
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	private void testCreateLeafNode015() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode015");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
			session.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE,value,DmtConstants.MIMETYPE);
			DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			DefaultTestBundleControl.pass("DmtIllegalStateException correctly thrown");
		} catch (Exception e) {
			DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown when the parent
	 * is not an interior node.
	 * 
	 * @spec DmtSession.createLeafNode(String)
	 */
	private void testCreateLeafNode016() {
		DmtSession session = null;
		DefaultTestBundleControl.log("#testCreateLeafNode016");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestExecPluginActivator.LEAF_NODE +"/test");
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
	 * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown when the parent
	 * is not an interior node.
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData)
	 */
	private void testCreateLeafNode017() {
		DmtSession session = null;
		DefaultTestBundleControl.log("#testCreateLeafNode017");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestExecPluginActivator.LEAF_NODE +"/test",value);
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
	 * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown when the parent
	 * is not an interior node.
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	private void testCreateLeafNode018() {
		DmtSession session = null;
		DefaultTestBundleControl.log("#testCreateLeafNode018");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestExecPluginActivator.LEAF_NODE +"/test",value,DmtConstants.MIMETYPE);
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
     * This method asserts that null can be passed as value using the method with two parameters.
     * 
     * @spec DmtSession.createLeafNode(String,DmtData)
     */
    private void testCreateLeafNode019() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode019");

            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(
                    TestExecPluginActivator.INEXISTENT_LEAF_NODE, null);

            DefaultTestBundleControl.pass("createLeafNode was successfully executed");

        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
        }
    }
    /**
     * This method asserts that null can be passed as value using the method with three parameters.
     * 
     * @spec DmtSession.createLeafNode(String,DmtData)
     */
    private void testCreateLeafNode020() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode020");

            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(
                    TestExecPluginActivator.INEXISTENT_LEAF_NODE, null,DmtConstants.MIMETYPE);

            DefaultTestBundleControl.pass("createLeafNode was successfully executed");

        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
        }
    }
    /**
     * This method asserts that null can be passed as mimetype
     * 
     * @spec DmtSession.createLeafNode(String,DmtData,String)
     */
    private void testCreateLeafNode021() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode021");

            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(
                    TestExecPluginActivator.INEXISTENT_LEAF_NODE, value,null);

            DefaultTestBundleControl.pass("createLeafNode was successfully executed");

        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
        }
    }
    /**
     * Asserts that if the parent node does not exist, it is created automatically,  
     * as if createInteriorNode(String) were called for the parent URI using the method with three parameters.
     * 
     * @spec DmtSession.createLeafNode(String)
     */
    private void testCreateLeafNode022() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode022");
            TestExecPlugin.resetCount();
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(
                    TestExecPluginActivator.INEXISTENT_INTERIOR_AND_LEAF_NODES);

           
            TestCase.assertTrue("Asserts that if the parent node does not exist, it is created automatically, " +
                    "as if createInteriorNode(String) were called for the parent URI", 
                    TestExecPlugin.getCreateInteriorNodeCount()==2 && TestExecPlugin.getCreateLeafNodeCount()==1);
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session, null);
            
        }
    }
    /**
     * Asserts that if the parent node does not exist, it is created automatically,  
     * as if createInteriorNode(String) were called for the parent URI using the method with three parameters.
     * 
     * @spec DmtSession.createLeafNode(String,DmtData)
     */
    private void testCreateLeafNode023() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode023");
            TestExecPlugin.resetCount();
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(
                    TestExecPluginActivator.INEXISTENT_INTERIOR_AND_LEAF_NODES, value);

           
            TestCase.assertTrue("Asserts that if the parent node does not exist, it is created automatically, " +
                    "as if createInteriorNode(String) were called for the parent URI", 
                    TestExecPlugin.getCreateInteriorNodeCount()==2 && TestExecPlugin.getCreateLeafNodeCount()==1);
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session, null);
            
        }
    }
    /**
     * Asserts that if the parent node does not exist, it is created automatically,  
     * as if createInteriorNode(String) were called for the parent URI using the method with three parameters.
     * 
     * @spec DmtSession.createLeafNode(String,DmtData,String)
     */
    private void testCreateLeafNode024() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode024");
            TestExecPlugin.resetCount();
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(
                    TestExecPluginActivator.INEXISTENT_INTERIOR_AND_LEAF_NODES, value,null);

           
            TestCase.assertTrue("Asserts that if the parent node does not exist, it is created automatically, " +
                    "as if createInteriorNode(String) were called for the parent URI", 
                    TestExecPlugin.getCreateInteriorNodeCount()==2 && TestExecPlugin.getCreateLeafNodeCount()==1);
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session, null);
            
        }
    }
    /**
     * Asserts that any exceptions encountered while creating the ancestors are propagated to 
     * the caller of this method
     * 
     * @spec DmtSession.createLeafNode(String)
     */
    private void testCreateLeafNode025() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode025");
            TestExecPlugin.setExceptionAtCreateInteriorNode(true);
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(
                    TestExecPluginActivator.INEXISTENT_INTERIOR_AND_LEAF_NODES);
            DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
        } catch (DmtIllegalStateException e) {
            DefaultTestBundleControl.pass("Asserts that any exceptions encountered while creating the ancestors are propagated to the caller of createLeafNode");
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
        } finally {
            tbc.cleanUp(session, null);
            TestExecPlugin.setExceptionAtCreateInteriorNode(false);
            
        }
    }
    /**
     * Asserts that any exceptions encountered while creating the ancestors are propagated to 
     * the caller of this method
     * 
     * @spec DmtSession.createLeafNode(String,DmtData)
     */
    private void testCreateLeafNode026() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode026");
            TestExecPlugin.setExceptionAtCreateInteriorNode(true);
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(
                    TestExecPluginActivator.INEXISTENT_INTERIOR_AND_LEAF_NODES, value);
            DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
        } catch (DmtIllegalStateException e) {
            DefaultTestBundleControl.pass("Asserts that any exceptions encountered while creating the ancestors are propagated to the caller of createLeafNode");
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
        } finally {
            tbc.cleanUp(session, null);
            TestExecPlugin.setExceptionAtCreateInteriorNode(false);
            
        }
    }
    /**
     * Asserts that any exceptions encountered while creating the ancestors are propagated to 
     * the caller of this method
     * 
     * @spec DmtSession.createLeafNode(String,DmtData,String)
     */
    private void testCreateLeafNode027() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode027");
            TestExecPlugin.setExceptionAtCreateInteriorNode(true);
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(
                    TestExecPluginActivator.INEXISTENT_INTERIOR_AND_LEAF_NODES, value,null);
            DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
        } catch (DmtIllegalStateException e) {
            DefaultTestBundleControl.pass("Asserts that any exceptions encountered while creating the ancestors are propagated to the caller of createLeafNode");
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
        } finally {
            tbc.cleanUp(session, null);
            TestExecPlugin.setExceptionAtCreateInteriorNode(false);
            
        }
    }
    /**
     * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
     * if the session is atomic and the plugin does not support non-atomic writing
     * 
     * @spec DmtSession.createLeafNode(String)
     */
    private void testCreateLeafNode028() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode028");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createLeafNode(TestNonAtomicPluginActivator.INEXISTENT_LEAF_NODE);
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
     * @spec DmtSession.createLeafNode(String,DmtData)
     */
    private void testCreateLeafNode029() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode029");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createLeafNode(TestNonAtomicPluginActivator.INEXISTENT_LEAF_NODE,value);
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
     * @spec DmtSession.createLeafNode(String,DmtData,String)
     */
    private void testCreateLeafNode030() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode030");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createLeafNode(TestNonAtomicPluginActivator.INEXISTENT_LEAF_NODE,value, DmtConstants.MIMETYPE);
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
     * if the session is atomic and the plugin is read-only 
     * 
     * @spec DmtSession.createLeafNode(String)
     */
    private void testCreateLeafNode031() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode031");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createLeafNode(TestReadOnlyPluginActivator.INEXISTENT_LEAF_NODE);
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
     * if the session is atomic and the plugin is read-only 
     * 
     * @spec DmtSession.createLeafNode(String,DmtData)
     */
    private void testCreateLeafNode032() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode032");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createLeafNode(TestReadOnlyPluginActivator.INEXISTENT_LEAF_NODE,value);
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
     * if the session is atomic and the plugin is read-only 
     * 
     * @spec DmtSession.createLeafNode(String,DmtData,String)
     */
    private void testCreateLeafNode033() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCreateLeafNode033");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createLeafNode(TestReadOnlyPluginActivator.INEXISTENT_LEAF_NODE,value, DmtConstants.MIMETYPE);
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
     * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown  
     * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin is read-only
     * 
     * @spec DmtSession.createLeafNode(String,DmtData,String)
     */
    private void testCreateLeafNode034() {
        DmtSession session = null;
        DefaultTestBundleControl.log("#testCreateLeafNode034");
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.createLeafNode(TestReadOnlyPluginActivator.INEXISTENT_LEAF_NODE,value, DmtConstants.MIMETYPE);
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
     * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown  
     * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin is read-only
     * 
     * @spec DmtSession.createLeafNode(String,DmtData)
     */
    private void testCreateLeafNode035() {
        DmtSession session = null;
        DefaultTestBundleControl.log("#testCreateLeafNode035");
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.createLeafNode(TestReadOnlyPluginActivator.INEXISTENT_LEAF_NODE,value);
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
     * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown  
     * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin is read-only
     * 
     * @spec DmtSession.createLeafNode(String)
     */
    private void testCreateLeafNode036() {
        DmtSession session = null;
        DefaultTestBundleControl.log("#testCreateLeafNode036");
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.createLeafNode(TestReadOnlyPluginActivator.INEXISTENT_LEAF_NODE);
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
     * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown  
     * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin 
     * does not support non-atomic writing
     * 
     * @spec DmtSession.createLeafNode(String,DmtData,String)
     */
    private void testCreateLeafNode037() {
        DmtSession session = null;
        DefaultTestBundleControl.log("#testCreateLeafNode037");
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.createLeafNode(TestNonAtomicPluginActivator.INEXISTENT_LEAF_NODE,value, DmtConstants.MIMETYPE);
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
     * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown  
     * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin 
     * does not support non-atomic writing
     * 
     * @spec DmtSession.createLeafNode(String,DmtData)
     */
    private void testCreateLeafNode038() {
        DmtSession session = null;
        DefaultTestBundleControl.log("#testCreateLeafNode038");
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.createLeafNode(TestNonAtomicPluginActivator.INEXISTENT_LEAF_NODE,value);
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
     * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown  
     * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin 
     * does not support non-atomic writing
     * 
     * @spec DmtSession.createLeafNode(String)
     */
    private void testCreateLeafNode039() {
        DmtSession session = null;
        DefaultTestBundleControl.log("#testCreateLeafNode039");
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.createLeafNode(TestNonAtomicPluginActivator.INEXISTENT_LEAF_NODE);
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
	 * URI the session was opened with (it throws DmtException.NODE_ALREADY_EXISTS)
	 * using the method with one parameter
	 * 
	 * @spec DmtSession.createLeafNode(String)
	 */
	private void testCreateLeafNode040() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode040");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.createLeafNode("");

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
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with (it throws DmtException.NODE_ALREADY_EXISTS)
	 * using the method with two parameters
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData)
	 */
	private void testCreateLeafNode041() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode041");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.createLeafNode("",value);

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
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with (it throws DmtException.NODE_ALREADY_EXISTS)
	 * using the method with three parameters
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	private void testCreateLeafNode042() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode042");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.createLeafNode("",value,DmtConstants.MIMETYPE);

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
}

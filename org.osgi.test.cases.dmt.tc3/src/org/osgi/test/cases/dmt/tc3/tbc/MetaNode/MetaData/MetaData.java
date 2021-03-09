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
 * Jun 08, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc3.tbc.MetaNode.MetaData;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.TestMetaNodeDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.MetaNode.TestMetaNodeDataPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * This test case validates if DmtException.METADATA_MISMATCH is thrown when
 * the Metadata does not allow the specified action.
 */
public class MetaData {

	private DmtTestControl tbc;
	private static DmtData LEAF_VALUE = new DmtData(100);
	private static String MIMETYPE = "text/xml";
	public MetaData(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        testMetaData001();
		testMetaData002();
		testMetaData003();
		testMetaData004();
		testMetaData005();
		testMetaData006();
		testMetaData007();
		testMetaData008();
		testMetaData009();
		testMetaData010();
		testMetaData011();
		testMetaData012();
		testMetaData013();
		testMetaData014();
		testMetaData015();
		testMetaData016();
		testMetaData017();
		testMetaData018();
		testMetaData019();
		testMetaData020();
		testMetaData021();
		testMetaData022();
		testMetaData023();
		testMetaData024();
		testMetaData025();
		testMetaData026();	
		testMetaData027();
		testMetaData028();
		testMetaData029();
		testMetaData030();
		testMetaData031();
		testMetaData032();
		testMetaData033();
		testMetaData034();
		testMetaData035();
		testMetaData036();	
		testMetaData037();
		testMetaData038();
		testMetaData039();
		testMetaData040();
		testMetaData041();
		testMetaData042();
		testMetaData043();
		testMetaData044();
		testMetaData045();
		testMetaData046();
		testMetaData047();
		testMetaData048();
		testMetaData049();
        testMetaData050();
        testMetaData051();
        testMetaData052();
        testMetaData053();
        testMetaData054();
        testMetaData055();
        testMetaData056();  
        testMetaData057();
        testMetaData058();
        testMetaData059();
        testMetaData060();
        testMetaData061();
        testMetaData062();
        testMetaData063();
        testMetaData064();
        testMetaData065();
        testMetaData066();
        testMetaData067();
        testMetaData068();
        testMetaData069();
        testMetaData070();
        testMetaData071();
        testMetaData072();
        testMetaData073();
	}


	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data defines it as an interior node
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData)  
	 */
	public void testMetaData001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData001");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode(); 
			TestPluginMetaDataActivator.metaNodeDefault.setValidValues(new DmtData[] { new DmtData(100)});
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(false);
			//TestDataPlugin returns the metaNodeDefault, an interior node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_NODE, new DmtData(100));
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data defines it as an interior node. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
		}

	}
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the specified value.
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData) 
	 */
	public void testMetaData002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData002");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			//TestDataPlugin metadata only allows DmtData as an integer with the value 100
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setValidValues(new DmtData[] { new DmtData(100)});
			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData(99));
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified value. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
	
		}

	}	
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the specified node name
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData) 
	 */
	public void testMetaData003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData003");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			//It's not a valid name for this node
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setValidNames(new String[] {TestPluginMetaDataActivator.LEAF_NODE_STRING  });
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setValidValues(new DmtData[] { new DmtData(100)});
			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE_INVALID_NAME, new DmtData(100));
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified node name, ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
		}

	}		
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data defines it as a permanent leaf node
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData)
	 */
	public void testMetaData004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData004");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setScope(MetaNode.PERMANENT);
			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData("<test></test>",DmtData.FORMAT_XML));
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data  defines it as a permanent leaf node. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
		}
	}
	

	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data doesn't allow the add operation
	 * 
	 * 	 * @spec DmtSession.createLeafNode(String,DmtData)
	 */
	public void testMetaData005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData005");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setCanAdd(false);
			
			//It's not a valid operation for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE,LEAF_VALUE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the add operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if 
	 * meta-data defines it as an interior node.
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	public void testMetaData006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData006");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(false);
			//TestDataPlugin metadata returns an interior node   
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_NODE, LEAF_VALUE, MIMETYPE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data defines it as an interior node. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
			
			
		}

	}
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the specified value.
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	public void testMetaData007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData007");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setValidValues(new DmtData[]{new DmtData(100)});
			
			//TestDataPlugin metadata only allows DmtData as an integer with the value 100
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData(99),MIMETYPE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified value. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}	
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if 
	 * meta-data doesn't allow the specified mimeType.
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	public void testMetaData008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData008");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setMimeTypes(new String[]{"text/xml"});
			
			//TestDataPlugin metadata only allows mimeType "text/xml", this call must not be fowarded to this plugin
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, LEAF_VALUE,"text/plain");
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified mimeType. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}

	}		
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the specified node name
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	public void testMetaData009() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData009");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setValidNames(new String[] {TestPluginMetaDataActivator.LEAF_NODE_STRING  });

			//It's not a valid name for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE_INVALID_NAME, LEAF_VALUE,MIMETYPE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified node name. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
		}

	}		
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data defines it as permanent
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	public void testMetaData010() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData010");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setScope(MetaNode.PERMANENT);			
			
			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData("<test></test>",DmtData.FORMAT_XML),MIMETYPE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data defines it as permanent. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
		}
	}
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the add operation
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	public void testMetaData011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData011");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setCanAdd(false);
			
			//It's not a valid operation for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, LEAF_VALUE,MIMETYPE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the add operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}		
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data doesn't have a default value
	 * 
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	public void testMetaData012() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData012");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);

			//There is no default value
			session.setDefaultNodeValue(TestPluginMetaDataActivator.LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't doesn't have a default value. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the replace operation
	 * 
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	public void testMetaData013() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData013");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setDefaultValue(new DmtData(100));
			TestPluginMetaDataActivator.metaNodeDefault.setCanReplace(false);
			
			session.setDefaultNodeValue(TestPluginMetaDataActivator.LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't doesn't allow the replace operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if 
	 * meta-data doesn't allow the specified value.
	 * 
	 * DmtSession.setNodeValue(String,DmtData)
	 */
	public void testMetaData014() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData014");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setValidValues(new DmtData[]{new DmtData(100)});
			
			session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,new DmtData(101));
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified value. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}


		
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if 
	 * meta-data doesn't allow the replace operation.
	 * 
	 * DmtSession.setNodeValue(String,DmtData)
	 */
	public void testMetaData015() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData015");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setCanReplace(false);
			
			session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,LEAF_VALUE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the replace operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data doesn't 
	 * allow the specified node name.
	 * 
	 * @spec DmtSession.createInteriorNode(String)
	 */
	public void testMetaData016() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData016");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setValidNames(new String[] {TestPluginMetaDataActivator.INTERIOR_NODE_STRING  });

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE_INVALID_NAME);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified node name. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data doesn't 
	 * allow the specified node name.
	 * 
	 * @spec DmtSession.createInteriorNode(String,String)
	 */
	public void testMetaData017() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData017");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setValidNames(new String[] {TestPluginMetaDataActivator.INTERIOR_NODE_STRING  });

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE_INVALID_NAME,DmtConstants.DDF);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified node name. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data doesn't 
	 * allow the add operation.
	 * 
	 * @spec DmtSession.createInteriorNode(String)
	 */
	public void testMetaData018() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData018");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanAdd(false);

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the add operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data doesn't 
	 * allow the add operation.
	 * 
	 * @spec DmtSession.createInteriorNode(String,String)
	 */
	public void testMetaData019() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData019");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanAdd(false);

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE,DmtConstants.DDF);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the add operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if zero occurrences of 
	 * the node are not allowed, it must not be the last one.
	 * 
	 * @spec DmtSession.deleteNode(String) 
	 */
	public void testMetaData020() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData020");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setZeroOccurrenceAllowed(false);
			TestPluginMetaDataActivator.metaNodeDefault.setMaxOccurrence(1);

			session.deleteNode(TestPluginMetaDataActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if zero occurrences of the node are not allowed, it must not be the last one.",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}

	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data 
	 * doesn't allow the delete operation.
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	public void testMetaData021() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData021");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanDelete(false);

			session.deleteNode(TestPluginMetaDataActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the delete operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the replace operation for this node.
	 * 
	 * @spec DmtSession.renameNode(String,String)
	 */
	public void testMetaData022() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData022");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanReplace(false);
			
			session.renameNode(TestPluginMetaDataActivator.INTERIOR_NODE,TestPluginMetaDataActivator.INEXISTENT_NODE_NAME);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the replace operation for this node. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the replace operation.
	 * 
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	public void testMetaData023() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData023");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setCanReplace(false);

			session.setNodeTitle(TestPluginMetaDataActivator.LEAF_NODE,"Title");
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the replace operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown  
	 * if meta-data doesn't allow the replace operation.
	 * 
	 * @spec DmtSession.setNodeType(String,String)
	 */
	public void testMetaData024() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData024");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanReplace(false);

			session.setNodeType(TestPluginMetaDataActivator.INTERIOR_NODE,DmtConstants.DDF);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the replace operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the get operation.
	 * 
	 * @spec DmtSession.getNodeType(String)
	 */
	public void testMetaData025() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData025");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeType(TestPluginMetaDataActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the get operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}		
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data doesn't 
	 * allow the get operation.
	 * 
	 * @spec DmtSession.getChildNodeNames(String)
	 */
	public void testMetaData026() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData026");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getChildNodeNames(TestPluginMetaDataActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the get operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the get operation.
	 * 
	 * @spec DmtSession.getNodeSize(String)
	 */
	public void testMetaData027() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData027");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeSize(TestPluginMetaDataActivator.LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the get operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the get operation.
	 * 
	 * @spec DmtSession.getNodeTimestamp(String)
	 */
	public void testMetaData028() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData028");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeTimestamp(TestPluginMetaDataActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the get operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the get operation.
	 * 
	 * @spec DmtSession.getNodeTitle(String)
	 */
	public void testMetaData029() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData029");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeTitle(TestPluginMetaDataActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the get operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the get operation.
	 * 
	 * @spec DmtSession.getNodeValue(String)
	 */
	public void testMetaData030() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData030");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeValue(TestPluginMetaDataActivator.LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the get operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the get operation.
	 * 
	 * @spec DmtSession.getNodeVersion(String)
	 */
	public void testMetaData031() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData031");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeVersion(TestPluginMetaDataActivator.LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the get operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the get operation.
	 * 
	 * @spec DmtSession.isLeafNode(String)
	 */
	public void testMetaData032() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData032");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.isLeafNode(TestPluginMetaDataActivator.LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the get operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the get operation.
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */
	public void testMetaData033() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData033");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getEffectiveNodeAcl(TestPluginMetaDataActivator.LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the get operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the get operation.
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	public void testMetaData034() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData034");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeAcl(TestPluginMetaDataActivator.LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the get operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data 
	 * doesn't allow deleting a permanent node.
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	public void testMetaData035() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData035");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setScope(MetaNode.PERMANENT);

			session.deleteNode(TestPluginMetaDataActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow deleting a permanent node. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data doesn't 
	 * allow creating a permanent node.
	 * 
	 * @spec DmtSession.createInteriorNode(String)
	 */
	public void testMetaData036() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData036");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setScope(MetaNode.PERMANENT);

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow creating a permanent node. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if the node cannot be 
	 * executed according to the meta-data  (does not have MetaNode.CMD_EXECUTE access type)
	 * 
	 * @spec DmtSession.execute(String,String) 
	 */
	public void testMetaData037() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData037");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanExecute(false);

			session.execute(TestPluginMetaDataActivator.INTERIOR_NODE,null);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown if the node cannot be executed " +
					"according to the meta-data  (does not have MetaNode.CMD_EXECUTE access type)",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if the node cannot be 
	 * executed according to the meta-data (does not have MetaNode.CMD_EXECUTE access type)
	 * 
	 * @spec DmtSession.execute(String,String,String) 
	 */
	public void testMetaData038() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData038");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanExecute(false);

			session.execute(TestPluginMetaDataActivator.INTERIOR_NODE,null,null);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown if the node cannot be executed " +
					"according to the meta-data  (does not have MetaNode.CMD_EXECUTE access type)",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data doesn't 
	 * allow creating a permanent node.
	 * 
	 * @spec DmtSession.createInteriorNode(String,String)
	 */
	public void testMetaData039() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData039");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setScope(MetaNode.PERMANENT);

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE,DmtConstants.DDF);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow creating a permanent node. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if the node exceeds 
	 * the maximum occurrence number
	 * 
	 * @spec DmtSession.createInteriorNode(String)
	 */
	public void testMetaData040() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData040");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setMaxOccurrence(2);

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if the creation of the new node exceeds the maximum occurrence number. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if the node exceeds 
	 * the maximum occurrence number
	 * 
	 * @spec DmtSession.createInteriorNode(String,String)
	 */
	public void testMetaData041() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData041");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setMaxOccurrence(2);

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE,DmtConstants.DDF);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if the creation of the new node exceeds the maximum occurrence number. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if the creation of the new node exceeds the maximum occurrence number
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData)
	 */
	public void testMetaData042() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData042");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setMaxOccurrence(2);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE,new DmtData(1));
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if the creation of the new node exceeds the maximum occurrence number. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if the creation of the new node exceeds the maximum occurrence number
	 * 
	 * @spec DmtSession.createLeafNode(String,DmtData,String)
	 */
	public void testMetaData043() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData043");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setMaxOccurrence(2);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			
			//It's not a valid operation for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE,new DmtData(1),MIMETYPE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if the creation of the new node exceeds the maximum occurrence number. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}

	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if the creation of the new node exceeds the maximum occurrence number
	 * 
	 * @spec DmtSession.createLeafNode(String)
	 */
	public void testMetaData044() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData044");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setMaxOccurrence(2);
			TestPluginMetaDataActivator.metaNodeDefault.setDefaultValue(new DmtData(10));
			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if the creation of the new node exceeds the maximum occurrence number. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the add operation
	 * 
	 * @spec DmtSession.createLeafNode(String)
	 */
	public void testMetaData045() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData045");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setCanAdd(false);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setDefaultValue(new DmtData(10));
			
			//It's not a valid operation for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the add operation. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data defines it as an interior node
	 * 
	 * @spec DmtSession.createLeafNode(String)
	 */
	public void testMetaData046() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData046");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setDefaultValue(new DmtData(10));
			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data defines it as an interior node.",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
			
			
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data defines it as a permanent leaf node
	 * 
	 * @spec DmtSession.createLeafNode(String)
	 */
	public void testMetaData047() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData047");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setScope(MetaNode.PERMANENT);
			TestPluginMetaDataActivator.metaNodeDefault.setDefaultValue(new DmtData(10));
			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data defines it as a permanent leaf node",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.cleanUp(session,true);
		}
	}
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * if meta-data doesn't allow the specified node name
	 * 
	 * @spec DmtSession.createLeafNode(String) 
	 */
	public void testMetaData048() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData048");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			//It's not a valid name for this node
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setValidNames(new String[] {TestPluginMetaDataActivator.LEAF_NODE_STRING  });
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setValidValues(new DmtData[] { new DmtData(100)});
			TestPluginMetaDataActivator.metaNodeDefault.setDefaultValue(new DmtData(100));
			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE_INVALID_NAME);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified node name ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
	
		}

	}
	
	/**
	 * Asserts that DmtException.METADATA_MISMATCH is thrown if meta-data doesn't allow the specified value.
	 * 
	 * @spec DmtSession.createLeafNode(String) 
	 */
	public void testMetaData049() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testMetaData049");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
		
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified value. ",DmtException.METADATA_MISMATCH,e.getCode());
			TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
		}

	}
    
    /**
     * Asserts that DmtException.METADATA_MISMATCH is thrown  
     * if meta-data doesn't have the mimeType specified
     * 
     * @spec DmtSession.setNodeType(String,String)
     */
    public void testMetaData050() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData050");

            session = tbc.getDmtAdmin().getSession(
                    TestPluginMetaDataActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
            TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
            TestPluginMetaDataActivator.metaNodeDefault.setMimeTypes(new String[] {"text/xml"});

            session.setNodeType(TestPluginMetaDataActivator.LEAF_NODE,"text/html");
            DefaultTestBundleControl.failException("",DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
                    "if meta-data doesn't have the mimeType specified. ",DmtException.METADATA_MISMATCH,e.getCode());
            TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
        } catch (Exception e) {
        	DmtTestControl.failExpectedOtherException(DmtException.class, e);

        } finally {
            tbc.cleanUp(session,true);
        }
    }
    /**
     * Asserts that DmtException.METADATA_MISMATCH is thrown 
     * if meta-data of the parent node does not allow the Add operation
     * 
     * @spec DmtSession.renameNode(String,String)
     */
    public void testMetaData051() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData051");

            session = tbc.getDmtAdmin().getSession(
            		TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            TestMetaNodeDataPlugin.setRootNodeAllowsAddOperation(false);
            session.copy(TestMetaNodeDataPluginActivator.INTERIOR_NODE,TestMetaNodeDataPluginActivator.INEXISTENT_NODE,true);
            DefaultTestBundleControl.failException("",DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
                    "if meta-data of the parent node does not allow the Add operation ",DmtException.METADATA_MISMATCH,e.getCode());
            TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
        } catch (Exception e) {
        	DmtTestControl.failExpectedOtherException(DmtException.class, e);

        } finally {
            tbc.cleanUp(session,true);
            TestMetaNodeDataPlugin.setRootNodeAllowsAddOperation(true);
        }
    }
    
    /**
     * Asserts that DmtException.METADATA_MISMATCH is thrown 
     * if meta-data doesn't allow the specified node name
     * 
     * @spec DmtSession.renameNode(String,String)
     */
    public void testMetaData052() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData052");

            session = tbc.getDmtAdmin().getSession(
                    TestPluginMetaDataActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            //It's not a valid name for this node
            TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
            TestPluginMetaDataActivator.metaNodeDefault.setValidNames(new String[] {"validName"});
           
            session.renameNode(TestPluginMetaDataActivator.INTERIOR_NODE,TestPluginMetaDataActivator.INEXISTENT_NODE_NAME);
            DefaultTestBundleControl.failException("",DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
                    "if meta-data doesn't allow the specified node name, ",DmtException.METADATA_MISMATCH,e.getCode());
            TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
        } catch (Exception e) {
        	DmtTestControl.failExpectedOtherException(DmtException.class, e);

        } finally {
            tbc.cleanUp(session,true);
        }

    }  
    
    /**
     * Asserts that DmtException.METADATA_MISMATCH is thrown 
     * if meta-data defines the source as a leaf node
     * 
     * @spec DmtSession.renameNode(String,String)
     */
    public void testMetaData053() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData053");

            session = tbc.getDmtAdmin().getSession(
                TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.renameNode(TestMetaNodeDataPluginActivator.LEAF_NODE,TestMetaNodeDataPluginActivator.INEXISTENT_NODE_NAME);
            DefaultTestBundleControl.failException("",DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
                    "if meta-data defines the source as a leaf node. ",DmtException.METADATA_MISMATCH,e.getCode());
            TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
        } catch (Exception e) {
        	DmtTestControl.failExpectedOtherException(DmtException.class, e);

        } finally {
            tbc.cleanUp(session,true);
        }

    }  
    
    /**
     * Asserts that DmtException.METADATA_MISMATCH is thrown 
     * if meta-data defines the target as a leaf node
     * 
     * @spec DmtSession.renameNode(String,String)
     */
    public void testMetaData054() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData054");

            session = tbc.getDmtAdmin().getSession(
                TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.renameNode(TestMetaNodeDataPluginActivator.INTERIOR_NODE,TestMetaNodeDataPluginActivator.INEXISTENT_LEAF_NODE_NAME);
            DefaultTestBundleControl.failException("",DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
                    "if meta-data defines the target as leaf node. ",DmtException.METADATA_MISMATCH,e.getCode());
            TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
        } catch (Exception e) {
        	DmtTestControl.failExpectedOtherException(DmtException.class, e);

        } finally {
            tbc.cleanUp(session,true);
        }

    }  
    
    /**
     * Asserts that DmtException.METADATA_MISMATCH is thrown if 
     * meta-data does not have a default value
     * 
     * DmtSession.setNodeValue(String,DmtData)
     */
    public void testMetaData055() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData055");

            session = tbc.getDmtAdmin().getSession(
                    TestPluginMetaDataActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
            TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
            //MetaNode does not have a default value
            session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,null);
            DefaultTestBundleControl.failException("",DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
                    "if meta-data does not have a default value. ",DmtException.METADATA_MISMATCH,e.getCode());
            TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
        } catch (Exception e) {
        	DmtTestControl.failExpectedOtherException(DmtException.class, e);

        } finally {
            tbc.cleanUp(session,true);
            
            
        }
    }   
    
    /**
     * Asserts that DmtException.METADATA_MISMATCH is thrown if 
     * meta-data does not have a default value
     * 
     * DmtSession.setNodeValue(String,DmtData)
     */
    public void testMetaData056() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData056");

            session = tbc.getDmtAdmin().getSession(
                    TestPluginMetaDataActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
            TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
            //MetaNode does not have a default value
            session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,null);
            DefaultTestBundleControl.failException("",DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
                    "if meta-data does not have a default value. ",DmtException.METADATA_MISMATCH,e.getCode());
            TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
        } catch (Exception e) {
        	DmtTestControl.failExpectedOtherException(DmtException.class, e);

        } finally {
            tbc.cleanUp(session,true);
            
            
        }
    }  
    
 
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and we create a node with all of DmtData types using the method with three
     * parameters
     * 
     * @spec DmtSession.createLeafNode(String,DmtData,String)
     */
    public void testMetaData057() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData057");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;

            //A DmtData instance can not have FORMAT_NODE, so it is form FORMAT_INTEGER (1) to FORMAT_NULL(512).
            for (int i=1;i<=512;i=i<<1){
                session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_NODE,DmtConstants.getDmtData(i),null);
                DefaultTestBundleControl.pass("No exception is thrown if the node does not have a metanode and a node is created using " + DmtConstants.getDmtDataCodeText(i));
            }
           
            
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    
    /**
     * Asserts that no exception is thrown when the target meta-data does not have any access type
     * (returns false for all operations). It ensures that only the original node needs to have the 
     * specified access type.
     * 
     * @spec DmtSession.renameNode(String,String)
     */
    public void testMetaData058() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData058");

            session = tbc.getDmtAdmin().getSession(
                TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.renameNode(TestMetaNodeDataPluginActivator.INTERIOR_NODE,TestMetaNodeDataPluginActivator.INEXISTENT_NODE_WITHOUT_PERMISSIONS_NAME);
            DefaultTestBundleControl.pass("renameNode could be called, even if the target does not have any permission");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and createInteriorNode is called
     * 
     * @spec DmtSession.createInteriorNode(String)
     */
    public void testMetaData059() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData059");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;
            session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE);
            DefaultTestBundleControl.pass("Asserts that no exception is thrown if the node does not have a metanode and createInteriorNode is called");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    }  
    
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and createInteriorNode is called
     * 
     * @spec DmtSession.createInteriorNode(String,String)
     */
    public void testMetaData060() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData060");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;
            session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE,null);
            DefaultTestBundleControl.pass("Asserts that no exception is thrown if the node does not have a metanode and createInteriorNode is called");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and createLeafNode is called
     * 
     * @spec DmtSession.createLeafNode(String)
     */
    public void testMetaData061() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData061");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;
            session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE);
            DefaultTestBundleControl.pass("Asserts that no exception is thrown if the node does not have a metanode and createLeafNode is called");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and createLeafNode is called
     * 
     * @spec DmtSession.createLeafNode(String,DmtData)
     */
    public void testMetaData062() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData062");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;
            session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE,null);
            DefaultTestBundleControl.pass("Asserts that no exception is thrown if the node does not have a metanode and createLeafNode is called");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and createLeafNode is called
     * 
     * @spec DmtSession.createLeafNode(String,DmtData,String)
     */
    public void testMetaData063() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData063");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;
            session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE,null,null);
            DefaultTestBundleControl.pass("Asserts that no exception is thrown if the node does not have a metanode and createLeafNode is called");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and copy is called
     * 
     * @spec DmtSession.copy(String,String,boolean)
     */
    public void testMetaData064() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData064");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;
            session.copy(TestPluginMetaDataActivator.INTERIOR_NODE,TestPluginMetaDataActivator.INEXISTENT_NODE,false);
            DefaultTestBundleControl.pass("Asserts that no exception is thrown if the node does not have a metanode and copy is called");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and renameNode is called
     * 
     * @spec DmtSession.renameNode(String,String)
     */
    public void testMetaData065() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData065");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;
            session.renameNode(TestPluginMetaDataActivator.INTERIOR_NODE,TestPluginMetaDataActivator.INEXISTENT_NODE_NAME);
            DefaultTestBundleControl.pass("Asserts that no exception is thrown if the node does not have a metanode and renameNode is called");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    }
    
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and createLeafNode is called with any value
     * 
     * @spec DmtSession.createLeafNode(String,DmtData)
     */
    public void testMetaData066() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData066");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;
            session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE,DmtData.NULL_VALUE);
            DefaultTestBundleControl.pass("Asserts that no exception is thrown if the node does not have a metanode and createLeafNode is called with any value");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and createLeafNode is called with any value
     * 
     * @spec DmtSession.createLeafNode(String,DmtData,String)
     */
    public void testMetaData067() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData067");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;
            session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE,DmtData.NULL_VALUE,"text/xml");
            DefaultTestBundleControl.pass("Asserts that no exception is thrown if the node does not have a metanode and createLeafNode is called with any value");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and setNodeValue is called with any value
     * 
     * @spec DmtSession.setNodeValue(String,DmtData)
     */
    public void testMetaData068() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData068");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;
            session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,DmtData.NULL_VALUE);
            DefaultTestBundleControl.pass("Asserts that no exception is thrown if the node does not have a metanode and setNodeValue is called with any value");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and deleteNode is called 
     * 
     * @spec DmtSession.deleteNode(String)
     */
    public void testMetaData069() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData069");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;
            session.deleteNode(TestPluginMetaDataActivator.INTERIOR_NODE);
            DefaultTestBundleControl.pass("Asserts that no exception is thrown if the node does not have a metanode and deleteNode is called");
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    
    /**
     * Asserts that DmtException.METADATA_MISMATCH is thrown 
     * if meta-data defines the source as permanent
     * 
     * @spec DmtSession.renameNode(String,String)
     */
    public void testMetaData070() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData070");

            session = tbc.getDmtAdmin().getSession(
                TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.renameNode(TestMetaNodeDataPluginActivator.PERMANENT_INTERIOR_NODE,TestMetaNodeDataPluginActivator.INEXISTENT_NODE_NAME);
            DefaultTestBundleControl.failException("",DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
                    "if meta-data defines the source as permanent ",DmtException.METADATA_MISMATCH,e.getCode());
            TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
        } catch (Exception e) {
        	DmtTestControl.failExpectedOtherException(DmtException.class, e);

        } finally {
            tbc.cleanUp(session,true);
        } 
    }
    
    /**
     * Asserts that DmtException.METADATA_MISMATCH is thrown 
     * if meta-data defines the target as permanent
     * 
     * @spec DmtSession.renameNode(String,String)
     */
    public void testMetaData071() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData071");

            session = tbc.getDmtAdmin().getSession(
                TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.renameNode(TestMetaNodeDataPluginActivator.INTERIOR_NODE,TestMetaNodeDataPluginActivator.PERMANENT_INEXISTENT_NODE_NAME);
            DefaultTestBundleControl.failException("",DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
                    "if meta-data defines the source as permanent ",DmtException.METADATA_MISMATCH,e.getCode());
            TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
        } catch (Exception e) {
        	DmtTestControl.failExpectedOtherException(DmtException.class, e);

        } finally {
            tbc.cleanUp(session,true);
        } 
    }
    
    /**
     * Asserts that no exception is thrown if the node does not have a metanode
     * and we create a node with all of DmtData types using the method with two
     * parameters
     * 
     * @spec DmtSession.createLeafNode(String,DmtData)
     */
    public void testMetaData072() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData072");
            
            session = tbc.getDmtAdmin().getSession(
                TestPluginMetaDataActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        
            TestPluginMetaDataActivator.metaNodeDefault = null;

            //A DmtData instance can not have FORMAT_NODE, so it is form FORMAT_INTEGER (1) to FORMAT_NULL(512).
            for (int i=1;i<=512;i=i<<1){
                session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_NODE,DmtConstants.getDmtData(i));
                DefaultTestBundleControl.pass("No exception is thrown if the node does not have a metanode and a node is created using " + DmtConstants.getDmtDataCodeText(i));
            }
           
            
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    
    /**
     * Asserts that DmtException.METADATA_MISMATCH is thrown 
     * if meta-data of the copied node does not allow the Get operation
     * 
     * @spec DmtSession.renameNode(String,String)
     */
    public void testMetaData073() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testMetaData073");

            session = tbc.getDmtAdmin().getSession(
            		TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
           
            session.copy(TestMetaNodeDataPluginActivator.INTERIOR_NODE_WITHOUT_GET_PERMISSION,TestMetaNodeDataPluginActivator.INEXISTENT_NODE,true);
            DefaultTestBundleControl.failException("",DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
                    "if meta-data of the copied node does not allow the Get operation, ",DmtException.METADATA_MISMATCH,e.getCode());
            TestCase.assertTrue("Asserts that the plugin's method was not called",DmtConstants.TEMPORARY=="");
        } catch (Exception e) {
        	DmtTestControl.failExpectedOtherException(DmtException.class, e);

        } finally {
            tbc.cleanUp(session,true);
        }

    } 
    
}




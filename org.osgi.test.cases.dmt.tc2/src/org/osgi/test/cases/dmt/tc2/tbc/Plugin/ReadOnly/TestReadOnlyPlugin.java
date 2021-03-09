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
 * Feb 25, 2005  Andre Assad
 * 11            Implement DMT Use Cases 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Plugin.ReadOnly;

import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * A test implementation of DataPluginFactory. This implementation validates the
 * DmtSession calls to a subtree handled by a DataPluginFactory.
 * 
 */
public class TestReadOnlyPlugin implements DataPlugin, ReadableDataSession {
	
	private DmtTestControl tbc;
	private static boolean exceptionAtClose;
    
	public TestReadOnlyPlugin(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	@Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
		return this;
	}

	@Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return null;
	}

	@Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return null;
	}
	

	@Override
	public void close() throws DmtException {
	    if (exceptionAtClose) {
	        throw new DmtException((String)null,DmtException.DATA_STORE_FAILURE,null);
        }
	}

	@Override
	public boolean isNodeUri(String[] nodeUri) {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestReadOnlyPluginActivator.ROOT)
				|| nodeName.equals(TestReadOnlyPluginActivator.INTERIOR_NODE)
				|| nodeName.equals(TestReadOnlyPluginActivator.LEAF_NODE)
				) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
		return new DmtData("");
	}

	@Override
	public String getNodeTitle(String[] nodeUri) throws DmtException {
		return null;
	}

	@Override
	public String getNodeType(String[] nodeUri) throws DmtException {
		return null;
	}

	@Override
	public int getNodeVersion(String[] nodeUri) throws DmtException {
		return 0;
	}

	@Override
	public Date getNodeTimestamp(String[] nodeUri) throws DmtException {
		return null;
	}

	@Override
	public int getNodeSize(String[] nodeUri) throws DmtException {
		return 0;
	}

	@Override
	public String[] getChildNodeNames(String[] nodeUri) throws DmtException {
			return null;
	}

	@Override
	public MetaNode getMetaNode(String[] nodeUri) throws DmtException {
		return null;
	}

	
	@Override
	public boolean isLeafNode(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestReadOnlyPluginActivator.LEAF_NODE)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void nodeChanged(String[] nodeUri) throws DmtException {

	}

    public static void setExceptionAtClose(boolean exceptionAtClose) {
        TestReadOnlyPlugin.exceptionAtClose = exceptionAtClose;
    }

}

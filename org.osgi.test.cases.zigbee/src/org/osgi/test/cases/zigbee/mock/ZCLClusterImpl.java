/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.zigbee.mock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLReadStatusRecord;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.test.cases.zigbee.config.file.ZCLClusterConfig;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * ZCLCluster intreface mocked implementation.
 * 
 * @author $Id$
 */
public class ZCLClusterImpl implements ZCLCluster {

	private int				id;
	private ZCLAttribute[]	attributes;
	private int[]			commandIds;

	public ZCLClusterImpl(int id, int[] commandIds, ZCLAttribute[] attributes) {
		this.id = id;
		this.commandIds = commandIds;
		this.attributes = attributes;
	}

	public ZCLClusterImpl(ZCLClusterConfig config) {
		this.id = config.getId();
		this.commandIds = config.getCommandIds();
		this.attributes = config.getAttributes();
	}

	public int getId() {
		return id;
	}

	public Promise getAttribute(int attributeId) {

		for (int i = 0; i < attributes.length; i++) {
			if (attributeId == attributes[i].getId()) {
				return Promises.resolved(attributes[i]);
			}
		}
		return Promises.failed(new ZCLException(ZCLException.UNSUPPORTED_ATTRIBUTE,
				ZCLException.FAILURE,
				"the AttributeId is not valid"));
	}

	public Promise getAttributes() {
		return Promises.resolved(attributes);
	}

	public Promise getCommandIds() {
		return Promises.resolved(this.commandIds.clone());
	}

	public Promise readAttributes(ZCLAttributeInfo[] attributesInfoArray) {
		if (attributesInfoArray == null) {
			return Promises.failed(new NullPointerException("attributes cannot be null"));
		} else if (attributesInfoArray.length == 0) {
			return Promises.failed(new IllegalArgumentException("attributes array cannot be empty"));
		}
		Map response = new HashMap();

		Set code = new HashSet();

		for (int i = 0; i < attributesInfoArray.length; i++) {
			ZCLAttributeInfo attributeInfo = attributesInfoArray[i];
			code.add(new Integer(attributeInfo.getManufacturerCode()));
			if (code.size() != 1) {
				return Promises.failed(new IllegalArgumentException(
						attributeInfo.getId() + " has a different manufacturer code compared to the others"));
			}
			int attrId = attributeInfo.getId();
			ZCLAttribute attribute = getAttributeFromId(attrId);
			if (attribute == null) {
				return Promises.failed(new ZCLException(ZCLException.UNSUPPORTED_ATTRIBUTE,
						ZCLException.FAILURE,
						"the AttributeId is not valid"));
			}
			if (attributeInfo.getDataType() != null && attribute.getDataType() != null) {

				if (attributeInfo.getDataType().getId() != attribute.getDataType().getId()) {
					return Promises.failed(new ZCLException(ZCLException.UNSUPPORTED_ATTRIBUTE,
							ZCLException.FAILURE,
							"the Attribute datatype is not valid"));
				}
			}
		}

		int i = 0;
		// for (int i : attributesIds) {
		final ZCLAttributeInfo attribute = attributesInfoArray[i];
		final byte[] attributeValue = {0};
		ZCLReadStatusRecord result = new ZCLReadStatusRecord() {

			public Object getValue() {
				return attributeValue;
			}

			public ZigBeeException getFailure() {
				return null;
			}

			public ZCLAttributeInfo getAttributeInfo() {
				return attribute;
			}
		};
		response.put(Integer.valueOf(Integer.toString(attribute.getId())), result);
		// }
		return Promises.resolved(response);
	}

	public Promise writeAttributes(boolean undivided, Map attributesIdsAndValues) {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise invoke(ZCLFrame frame) {
		// mocked invocation.
		return Promises.resolved(frame);
	}

	public Promise invoke(ZCLFrame frame, String exportedServicePID) {
		// mocked invocation.
		return Promises.resolved(frame);
	}

	public String toString() {
		String attributesAsAString = null;
		if (attributes != null) {
			attributesAsAString = "[";
			int i = 0;
			while (i < attributes.length) {
				attributesAsAString = attributesAsAString + attributes[i].toString();
				i = i + 1;
			}
			attributesAsAString = attributesAsAString + "]";
		}

		String commandIdsAsAString = null;
		if (commandIds != null) {
			commandIdsAsAString = "[";
			int i = 0;
			while (i < commandIds.length) {
				commandIdsAsAString = commandIdsAsAString + commandIds[i];
				i = i + 1;
			}
			commandIdsAsAString = commandIdsAsAString + "]";
		}

		return "" + this.getClass().getName() + "[id: " + id + ", attributes: " + attributesAsAString + ", commandIds: "
				+ commandIdsAsAString + "]";
	}

	public Promise getAttribute(int attributeId, int code) {
		if (code == -1) {
			return getAttribute(attributeId);
		}
		return Promises.failed(new UnsupportedOperationException("getAttribute:Please implement it"));
	}

	public Promise getAttributes(int code) {
		if (code == -1) {
			return getAttributes();
		}
		return Promises.failed(new UnsupportedOperationException("getAttribute:Please implement it"));
	}

	private ZCLAttribute getAttributeFromId(int attrId) {
		if (attributes != null) {
			for (int i = 0; i < attributes.length; i++) {
				ZCLAttribute attr = attributes[i];
				if (attr != null && attr.getId() == attrId) {
					return attr;
				}
			}
		}
		return null;
	}
}
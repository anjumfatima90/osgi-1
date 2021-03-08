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

package org.osgi.service.zigbee.types;

import java.io.IOException;

import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * A singleton class that represents the 'General Data 8-bit' data type, as it
 * is defined in the ZigBee Cluster Library specification.
 * 
 * @author $Id$
 * 
 */
public class ZigBeeGeneralData8
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeGeneralData8 instance = new ZigBeeGeneralData8();

	private ZigBeeGeneralData8() {
	}

	/**
	 * Gets a singleton instance of this class.
	 * 
	 * @return the singleton instance.
	 */
	public static ZigBeeGeneralData8 getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "GeneralData8";
	}

	@Override
	public boolean isAnalog() {
		return false;
	}

	@Override
	public Class< ? > getJavaDataType() {
		return Byte.class;
	}

	@Override
	public short getId() {
		return ZigBeeDataTypes.GENERAL_DATA_8;
	}

	@Override
	public void serialize(ZigBeeDataOutput os, Object value) throws IOException {
		ZigBeeDefaultSerializer.serializeDataType(os, ZigBeeDataTypes.GENERAL_DATA_8, value);
	}

	@Override
	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDefaultSerializer.deserializeDataType(is, ZigBeeDataTypes.GENERAL_DATA_8);
	}

}

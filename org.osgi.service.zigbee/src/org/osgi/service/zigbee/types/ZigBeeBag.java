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

import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

/**
 * A singleton class that represents the 'Bag' data type, as it is defined in
 * the ZigBee Cluster Library specification.
 * 
 * @author $Id$
 * 
 */
public class ZigBeeBag
		implements ZCLDataTypeDescription {

	private final static ZigBeeBag instance = new ZigBeeBag();

	private ZigBeeBag() {
	}

	/**
	 * Gets a singleton instance of this class.
	 * 
	 * @return the singleton instance.
	 */
	public static ZigBeeBag getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "Bag";
	}

	@Override
	public boolean isAnalog() {
		return false;
	}

	@Override
	public Class< ? > getJavaDataType() {
		return null;
	}

	@Override
	public short getId() {
		return ZigBeeDataTypes.BAG;
	}

}

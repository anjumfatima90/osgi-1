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

package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * 
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @author $Id$
 */
public class ZigBeeEndpointConf extends ZigBeeEndpointImpl {

	private ZigBeeSimpleDescriptor desc;

	public ZigBeeEndpointConf(short id, ZCLCluster[] inputs, ZCLCluster[] ouputs, ZigBeeSimpleDescriptor desc) {
		super(id, inputs, ouputs, desc);
		this.desc = desc;

		// TODO Auto-generated constructor stub
	}

	public ZigBeeSimpleDescriptor getSimpleDescriptor() {
		// TODO Auto-generated method stub
		return desc;
	}

}
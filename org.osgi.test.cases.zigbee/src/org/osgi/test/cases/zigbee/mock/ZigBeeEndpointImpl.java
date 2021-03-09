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

package org.osgi.test.cases.zigbee.mock;

import java.math.BigInteger;
import java.util.List;

import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * Mocked impl of ZigBeeEndpoint.
 * 
 * @author $Id$
 */
public class ZigBeeEndpointImpl implements ZigBeeEndpoint {

	private short					id;
	private ZigBeeSimpleDescriptor	desc;
	private ZCLCluster[]			inputs;
	private ZCLCluster[]			outputs;

	public ZigBeeEndpointImpl(short id, ZCLCluster[] inputs, ZCLCluster[] ouputs, ZigBeeSimpleDescriptor desc) {
		this.id = id;
		this.inputs = inputs;
		this.outputs = ouputs;
		this.desc = desc;
	}

	public short getId() {
		return this.id;
	}

	public BigInteger getNodeAddress() {
		return BigInteger.valueOf(-1);
	}

	public Promise<ZigBeeSimpleDescriptor> getSimpleDescriptor() {
		return Promises.resolved(desc);
	}

	public ZCLCluster[] getServerClusters() {
		return inputs;
	}

	public ZCLCluster getServerCluster(int serverClusterId) {
		return inputs[serverClusterId];
	}

	public ZCLCluster[] getClientClusters() {
		return outputs;
	}

	public ZCLCluster getClientCluster(int clientClusterId) {
		return outputs[clientClusterId];
	}

	public Promise<Void> bind(String servicePid, int clusterId) {
		return Promises.failed(new UnsupportedOperationException());
	}

	public Promise<Void> unbind(String servicePid, int clusterId) {
		return Promises.failed(new UnsupportedOperationException());
	}

	public void notExported(ZigBeeException e) {

	}

	public Promise<List<String>> getBoundEndPoints(int clusterId) {
		return Promises.failed(new UnsupportedOperationException());
	}

	public String toString() {
		return "" + this.getClass().getName() + "[id: " + id + ", desc: " + desc + ", inputs: " + inputs + ", outputs: "
				+ outputs + "]";
	}

}

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

package org.osgi.impl.service.zigbee.basedriver.descriptors;

import org.osgi.impl.service.zigbee.basedriver.ZigBeeFrequencyBandImpl;
import org.osgi.service.zigbee.descriptors.ZigBeeFrequencyBand;
import org.osgi.service.zigbee.descriptors.ZigBeeMacCapabiliyFlags;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeServerMask;

/**
 * Mocked implementation of a ZigBeeNodeDescriptor. Only the fields that are
 * actually tested by the CT are returning a meaningful value.
 * 
 * @author $Id$
 */
public class ZigBeeNodeDescriptorImpl implements ZigBeeNodeDescriptor {

	private short					logicalType;
	private ZigBeeFrequencyBand		frequencyBand;
	private int						manufacturerCode;
	private int						maxBufferSize;
	private boolean					isComplexDescriptorAvailable;
	private boolean					isUserDescriptorAvailable;
	private ZigBeeMacCapabiliyFlags	flags;

	public ZigBeeNodeDescriptorImpl(short logicalType, short band, int manufCode, int maxBufSize, boolean isComplexAvail, boolean isUserAvail, ZigBeeMacCapabiliyFlags flags) {
		this.logicalType = logicalType;
		this.frequencyBand = new ZigBeeFrequencyBandImpl(band);
		this.manufacturerCode = manufCode;
		this.maxBufferSize = maxBufSize;
		this.isComplexDescriptorAvailable = isComplexAvail;
		this.isUserDescriptorAvailable = isUserAvail;
		this.flags = flags;
	}

	@Override
	public ZigBeeFrequencyBand getFrequencyBand() {
		return frequencyBand;
	}

	@Override
	public int getManufacturerCode() {
		return manufacturerCode;
	}

	@Override
	public int getMaxBufferSize() {
		return maxBufferSize;
	}

	@Override
	public int getMaxIncomingTransferSize() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	@Override
	public int getMaxOutgoingTransferSize() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	@Override
	public ZigBeeServerMask getServerMask() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	@Override
	public boolean isExtendedActiveEndpointListAvailable() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	@Override
	public boolean isExtendedSimpleDescriptorListAvailable() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	@Override
	public boolean isComplexDescriptorAvailable() {
		return isComplexDescriptorAvailable;
	}

	@Override
	public boolean isUserDescriptorAvailable() {
		return isUserDescriptorAvailable;
	}

	@Override
	public short getLogicalType() {
		return this.logicalType;
	}

	@Override
	public ZigBeeMacCapabiliyFlags getMacCapabilityFlags() {
		return flags;
	}
}

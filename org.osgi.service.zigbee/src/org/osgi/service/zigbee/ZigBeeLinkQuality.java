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

package org.osgi.service.zigbee;

/**
 * This interface represents an entry of the NeighborTableList.
 * <p>
 * See Table 2.126 NeighborTableList Record Format in ZIGBEE SPECIFICATION:
 * 1_053474r17ZB_TSC-ZigBee-Specification.pdf.
 * 
 * @noimplement
 * 
 * @author $Id$
 */
public interface ZigBeeLinkQuality {

	/**
	 * Constant value representing a parent relationship between current
	 * {@link ZigBeeNode} and the neighbor.
	 */
	public static final int	PARENT_NEIGHBOR			= 0xf0;

	/**
	 * Constant value representing a child relationship between current
	 * {@link ZigBeeNode} and the neighbor.
	 */
	public static final int	CHILD_NEIGHBOR			= 0xf1;

	/**
	 * Constant value representing a sibling relationship between current
	 * {@link ZigBeeNode} and the neighbor.
	 */
	public static final int	SIBLING_NEIGHBOR		= 0xf2;

	/**
	 * Constant value representing a others relationship between current
	 * {@link ZigBeeNode} and the neighbor.
	 */
	public static final int	OTHERS_NEIGHBOR			= 0xf3;

	/**
	 * Constant value representing a previous child relationship between current
	 * {@link ZigBeeNode} and the neighbor.
	 */
	public static final int	PREVIOUS_CHILD_NEIGHBOR	= 0xf4;

	/**
	 * Returns the Service.PID referring to the {@link ZigBeeNode} representing
	 * a neighbor.
	 * 
	 * @return the Service.PID referring to the {@link ZigBeeNode} representing
	 *         a neighbor.
	 */
	public String getNeighbor();

	/**
	 * Returns the Link Quality Indicator. See the LQI field of the
	 * NeighborTableList Record Format.
	 * 
	 * @return the Link Quality Indicator estimated by {@link ZigBeeNode}
	 *         returning this for communicating with {@link ZigBeeNode}
	 *         identified by the {@link #getNeighbor()}.
	 */
	public int getLQI();

	/**
	 * Returns the depth field of the NeighborTableList Record Format.
	 * 
	 * @return the tree-depth of device.
	 */
	public int getDepth();

	/**
	 * Returns the relationship with the neighbor. See the Relationship field of
	 * the NeighborTableList Record Format.
	 * 
	 * @return the relationship between {@link ZigBeeNode} returning this LQI
	 *         and the {@link ZigBeeNode} identified by the
	 *         {@link #getNeighbor()}.
	 */
	public int getRelationship();

}

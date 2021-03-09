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
 * This interface the reading result of
 * {@link ZCLCluster#readAttributes(ZCLAttributeInfo[])}.
 * 
 * @author $Id$
 */
public interface ZCLReadStatusRecord {

	/**
	 * Returns the value of the related read attribute.
	 * 
	 * @return null in case of failure or invalid data, otherwise the Java
	 *         {@link Object} representing the ZigBee value.
	 */
	public Object getValue();

	/**
	 * Returns the potential failure of the reading operation.
	 * 
	 * @return null in case of success, otherwise the {@link ZigBeeException}
	 *         specifying the failing of the reading.
	 */
	public ZigBeeException getFailure();

	/**
	 * Returns the {@link ZCLAttributeInfo} related to the reading operation.
	 * 
	 * @return the {@link ZCLAttributeInfo} related to the reading operation.
	 */
	public ZCLAttributeInfo getAttributeInfo();

}

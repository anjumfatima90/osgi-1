/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
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

package org.osgi.service.zigbee.descriptors;

import org.osgi.service.zigbee.ZigBeeHandler;

/**
 * This interface represents a User Descriptor as described in the ZigBee
 * Specification The User Descriptor contains information that allows the user
 * to identify the device using user-friendly character string. The use of the
 * User Descriptor is optional.
 * 
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
 */
public interface ZigBeeUserDescriptor {

	/**
	 * @return a user-friendly that identify the device, such as 'Bedroom TV' or
	 *         'Stairs light'
	 */
	String getUserDescriptor();

	/**
	 * As described in "Table 2.137 ZDP Enumerations Description" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a set user desc
	 * request may throw: NOT_SUPPORTED, DEVICE_NOT_FOUND, INV_REQUESTTYPE or
	 * NO_DESCRIPTOR.
	 * 
	 * @param userDescriptor the user descriptor
	 * @param handler the response handler
	 */
	void setUserDescriptor(String userDescriptor, ZigBeeHandler handler);

}
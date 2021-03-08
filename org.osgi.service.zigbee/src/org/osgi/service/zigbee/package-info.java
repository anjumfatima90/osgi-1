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

/**
 * Device Service Specification for ZigBee Technology.
 * 
 * <p>
 * This is the main package of this specification. It defines the interfaces
 * that models the ZigBee concepts, like the ZigBee node and the ZigBee
 * endpoint.
 * 
 * <p>
 * Each time a new ZigBee node is discovered, the driver will register a
 * {@link org.osgi.service.zigbee.ZigBeeNode} service and then one 
 * {@link org.osgi.service.zigbee.ZigBeeEndpoint} service for
 * each ZigBee endpoint discovered on the node.
 * 
 * <p>
 * {@link org.osgi.service.zigbee.ZigBeeEndpoint} interface provides the
 * {@link org.osgi.service.zigbee.ZigBeeEndpoint#getServerCluster(int)} method 
 * to get an  interface reference to a ZCLCluster object.
 * 
 * <p>
 * {@link org.osgi.service.zigbee.ZCLCluster} interface contains methods that directly maps to the ZCL
 * profile-wide commands, like Read Attributes and Write Attributes, and allow
 * the developer to forge its own commands and send them through the invoke()
 * methods.
 * 
 * <p>
 * ZCL Attribute reportings are configured, registering a
 * {@link org.osgi.service.zigbee.ZCLEventListener}, provided that this service 
 * is registered with the right service properties.
 * 
 * <p>
 * In addition to ZCL frames, the current specification allows also to send ZDP
 * frames. Broadcasting and endpoint broadcasting is also supported for ZCL
 * frames.
 * 
 * <p>
 * Bundles wishing to use this package must list the package in the
 * Import-Package header of the bundle's manifest. This package has two types of
 * users: the consumers that use the API in this package and the providers that
 * implement the API in this package.
 * 
 * <p>
 * Example import for consumers using the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.zigbee; version="[1.0,2.0)"}
 * <p>
 * Example import for providers implementing the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.zigbee; version="[1.0,1.1)"}
 * 
 * @author $Id$
 */
@Version("1.0.1")
package org.osgi.service.zigbee;

import org.osgi.annotation.versioning.Version;

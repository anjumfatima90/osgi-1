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

package org.osgi.impl.service.rest.pojos;

import java.util.ArrayList;
import org.osgi.impl.service.rest.PojoReflector.RootNode;

/**
 * List of bundle representation pojos.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
@SuppressWarnings("serial")
@RootNode(name = "bundles")
public final class BundleRepresentationsList extends ArrayList<BundlePojo> {

	public BundleRepresentationsList(final org.osgi.framework.Bundle[] bundles) {
		for (final org.osgi.framework.Bundle bundle : bundles) {
			add(new BundlePojo(bundle));
		}
	}

}

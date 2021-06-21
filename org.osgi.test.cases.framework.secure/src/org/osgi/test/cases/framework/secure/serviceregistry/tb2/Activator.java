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
package org.osgi.test.cases.framework.secure.serviceregistry.tb2;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.framework.secure.junit.serviceregistry.export.ReferenceProvider;
import org.osgi.test.cases.framework.secure.junit.serviceregistry.export.TestService;

public class Activator implements BundleActivator {
	/**
	 * Starts the bundle.
	 */
    public void start(BundleContext context) {
        ServiceReference<ReferenceProvider> providerRef = context.getServiceReference(ReferenceProvider.class);
        assertNotNull(providerRef);
        ReferenceProvider provider = context.getService(providerRef);

        ServiceReference<TestService> reference = provider.getReference();
        assertNotNull(reference);

        try {
            context.getServiceObjects(reference);
            fail("expected SecurityException");
        } catch (SecurityException e) {
            // expected
        }
	}

	/**
	 * Stops the bundle.
	 */
    public void stop(BundleContext context) {
		// empty
	}
}

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
package org.osgi.test.cases.framework.junit.wiring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.wiring.Wiring;

public abstract class WiringTest extends OSGiTestCase {
	protected final List<Bundle> bundles = new ArrayList<Bundle>();
	protected FrameworkWiring frameworkWiring;
	
	public Bundle install(String bundle) {
		Bundle result = null;
		try {
			result = super.install(bundle);
		} catch (BundleException e) {
			fail("failed to install bundle: " + bundle, e);
		} catch (IOException e) {
			fail("failed to install bundle: " + bundle, e);
		}
		if (!bundles.contains(result))
			bundles.add(result);
		return result;
	}
	
	protected void refreshBundles(List<Bundle> b) {
		Wiring.synchronousRefreshBundles(getContext(), b);
	}
	
	protected void setUp() throws Exception {
		bundles.clear();
		frameworkWiring = getContext().getBundle(0).adapt(FrameworkWiring.class);
	}

	protected void tearDown() throws Exception {
		for (Iterator<Bundle> iBundles = bundles.iterator(); iBundles.hasNext();)
			try {
				iBundles.next().uninstall();
			} catch (BundleException e) {
				// nothing
			} catch (IllegalStateException e) {
				// happens if the test uninstalls the bundle itself
			}
		refreshBundles(bundles);
		bundles.clear();
	}
	
	protected void uninstallSilently(Bundle bundle) {
		try {
			bundle.uninstall();
		}
		catch (Exception e) {}
	}
}

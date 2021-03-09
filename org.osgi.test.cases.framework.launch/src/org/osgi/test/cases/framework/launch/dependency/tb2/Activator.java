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
package org.osgi.test.cases.framework.launch.dependency.tb2;

import java.util.Collections;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.framework.launch.dependency.tb1.TB1;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		TB1 tb1 = new TB1();
		System.out.println(
				context.getBundle().toString() + " - Constructed TB1: " + tb1);
		context.registerService(TB1.class, tb1,
				new Hashtable<>(Collections.singletonMap("test", "tb2")));
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// nothing
	}

}

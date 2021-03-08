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
package org.osgi.test.cases.framework.launch.tb2;

import java.lang.reflect.Method;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class Activator implements BundleActivator {

	public void start(BundleContext arg0) throws Exception {
		Method findLibrary = ClassLoader.class.getDeclaredMethod("findLibrary", new Class[] {String.class});
		findLibrary.setAccessible(true);
		String lib = (String) findLibrary.invoke(this.getClass().getClassLoader(), new Object[] {"nativecode"});
		if (lib == null) {
			throw new BundleException("Could not find library");
		}
	}

	public void stop(BundleContext arg0) throws Exception {
	
	}

}

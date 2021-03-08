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
package org.osgi.test.cases.component.tb18;

import java.util.Dictionary;

import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.ComponentEnabler;
import org.osgi.test.cases.component.service.TestObject;

public class EnablerProvider implements ComponentEnabler {
	private ComponentContext	ctxt;

	public void activate(ComponentContext ctxt) {
		this.ctxt = ctxt;
	}

	public void deactivate(ComponentContext ctxt) {
		this.ctxt = null;
	}

	public void enableComponent(String name, boolean flag) {
		if (flag)
			ctxt.enableComponent(name);
		else
			ctxt.disableComponent(name);
	}

	public Dictionary<String,Object> getProperties() {
		return ctxt.getProperties();
	}

	public TestObject getTestObject() {
		return null;
	}

}

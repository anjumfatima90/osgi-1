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
package org.osgi.test.cases.wireadmin.junit;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class FilteredConsumerImpl implements Consumer {
	private List<Object> valuesReceived = new ArrayList<>();

	@Override
	public void updated(Wire wire, Object value) {
		if ("42".equals(wire.getProperties().get(
				"org.osgi.test.wireadmin.property"))) {
			DefaultTestBundleControl.log("consumer received value " + value);
			synchronized (this) {
				valuesReceived.add(value);
			}
			return;
		}
		DefaultTestBundleControl
				.log("filter test consumer received update from unkown wire "
						+ wire + " value is " + value);
	}

	@Override
	public void producersConnected(Wire[] wires) {
		// empty
	}

	synchronized int numberValuesReceived() {
		return valuesReceived.size();
	}

	synchronized List<Object> resetValuesReceived() {
		List<Object> result = valuesReceived;
		valuesReceived = new ArrayList<>();
		return result;
	}
}

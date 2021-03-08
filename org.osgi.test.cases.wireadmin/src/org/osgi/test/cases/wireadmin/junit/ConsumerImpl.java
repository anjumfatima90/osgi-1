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
/*
 * Project Title : Wire Admin Test Case
 * Author        : Neviana Ducheva
 * Company       : ProSyst
 */
package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * A simple consumer implementation for test purposes
 * 
 * @author Neviana Ducheva
 */
public class ConsumerImpl implements Consumer {
	private final WireAdminControl	wac;
	private final String			pid;

	public ConsumerImpl(WireAdminControl wac, String pid) {
		this.wac = wac;
		this.pid = pid;
	}

	@Override
	public void producersConnected(Wire[] wires) {
		if (wac.getProperty("dump.now") != null) {
			DefaultTestBundleControl
					.log("**********************************************************************");
			DefaultTestBundleControl
					.log("producersConnected called and will set counter to "
							+ (wac.synchCounterx + 1));
			DefaultTestBundleControl.log("consumer is: " + pid + " " + hashCode());
			if (wires != null) {
				for (int i = 0; i < wires.length; i++) {
					Wire wire = wires[i];
					DefaultTestBundleControl.log("wire is: " + wire);
					DefaultTestBundleControl.log("connected: " + wire.isConnected());
					DefaultTestBundleControl.log("properties: " + wire.getProperties());
				}
			}
			else {
				DefaultTestBundleControl.log("wires are null");
			}
			DefaultTestBundleControl
					.log("**********************************************************************");
			// new Exception("Stack trace").printStackTrace();
		}
		wac.addInHashtable(pid, wires);
		wac.syncup(pid + " " + wires);
	}

	@Override
	public void updated(Wire wire, Object value) {
		// empty
	}
}

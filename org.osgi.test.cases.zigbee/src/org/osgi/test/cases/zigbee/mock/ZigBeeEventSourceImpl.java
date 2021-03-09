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

package org.osgi.test.cases.zigbee.mock;

import org.osgi.framework.BundleContext;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Mocked test event source.
 * 
 * @author $Id$
 */
public class ZigBeeEventSourceImpl implements Runnable {

	private static final String	TAG	= ZigBeeEventSourceImpl.class.getName();

	private BundleContext		bc;
	private ZigBeeEvent			zigbeeEvent;
	private Thread				thread;
	private ServiceTracker<ZCLEventListener,ZCLEventListener>	serviceTracker;

	public ZigBeeEventSourceImpl(BundleContext bc, ZigBeeEvent zigbeeEvent) {
		this.bc = bc;
		this.zigbeeEvent = zigbeeEvent;
	}

	/**
	 * Launch this testEventSource.
	 */
	public void start() {
		DefaultTestBundleControl.log("start.");
		serviceTracker = new ServiceTracker<>(bc, ZCLEventListener.class, null);
		serviceTracker.open();
		thread = new Thread(this, TAG + " - Whiteboard");
		thread.start();
	}

	/**
	 * Terminate this testEventSource.
	 */
	public void stop() {
		DefaultTestBundleControl.log(TAG + " - stop.");
		serviceTracker.close();
		thread = null;
	}

	public synchronized void run() {
		DefaultTestBundleControl.log(TAG + " - run.");
		Thread current = Thread.currentThread();
		int n = 0;
		while (current == thread) {
			ZCLEventListener[] listeners = serviceTracker
					.getServices(new ZCLEventListener[0]);

			if (listeners != null && listeners.length > 0) {
				if (n >= listeners.length) {
					n = 0;
				}

				ZCLEventListener aZCLEventListener = listeners[n++];
				DefaultTestBundleControl.log(TAG + " - is sending the following event: " + zigbeeEvent);
				aZCLEventListener.notifyEvent(zigbeeEvent);
			}
			try {
				int waitinms = 1000;
				wait(waitinms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

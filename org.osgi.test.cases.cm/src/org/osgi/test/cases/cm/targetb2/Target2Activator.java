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
package org.osgi.test.cases.cm.targetb2;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.test.cases.cm.shared.Constants;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;

/**
 * This bundle can register duplicated ManagedService with the same PID.
 * 
 * @author Ikuo YAMASAKI, NTT Corporation
 * 
 */
public class Target2Activator implements BundleActivator {

	private static final boolean DEBUG = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		log("going to start.");
		final String clazz = ManagedService.class.getName();

		String filter = "(" + Constants.SERVICEPROP_KEY_SYNCID + "=sync2)";
		Synchronizer sync = Util.getService(context,
				Synchronizer.class, filter);
		Dictionary<String,Object> props = new Hashtable<>();

		final String pid1 = Util.createPid("bundlePid1");
		// final String pid2 = Util.createPid("targetB2Pid");
		int count = Integer.parseInt(System.getProperty(
				Constants.SYSTEMPROP_KEY_DUPCOUNT, "1"));
		for (int i = 0; i < count; i++) {
			Object service = new ManagedServiceImpl(sync, i);
			log("Going to register ManagedService " + i + " . pid:\n\t" + pid1);
			props.put(org.osgi.framework.Constants.SERVICE_PID, pid1);
			if (count > 1)
				props.put("DuplicatedID", Integer.valueOf(i));
			try {
				context.registerService(clazz,
						service, props);
				log("Succeed in registering service " + i + ": " + clazz);
			} catch (Exception e) {
				log("Fail to register service " + i + ": " + clazz);
				// e.printStackTrace();
				throw e;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		log("going to stop.");
	}

	static class ManagedServiceImpl implements ManagedService {
		final private Synchronizer sync;
		final private int id;

		public ManagedServiceImpl(Synchronizer sync, int id) {
			this.sync = sync;
			this.id = id;
		}

		@Override
		public void updated(Dictionary<String, ? > props)
				throws ConfigurationException {
			if (props != null) {
				String pid = (String) props
						.get(org.osgi.framework.Constants.SERVICE_PID);
				log("ManagedService[" + id
						+ "]#updated() is called back. pid: " + pid);
			} else {
				log("ManagedService[" + id
						+ "]#updated() is called back. props == null ");
			}
			if (sync != null)
				sync.signal(props);
			else
				log("sync == null.");
		}
	}

	static void log(String msg) {
		if (DEBUG)
			System.out.println("# Register Test> " + msg);
	}
}

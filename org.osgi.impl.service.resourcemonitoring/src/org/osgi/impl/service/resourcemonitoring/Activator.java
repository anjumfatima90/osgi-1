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

package org.osgi.impl.service.resourcemonitoring;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.resourcemonitoring.bundlemanagement.BundleManager;
import org.osgi.impl.service.resourcemonitoring.bundlemanagement.BundleManagerException;
import org.osgi.impl.service.resourcemonitoring.bundlemanagement.BundleManagerImpl;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;

/**
 * Activator
 */
public class Activator implements BundleActivator {

	/**
	 * bundle manager.
	 */
	private BundleManager					bundleManager;

	/**
	 * ResourceMonitoringServiceImpl.
	 */
	private ResourceMonitoringServiceImpl	resourceMonitoringServiceImpl;

	/**
	 * event notifier.
	 */
	private ResourceContextEventNotifier	eventNotifier;

	/**
	 * service registration for {@link ResourceMonitoringService}.
	 */
	private ServiceRegistration<ResourceMonitoringService>	resourceMonitoringServiceSr;

	/**
	 * bundle context.
	 */
	private BundleContext					bundleContext;

	/**
	 * @param context
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Start org.osgi.impl.service.resourcemonitoring.Activator");

		bundleContext = context;

		// threadManager = new ThreadManagerImpl(bundleManager);
		// threadManager.start(context);

		eventNotifier = new ResourceContextEventNotifierImpl();
		eventNotifier.start(context);

		startResourceMonitoringServiceImpl();
	}

	/**
	 * @param context
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		stopResourceMonitoringServiceImpl();

		eventNotifier.stop(context);
		eventNotifier = null;
	}

	/**
	 * Start the resourceMonitoringServiceImpl.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link ResourceMonitoringServiceImpl#start(BundleContext)}
	 * @throws BundleManagerException, see
	 *         {@link BundleManager#start(BundleContext)}
	 */
	private void startResourceMonitoringServiceImpl() throws IllegalArgumentException, BundleManagerException {
		bundleManager = new BundleManagerImpl();
		bundleManager.start(bundleContext);

		resourceMonitoringServiceImpl = new ResourceMonitoringServiceImpl(bundleManager, eventNotifier);
		resourceMonitoringServiceImpl.start(bundleContext);

		resourceMonitoringServiceSr = bundleContext
				.registerService(ResourceMonitoringService.class,
						resourceMonitoringServiceImpl, null);
	}

	/**
	 * Stop the resourceMonitoringServiceImpl.
	 */
	private void stopResourceMonitoringServiceImpl() {
		if (resourceMonitoringServiceSr != null) {
			resourceMonitoringServiceSr.unregister();
			resourceMonitoringServiceSr = null;
		}

		if (resourceMonitoringServiceImpl != null) {
			resourceMonitoringServiceImpl.stop(bundleContext);
			resourceMonitoringServiceImpl = null;

			bundleManager.stop();
			bundleManager = null;
		}
	}

}

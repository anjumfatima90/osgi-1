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

package org.osgi.test.cases.resourcemonitoring.utils;

import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitorException;

/**
 * A fake resource monitor.
 * 
 * @author $Id$
 */
public class FakeResourceMonitor implements ResourceMonitor<Object> {

	private String			resourceType;
	private ResourceContext	resourceContext;

	/**
	 * Constructor
	 */
	public FakeResourceMonitor() {
		// nothing to do.
	}

	/**
	 * Constructor
	 * 
	 * @param pResourceType
	 * @param pResourceContext
	 */
	public FakeResourceMonitor(String pResourceType, ResourceContext pResourceContext) {
		resourceType = pResourceType;
		resourceContext = pResourceContext;
	}

	public ResourceContext getContext() {
		return resourceContext;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void delete() {
		// TODO Auto-generated method stub
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void enable() throws ResourceMonitorException {
		// TODO Auto-generated method stub
	}

	public void disable() throws ResourceMonitorException {
		// TODO Auto-generated method stub
	}

	public Comparable<Object> getUsage() throws ResourceMonitorException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getSamplingPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getMonitoredPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}

}

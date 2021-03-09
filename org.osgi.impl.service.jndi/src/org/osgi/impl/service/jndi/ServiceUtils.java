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


package org.osgi.impl.service.jndi;

import java.util.Arrays;
import java.util.Comparator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class holds utility methods for handling OSGi services/service references
 *
 * 
 * 
 * @author $Id$
 */
class ServiceUtils {
	/* private constructor, static utility class */
	private ServiceUtils() {}

	/**
	 * Utility method to sort an array of ServiceReferences using the service
	 * ranking (if specified).
	 * 
	 * This utility should follow any service ranking rules already defined in
	 * the OSGi specification.
	 * 
	 * @param serviceTracker tracker to use to provide the initial array to sort
	 * @return sorted array of ServiceReferences, or a zero-length array if no
	 *         matching services were found
	 */
	@SuppressWarnings("unchecked")
	static <T> ServiceReference<T>[] sortServiceTrackerReferences(
			ServiceTracker<T,T> serviceTracker) {
		final ServiceReference<T>[] serviceReferences = serviceTracker
				.getServiceReferences();
		if (serviceReferences == null) {
			return new ServiceReference[0];
		}
	
		return sortServiceReferences(serviceReferences);
	}

	
	/**
	 * Utility method to sort an array of ServiceReferences using the OSGi
	 * service ranking.  
	 * 
	 * This utility should follow any service ranking rules already defined in
	 * the OSGi specification.
	 * 
	 * 
	 * @param serviceReferences an array of ServiceReferences to sort
	 * @return the array of ServiceReferences passed into this method, but sorted 
	 *         according to OSGi service ranking.  
	 */
	static <SR extends ServiceReference< ? >> SR[] sortServiceReferences(
			final SR[] serviceReferences) {
		Arrays.sort(serviceReferences, Comparator.reverseOrder());
		return serviceReferences;
	}

	
	/**
	 * Utility method to obtain the list of ServiceReferences that match 
	 * a query using the JNDI "service name" service property.  
	 * 
	 * @param bundleContext the BundleContext to use to obtain services
	 * @param urlParser the parser associated with this request
	 * @return an array of ServiceReferences that match the given request
	 * @throws InvalidSyntaxException on filter parsing error
	 */
	static ServiceReference< ? >[] getServiceReferencesByServiceName(
			BundleContext bundleContext, OSGiURLParser urlParser)
			throws InvalidSyntaxException {
		final String serviceNameFilter = "("
				+ JNDIConstants.JNDI_SERVICENAME + "="
				+ urlParser.getServiceInterface() + ")";
		ServiceReference< ? >[] serviceReferencesByName = 
                bundleContext.getServiceReferences((String) null, serviceNameFilter);
		return serviceReferencesByName;
	}
	
}

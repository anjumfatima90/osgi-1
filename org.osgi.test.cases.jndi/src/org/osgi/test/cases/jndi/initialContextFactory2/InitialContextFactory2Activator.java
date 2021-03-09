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


package org.osgi.test.cases.jndi.initialContextFactory2;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.naming.spi.InitialContextFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.provider.CTInitialContextFactory;

/**
 * @author $Id$
 */
public class InitialContextFactory2Activator implements BundleActivator {

	private ServiceRegistration< ? > sr1;
	
	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting: " + context.getBundle().getLocation());
		Dictionary<String,Object> props = new Hashtable<>();
		
		String[] interfaces ={CTInitialContextFactory.class.getName(), InitialContextFactory.class.getName()};
		
		props.put("osgi.jndi.serviceName", "CTInitialContextFactory"); 
		props.put(Constants.SERVICE_RANKING, Integer.valueOf(3));
		
		Hashtable<String,Object> env = new Hashtable<>();
		
		env.put("test1", "test1");
		
		CTInitialContextFactory ctf = new CTInitialContextFactory(env);
		
		sr1 = context.registerService(interfaces, ctf, props);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping: " + context.getBundle().getLocation());
		sr1.unregister();
	}

}

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


package org.osgi.test.cases.jndi.service2;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.service.ExampleService;
import org.osgi.test.cases.jndi.service.ExampleServiceImpl;

/** 
 * @author $Id$
 */

public class ExampleServiceActivator2 implements BundleActivator {
	
	private ServiceRegistration< ? >	sr1;
	private ServiceRegistration< ? >	sr2;

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting: " + context.getBundle().getLocation());
		Dictionary<String,Object> props1 = new Hashtable<>();
		props1.put("osgi.jndi.service.name", "ExampleService1");
		
		String[] interfaces = {ExampleService.class.getName()};
		ExampleServiceImpl service1 = new ExampleServiceImpl();
		
		sr1 = context.registerService(interfaces, service1, props1);	
		
		Dictionary<String,Object> props2 = new Hashtable<>();
		props2.put("osgi.jndi.service.name", "ExampleService2");
		
		ExampleServiceImpl service2 = new ExampleServiceImpl();
		
		sr2 = context.registerService(interfaces, service2, props2);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping: " + context.getBundle().getLocation());
		sr1.unregister();
		sr2.unregister();
	}

}

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


package org.osgi.test.cases.jndi.exceptionalInitialContextFactoryBuilder1;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.naming.spi.InitialContextFactoryBuilder;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.provider.CTExceptionalInitialContextFactoryBuilder;
import org.osgi.test.cases.jndi.provider.CTInitialContextFactoryBuilder;

/** 
 * @author $Id$
 */
public class ExceptionalInitialContextFactoryBuilder1Activator implements
		BundleActivator {

	private ServiceRegistration< ? >	sr1;
	private ServiceRegistration< ? >	sr2;
	
	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting: " + context.getBundle().getLocation());
		Dictionary<String,Object> props1 = new Hashtable<>();
		Dictionary<String,Object> props2 = new Hashtable<>();
		String[] interfaces ={InitialContextFactoryBuilder.class.getName()};
		

		props1.put(Constants.SERVICE_RANKING, Integer.valueOf(3));
		props2.put(Constants.SERVICE_RANKING, Integer.valueOf(2));
		
		CTExceptionalInitialContextFactoryBuilder ctfb1 = new CTExceptionalInitialContextFactoryBuilder();
		CTInitialContextFactoryBuilder ctfb2 = new CTInitialContextFactoryBuilder();
		
		sr1 = context.registerService(interfaces, ctfb1, props1);
		sr2 = context.registerService(interfaces, ctfb2, props2);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping: " + context.getBundle().getLocation());
		sr1.unregister();
		sr2.unregister();
	}

}

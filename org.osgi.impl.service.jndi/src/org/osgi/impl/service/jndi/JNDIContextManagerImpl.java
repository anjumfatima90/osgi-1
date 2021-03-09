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

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.directory.DirContext;
import javax.naming.spi.InitialContextFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

class JNDIContextManagerImpl implements CloseableJNDIContextManager {

	private static final Logger logger = Logger.getLogger(JNDIContextManagerImpl.class.getName());
	
	private final OSGiInitialContextFactoryBuilder	m_builder;
	
	/* list of Context implementations */
	private final List<Context>						m_listOfContexts	= Collections
			.synchronizedList(new LinkedList<>());

	JNDIContextManagerImpl(Bundle callingBundle, BundleContext implBundleContext) {
		// create a new builder for each client bundle
		// since the JNDI services (factories) should be accessed
		// by the JNDIContextManager service on behalf of the calling bundle
		m_builder = new OSGiInitialContextFactoryBuilder(callingBundle.getBundleContext(), implBundleContext);
	}


	@Override
	public Context newInitialContext() throws NamingException {
		synchronized (m_builder) {
			final Context initialContext = createNewInitialContext(
					new Hashtable<>());
			m_listOfContexts.add(initialContext);
			return initialContext;
		}
	}

	@Override
	public Context newInitialContext(Map<String, ? > environment)
			throws NamingException {
		synchronized (m_builder) {
			final Context initialContext = createNewInitialContext(environment);
			m_listOfContexts.add(initialContext);
			return initialContext;
		}
	}

	@Override
	public DirContext newInitialDirContext() throws NamingException {
		synchronized (m_builder) {
			Context contextToReturn = createNewInitialContext(
					new Hashtable<>());
			if (contextToReturn instanceof DirContext) {
				m_listOfContexts.add(contextToReturn);
				return (DirContext) contextToReturn;
			}
		}
		
		throw new NoInitialContextException("DirContext could not be created.  The matching InitialContextFactory did not create a matching type."); 
	}

	@Override
	public DirContext newInitialDirContext(Map<String, ? > environment)
			throws NamingException {
		synchronized (m_builder) {
			Context context = createNewInitialContext(environment);
			if (context instanceof DirContext) {
				m_listOfContexts.add(context);
				return (DirContext) context;
			}
		}
		
		throw new NoInitialContextException("DirContext could not be created.  The matching InitialContextFactory did not create a matching type.");
	}
	
	/**
	 * Closes all the known context implementations that have 
	 * been provided by this service.  
	 */
	@Override
	public void close() {
		// close known Context implementations
		synchronized (m_listOfContexts) {
			Iterator<Context> iterator = m_listOfContexts.iterator();
			// call close() on all known contexts
			while (iterator.hasNext()) {
				Context context = iterator.next();
				try {
					context.close();
				}
				catch (NamingException e) {
					logger.log(Level.INFO,
							   "NamingException occurred while trying to close an existing JNDI Context",
							    e);
				}
			}
		}
		
		m_listOfContexts.clear();
		
		synchronized (m_builder) {
			// close the Builder implementation
			m_builder.close();
		}
	}

	private Context createNewInitialContext(final Map<String, ? > environment)
			throws NamingException {
		final Hashtable<String,Object> jndiEnvironment = new Hashtable<>(
				environment);
		InitialContextFactory factory = 
			m_builder.createInitialContextFactory(jndiEnvironment);
		return factory.getInitialContext(jndiEnvironment);
	}

}

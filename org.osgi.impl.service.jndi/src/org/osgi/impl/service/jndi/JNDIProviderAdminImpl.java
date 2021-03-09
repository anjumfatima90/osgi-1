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

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.BundleContext;

class JNDIProviderAdminImpl implements CloseableJNDIProviderAdmin {

	private final OSGiInitialContextFactoryBuilder	m_objectFactoryBuilder;

	JNDIProviderAdminImpl(BundleContext bundleContext) {
		m_objectFactoryBuilder = 
			new OSGiInitialContextFactoryBuilder(bundleContext, bundleContext);
	}

	@Override
	public Object getObjectInstance(Object refInfo, Name name, Context context,
			Map<String, ? > environment) throws NamingException {
		synchronized (m_objectFactoryBuilder) {
			Hashtable<String,Object> jndiEnvironment = new Hashtable<>();
			if (environment != null) {
				jndiEnvironment.putAll(environment);
			}
			ObjectFactory objectFactory = 
				m_objectFactoryBuilder.createObjectFactory(refInfo, jndiEnvironment);
			try {
				return objectFactory.getObjectInstance(refInfo, name, context, jndiEnvironment);
			}
			catch (Exception e) {
				NamingException namingException = new NamingException(
						"Error while attempting to resolve reference");
				namingException.initCause(e);
				throw namingException;
			}
		}
	}

	@Override
	public Object getObjectInstance(Object refInfo, Name name, Context context,
			Map<String, ? > environment, Attributes attributes)
			throws NamingException {
		synchronized (m_objectFactoryBuilder) {
			Hashtable<String,Object> jndiEnvironment = new Hashtable<>();
			if (environment != null) {
				jndiEnvironment.putAll(environment);
			}
			DirObjectFactory dirObjectFactory = m_objectFactoryBuilder
					.getDirObjectFactory(refInfo, jndiEnvironment);
			try {
				return dirObjectFactory.getObjectInstance(refInfo, name,
						context, jndiEnvironment, attributes);
			}
			catch (Exception e) {
				NamingException namingException = new NamingException(
						"Error while attempting to resolve reference");
				namingException.initCause(e);
				throw namingException;
			}
		}
	}
	
	@Override
	public void close() {
		synchronized (m_objectFactoryBuilder) {
			m_objectFactoryBuilder.close();
		}
	}
}

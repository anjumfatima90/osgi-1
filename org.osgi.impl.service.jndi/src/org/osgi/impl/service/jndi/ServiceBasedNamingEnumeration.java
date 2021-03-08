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

import java.util.NoSuchElementException;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Abstract NamingEnumeration implementation that contains the 
 * basic logic for an enumeration over a set of NameClassPair objects.  
 * 
 * 
 *
 * 
 * @author $Id$
 */
abstract class ServiceBasedNamingEnumeration<T extends NameClassPair>
		implements NamingEnumeration<T> {

	protected boolean				m_isOpen	= false;
	protected int					m_index	= -1;
	protected final BundleContext	m_bundleContext;
	protected final String			m_interfaceName;
	protected final ServiceReference< ? >[]	m_serviceReferences;
	protected T[]							m_nameClassPairs;

	public ServiceBasedNamingEnumeration(BundleContext bundleContext,
			ServiceReference< ? >[] serviceReferences, String interfaceName) {
		m_bundleContext = bundleContext;
		if(interfaceName == null) {
			m_interfaceName = "";
		} else {
			m_interfaceName = interfaceName;
		}
		
		m_serviceReferences = serviceReferences;
		if(m_serviceReferences.length > 0) {
			m_isOpen = true;
			m_index = 0;
		}
	}

	@Override
	public void close() throws NamingException {
		m_isOpen = false;
	}

	@Override
	public boolean hasMore() throws NamingException {
		checkIsOpen();
		return (isIndexValid());
	}

	@Override
	public T next() throws NamingException {
		checkIsOpen();
		return internalNextElement();
	}

	@Override
	public boolean hasMoreElements() {
		if(!m_isOpen) {
			return false;
		} else {
			return (isIndexValid());
		}
		
	}

	@Override
	public T nextElement() {
		return internalNextElement();
	}

	private void checkIsOpen() throws NamingException {
		if (!m_isOpen) {
			throw new NamingException("Operation cannot complete, since this NamingEnumeration has been closed");
		}
	}

	private boolean isIndexValid() {
		return m_index < m_nameClassPairs.length;
	}

	private T internalNextElement() {
		if(isIndexValid()) {
			return internalNextClassPair();
		} else {
			throw new NoSuchElementException("No additional elements exist in this NamingEnumeration");
		}
	}

	private T internalNextClassPair() {
		return m_nameClassPairs[m_index++];
	}

}

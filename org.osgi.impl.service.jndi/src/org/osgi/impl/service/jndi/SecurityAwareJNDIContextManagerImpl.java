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

import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.directory.DirContext;


/**
 * Decorator for the JNDIContextManager that can handle invoking methods on the 
 * underlying context manager implementation in a doPrivileged() Action. 
 * 
 * @author $Id$
 */
class SecurityAwareJNDIContextManagerImpl implements CloseableJNDIContextManager {

	private static final Logger logger = 
		Logger.getLogger(SecurityAwareJNDIContextManagerImpl.class.getName());
	
	final CloseableJNDIContextManager	m_contextManager;
	
	public SecurityAwareJNDIContextManagerImpl(CloseableJNDIContextManager contextManager) {
		m_contextManager = contextManager;
	}
	
	
	@Override
	public Context newInitialContext() throws NamingException {
		return invokePrivilegedAction(new NewInitialContextAction());
	}
	

	@Override
	public Context newInitialContext(Map<String, ? > environment)
			throws NamingException {
		return invokePrivilegedAction(new NewInitialContextWithEnvironmentAction(environment));
	}

	
	@Override
	public DirContext newInitialDirContext() throws NamingException {
		return invokePrivilegedAction(new NewInitialDirContextAction());
	}

	
	@Override
	public DirContext newInitialDirContext(Map<String, ? > environment)
			throws NamingException {
		return invokePrivilegedAction(new NewInitialDirContextWithEnvironmentAction(environment));
	}
	
	@Override
	public void close() {
		invokePrivilegedActionWithoutReturn(new CloseJNDIContextManagerAction());
	}
	

	private static <O> O invokePrivilegedAction(
			final PrivilegedExceptionAction<O> action) throws NamingException {
		try {
			return SecurityUtils.invokePrivilegedAction(action);
		} catch (Exception exception) {
			if(exception instanceof NamingException) {
				throw (NamingException)exception;
			} else {
				logExceptionFromPrivilegedAction(exception);
				
				NamingException namingException = 
					new NoInitialContextException("Error occurred during a privileged operation");
				namingException.setRootCause(exception);
				throw namingException;
			}
		}
	}
	
	private static void invokePrivilegedActionWithoutReturn(
			final PrivilegedExceptionAction< ? > action) {
		try {
			SecurityUtils.invokePrivilegedActionNoReturn(action);
		}
		catch (Exception exception) {
			logExceptionFromPrivilegedAction(exception);
		}
	}
	
	
	private static void logExceptionFromPrivilegedAction(Exception e) {
		logger.log(Level.FINE, 
				   "Exception occurred while invoking a PrivilegedAction",
				   e);
	}
	
	
	
	// actions for each of the operations supported by the JNDIContextManager service
	private class NewInitialContextAction
			implements PrivilegedExceptionAction<Context> {
		NewInitialContextAction() {
			super();
		}

		@Override
		public Context run() throws Exception {
			return m_contextManager.newInitialContext();
		}
	}
	
	private class NewInitialContextWithEnvironmentAction
			implements PrivilegedExceptionAction<Context> {

		private final Map<String, ? > m_environment;
		
		public NewInitialContextWithEnvironmentAction(
				Map<String, ? > environment) {
			m_environment = environment;
		}
		
		@Override
		public Context run() throws Exception {
			return m_contextManager.newInitialContext(m_environment);
		}
	}
	
	
	private class NewInitialDirContextAction
			implements PrivilegedExceptionAction<DirContext> {
		NewInitialDirContextAction() {
			super();
		}

		@Override
		public DirContext run() throws Exception {
			return m_contextManager.newInitialDirContext();
		}
	}
	
	private class NewInitialDirContextWithEnvironmentAction
			implements PrivilegedExceptionAction<DirContext> {
		private final Map<String, ? > m_environment;
		
		public NewInitialDirContextWithEnvironmentAction(
				Map<String, ? > environment) {
			m_environment = environment;
		}
		
		@Override
		public DirContext run() throws Exception {
			return m_contextManager.newInitialDirContext(m_environment);
		}
	}
	
	private class CloseJNDIContextManagerAction
			implements PrivilegedExceptionAction<Void> {
		CloseJNDIContextManagerAction() {
			super();
		}

		@Override
		public Void run() throws Exception {
			m_contextManager.close();
			return null;
		}
		
	}
}

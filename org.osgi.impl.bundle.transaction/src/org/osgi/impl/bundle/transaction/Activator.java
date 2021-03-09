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
package org.osgi.impl.bundle.transaction;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.apache.geronimo.transaction.GeronimoUserTransaction;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final String SERVICE_DESCRIPTION = "Transactions in OSGi";
	private static final int TRANSACTION_TIMEOUT_SECONDS = 30;

	private GeronimoTransactionManager manager;
	private BundleContext context;

	/**
	 * Start
	 */
	@Override
	public void start(@SuppressWarnings("hiding") BundleContext context)
			throws Exception {
		this.manager = new GeronimoTransactionManager(TRANSACTION_TIMEOUT_SECONDS);
		this.context = context;

		registerTransactionManager();
		registerUserTransaction();
	}

	/**
	 * Stop
	 */
	@Override
	public void stop(@SuppressWarnings("hiding") BundleContext context)
			throws Exception {
		// nothing to do
	}
	
	private void registerTransactionManager() {
        Dictionary<String, Object> p = new Hashtable<String, Object>();
		p.put("service.description", SERVICE_DESCRIPTION);
		String[] ifaces = { TransactionManager.class.getName(),
				TransactionSynchronizationRegistry.class.getName() };
		context.registerService(ifaces, manager, p);
	}

	private void registerUserTransaction() {
        Dictionary<String, Object> p = new Hashtable<String, Object>();
		p.put("service.description", SERVICE_DESCRIPTION);
		GeronimoUserTransaction userTx = new GeronimoUserTransaction(manager);
		String iface = UserTransaction.class.getName();
		context.registerService(iface, userTx, p);
	}


}

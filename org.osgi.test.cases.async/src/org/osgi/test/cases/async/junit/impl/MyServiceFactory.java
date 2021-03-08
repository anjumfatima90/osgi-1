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
package org.osgi.test.cases.async.junit.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.async.services.MyService;

import junit.framework.TestCase;

public class MyServiceFactory implements ServiceFactory<MyService> {
	private final CountDownLatch latch = new CountDownLatch(2);
	private final AtomicBoolean returnNullFromFactory = new AtomicBoolean(false);
	private final MyServiceImpl myService;

	public MyServiceFactory() {
		this(null);
	}

	public MyServiceFactory(MyServiceImpl myService) {
		this.myService = myService == null ? new MyServiceImpl() : myService;
	}

	public MyService getService(Bundle bundle,
			ServiceRegistration<MyService> registration) {
		if (returnNullFromFactory.get()) {
			return null;
		}
		if (latch.getCount() == 2) {
			latch.countDown();
		}
		return myService;
	}

	public void ungetService(Bundle bundle,
			ServiceRegistration<MyService> registration, MyService service) {
		if (latch.getCount() == 1) {
			latch.countDown();
		}
	}
	public void awaitUngetService() throws InterruptedException {
		latch.await(5, TimeUnit.SECONDS);
		if (latch.getCount() != 0) {
			TestCase.fail("ServiceFactory.ungetService never called: "
					+ latch.getCount());
		}
	}

	public void setReturnNull(boolean flag) {
		returnNullFromFactory.set(flag);
	}

	public MyServiceImpl getMySerivceImpl() {
		return myService;
	}
}

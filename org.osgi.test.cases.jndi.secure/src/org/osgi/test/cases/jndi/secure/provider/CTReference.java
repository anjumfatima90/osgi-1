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


package org.osgi.test.cases.jndi.secure.provider;

import javax.naming.RefAddr;
import javax.naming.Reference;

/**
 * @author $Id$
 */
public class CTReference extends Reference {

	private static final long	serialVersionUID	= 1L;

	public CTReference(String className) {
		super(className, null, null);
	}
	
	public CTReference(String className, RefAddr addr) {
		super(className, addr);
	}
	
	public CTReference(String className, String factoryName) {
		super(className, factoryName, null);
	}
	public CTReference(String className, RefAddr addr, String factory, String factoryLocation) {
		super(className, addr, factory, factoryLocation);
	}
	
	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getFactoryClassName() {
		return classFactory; 
	}	
	
	
}

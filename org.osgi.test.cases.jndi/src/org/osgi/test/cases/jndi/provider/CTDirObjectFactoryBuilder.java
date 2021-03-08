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


package org.osgi.test.cases.jndi.provider;

import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

/**
 * @author $Id$
 */
public class CTDirObjectFactoryBuilder implements ObjectFactoryBuilder {
	
	@SuppressWarnings("unchecked")
	@Override
	public ObjectFactory createObjectFactory(Object obj,
			Hashtable< ? , ? > environment)
			throws NamingException {
		if (obj instanceof CTReference || obj instanceof String) {
			if (environment != null) {
				return new CTDirObjectFactory(
						(Hashtable<String,Object>) environment);
			} else {
				return new CTDirObjectFactory();
			}
		} else { 
			return null;
		}
	}
}

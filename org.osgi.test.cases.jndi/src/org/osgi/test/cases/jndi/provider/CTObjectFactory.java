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

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;

/**
 * @author $Id$
 */
public class CTObjectFactory implements javax.naming.spi.ObjectFactory {
	
	private Hashtable<String,Object> env = new Hashtable<>();
	
	public CTObjectFactory() {
		
	}
	
	public CTObjectFactory(Hashtable<String,Object> env) {
		this.env = env;
	}

	@Override
	public Object getObjectInstance(Object obj, Name name, Context context,
			Hashtable< ? , ? > table) throws Exception {
			
		if (obj instanceof CTReference) {
			RefAddr value = ((CTReference) obj).get("value"); 
			if (value != null) {
				return new CTTestObject(value.getContent().toString());
			} 			
			return new CTTestObject();
		} else if (obj instanceof String) {
			if (((String)obj).equals(CTTestObject.class.getName())) {
				return new CTTestObject();
			}
		}
		return null;
	}
	
	public Hashtable< ? , ? > getEnvironment() {
		return env;
	}

}

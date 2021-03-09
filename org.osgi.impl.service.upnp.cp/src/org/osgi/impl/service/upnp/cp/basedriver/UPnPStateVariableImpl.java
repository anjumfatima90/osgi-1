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
package org.osgi.impl.service.upnp.cp.basedriver;

import java.util.Enumeration;
import java.util.Vector;

import org.osgi.impl.service.upnp.cp.description.StateVariable;
import org.osgi.impl.service.upnp.cp.util.SamsungUPnPStateVariable;

public class UPnPStateVariableImpl implements SamsungUPnPStateVariable {
	private StateVariable	var;
	private String			name;
	private String			type;
	private String[]		vals;
	private String			newValue;

	UPnPStateVariableImpl(StateVariable var) {
		this.var = var;
		name = var.getName();
		type = var.getDataType();
		newValue = var.getDefaultValue();
		Vector<String> values = var.getAllowedValueList();
		if (null != values) {
			@SuppressWarnings("hiding")
			String[] vals = new String[values.size()];
			int i = 0;
			for (Enumeration<String> e = values.elements(); e
					.hasMoreElements(); i++) {
				vals[i] = e.nextElement();
			}
			this.vals = vals;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	// This method returns the java data type of the state variable.
	@Override
	public Class< ? > getJavaDataType() {
		if ((type.equals("ui1")) || (type.equals("ui2")) || (type.equals("i1"))
				|| (type.equals("i2")) || (type.equals("i4"))
				|| (type.equals("int"))) {
			Integer in = Integer.valueOf("1");
			return in.getClass();
		}
		else
			if (type.equals("ui2")) {
				Long ll = Long.valueOf("1");
				return ll.getClass();
			}
			else
				if ((type.equals("r4")) || (type.equals("float"))) {
					Float ff = Float.valueOf("1");
					return ff.getClass();
				}
				else
					if ((type.equals("r8")) || (type.equals("number"))
							|| (type.equals("fixed.14.4"))) {
						Double dd = Double.valueOf("1");
						return dd.getClass();
					}
					else
						if ((type.equals("string")) || (type.equals("uri"))
								|| (type.equals("uuid"))) {
							String ss = new String("aa");
							return ss.getClass();
						}
						else
							if (type.equals("char")) {
								Character cc = Character.valueOf('a');
								return cc.getClass();
							}
							else
								if (type.equals("boolean")) {
									Boolean bb = Boolean.valueOf(true);
									return bb.getClass();
								}
		return null;
	}

	// This method returns the UPnPData type of the state variable.
	@Override
	public String getUPnPDataType() {
		return type;
	}

	// This method returns the default value of the state variable.
	@Override
	public Object getDefaultValue() {
		return var.getDefaultValue();
	}

	// This method returns the allowed values of the state variable.
	@Override
	public String[] getAllowedValues() {
		return vals;
	}

	// This method returns the minimum value of the state variable.
	@Override
	public Number getMinimum() {
		return var.getMinimum();
	}

	//	
	@Override
	public Number getMaximum() {
		return var.getMaximum();
	}

	// This method returns the step value of the state variable.
	@Override
	public Number getStep() {
		return var.getStep();
	}

	// This method returns the value of the state variable's sendEvent
	// attribute.
	@Override
	public boolean sendsEvents() {
		if (var.getSendEvents().equals("yes")) {
			return true;
		}
		return false;
	}

	// This method sets the state variable value.
	@Override
	public void setChangedValue(String value) {
		newValue = value;
	}

	// This method returns the state variable value.
	@Override
	public String getChangedValue() {
		return newValue;
	}
}

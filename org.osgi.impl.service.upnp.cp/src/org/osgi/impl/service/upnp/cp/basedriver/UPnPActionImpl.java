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

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.impl.service.upnp.cp.description.Action;
import org.osgi.impl.service.upnp.cp.description.ArgumentList;
import org.osgi.impl.service.upnp.cp.util.Converter;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPStateVariable;

public class UPnPActionImpl implements UPnPAction {
	private UPnPBaseDriver	basedriver;
	private UPnPServiceImpl	upnpservice;
	private String			name;
	private String[]		inputargs;
	private String[]		outputargs;
	private String			host;
	private String			controlurl;
	private String			servicetype;
	private ArgumentList[]	args;

	// This constructor creates the UPnPAction object with the given parameters.
	UPnPActionImpl(String name, String curl, String stype, String host,
			UPnPBaseDriver basedriver) {
		this.name = name;
		controlurl = curl;
		servicetype = stype;
		this.host = host;
		this.basedriver = basedriver;
	}

	// This constructor creates the UPnPAction object with the given parameters.
	UPnPActionImpl(Action action, UPnPBaseDriver basedriver,
			UPnPServiceImpl upnpservice) {
		this.basedriver = basedriver;
		this.upnpservice = upnpservice;
		this.host = upnpservice.host;
		name = action.getName();
		args = action.getArguments();
		if (args != null) {
			int i = 0;
			int j = 0;
			for (int k = 0; k < args.length; k++) {
				if (args[k].getDirection().equals("in")) {
					i++;
				}
				else
					if (args[k].getDirection().equals("out")) {
						j++;
					}
			}
			inputargs = new String[i];
			outputargs = new String[j];
			int m = 0;
			int n = 0;
			for (int l = 0; l < args.length; l++) {
				if (args[l].getDirection().equals("in")) {
					inputargs[m] = args[l].getName();
					m++;
				}
				else
					if (args[l].getDirection().equals("out")) {
						outputargs[n] = args[l].getName();
						n++;
					}
			}
		}
		else
			if (args == null) {
				System.out.println("BASEDRIVER: NO SERVICE INFORMATION");
			}
		controlurl = upnpservice.serviceinfo.getControlURL();
		servicetype = upnpservice.getType();
	}

	// This method returns the name of the action.
	@Override
	public String getName() {
		return name;
	}

	// This method returns the argument name of the action.
	@Override
	public String getReturnArgumentName() {
		if (outputargs.length > 1) {
			return outputargs[0];
		}
		return null;
	}

	// This method returns the input argument names of the action.
	@Override
	public String[] getInputArgumentNames() {
		return inputargs;
	}

	// This method returns the output argument names of the action.
	@Override
	public String[] getOutputArgumentNames() {
		return outputargs;
	}

	// This method returns the UPnPStateVariable of the action.
	@Override
	public UPnPStateVariable getStateVariable(String argName) {
		for (int i = 0; i < args.length; i++) {
			String argname = args[i].getName();
			if (argname.equals(argName)) {
				String stName = args[i].getRelatedStateVariable();
				return upnpservice.getStateVariable(stName);
			}
		}
		return null;
	}

	private Hashtable<String,Object> getReturnSVDataType() {
		Hashtable<String,Object> stat_dt = new Hashtable<>();
		for (int i = 0; i < outputargs.length; i++) {
			for (int j = 0; j < args.length; j++) {
				String argname = args[j].getName();
				if (outputargs[i].equals(argname)) {
					String stName = args[j].getRelatedStateVariable();
					UPnPStateVariable USV = upnpservice
							.getStateVariable(stName);
					stat_dt.put(argname, USV.getUPnPDataType());
				}
			}
		}
		return stat_dt;
	}

	private Hashtable<String,Object> getInputSVDataType() {
		Hashtable<String,Object> Input_dict = new Hashtable<>();
		for (int i = 0; i < inputargs.length; i++) {
			for (int j = 0; j < args.length; j++) {
				String argname = args[j].getName();
				if (inputargs[i].equals(argname)) {
					String stName = args[j].getRelatedStateVariable();
					UPnPStateVariable USV = upnpservice
							.getStateVariable(stName);
					Input_dict.put(argname, USV.getUPnPDataType());
				}
			}
		}
		return Input_dict;
	}

	// This method invokes the action.
	@Override
	public Dictionary<String,Object> invoke(Dictionary<String,Object> dict)
			throws Exception {
		UPnPBaseDriver baseDriverLocal = this.basedriver;
		if (null == baseDriverLocal) {
			throw new IllegalStateException(
					"UPnP device has been removed from the network.");
		}
		Converter convert = new Converter();
		Hashtable<String,Object> stat_dt = getReturnSVDataType();
		Hashtable<String,Object> Input_dict = getInputSVDataType();
		Dictionary<String,Object> inparms = convert
				.java2upnp(dict, Input_dict);
		Dictionary<String,Object> outparms = baseDriverLocal.control
				.sendControlRequest(controlurl,
				host, servicetype, name, inparms, true);
		return convert.upnp2java(outparms, stat_dt);
	}
	
	/* package-private */void release() {
		this.basedriver = null;
	}
}

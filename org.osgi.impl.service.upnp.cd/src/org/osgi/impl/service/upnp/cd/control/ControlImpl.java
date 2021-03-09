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
package org.osgi.impl.service.upnp.cd.control;

import java.io.DataOutputStream;
import java.util.Dictionary;

import org.osgi.impl.service.upnp.cd.ssdp.UPnPExporter;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPService;

// This class contains the implementation of Control interface defined in the api package.
public class ControlImpl {
	// This is a mapping of the control Url and the Action objects
	SOAPMaker				maker;
	@SuppressWarnings("unused")
	private SOAPErrorCodes	errorCodes;
	private SOAPParser		parser;

	public ControlImpl() {
		maker = new SOAPMaker();
		errorCodes = new SOAPErrorCodes();
		parser = new SOAPParser();
	}

	// This method will be called by the HttpServer to send the control/query
	// request,
	// which is coming from the CP side.
	public synchronized void sendHttpRequest(Dictionary<String,String> headers,
			String xml,
			DataOutputStream dos) {
		@SuppressWarnings("unused")
		boolean invalidReq = false;
		boolean man = false;
		String result = null;
		try {
			String controlUrl = headers.get("post");
			if (controlUrl.startsWith("/")) {
				controlUrl = controlUrl.substring(1);
			}
			if (controlUrl == null) {
				controlUrl = headers.get("m-post");
				man = true;
			}
			if (controlUrl == null) {
				writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
				return;
			}
			if (headers.get("host") == null) {
				writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
				return;
			}
			String cType = headers.get("content-type");
			if (cType == null) {
				writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
				return;
			}
			int indexSemic = cType.indexOf(";");
			if (indexSemic == -1) {
				writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
				return;
			}
			if (!(cType.substring(0, indexSemic)).equals("text/xml")) {
				invalidReq = true;
				writedata(SOAPConstants.http + SOAPConstants.ERROR_415, dos);
				return;
			}
			String soapAction;
			if (!man) {
				soapAction = headers.get("soapaction");
			}
			else {
				String manH = headers.get("man");
				int indexSemi = manH.indexOf(";");
				if (indexSemi == -1) {
					invalidReq = true;
					writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
					return;
				}
				if (!manH.substring(0, indexSemi - 1).equals(
						SOAPConstants.httpEnv)) {
					invalidReq = true;
					writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
					return;
				}
				String ns = manH.substring(indexSemi + 5);
				soapAction = headers.get(ns + "-soapaction");
			}
			if (!soapAction.startsWith("\"") || !soapAction.endsWith("\"")) {
				writedata(SOAPConstants.http + SOAPConstants.ERROR_400, dos);
				return;
			}
			UPnPService upnpservice = UPnPExporter.getControlEntry(controlUrl);
			if (upnpservice == null) {
				result = maker.createResponseError("404");
				writedata(result, dos);
				return;
			}
			if (soapAction
					.equals("\"urn:schemas-upnp-org:control-1-0#QueryStateVariable\"")) {
				return;
			}
			else {
				ParsedRequest req = parser.controlReqParse(xml);
				String actionName = req.getActionName();
				Dictionary<String,Object> params = req.getArguments(upnpservice,
						actionName);
				UPnPAction upnpaction = upnpservice.getAction(actionName);
				if (upnpaction == null) {
					result = maker.createResponseError("404");
					writedata(result, dos);
					return;
				}
				else {
					new InvokeDeviceCallback(upnpaction, params, maker, dos,
							req).start();
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			result = maker.createResponseError("501");
			writedata(result, dos);
			return;
		}
	}

	public class InvokeDeviceCallback extends Thread {
		UPnPAction			action;
		Dictionary<String,Object>	parameters;
		SOAPMaker			soapmaker;
		DataOutputStream	dos;
		ParsedRequest		request;

		public InvokeDeviceCallback(UPnPAction action,
				Dictionary<String,Object> params,
				SOAPMaker maker, DataOutputStream dos, ParsedRequest request) {
			this.action = action;
			this.parameters = params;
			this.soapmaker = maker;
			this.dos = dos;
			this.request = request;
		}

		@Override
		public void run() {
			try {
				Dictionary<String,Object> outParams = action.invoke(parameters);
				Dictionary<String,Object> ConvertedParams = request
						.getParams(action,
						outParams);
				String result = maker.createControlResponseOK(action.getName(),
						request.getServiceType(), ConvertedParams);
				dos.writeBytes(result);
				dos.close();
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
	}

	public void writedata(String result, DataOutputStream dos) {
		try {
			dos.writeBytes(result);
			dos.flush();
			dos.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

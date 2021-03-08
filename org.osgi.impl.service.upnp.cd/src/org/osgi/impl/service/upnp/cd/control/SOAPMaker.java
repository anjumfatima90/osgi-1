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

import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;

// This class creates a SOAP message to send to the contol device via HTTP.
public class SOAPMaker extends SOAPConstants {
	// This is the object whose lock will be used to synchronize the createQuery
	// method.
	private Object	queryLock	= new Object();
	// This is the object whose lock will be used to synchronize the
	// createControl method.
	private Object	controlLock	= new Object();

	// Default contructor
	public SOAPMaker() {
	}

	// This method will take the parameters and then create a SOAP message for
	// the
	// Control response when the control request is successful.
	public String createControlResponseOK(String actionName,
			String serviceType, java.util.Dictionary<String,Object> arguments) {
		synchronized (controlLock) {
			StringBuffer soapBuf = new StringBuffer();
			String soap;
			soapBuf.append(startEnvelope);
			soapBuf.append(rn);
			soapBuf.append(startBody);
			soapBuf.append(rn);
			soapBuf.append("<u:" + actionName + response + " xmlns:u=" + saUrn
					+ "urn:schemas-upnp-org:service:" + serviceType + "\">");
			soapBuf.append(rn);
			if (arguments != null) {
				for (Enumeration<String> e = arguments.keys(); e
						.hasMoreElements();) {
					String argName = e.nextElement();
					soapBuf.append("<" + argName + ">"
							+ (String) arguments.get(argName) + "</" + argName
							+ ">");
					soapBuf.append(rn);
				}
			}
			soapBuf.append("</u:" + actionName + response + ">");
			soapBuf.append(rn);
			soapBuf.append(stopBody);
			soapBuf.append(rn);
			soapBuf.append(stopEnvelope);
			soap = soapBuf.toString();
			StringBuffer controlBuf = new StringBuffer();
			controlBuf.append(http + OK_200);
			controlBuf.append(rn);
			controlBuf.append(contentLength);
			controlBuf.append(soap.length());
			controlBuf.append(rn);
			controlBuf.append(contentType);
			controlBuf.append(rn);
			controlBuf.append(date);
			controlBuf.append(DateFormat.getDateTimeInstance().format(
					new Date(System.currentTimeMillis())));
			controlBuf.append(rn);
			controlBuf.append(ext);
			controlBuf.append(rn);
			controlBuf.append(server);
			controlBuf.append(osNameVersion);
			controlBuf.append(upnpVersion);
			controlBuf.append(productVersion);
			controlBuf.append(rn);
			controlBuf.append(rn);
			controlBuf.append(soap);
			return controlBuf.toString();
		}
	}

	// This method will take the error code and then create a SOAP message for
	// the
	// Control response when the control request encounters some error.
	public String createResponseError(String errorCode) {
		synchronized (controlLock) {
			StringBuffer soapBuf = new StringBuffer();
			String soap;
			soapBuf.append(startEnvelope);
			soapBuf.append(rn);
			soapBuf.append(startBody);
			soapBuf.append(rn);
			soapBuf.append(startFault);
			soapBuf.append(rn);
			soapBuf.append(faultCode);
			soapBuf.append(rn);
			soapBuf.append(faultString);
			soapBuf.append(rn);
			soapBuf.append(startDetail);
			soapBuf.append(rn);
			soapBuf.append("<" + upnpErr + " " + upnpXmlns + ">");
			soapBuf.append(rn);
			soapBuf.append(startErrCode);
			soapBuf.append(errorCode);
			soapBuf.append(stopErrCode);
			soapBuf.append(rn);
			soapBuf.append(startErrDesc);
			soapBuf.append(SOAPErrorCodes.getErrorDesc(errorCode));
			soapBuf.append(stopErrDesc);
			soapBuf.append(rn);
			soapBuf.append("</" + upnpErr + ">");
			soapBuf.append(rn);
			soapBuf.append(stopDetail);
			soapBuf.append(rn);
			soapBuf.append(stopFault);
			soapBuf.append(rn);
			soapBuf.append(stopBody);
			soapBuf.append(rn);
			soapBuf.append(stopEnvelope);
			soap = soapBuf.toString();
			StringBuffer controlBuf = new StringBuffer();
			controlBuf.append(http + ERROR_500);
			controlBuf.append(rn);
			controlBuf.append(contentLength);
			controlBuf.append(soap.length());
			controlBuf.append(rn);
			controlBuf.append(contentType);
			controlBuf.append(rn);
			controlBuf.append(date);
			controlBuf.append(DateFormat.getDateTimeInstance().format(
					new Date(System.currentTimeMillis())));
			controlBuf.append(rn);
			controlBuf.append(ext);
			controlBuf.append(rn);
			controlBuf.append(server);
			controlBuf.append(osNameVersion);
			controlBuf.append(upnpVersion);
			controlBuf.append(productVersion);
			controlBuf.append(rn);
			controlBuf.append(rn);
			controlBuf.append(soap);
			return controlBuf.toString();
		}
	}

	// This method will take the parameters and then create a SOAP message for
	// the
	// Query response when the control request is successful.
	public String createQueryResponse(String value) {
		synchronized (queryLock) {
			StringBuffer soapBuf = new StringBuffer();
			String soap;
			soapBuf.append(startEnvelope);
			soapBuf.append(rn);
			soapBuf.append(startBody);
			soapBuf.append(rn);
			soapBuf.append("<" + queryStateVar + response
					+ " xmlns:u=\"urn:schemas-upnp-org:control-1-0\">");
			soapBuf.append(rn);
			soapBuf.append("<" + result + ">" + value + "</" + result + ">");
			soapBuf.append(rn);
			soapBuf.append("</" + queryStateVar + response + ">");
			soapBuf.append(rn);
			soapBuf.append(stopBody);
			soapBuf.append(rn);
			soapBuf.append(stopEnvelope);
			soap = soapBuf.toString();
			StringBuffer controlBuf = new StringBuffer();
			controlBuf.append(http + OK_200);
			controlBuf.append(rn);
			controlBuf.append(contentLength);
			controlBuf.append(soap.length());
			controlBuf.append(rn);
			controlBuf.append(contentType);
			controlBuf.append(rn);
			controlBuf.append(date);
			controlBuf.append(DateFormat.getDateTimeInstance().format(
					new Date(System.currentTimeMillis())));
			controlBuf.append(rn);
			controlBuf.append(ext);
			controlBuf.append(rn);
			controlBuf.append(server);
			controlBuf.append(osNameVersion);
			controlBuf.append(upnpVersion);
			controlBuf.append(productVersion);
			controlBuf.append(rn);
			controlBuf.append(rn);
			controlBuf.append(soap);
			return controlBuf.toString();
		}
	}
}

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
package org.osgi.impl.service.upnp.cp.util;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class Converter {
	public Dictionary<String,Object> java2upnp(
			Dictionary<String,Object> inparms,
			Hashtable<String,Object> Input_dict)
			throws Exception {
		for (Enumeration<String> e = inparms.keys(); e.hasMoreElements();) {
			String key = e.nextElement();
			Object value = inparms.get(key);
			String type = (String) Input_dict.get(key);
			inparms.remove(key);
			if (type.equals(SoapTypeConstants.SOAP_TYPE_UI1)
					|| type.equals(SoapTypeConstants.SOAP_TYPE_UI2))
				inparms.put(key, SoapTypeConverter.UI1_Converter
						.convertToString(value));
			else
				if (type.equals(SoapTypeConstants.SOAP_TYPE_INT))
					inparms.put(key, SoapTypeConverter.INT_Converter
							.convertToString(value));
				else
					if (type.equals(SoapTypeConstants.SOAP_TYPE_I1)
							|| type.equals(SoapTypeConstants.SOAP_TYPE_I2)
							|| type.equals(SoapTypeConstants.SOAP_TYPE_I4))
						inparms.put(key, SoapTypeConverter.UI1_Converter
								.convertToString(value));
					else
						if (type.equals(SoapTypeConstants.SOAP_TYPE_UI4))
							inparms.put(key, SoapTypeConverter.UI4_Converter
									.convertToString(value));
						else
							if (type.equals(SoapTypeConstants.SOAP_TYPE_R4)
									|| type
											.equals(SoapTypeConstants.SOAP_TYPE_FLOAT))
								inparms.put(key, SoapTypeConverter.R4_Converter
										.convertToString(value));
							else
								if (type.equals(SoapTypeConstants.SOAP_TYPE_R8))
									inparms.put(key,
											SoapTypeConverter.R8_Converter
													.convertToString(value));
								else
									if (type
											.equals(SoapTypeConstants.SOAP_TYPE_NUMBER))
										inparms
												.put(
														key,
														SoapTypeConverter.NUMBER_Converter
																.convertToString(value));
									else
										if (type
												.equals(SoapTypeConstants.SOAP_TYPE_FIXED_14_4))
											inparms
													.put(
															key,
															SoapTypeConverter.FIXED_14_4_Converter
																	.convertToString(value));
										else
											if (type
													.equals(SoapTypeConstants.SOAP_TYPE_CHAR))
												inparms
														.put(
																key,
																SoapTypeConverter.CHAR_Converter
																		.convertToString(value));
											else
												if (type
														.equals(SoapTypeConstants.SOAP_TYPE_STRING)
														|| type
																.equals(SoapTypeConstants.SOAP_TYPE_URI)
														|| type
																.equals(SoapTypeConstants.SOAP_TYPE_UUID))
													inparms
															.put(
																	key,
																	SoapTypeConverter.STRING_Converter
																			.convertToString(value));
												else
													if (type
															.equals(SoapTypeConstants.SOAP_TYPE_DATE))
														inparms
																.put(
																		key,
																		SoapTypeConverter.DATE_Converter
																				.convertToString(value));
													else
														if (type
																.equals(SoapTypeConstants.SOAP_TYPE_BOOLEAN))
															inparms
																	.put(
																			key,
																			SoapTypeConverter.BOOLEAN_Converter
																					.convertToString(value));
														else
															if (type
																	.equals(SoapTypeConstants.SOAP_TYPE_BIN_HEX)
																	|| type
																			.equals(SoapTypeConstants.SOAP_TYPE_BIN_BASE64))
																inparms
																		.put(
																				key,
																				SoapTypeConverter.BIN_HEX_Converter
																						.convertToString(value));
		}
		return inparms;
	}

	public Dictionary<String,Object> upnp2java(
			Dictionary<String,Object> outparms,
			Hashtable<String,Object> stat_dt)
			throws Exception {
		for (Enumeration<String> e = outparms.keys(); e.hasMoreElements();) {
			String key = e.nextElement();
			String value = (String) outparms.get(key);
			String type = (String) stat_dt.get(key);
			outparms.remove(key);
			if (type.equals(SoapTypeConstants.SOAP_TYPE_UI1)
					|| type.equals(SoapTypeConstants.SOAP_TYPE_UI2)
					|| type.equals(SoapTypeConstants.SOAP_TYPE_INT))
				outparms.put(key, SoapTypeConverter.UI1_Converter
						.convertToJavaType(value));
			else
				if (type.equals(SoapTypeConstants.SOAP_TYPE_I1)
						|| type.equals(SoapTypeConstants.SOAP_TYPE_I2)
						|| type.equals(SoapTypeConstants.SOAP_TYPE_I4))
					outparms.put(key, SoapTypeConverter.UI1_Converter
							.convertToJavaType(value));
				else
					if (type.equals(SoapTypeConstants.SOAP_TYPE_UI4))
						outparms.put(key, SoapTypeConverter.UI4_Converter
								.convertToJavaType(value));
					else
						if (type.equals(SoapTypeConstants.SOAP_TYPE_R4)
								|| type
										.equals(SoapTypeConstants.SOAP_TYPE_FLOAT))
							outparms.put(key, SoapTypeConverter.R4_Converter
									.convertToJavaType(value));
						else
							if (type.equals(SoapTypeConstants.SOAP_TYPE_R8)
									|| type
											.equals(SoapTypeConstants.SOAP_TYPE_NUMBER)
									|| type
											.equals(SoapTypeConstants.SOAP_TYPE_FIXED_14_4))
								outparms.put(key,
										SoapTypeConverter.R8_Converter
												.convertToJavaType(value));
							else
								if (type
										.equals(SoapTypeConstants.SOAP_TYPE_CHAR))
									outparms.put(key,
											SoapTypeConverter.CHAR_Converter
													.convertToJavaType(value));
								else
									if (type
											.equals(SoapTypeConstants.SOAP_TYPE_STRING)
											|| type
													.equals(SoapTypeConstants.SOAP_TYPE_URI)
											|| type
													.equals(SoapTypeConstants.SOAP_TYPE_UUID))
										outparms
												.put(
														key,
														SoapTypeConverter.STRING_Converter
																.convertToJavaType(value));
									else
										if (type
												.equals(SoapTypeConstants.SOAP_TYPE_DATE))
											outparms
													.put(
															key,
															SoapTypeConverter.DATE_Converter
																	.convertToJavaType(value));
										else
											if (type
													.equals(SoapTypeConstants.SOAP_TYPE_BOOLEAN))
												outparms
														.put(
																key,
																SoapTypeConverter.BOOLEAN_Converter
																		.convertToJavaType(value));
											else
												if (type
														.equals(SoapTypeConstants.SOAP_TYPE_BIN_HEX)
														|| type
																.equals(SoapTypeConstants.SOAP_TYPE_BIN_BASE64))
													outparms
															.put(
																	key,
																	SoapTypeConverter.BIN_HEX_Converter
																			.convertToJavaType(value));
		}
		return outparms;
	}
}

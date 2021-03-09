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
package org.osgi.test.cases.framework.secure.junit.permissions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AllPermission;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.PropertyPermission;

import org.osgi.framework.AdaptPermission;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.framework.secure.permissions.util.PermissionsFilterException;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.sleep.Sleep;
import org.osgi.test.support.wiring.Wiring;

/**
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 *
 *         This class provides TestCases for RFC131, extended ServicePermission
 *         and extended PackagePermission as whole framework.
 *
 */
public class FilteredTestControl extends OSGiTestCase {

	private static final String	S_NAME_UTIL_ISERVICE1				= "org.osgi.test.cases.framework.secure.permissions.util.IService1";
	private static final String	S_NAME_UTIL_ISERVICE2				= "org.osgi.test.cases.framework.secure.permissions.util.IService2";
	private static final String	P_NAME_UTIL							= "org.osgi.test.cases.framework.secure.permissions.util";
	private static final String	P_NAME_SHARED						= "org.osgi.test.cases.framework.secure.permissions.sharedPkg";
	private static final String	SP									= "org.osgi.framework.ServicePermission";
	private static final String	PP									= "org.osgi.framework.PackagePermission";
	private PermissionAdmin		permAdmin;

	/**
	 * Prior to each test, flag is set to false. If the target exception is
	 * caught, it will be set to true.
	 */
	private boolean				exceptionFlag;
	boolean						flagRegisterEvent;
	boolean						flagModifyEvent;
	boolean						flagUnregisterEvent;

	private static final String	REGISTER_BUNDLE_LOCATION			= "permissions.register.jar";
	private static final String	REGISTER_MODIFY_BUNDLE_LOCATION		= "permissions.registerModify.jar";
	private static final String	REGISTER_PLURAL_BUNDLE_LOCATION		= "permissions.registerPlural.jar";
	private static final String	GET_BUNDLE_LOCATION					= "permissions.get.jar";
	private static final String	RESET_PERMISSION_BUNDLE_LOCATION	= "permissions.setPermission.jar";
	private static final String	EXPORT_BUNDLE_1_LOCATION			= "permissions.exporter1.jar";
	private static final String	EXPORT_BUNDLE_2_LOCATION			= "permissions.exporter2.jar";
	private static final String	IMPORT_BUNDLE_1_LOCATION			= "permissions.importer1.jar";
	private static final String	IMPORT_BUNDLE_2_LOCATION			= "permissions.importer2.jar";

	private Bundle				registerBundle						= null;
	private Bundle				registerPluralBundle				= null;
	private Bundle				getBundle							= null;
	private Bundle				registerModifyBundle				= null;
	private Bundle				exportBundle1						= null;
	private Bundle				importBundle1						= null;
	private Bundle				importBundle2						= null;
	// private Bundle registerForServiceRegistrationTestBundle = null;
	private Bundle				setPermBundle						= null;
	private Bundle				exportBundle2						= null;
	private LinkedList<PermissionInfo>	list;

	private static final int	SLEEP_PERIOD_IN_MSEC				= 200;

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws Exception {
		this.exceptionFlag = false;
		this.flagModifyEvent = false;
		this.flagRegisterEvent = false;
		this.flagUnregisterEvent = false;
		permAdmin = this.getPermissionAdmin();
		this.resetBundles(false);
		this.setAllpermission(RESET_PERMISSION_BUNDLE_LOCATION);
		setPermBundle = this.installBundle(RESET_PERMISSION_BUNDLE_LOCATION);
		setPermBundle.start();
		list = new LinkedList<>();
		this.registerBundle = this.installBundle(REGISTER_BUNDLE_LOCATION);
		this.registerPluralBundle = this
				.installBundle(REGISTER_PLURAL_BUNDLE_LOCATION);
		this.getBundle = this.installBundle(GET_BUNDLE_LOCATION);
		this.registerModifyBundle = this
				.installBundle(REGISTER_MODIFY_BUNDLE_LOCATION);
		this.exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		this.exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);
		this.importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		this.importBundle2 = this.installBundle(IMPORT_BUNDLE_2_LOCATION);
		this.setBasePermissions();
	}

	private void resetBundles() throws BundleException {
		resetBundles(true);
	}

	private void resetBundles(boolean refreshAndResolve) throws BundleException {
		if (this.registerBundle != null) {
			this.registerBundle.uninstall();
			this.registerBundle = null;
		}
		if (this.registerPluralBundle != null) {
			this.registerPluralBundle.uninstall();
			this.registerPluralBundle = null;
		}
		if (this.getBundle != null) {
			this.getBundle.uninstall();
			this.getBundle = null;
		}
		if (this.registerModifyBundle != null) {
			this.registerModifyBundle.uninstall();
			this.registerModifyBundle = null;
		}
		if (this.exportBundle1 != null) {
			this.exportBundle1.uninstall();
			this.exportBundle1 = null;
		}
		if (this.importBundle1 != null) {
			this.importBundle1.uninstall();
			this.importBundle1 = null;
		}

		if (this.importBundle2 != null) {
			this.importBundle2.uninstall();
			this.importBundle2 = null;
		}
		// if (this.registerForServiceRegistrationTestBundle != null) {
		// this.registerForServiceRegistrationTestBundle.uninstall();
		// this.registerForServiceRegistrationTestBundle = null;
		// }
		if (this.exportBundle2 != null) {
			this.exportBundle2.uninstall();
			this.exportBundle2 = null;
		}

		if (refreshAndResolve)
			this.refreshAndResolveBundles();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
		this.resetBundles();
		setPermBundle.uninstall();
	}

	/*
	 * ----------------------------------------- Test methods.
	 */

	public void testServiceRegistration1_1_1() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_2() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP,
				"org.osgi.test.cases.framework.secure.permissions.util.*",
				"REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_3() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, "org.osgi.test.cases.framework.secure.*", "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_4() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, "*", "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_2_1() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		// add(list, SP, S_NAME_UTIL_ISERVICE, "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_3_1() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		// It must be ignored because filter is invalid for REGISTER action.
		add(list, SP, "(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")", "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_3_2() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		// It must be ignored because filter is invalid for REGISTER action.
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(segment=providerA))", "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testPluralInterfaceRegister1_4_1() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "REGISTER");
		add(list, SP, S_NAME_UTIL_ISERVICE2, "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerPluralBundle, list);
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testPluralInterfaceRegister1_4_2() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "REGISTER");
		// add(list, SP, S_NAME_UTIL_ISERVICE2, "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerPluralBundle, list);
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testPluralInterfaceRegister1_4_3() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, "org.osgi.test.cases.framework.secure.*", "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerPluralBundle, list);
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_01_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_01_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "org.osgi.test.cases.framework.secure.permissions.*",
				"GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_01_3() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP,
				"org.osgi.test.cases.framework.secure.permissions.util.*",
				"GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_01_4() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "*", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_02_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_02_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP,
				"(objectClass=org.osgi.test.cases.framework.secure.permissions.*)",
				"GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_02_3() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=org.osgi.test.*)", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_02_4() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=*)", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_03_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		// add(list, SP, S_NAME_UTIL_ISERVICE, "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_03_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "something.else", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_03_3() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "something.*", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_04_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(!(objectClass=" + S_NAME_UTIL_ISERVICE2 + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_05_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=" + S_NAME_UTIL_ISERVICE2 + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_05_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(!(objectClass=" + S_NAME_UTIL_ISERVICE1 + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	// plural interface
	public void testServiceGet2_06_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		this.getBundle.stop();
		list.clear();
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=" + S_NAME_UTIL_ISERVICE2 + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		this.getBundle.stop();
		list.clear();
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		this.getBundle.stop();
		list.clear();
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE2, "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_06_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(!(objectClass=something.else))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_06_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(!(objectClass=" + S_NAME_UTIL_ISERVICE1 + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	// service properties
	public void testServiceGet2_07_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(segment=providerA))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_07_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(segment=providerFail))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_07_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(!(segment=providerFail)))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_07_4() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(!(segment=providerFail)))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	// bundle identifiers
	public void testServiceGet2_08_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(location="
				+ this.registerBundle.getLocation() + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_08_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String location = this.registerBundle.getLocation();
		String sub = location.substring(0, location.indexOf(".") + 1) + "*";
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(location="
				+ sub + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_08_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(location=something.else))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_08_4() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(id="
				+ this.registerBundle.getBundleId() + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_08_5() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(id="
				+ getContext().getBundle().getBundleId() + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_08_6() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ this.registerBundle.getSymbolicName() + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_08_7() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String name = this.registerBundle.getSymbolicName();
		String sub = name.substring(0, name.indexOf(".") + 1) + "*";
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ sub + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_08_8() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String sub = "something.else";
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ sub + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_08_9() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String signerValue = "CN=John Smith,O=ACME Inc,OU=ACME Cert Authority,L=Austin,ST=Texas,C=US";
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(signer="
				+ signerValue + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_08_10() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String signerValue = "\\*,O=ACME Inc,OU=ACME Cert Authority,L=Austin,ST=Texas,C=US";
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(signer="
				+ signerValue + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_08_11() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String signerValue = "\\*,OU=ACME Cert Authority,L=Austin,ST=Texas,C=US";
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(signer="
				+ signerValue + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_08_12() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String signerValue = "\\*,O=NTT,OU=NTT Cert Authority,C=JP";
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(signer="
				+ signerValue + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	// combination of service properties and bundle identifiers.
	public void testServiceGet2_09_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ getContext().getBundle().getSymbolicName()
				+ ")(segment=BBBB)", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_09_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(|(name="
				+ this.registerBundle.getSymbolicName() + ")(segment=BBBB)))",
				"GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	// only bundle identifiers no objectClass
	public void testServiceGet2_10_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String sub = this.registerBundle.getLocation();
		add(list, SP, "(location=" + sub + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_10_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String tmp = this.registerBundle.getLocation();
		String sub = tmp.substring(0, tmp.indexOf(".") + 1) + "*";
		add(list, SP, "(location=" + sub + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_10_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(id=" + this.registerBundle.getBundleId() + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_10_4() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String sub = this.registerBundle.getSymbolicName();
		add(list, SP, "(name=" + sub + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_10_5() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String tmp = this.registerBundle.getSymbolicName();
		String sub = tmp.substring(0, tmp.indexOf(".") + 1) + "*";
		add(list, SP, "(name=" + sub + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_10_6() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String signerValue = "CN=John Smith,O=ACME Inc,OU=ACME Cert Authority,L=Austin,ST=Texas,C=US";
		add(list, SP, "(signer=" + signerValue + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_10_7() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String signerValue = "\\*,O=ACME Inc,OU=ACME Cert Authority,L=Austin,ST=Texas,C=US";
		add(list, SP, "(signer=" + signerValue + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_11_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(location=something.else)", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_11_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(id=" + getContext().getBundle().getBundleId() + ")",
				"GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_11_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(name=something.else)", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_11_4() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		String signerValue = "\\*,O=NTT,OU=NTT Cert Authority,L=Austin,ST=Texas,C=US";
		add(list, SP, "(signer=" + signerValue + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_12_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(@id=id.NTT))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_12_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(@@location=location.NTT))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_12_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(@@@name=name.NTT))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	// /////////////////////////////////////////

	public void testServiceEvent3_1_1() throws Exception {
		ServiceListener sl = addServiceListener(S_NAME_UTIL_ISERVICE1);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ this.registerModifyBundle.getSymbolicName() + "))", "GET");
		// add(list, SP, PermissionAdmin.class.getName(), "get");
		this.setBundlePermission(getContext().getBundle(), list);
		this.startBundleAndCheckSecurityException(this.registerModifyBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		assertFalse("Fail to register or modify service. It MUST succeed.",
				exceptionFlag);

		assertTrue("ServiceEvent.REGISTERED has not been delivered.",
				flagRegisterEvent);
		assertTrue("ServiceEvent.MODIFIED has not been delivered.",
				flagModifyEvent);
		this.registerModifyBundle.stop();
		assertTrue("ServiceEvent.UNREGISTERED has not been delivered.",
				flagUnregisterEvent);
		getContext().removeServiceListener(sl);
	}

	public void testServiceEvent3_1_2() throws Exception {
		ServiceListener sl = addServiceListener(S_NAME_UTIL_ISERVICE1);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(name=something.else))", "GET");
		this.setBundlePermission(getContext().getBundle(), list);
		this.startBundleAndCheckSecurityException(this.registerModifyBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		assertFalse("ServiceEvent.REGISTERED has been delivered.",
				flagRegisterEvent);
		assertFalse("ServiceEvent.MODIFIED has been delivered.",
				flagModifyEvent);
		this.registerModifyBundle.stop();
		assertFalse("ServiceEvent.UNREGISTERED has been delivered.",
				flagUnregisterEvent);
		getContext().removeServiceListener(sl);
	}

	public void testServiceEvent3_1_3() throws Exception {
		ServiceListener sl = addServiceListener(S_NAME_UTIL_ISERVICE1);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(segment=providerA))", "GET");
		this.setBundlePermission(getContext().getBundle(), list);
		this.startBundleAndCheckSecurityException(this.registerModifyBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		assertTrue("ServiceEvent.REGISTERED has not been delivered.",
				flagRegisterEvent);
		assertFalse("ServiceEvent.MODIFIED has been delivered.",
				flagModifyEvent);
		this.registerModifyBundle.stop();
		assertFalse("ServiceEvent.UNREGISTERED has been delivered.",
				flagUnregisterEvent);
		getContext().removeServiceListener(sl);
	}

	public void testServiceEvent3_1_4() throws Exception {
		ServiceListener sl = addServiceListener(S_NAME_UTIL_ISERVICE1);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(vendor=ACME))", "GET");
		this.setBundlePermission(getContext().getBundle(), list);
		this.startBundleAndCheckSecurityException(this.registerModifyBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		assertFalse("ServiceEvent.REGISTERED has been delivered.",
				flagRegisterEvent);
		assertTrue("ServiceEvent.MODIFIED has not been delivered.",
				flagModifyEvent);
		this.registerModifyBundle.stop();
		assertTrue("ServiceEvent.UNREGISTERED has not been delivered.",
				flagUnregisterEvent);
		getContext().removeServiceListener(sl);
	}

	private ServiceListener addServiceListener(final String clazz) {
		ServiceListener sl = new ServiceListener() {
			public void serviceChanged(ServiceEvent se) {
				String[] clazzes = (String[]) se.getServiceReference()
						.getProperty(Constants.OBJECTCLASS);
				for (int i = 0; i < clazzes.length; i++) {
					if (clazzes[i].equals(clazz)) {
						switch (se.getType()) {
							case ServiceEvent.REGISTERED :
								flagRegisterEvent = true;
								break;
							case ServiceEvent.MODIFIED :
								flagModifyEvent = true;
								break;
							case ServiceEvent.UNREGISTERING :
								flagUnregisterEvent = true;
								break;
							default :
								break;
						}
						break;
					}
				}
			}
		};
		getContext().addServiceListener(sl);
		return sl;
	}

	// ///////////////////////////

	public void testGetRegisteredService4_1_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "GET");
		this.setBundlePermission(getContext().getBundle(), list);

		ServiceReference< ? >[] ref = registerPluralBundle
				.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to get registered service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_1_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "something.else", "GET");
		this.setBundlePermission(getContext().getBundle(), list);

		ServiceReference< ? >[] ref = registerPluralBundle
				.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in getting registered service under multiple name. It MUST fail.");
	}

	public void testGetRegisteredService4_2_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(vendor=NTT))", "GET");
		this.setBundlePermission(getContext().getBundle(), list);

		ServiceReference< ? >[] ref = registerPluralBundle
				.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to get registered service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_2_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(vendor=something.else))", "GET");
		this.setBundlePermission(getContext().getBundle(), list);

		ServiceReference< ? >[] ref = registerPluralBundle
				.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in getting registered service under multiple name. It MUST fail.");
	}

	public void testGetRegisteredService4_2_3() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ this.registerPluralBundle.getSymbolicName() + "))", "GET");
		this.setBundlePermission(getContext().getBundle(), list);

		ServiceReference< ? >[] ref = registerPluralBundle
				.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to get registered service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_2_4() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(name=something.else))", "GET");
		this.setBundlePermission(getContext().getBundle(), list);

		ServiceReference< ? >[] ref = registerPluralBundle
				.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in getting registered service under multiple name. It MUST fail.");
	}

	public void testGetRegisteredService4_2_5() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(name=" + this.registerPluralBundle.getSymbolicName()
				+ ")", "GET");
		this.setBundlePermission(getContext().getBundle(), list);

		ServiceReference< ? >[] ref = registerPluralBundle
				.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to get registered service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_2_6() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(name=something.else)", "GET");
		this.setBundlePermission(getContext().getBundle(), list);

		ServiceReference< ? >[] ref = registerPluralBundle
				.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in getting registered service under multiple name. It MUST fail.");
	}

	public void testGetServicesInUse5_1_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "GET");
		this.setBundlePermission(getContext().getBundle(), list);
		ServiceReference< ? >[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServicesInUse(). It MUST succeed.");
	}

	public void testGetServicesInUse5_1_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "something.else", "GET");
		this.setBundlePermission(getContext().getBundle(), list);
		ServiceReference< ? >[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServicesInUse. It MUST fail.");
	}

	public void testGetServicesInUse5_2_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(vendor=NTT))", "GET");
		this.setBundlePermission(getContext().getBundle(), list);
		ServiceReference< ? >[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServicesInUse(). It MUST succeed.");
	}

	public void testGetServicesInUse5_2_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(vendor=something.else))", "GET");
		this.setBundlePermission(getContext().getBundle(), list);
		ServiceReference< ? >[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServicesInUse. It MUST fail.");
	}

	public void testGetServicesInUse5_2_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ this.registerPluralBundle.getSymbolicName() + "))", "GET");
		this.setBundlePermission(getContext().getBundle(), list);
		ServiceReference< ? >[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServicesInUse(). It MUST succeed.");
	}

	public void testGetServicesInUse5_2_4() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ "something.else" + "))", "GET");
		this.setBundlePermission(getContext().getBundle(), list);
		ServiceReference< ? >[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServicesInUse. It MUST fail.");
	}

	public void testGetServicesInUse5_2_5() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(location=" + this.registerPluralBundle.getLocation()
				+ ")", "GET");
		this.setBundlePermission(getContext().getBundle(), list);
		ServiceReference< ? >[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServicesInUse(). It MUST succeed.");
	}

	public void testGetServicesInUse5_2_6() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, AdaptPermission.class.getName(), "*", "adapt");
		add(list, PropertyPermission.class.getName(), "org.osgi.test.*", "read");
		add(list, SP, "(location=" + "something.else" + ")", "GET");
		this.setBundlePermission(getContext().getBundle(), list);
		ServiceReference< ? >[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServicesInUse. It MUST fail.");
	}

	public void testExportPackage7_1_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, P_NAME_SHARED, "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_1_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.test.cases.framework.secure.*", "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_1_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.*", "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_1_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "*", "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	private void checkExport1Succeed() {
		refreshAndResolveBundles(exportBundle1);
		if (exportBundle1.getState() != Bundle.RESOLVED)
			fail("Fail to export package. It MUST succeed.");

		BundleWiring wiring = exportBundle1.adapt(BundleWiring.class);
		List<BundleCapability> pkgs = wiring
				.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertNotNull("no list returned", pkgs);
		assertEquals("list does not have 1 entry", 1, pkgs.size());
		assertEquals("Fail to export package", P_NAME_SHARED, pkgs.get(0)
				.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE));

		List<BundleWire> wires = wiring
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		for (BundleWire wire : wires) {
			String name = (String) wire.getCapability().getAttributes()
					.get(PackageNamespace.PACKAGE_NAMESPACE);
			if (name.equals(P_NAME_SHARED)) {
				fail("There MUST be no importers.");
			}
		}
	}

	public void testExportPackage7_2_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.else", "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Fail();
	}

	public void testExportPackage7_2_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.*", "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Fail();
	}

	private void checkExport1Fail() {
		refreshAndResolveBundles(exportBundle1);
		if (exportBundle1.getState() != Bundle.RESOLVED)
			fail("Fail to export package. It MUST succeed.");

		BundleWiring wiring = exportBundle1.adapt(BundleWiring.class);
		List<BundleCapability> pkgs = wiring
				.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertNotNull("no list returned", pkgs);
		assertEquals("list not empty", 0, pkgs.size());
	}

	public void testExportPackage7_3_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, P_NAME_SHARED, "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		this.checkExport1Succeed();
	}

	public void testExportPackage7_3_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.test.cases.framework.secure.*", "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_3_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.*", "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_3_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "*", "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_4_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.else", "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Fail();
	}

	public void testExportPackage7_4_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.*", "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Fail();
	}

	public void testExportPackage7_5_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);

		this.printoutHeader(exportBundle1);
		this.printoutHeader(exportBundle2);

		add(list, PP, "*", "exportonly");
		add(list, PP, "*", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Export2Succeed();
	}

	public void testExportPackage7_5_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);

		add(list, PP, P_NAME_SHARED, "exportonly");
		add(list, PP, "(&(package.name=" + P_NAME_SHARED + ")(name="
				+ this.exportBundle2.getSymbolicName() + "))", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Export2Succeed();
	}

	public void testExportPackage7_5_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);

		add(list, PP, P_NAME_SHARED, "exportonly");
		add(list, PP, "(&(package.name=" + P_NAME_SHARED + ")(name="
				+ this.exportBundle2.getSymbolicName() + "))", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);

		list.clear();
		add(list, PP, P_NAME_SHARED, "exportonly");
		add(list, PP, "(&(package.name=" + P_NAME_SHARED + ")(name="
				+ this.exportBundle1.getSymbolicName() + "))", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle2, list);

		checkExport1Export2Succeed();
	}

	public void testExportPackage7_5_4() throws Exception {
		this.resetBundles();

		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);

		add(list, PP, P_NAME_SHARED, "exportonly");
		add(list, PP, "(&(package.name=" + "something.else" + ")(name="
				+ this.exportBundle2.getSymbolicName() + "))", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		refreshAndResolveBundles(exportBundle1, exportBundle2);

		BundleWiring wiring3 = exportBundle1.adapt(BundleWiring.class);
		List<BundleCapability> pkgs3 = wiring3
				.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertNotNull("no list returned", pkgs3);
		assertEquals("list does not have 1 entry", 1, pkgs3.size());
		assertEquals("Fail to export package", P_NAME_SHARED, pkgs3.get(0)
				.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE));

		List<BundleWire> wires3 = wiring3
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		for (BundleWire wire : wires3) {
			String name = (String) wire.getCapability().getAttributes()
					.get(PackageNamespace.PACKAGE_NAMESPACE);
			if (name.equals(P_NAME_SHARED)) {
				fail("It MUST not be imported.");
			}
		}

		BundleWiring wiring4 = exportBundle2.adapt(BundleWiring.class);
		List<BundleCapability> pkgs4 = wiring4
				.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertNotNull("no list returned", pkgs4);
		assertEquals("list does not have 1 entry", 1, pkgs4.size());
		assertEquals("Fail to export package", P_NAME_SHARED, pkgs4.get(0)
				.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE));

		List<BundleWire> wires4 = wiring4
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		for (BundleWire wire : wires4) {
			String name = (String) wire.getCapability().getAttributes()
					.get(PackageNamespace.PACKAGE_NAMESPACE);
			if (name.equals(P_NAME_SHARED)) {
				fail("It MUST not be imported.");
			}
		}

		if (this.exportBundle1.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_5_5() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);

		add(list, PP, "something.else", "exportonly");
		add(list, PP, "(&(package.name=" + P_NAME_SHARED + ")(name="
				+ this.exportBundle2.getSymbolicName() + "))", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		refreshAndResolveBundles(exportBundle1, exportBundle2);
		BundleWiring wiring3 = exportBundle1.adapt(BundleWiring.class);
		List<BundleCapability> pkgs3 = wiring3
				.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertNotNull("no list returned", pkgs3);
		assertEquals("list not empty", 0, pkgs3.size());

		BundleWiring wiring4 = exportBundle2.adapt(BundleWiring.class);
		List<BundleCapability> pkgs4 = wiring4
				.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertNotNull("no list returned", pkgs4);
		assertEquals("list does not have 1 entry", 1, pkgs4.size());
		assertEquals("Fail to export package", P_NAME_SHARED, pkgs4.get(0)
				.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE));

		List<BundleWire> wires4 = wiring4
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		for (BundleWire wire : wires4) {
			String name = (String) wire.getCapability().getAttributes()
					.get(PackageNamespace.PACKAGE_NAMESPACE);
			if (name.equals(P_NAME_SHARED)) {
				if (exportBundle1.equals(wire.getRequirerWiring().getBundle())) {
					return;
				}
			}
		}
		fail("package not imported.");
	}

	private void checkExport1Export2Succeed() {
		refreshAndResolveBundles(exportBundle1, exportBundle2);

		if (this.exportBundle1.getState() != Bundle.RESOLVED)
			fail("Fail to resolve exportBundle1");
		if (this.exportBundle2.getState() != Bundle.RESOLVED)
			fail("Fail to resolve exportBundle2");

		BundleWiring wiring = exportBundle2.adapt(BundleWiring.class);
		List<BundleCapability> pkgs2 = wiring
				.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertNotNull("no list returned", pkgs2);
		assertEquals("list does not have 1 entry", 1, pkgs2.size());
		assertEquals("Fail to export package", P_NAME_SHARED, pkgs2.get(0)
				.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE));

		List<BundleWire> wires = wiring
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		for (BundleWire wire : wires) {
			String name = (String) wire.getCapability().getAttributes()
					.get(PackageNamespace.PACKAGE_NAMESPACE);
			if (name.equals(P_NAME_SHARED)) {
				if (exportBundle1.equals(wire.getRequirerWiring().getBundle())) {
					return;
				}
			}
		}
		fail("package not imported.");
	}

	private void refreshAndResolveBundles(Bundle... bundles) {
		Wiring.synchronousRefreshBundles(getContext(), bundles);
		Wiring.resolveBundles(getContext(), bundles);
    }

	private void printoutHeader(Bundle bundle) {
		Dictionary<String,String> headers = bundle.getHeaders();
		System.out.println("bundle=" + bundle);

		for (Enumeration<String> keys = headers.keys(); keys
				.hasMoreElements();) {
			String key = keys.nextElement();
			if (key.equals(Constants.IMPORT_PACKAGE)
					|| key.equals(Constants.EXPORT_PACKAGE)
					|| key.equals(Constants.BUNDLE_SYMBOLICNAME))
				System.out.println("\t[" + key + ": " + headers.get(key) + "]");
		}

	}

	// /////////////////////////////////////////////////////

	public void testImportPackage8_1_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, P_NAME_SHARED, "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_1_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.test.cases.framework.secure.permissions.*",
				"import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_1_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.test.cases.*", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_1_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "*", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_2_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=" + P_NAME_SHARED + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_2_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=org.osgi.test.cases.framework.secure.*)",
				"import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_2_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=org.osgi.*)", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_2_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=*)", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	private void checkExport1SucceedImport1Succeed() {
		refreshAndResolveBundles(exportBundle1, importBundle1);
		this.printoutHeader(exportBundle1);
		this.printoutHeader(importBundle1);

		if (importBundle1.getState() != Bundle.RESOLVED)
			fail("Fail to import package. It MUST succeed.");

		BundleWiring wiring = exportBundle1.adapt(BundleWiring.class);
		List<BundleCapability> pkgs = wiring
				.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertNotNull("no list returned", pkgs);
		assertEquals("list does not have 1 entry", 1, pkgs.size());
		assertEquals("Fail to export package", P_NAME_SHARED, pkgs.get(0)
				.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE));

		List<BundleWire> wires = wiring
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		boolean flag = false;
		for (BundleWire wire : wires) {
			String name = (String) wire.getCapability().getAttributes()
					.get(PackageNamespace.PACKAGE_NAMESPACE);
			if (name.equals(P_NAME_SHARED)) {
				if (importBundle1.equals(wire.getRequirerWiring().getBundle())) {
					flag = true;
					break;
				}
			}
		}

		if (!flag)
			fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_3_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.else", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_3_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.*", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_3_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=something.else)", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_3_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=something.*)", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	private void checkExport1SucceedImport1Fail() {
		refreshAndResolveBundles(exportBundle1, importBundle1);
		if (importBundle1.getState() != Bundle.INSTALLED)
			fail("Succeed in importing package. It MUST fail.");

		BundleWiring wiring = exportBundle1.adapt(BundleWiring.class);
		List<BundleCapability> pkgs = wiring
				.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertNotNull("no list returned", pkgs);
		assertEquals("list does not have 1 entry", 1, pkgs.size());
		assertEquals("Fail to export package", P_NAME_SHARED, pkgs.get(0)
				.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE));

		List<BundleWire> wires = wiring
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		boolean flag = false;
		for (BundleWire wire : wires) {
			String name = (String) wire.getCapability().getAttributes()
					.get(PackageNamespace.PACKAGE_NAMESPACE);
			if (name.equals(P_NAME_SHARED)) {
				if (importBundle1.equals(wire.getRequirerWiring().getBundle())) {
					flag = true;
					break;
				}
			}
		}

		if (flag)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_4_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		String sub = this.exportBundle1.getSymbolicName();
		add(list, PP, "(name=" + sub + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_4_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		String tmp = this.exportBundle1.getSymbolicName();
		String sub = tmp.substring(0, tmp.indexOf(".") + 1) + "*";
		add(list, PP, "(name=" + sub + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_4_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		long id = this.exportBundle1.getBundleId();
		add(list, PP, "(id=" + id + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_4_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		String sub = this.exportBundle1.getLocation();
		add(list, PP, "(location=" + sub + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_4_5() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		String tmp = this.exportBundle1.getLocation();
		String sub = tmp.substring(0, tmp.indexOf(".") + 1) + "*";

		add(list, PP, "(location=" + sub + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_4_6() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		String signerValue = "CN=John Smith,O=ACME Inc,OU=ACME Cert Authority,L=Austin,ST=Texas,C=US";
		add(list, PP, "(signer=" + signerValue + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_4_7() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		String signerValue = "CN=\\*,O=ACME Inc,OU=ACME Cert Authority,L=Austin,ST=Texas,C=US";
		add(list, PP, "(signer=" + signerValue + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_5_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(name=" + "something.else" + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_5_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(id=" + getContext().getBundle().getBundleId() + ")",
				"import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_5_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(location=" + "something.else" + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_5_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		String signerValue = "CN=\\*,O=NTT,OU=NTT Cert Authority,L=Austin,ST=Texas,C=US";
		add(list, PP, "(signer=" + signerValue + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_6_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle2 = this.installBundle(IMPORT_BUNDLE_2_LOCATION);
		add(list, PP, P_NAME_SHARED, "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle2, list);
		checkExport1SucceedImport2Fail();
	}

	private void checkExport1SucceedImport2Fail() {
		refreshAndResolveBundles(exportBundle1, importBundle2);

		if (importBundle2.getState() != Bundle.INSTALLED)
			fail("It must be INSTALLED(" + Bundle.INSTALLED + "). state="
					+ importBundle2.getState());

		BundleWiring wiring = exportBundle1.adapt(BundleWiring.class);
		List<BundleCapability> pkgs = wiring
				.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertNotNull("no list returned", pkgs);
		assertEquals("list does not have 1 entry", 1, pkgs.size());
		assertEquals("Fail to export package", P_NAME_SHARED, pkgs.get(0)
				.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE));

		List<BundleWire> wires = wiring
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		boolean flag = false;
		for (BundleWire wire : wires) {
			String name = (String) wire.getCapability().getAttributes()
					.get(PackageNamespace.PACKAGE_NAMESPACE);
			if (name.equals(P_NAME_SHARED)) {
				flag = true;
				break;
			}
		}

		if (flag)
			fail("There MUST be no bundle who imports the package.");
	}

	/*
	 * -----------------------------------------
	 *
	 * Utility methods.
	 */

	@SuppressWarnings("unused")
	private static void sleep() {
		try {
			Sleep.sleep(SLEEP_PERIOD_IN_MSEC);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setAllpermission(String bundleLocation) {
		ServiceReference<PermissionAdmin> ref = getContext()
				.getServiceReference(PermissionAdmin.class);
		if (ref == null) {
			System.out.println("Fail to get ServiceReference of "
					+ PermissionAdmin.class.getName());
			return;
		}
		permAdmin = getContext().getService(ref);
		PermissionInfo[] pisAllPerm = new PermissionInfo[1];
		pisAllPerm[0] = new PermissionInfo("(" + AllPermission.class.getName()
				+ ")");
		permAdmin.setPermissions(bundleLocation, pisAllPerm);
	}

	private void setBundlePermission(Bundle b, List<PermissionInfo> list) {
		PermissionInfo[] pis = new PermissionInfo[list.size()];
		pis = list.toArray(pis);
		permAdmin.setPermissions(b.getLocation(), pis);
	}

	// private void printPermissions() {
	// PermissionInfo[] pis = permAdmin.getDefaultPermissions();
	// if (pis == null) {
	// System.out.println("DefaultPermissions[] is not set");
	// }
	// else {
	// for (int i = 0; i < pis.length; i++)
	// System.out.println("DefaultPermissions[" + i + "]="
	// + pis[i].getEncoded());
	// }
	// String[] locations = permAdmin.getLocations();
	// if (locations == null) {
	// System.out.println("pa.getLocation() == null");
	// }
	// else {
	// for (int j = 0; j < locations.length; j++) {
	// System.out.println("Permissions of (" + locations[j] + "):");
	// pis = permAdmin.getPermissions(locations[j]);
	// if (pis == null) {
	// System.out.println("Permissions of (" + locations[j]
	// + ") is not set");
	// }
	// else {
	// for (int i = 0; i < pis.length; i++) {
	// System.out.println("\tPermission[" + i + "]="
	// + pis[i].getEncoded());
	// }
	// }
	// }
	// }
	// }

	private Bundle installBundle(String location) throws IOException {
		URL url = getContext().getBundle().getResource(location);
		InputStream is = url.openStream();
		Bundle bundle = null;
		try {
			bundle = getContext().installBundle(location, is);
		}
		catch (Exception e) {
			IllegalArgumentException iae = new IllegalArgumentException(
					"Fail to install bundle from "
					+ location);
			iae.initCause(e);
			throw iae;
		}
		finally {
			is.close();
		}
		return bundle;
	}

	/**
	 * If specified BundleException has nested exception of SecurityException,
	 * exceptionFlag is set to true. Otherwise, it will be thrown to the caller
	 * of this method.
	 *
	 * @param be
	 * @throws Exception
	 */
	private void checkIfExIsSecurityException(BundleException be)
			throws Exception {
		be.printStackTrace();
		Throwable th = be.getNestedException();
		if (th instanceof SecurityException)
			exceptionFlag = true;
		else
			throw (Exception) th;
	}

	/**
	 * If specified BundleException has nested exception of
	 * PermissionsFilterException, exceptionFlag is set to true. Otherwise, it
	 * will be thrown to the caller of this method.
	 *
	 * @param be
	 * @throws Exception
	 */
	private void checkIfExIsPermissionsFilterException(BundleException be)
			throws Exception {
		Throwable th = be.getNestedException();
		if (th instanceof PermissionsFilterException)
			exceptionFlag = true;
		else
			throw (Exception) th;
	}

	private void startBundleAndCheckSecurityException(Bundle bundle)
			throws Exception {
		try {
			bundle.start();
		}
		catch (BundleException be) {
			this.checkIfExIsSecurityException(be);
		}
	}

	/**
	 * Start specified bundle.
	 *
	 * If
	 *
	 * @param bundle
	 * @throws Exception
	 */
	private void startBundleAndCheckPermissionsFilterException(Bundle bundle)
			throws Exception {
		try {
			bundle.start();
		}
		catch (BundleException be) {
			this.checkIfExIsPermissionsFilterException(be);
		}
	}

	private PermissionAdmin getPermissionAdmin() {
		ServiceReference<PermissionAdmin> ref = getContext()
				.getServiceReference(PermissionAdmin.class);
		if (ref == null)
			throw new IllegalStateException("Fail to get ServiceReference of "
					+ PermissionAdmin.class.getName());

		PermissionAdmin permissionAdmin = getContext()
				.getService(ref);
		return permissionAdmin;
	}

	private void add(List<PermissionInfo> permissionsInfos, String clazz,
			String name, String actions) {
		permissionsInfos.add(new PermissionInfo(clazz, name, actions));
	}

	private void setBasePermissions() {
		Bundle[] bundles = getContext().getBundles();
		for (int i = 0; i < bundles.length; i++) {
			this.setAllpermission(bundles[i].getLocation());
		}
		permAdmin
				.setDefaultPermissions(new PermissionInfo[] {new PermissionInfo(
						"java.util.PropertyPermission", "java.home", "read")});

	}

}

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
package org.osgi.test.cases.permissionadmin.junit;

import java.io.InputStream;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.permissionadmin.service.PermissionSignatureTBCService;

public class PermissionSignatureUtility {

	// Signature keys
	static final String						ID				= "id";
	static final String						LOCATION		= "location";
	static final String						NAME			= "name";
	static final String						SIGNER			= "signer";
	static final String						DN_S			= "DNs";

	static final String						CONFIG_FPID		= "permission.config.test.fpid";
	static final String						CONFIG_PROPERTY	= "config.property";

	private PermissionSignatureTestControl	control;

	public PermissionSignatureUtility(PermissionSignatureTestControl control,
			PermissionSignatureTBCService tbc) {
		this.control = control;
	}

	// returns true if 'method' succeed
	public Object allowed_Bundle_getHeaders(String message, Bundle bundle)
			throws Exception {
		return control.allowed_call("call Bundle.getHeaders() " + message,
				"callBundle_getHeaders", new Class[] {Bundle.class},
				new Object[] {bundle});
	}

	// returns true if 'method' failed
	public boolean not_allowed_Bundle_getHeaders(String message, Bundle bundle)
			throws Exception {
		return control.not_allowed_call("call Bundle.getHeaders() " + message,
				"callBundle_getHeaders", new Class[] {Bundle.class},
				new Object[] {bundle}, SecurityException.class);
	}

	public Object allowed_Bundle_getHeaders_byLocation(String message,
			Bundle bundle) throws Exception {
		return control.allowed_call(
				"call Bundle.getHeaders(String) " + message,
				"callBundle_getHeaders", new Class[] {Bundle.class,
						String.class}, new Object[] {bundle, null});
	}

	public boolean not_allowed_Bundle_getHeaders_byLocation(String message,
			Bundle bundle) throws Exception {
		return control.not_allowed_call("call Bundle.getHeaders(String) "
				+ message, "callBundle_getHeaders", new Class[] {Bundle.class,
				String.class}, new Object[] {bundle, null},
				SecurityException.class);
	}

	public Object allowed_Bundle_getLocation(String message, Bundle bundle)
			throws Exception {
		return control.allowed_call("call Bundle.getLocation() " + message,
				"callBundle_getLocation", new Class[] {Bundle.class},
				new Object[] {bundle});
	}

	public boolean not_allowed_Bundle_getLocation(String message, Bundle bundle)
			throws Exception {
		return control.not_allowed_call("call Bundle.getLocation() " + message,
				"callBundle_getLocation", new Class[] {Bundle.class},
				new Object[] {bundle}, SecurityException.class);
	}

	// if not ok returns null, not java.lang.SecurityException
	public boolean allowed_Bundle_getResource(String message, Bundle bundle,
			String name) throws Throwable {
		return control.allowed_call_assertNotNull(
				"call Bundle.getResource(String) " + message,
				"callBundle_getResource", new Class[] {Bundle.class,
						String.class}, new Object[] {bundle, name});
	}

	// if not ok returns null, not java.lang.SecurityException
	public boolean not_allowed_Bundle_getResource(String message,
			Bundle bundle, String name) throws Throwable {
		return control.not_allowed_call_assertNull(
				"call Bundle.getResource(String) " + message,
				"callBundle_getResource", new Class[] {Bundle.class,
						String.class}, new Object[] {bundle, name});
	}

	// if not ok returns null, not java.lang.SecurityException
	public boolean allowed_Bundle_getResources(String message, Bundle bundle,
			String name) throws Throwable {
		return control.allowed_call_assertNotNull(
				"call Bundle.getResources(String) " + message,
				"callBundle_getResources", new Class[] {Bundle.class,
						String.class}, new Object[] {bundle, name});
	}

	// if not ok returns null, not java.lang.SecurityException
	public boolean not_allowed_Bundle_getResources(String message,
			Bundle bundle, String name) throws Throwable {
		return control.not_allowed_call_assertNull(
				"call Bundle.getResources(String) " + message,
				"callBundle_getResources", new Class[] {Bundle.class,
						String.class}, new Object[] {bundle, name});
	}

	// if not ok returns null, not java.lang.SecurityException
	public boolean allowed_Bundle_getEntry(String message, Bundle bundle,
			String name) throws Throwable {
		return control.allowed_call_assertNotNull(
				"call Bundle.getEntry(String) " + message,
				"callBundle_getEntry",
				new Class[] {Bundle.class, String.class}, new Object[] {bundle,
						name});
	}

	// if not ok returns null, not java.lang.SecurityException
	public boolean not_allowed_Bundle_getEntry(String message, Bundle bundle,
			String name) throws Throwable {
		return control.not_allowed_call_assertNull(
				"call Bundle.getEntry(String) " + message,
				"callBundle_getEntry",
				new Class[] {Bundle.class, String.class}, new Object[] {bundle,
						name});
	}

	// if not ok returns null, not java.lang.SecurityException
	public boolean allowed_Bundle_getEntryPaths(String message, Bundle bundle,
			String name) throws Throwable {
		return control.allowed_call_assertNotNull(
				"call Bundle.getEntryPaths(String) " + message,
				"callBundle_getEntryPaths", new Class[] {Bundle.class,
						String.class}, new Object[] {bundle, name});
	}

	// if not ok returns null, not java.lang.SecurityException
	public boolean not_allowed_Bundle_getEntryPaths(String message,
			Bundle bundle, String name) throws Throwable {
		return control.not_allowed_call_assertNull(
				"call Bundle.getEntryPaths(String) " + message,
				"callBundle_getEntryPaths", new Class[] {Bundle.class,
						String.class}, new Object[] {bundle, name});
	}

	public Object allowed_Bundle_loadClass(String message, Bundle bundle,
			String name) throws Exception {
		return control.allowed_call("call Bundle.loadClass(String) " + message,
				"callBundle_loadClass",
				new Class[] {Bundle.class, String.class}, new Object[] {bundle,
						name});
	}

	public boolean not_allowed_Bundle_loadClass(String message, Bundle bundle,
			String name) throws Exception {
		return control.not_allowed_call("call Bundle.loadClass(String) "
				+ message, "callBundle_loadClass", new Class[] {Bundle.class,
				String.class}, new Object[] {bundle, name},
				java.lang.ClassNotFoundException.class);
	}

	public Object allowed_Bundle_stop(String message, Bundle bundle)
			throws Exception {
		return control.allowed_call("call Bundle.stop() " + message,
				"callBundle_stop", new Class[] {Bundle.class},
				new Object[] {bundle});
	}

	public boolean not_allowed_Bundle_stop(String message, Bundle bundle)
			throws Exception {
		return control.not_allowed_call("call Bundle.stop() " + message,
				"callBundle_stop", new Class[] {Bundle.class},
				new Object[] {bundle}, SecurityException.class);
	}

	public Object allowed_Bundle_uninstall(String message, Bundle bundle)
			throws Exception {
		return control.allowed_call("call Bundle.uninstall() " + message,
				"callBundle_uninstall", new Class[] {Bundle.class},
				new Object[] {bundle});
	}

	public boolean not_allowed_Bundle_uninstall(String message, Bundle bundle)
			throws Exception {
		return control.not_allowed_call("call Bundle.uninstall() " + message,
				"callBundle_uninstall", new Class[] {Bundle.class},
				new Object[] {bundle}, SecurityException.class);
	}

	public Object allowed_Bundle_update(String message, Bundle bundle)
			throws Exception {
		return control.allowed_call("call Bundle.update() " + message,
				"callBundle_update", new Class[] {Bundle.class},
				new Object[] {bundle});
	}

	public boolean not_allowed_Bundle_update(String message, Bundle bundle)
			throws Exception {
		return control.not_allowed_call("call Bundle.update() " + message,
				"callBundle_update", new Class[] {Bundle.class},
				new Object[] {bundle}, SecurityException.class);
	}

	public Object allowed_Bundle_update_by_InputStream(String message,
			Bundle bundle, InputStream is) throws Exception {
		return control.allowed_call("call Bundle.update(InputStream) "
				+ message, "callBundle_update", new Class[] {Bundle.class,
				InputStream.class}, new Object[] {bundle, is});
	}

	public boolean not_allowed_Bundle_update_by_InputStream(String message,
			Bundle bundle, InputStream is) throws Exception {
		return control.not_allowed_call("call Bundle.update(InputStream) "
				+ message, "callBundle_update", new Class[] {Bundle.class,
				InputStream.class}, new Object[] {bundle, is},
				SecurityException.class);
	}

	public Object allowed_Bundle_start(String message, Bundle bundle)
			throws Exception {
		return control.allowed_call("call Bundle.start() " + message,
				"callBundle_start", new Class[] {Bundle.class},
				new Object[] {bundle});
	}

	public boolean not_allowed_Bundle_start(String message, Bundle bundle)
			throws Exception {
		return control.not_allowed_call("call Bundle.start() " + message,
				"callBundle_start", new Class[] {Bundle.class},
				new Object[] {bundle}, SecurityException.class);
	}

	public Object allowed_BundleContext_installBundle(String message,
			Bundle bundle, String location) throws Exception {
		return control.allowed_call("call BundleContext.installBundle(String) "
				+ message, "callBundleContext_installBundle", new Class[] {
				BundleContext.class, String.class}, new Object[] {
				bundle.getBundleContext(), location});
	}

	public boolean not_allowed_BundleContext_installBundle(String message,
			Bundle bundle,
			String location) throws Exception {
		return control.not_allowed_call(
				"call BundleContext.installBundle(String) " + message,
				"callBundleContext_installBundle", new Class[] {
						BundleContext.class, String.class}, new Object[] {
						bundle.getBundleContext(), location},
				SecurityException.class);
	}

	public Object allowed_BundleContext_installBundle_by_InputStream(
			String message, Bundle bundle, String location, InputStream is)
			throws Exception {
		return control.allowed_call(
				"call BundleContext.installBundle(String, InputStream) "
						+ message, "callBundleContext_installBundle",
				new Class[] {BundleContext.class, String.class,
						InputStream.class},
 new Object[] {
						bundle.getBundleContext(), location, is});
	}

	public boolean not_allowed_BundleContext_installBundle_by_InputStream(
			String message, Bundle bundle, String location, InputStream is)
			throws Exception {
		return control.not_allowed_call(
				"call BundleContext.installBundle(String, InputStream) "
						+ message, "callBundleContext_installBundle",
				new Class[] {BundleContext.class, String.class,
						InputStream.class},
 new Object[] {
						bundle.getBundleContext(), location, is},
				SecurityException.class);
	}

	public Object allowed_BundleContext_addBundleListener(String message,
			Bundle bundle)
			throws Exception {
		return control.allowed_call(
				"call BundleContext.addBundleListener(SynchronousBundleListener) "
						+ message, "callBundleContext_addBundleListener",
				new Class[] {BundleContext.class}, new Object[] {bundle
						.getBundleContext()});
	}

	public boolean not_allowed_BundleContext_addBundleListener(String message,
			Bundle bundle)
			throws Exception {
		return control.not_allowed_call(
				"call BundleContext.addBundleListener(SynchronousBundleListener) "
						+ message, "callBundleContext_addBundleListener",
				new Class[] {BundleContext.class}, new Object[] {bundle
						.getBundleContext()},
				SecurityException.class);
	}

	public Object allowed_BundleContext_removeBundleListener(String message,
			Bundle bundle)
			throws Exception {
		return control.allowed_call(
				"call BundleContext.removeBundleListener(SynchronousBundleListener) "
						+ message, "callBundleContext_removeBundleListener",
				new Class[] {BundleContext.class}, new Object[] {bundle
						.getBundleContext()});
	}

	public boolean not_allowed_BundleContext_removeBundleListener(
			String message, Bundle bundle)
			throws Exception {
		return control.not_allowed_call(
				"call BundleContext.removeBundleListener(SynchronousBundleListener) "
						+ message, "callBundleContext_removeBundleListener",
				new Class[] {BundleContext.class}, new Object[] {bundle
						.getBundleContext()},
				SecurityException.class);
	}

	public Object allowed_StartLevel_setBundleStartLevel(String message,
			Bundle bundle, int startlevel) throws Exception {
		return control.allowed_call(
				"call StartLevel.setBundleStartLevel(Bundle, int) " + message,
				"callStartLevel_setBundleStartLevel", new Class[] {
						Bundle.class, Integer.class}, new Object[] {bundle,
						Integer.valueOf(startlevel)});
	}

	public boolean not_allowed_StartLevel_setBundleStartLevel(String message,
			Bundle bundle, int startlevel) throws Exception {
		return control.not_allowed_call(
				"call StartLevel.setBundleStartLevel(Bundle, int) " + message,
				"callStartLevel_setBundleStartLevel", new Class[] {
						Bundle.class, Integer.class}, new Object[] {bundle,
						Integer.valueOf(startlevel)}, SecurityException.class);
	}

	public Object allowed_StartLevel_setStartLevel(String message,
			int startlevel) throws Exception {
		return control.allowed_call("call StartLevel.setStartLevel(int) "
				+ message, "callStartLevel_setStartLevel",
				new Class[] {Integer.class}, new Object[] {Integer.valueOf(
						startlevel)});
	}

	public boolean not_allowed_StartLevel_setStartLevel(String message,
			int startlevel) throws Exception {
		return control
				.not_allowed_call("call StartLevel.setBundleStartLevel(int) "
						+ message, "callStartLevel_setStartLevel",
						new Class[] {Integer.class}, new Object[] {Integer.valueOf(
								startlevel)}, SecurityException.class);
	}

	public Object allowed_StartLevel_setInitialBundleStartLevel(String message,
			int startlevel) throws Exception {
		return control.allowed_call(
				"call StartLevel.setInitialBundleStartLevel(int) " + message,
				"callStartLevel_setInitialBundleStartLevel",
				new Class[] {Integer.class}, new Object[] {Integer.valueOf(
						startlevel)});
	}

	public boolean not_allowed_StartLevel_setInitialBundleStartLevel(
			String message, int startlevel) throws Exception {
		return control.not_allowed_call(
				"call StartLevel.setInitialBundleStartLevel(int) " + message,
				"callStartLevel_setInitialBundleStartLevel",
				new Class[] {Integer.class}, new Object[] {Integer.valueOf(
						startlevel)}, SecurityException.class);
	}

	public Object allowed_BundleStartLevel_setStartLevel(String message,
			Bundle bundle, int startlevel) throws Exception {
		return control.allowed_call(
				"call BundleStartLevel.setStartLevel(Bundle, int) " + message,
				"callBundleStartLevel_setStartLevel", new Class[] {
						Bundle.class, Integer.class}, new Object[] {bundle,
						Integer.valueOf(startlevel)});
	}

	public boolean not_allowed_BundleStartLevel_setStartLevel(String message,
			Bundle bundle, int startlevel) throws Exception {
		return control.not_allowed_call(
				"call BundleStartLevel.setStartLevel(Bundle, int) " + message,
				"callBundleStartLevel_setStartLevel", new Class[] {
						Bundle.class, Integer.class}, new Object[] {bundle,
						Integer.valueOf(startlevel)}, SecurityException.class);
	}

	public Object allowed_FrameworkStartLevel_setStartLevel(String message,
			int startlevel) throws Exception {
		return control.allowed_call(
				"call FrameworkStartLevel.setStartLevel(int) " + message,
				"callFrameworkStartLevel_setStartLevel",
				new Class[] {Integer.class}, new Object[] {Integer.valueOf(
						startlevel)});
	}

	public boolean not_allowed_FrameworkStartLevel_setStartLevel(
			String message, int startlevel) throws Exception {
		return control.not_allowed_call(
				"call FrameworkStartLevel.setStartLevel(int) " + message,
				"callFrameworkStartLevel_setStartLevel",
				new Class[] {Integer.class}, new Object[] {Integer.valueOf(
						startlevel)}, SecurityException.class);
	}

	public Object allowed_FrameworkStartLevel_setInitialBundleStartLevel(
			String message, int startlevel) throws Exception {
		return control.allowed_call(
				"call FrameworkStartLevel.setInitialBundleStartLevel(int) "
						+ message,
				"callFrameworkStartLevel_setInitialBundleStartLevel",
				new Class[] {Integer.class}, new Object[] {Integer.valueOf(
						startlevel)});
	}

	public boolean not_allowed_FrameworkStartLevel_setInitialBundleStartLevel(
			String message, int startlevel) throws Exception {
		return control.not_allowed_call(
				"call FrameworkStartLevel.setInitialBundleStartLevel(int) "
						+ message,
				"callFrameworkStartLevel_setInitialBundleStartLevel",
				new Class[] {Integer.class}, new Object[] {Integer.valueOf(
						startlevel)}, SecurityException.class);
	}

	public Object allowed_PermissionAdmin_setPermissions(String message,
			String location, PermissionInfo[] permissions) throws Exception {
		return control.allowed_call(
				"call PermissionAdmin.setPermissions(String, PermissionInfo[]) "
						+ message, "callPermissionAdmin_setPermissions",
				new Class[] {String.class, PermissionInfo[].class},
				new Object[] {location, permissions});
	}

	public boolean not_allowed_PermissionAdmin_setPermissions(String message,
			String location, PermissionInfo[] permissions) throws Exception {
		return control.not_allowed_call(
				"call PermissionAdmin.setPermissions(String, PermissionInfo[]) "
						+ message, "callPermissionAdmin_setPermissions",
				new Class[] {String.class, PermissionInfo[].class},
				new Object[] {location, permissions}, SecurityException.class);
	}

	public Object allowed_PermissionAdmin_setDefaultPermissions(String message,
			PermissionInfo[] permissions) throws Exception {
		return control.allowed_call(
				"call PermissionAdmin.setDefaultPermissions(PermissionInfo[]) "
						+ message, "callPermissionAdmin_setDefaultPermissions",
				new Class[] {PermissionInfo[].class},
				new Object[] {permissions});
	}

	public boolean not_allowed_PermissionAdmin_setDefaultPermissions(
			String message, PermissionInfo[] permissions) throws Exception {
		return control.not_allowed_call(
				"call PermissionAdmin.setDefaultPermissions(PermissionInfo[]) "
						+ message, "callPermissionAdmin_setDefaultPermissions",
				new Class[] {PermissionInfo[].class},
				new Object[] {permissions}, SecurityException.class);
	}

	public Object allowed_PackageAdmin_refreshPackages(String message,
			Bundle[] bundles) throws Exception {
		return control.allowed_call(
				"call PackageAdmin.refreshPackages(Bundle[]) " + message,
				"callPackageAdmin_refreshPackages",
				new Class[] {Bundle[].class}, new Object[] {bundles});
	}

	public boolean not_allowed_PackageAdmin_refreshPackages(String message,
			Bundle[] bundles) throws Exception {
		return control.not_allowed_call(
				"call PackageAdmin.refreshPackages(Bundle[]) " + message,
				"callPackageAdmin_refreshPackages",
				new Class[] {Bundle[].class}, new Object[] {bundles},
				SecurityException.class);
	}

	public Object allowed_PackageAdmin_resolveBundles(String message,
			Bundle[] bundles) throws Exception {
		return control.allowed_call(
				"call PackageAdmin.resolveBundles(Bundle[]) " + message,
				"callPackageAdmin_resolveBundles",
				new Class[] {Bundle[].class}, new Object[] {bundles});
	}

	public boolean not_allowed_PackageAdmin_resolveBundles(String message,
			Bundle[] bundles) throws Exception {
		return control.not_allowed_call(
				"call PackageAdmin.resolveBundles(Bundle[]) " + message,
				"callPackageAdmin_resolveBundles",
				new Class[] {Bundle[].class}, new Object[] {bundles},
				SecurityException.class);
	}

	public Object allowed_FrameworkWiring_refreshBundles(String message,
			Bundle... bundles) throws Exception {
		return control.allowed_call(
				"call FrameworkWiring.refreshBundles(Bundle...) " + message,
				"callFrameworkWiring_refreshBundles",
				new Class[] {Bundle[].class}, new Object[] {bundles});
	}

	public boolean not_allowed_FrameworkWiring_refreshBundles(String message,
			Bundle... bundles) throws Exception {
		return control.not_allowed_call(
				"call FrameworkWiring.refreshBundles(Bundle...) " + message,
				"callFrameworkWiring_refreshBundles",
				new Class[] {Bundle[].class}, new Object[] {bundles},
				SecurityException.class);
	}

	public Object allowed_FrameworkWiring_resolveBundles(String message,
			Bundle... bundles) throws Exception {
		return control.allowed_call(
				"call FrameworkWiring.resolveBundles(Bundle[]) " + message,
				"callFrameworkWiring_resolveBundles",
				new Class[] {Bundle[].class}, new Object[] {bundles});
	}

	public boolean not_allowed_FrameworkWiring_resolveBundles(String message,
			Bundle... bundles) throws Exception {
		return control.not_allowed_call(
				"call FrameworkWiring.resolveBundles(Bundle[]) " + message,
				"callFrameworkWiring_resolveBundles",
				new Class[] {Bundle[].class}, new Object[] {bundles},
				SecurityException.class);
	}

	public Object allowed_ConfigurationAdmin_getConfiguration(String message,
			String pid) throws Exception {
		return control.allowed_call(
				"call ConfigurationAdmin.getConfiguration(String) " + message,
				"callConfigurationAdmin_getConfiguration",
				new Class[] {String.class}, new Object[] {pid});
	}

	public boolean not_allowed_ConfigurationAdmin_getConfiguration(
			String message, String pid) throws Exception {
		return control.not_allowed_call(
				"call ConfigurationAdmin.getConfiguration(String) " + message,
				"callConfigurationAdmin_getConfiguration",
				new Class[] {String.class}, new Object[] {pid},
				SecurityException.class);
	}

	public Object allowed_ConfigurationAdmin_getConfiguration(String message,
			String pid, String location) throws Exception {
		return control.allowed_call(
				"call ConfigurationAdmin.getConfiguration(String, String) "
						+ message, "callConfigurationAdmin_getConfiguration",
				new Class[] {String.class, String.class}, new Object[] {pid,
						location});
	}

	public boolean not_allowed_ConfigurationAdmin_getConfiguration(
			String message, String pid, String location) throws Exception {
		return control.not_allowed_call(
				"call ConfigurationAdmin.getConfiguration(String, String) "
						+ message, "callConfigurationAdmin_getConfiguration",
				new Class[] {String.class, String.class}, new Object[] {pid,
						location}, SecurityException.class);
	}

	// TO DO SecurityException ???
	public Object allowed_ConfigurationAdmin_listConfigurations(String message,
			String filter) throws Exception {
		return control
				.allowed_call(
						"call ConfigurationAdmin.listConfigurations(String) "
								+ message,
						"callConfigurationAdmin_listConfigurations",
						new Class[] {String.class}, new Object[] {filter});
	}

	// TO DO SecurityException ???
	public boolean not_allowed_ConfigurationAdmin_listConfigurations(
			String message, String filter) throws Exception {
		return control
				.not_allowed_call(
						"call ConfigurationAdmin.listConfigurations(String) "
								+ message,
						"callConfigurationAdmin_listConfigurations",
						new Class[] {String.class}, new Object[] {filter},
						SecurityException.class);
	}

	public Object allowed_ConfigurationAdmin_createFactoryConfiguration(
			String message, String factoryPid) throws Exception {
		return control.allowed_call(
				"call ConfigurationAdmin.createFactoryConfiguration(String) "
						+ message,
				"callConfigurationAdmin_createFactoryConfiguration",
				new Class[] {String.class}, new Object[] {factoryPid});
	}

	public boolean not_allowed_ConfigurationAdmin_createFactoryConfiguration(
			String message, String factoryPid) throws Exception {
		return control.not_allowed_call(
				"call ConfigurationAdmin.createFactoryConfiguration(String) "
						+ message,
				"callConfigurationAdmin_createFactoryConfiguration",
				new Class[] {String.class}, new Object[] {factoryPid},
				SecurityException.class);
	}

	public Object allowed_ConfigurationAdmin_createFactoryConfiguration(
			String message, String factoryPid, String location)
			throws Exception {
		return control.allowed_call(
				"call ConfigurationAdmin.createFactoryConfiguration(String, String) "
						+ message,
				"callConfigurationAdmin_createFactoryConfiguration",
				new Class[] {String.class, String.class}, new Object[] {
						factoryPid, location});
	}

	public boolean not_allowed_ConfigurationAdmin_createFactoryConfiguration(
			String message, String factoryPid, String location)
			throws Exception {
		return control.not_allowed_call(
				"call ConfigurationAdmin.createFactoryConfiguration(String, String) "
						+ message,
				"callConfigurationAdmin_createFactoryConfiguration",
				new Class[] {String.class, String.class}, new Object[] {
						factoryPid, location}, SecurityException.class);
	}

	public Object allowed_Configuration_delete(String message, String pid)
			throws Exception {
		return control.allowed_call("call Configuration.delete() " + message,
				"callConfiguration_delete", new Class[] {String.class},
				new Object[] {pid});
	}

	public boolean not_allowed_Configuration_delete(String message, String pid)
			throws Exception {
		return control.not_allowed_call("call Configuration.delete() "
				+ message, "callConfiguration_delete",
				new Class[] {String.class}, new Object[] {pid},
				SecurityException.class);
	}

	public Object allowed_Configuration_update(String message, String pid)
			throws Exception {
		return control.allowed_call("call Configuration.update() " + message,
				"callConfiguration_update", new Class[] {String.class},
				new Object[] {pid});
	}

	public boolean not_allowed_Configuration_update(String message, String pid)
			throws Exception {
		return control.not_allowed_call("call Configuration.update() "
				+ message, "callConfiguration_update",
				new Class[] {String.class}, new Object[] {pid},
				SecurityException.class);
	}

	public Object allowed_Configuration_update(String message, String pid,
			Dictionary< ? , ? > properties) throws Exception {
		return control
				.allowed_call("call Configuration.update(Dictionary) "
						+ message, "callConfiguration_update", new Class[] {
						String.class, Dictionary.class}, new Object[] {pid,
						properties});
	}

	public boolean not_allowed_Configuration_update(String message, String pid,
			Dictionary< ? , ? > properties) throws Exception {
		return control.not_allowed_call(
				"call Configuration.update(Dictionary) " + message,
				"callConfiguration_update", new Class[] {String.class,
						Dictionary.class}, new Object[] {pid, properties},
				SecurityException.class);
	}

	public Object allowed_Configuration_setBundleLocation(String message,
			String pid) throws Exception {
		return control.allowed_call(
				"call Configuration.setBundleLocation(String) " + message,
				"callConfiguration_setBundleLocation",
				new Class[] {String.class}, new Object[] {pid});
	}

	public boolean not_allowed_Configuration_setBundleLocation(String message,
			String pid) throws Exception {
		return control.not_allowed_call(
				"call Configuration.setBundleLocation(String) " + message,
				"callConfiguration_setBundleLocation",
				new Class[] {String.class}, new Object[] {pid},
				SecurityException.class);
	}

	List<PermissionInfo> createWildcardPermissionInfo(
			Class< ? extends Permission> permission, String name,
			String action, String value) {
		List<PermissionInfo> infos = new ArrayList<>();
		if (value == null)
			return infos;
		String key = "";
		if (permission.getName().equals(AdminPermission.class.getName())) {
			key = name + "=";
		}

		PermissionInfo info = new PermissionInfo(permission.getName(), key
				+ value, action);
		infos.add(info);

		int index = value.indexOf(".");
		int lastIndex;

		while (index != -1) {
			lastIndex = index + 1;
			info = new PermissionInfo(permission.getName(), key
					+ value.substring(0, lastIndex) + "*", action);
			infos.add(info);
			index = value.indexOf(".", lastIndex);
		}

		return infos;
	}

	List<PermissionInfo> getPInfosForAdminPermisssion(String action,
			long bundleId,
			String location, String symbolicName) {
		List<PermissionInfo> permissions = new ArrayList<>();

		PermissionInfo info = new PermissionInfo(AdminPermission.class
				.getName(), "*", action);
		permissions.add(info);

		if (bundleId != -1) {
			info = new PermissionInfo(AdminPermission.class.getName(), "(" + ID
					+ "=" + bundleId + ")", action);
			permissions.add(info);
		}

		if (location != null) {
			info = new PermissionInfo(AdminPermission.class.getName(), "("
					+ LOCATION + "=" + location + ")", action);
			permissions.add(info);
		}

		if (symbolicName != null) {
			info = new PermissionInfo(AdminPermission.class.getName(), "("
					+ NAME + "=" + symbolicName + ")", action);
			permissions.add(info);
		}
		//		
		// permissions.addAll(getSignerFilter(action));

		return permissions;
	}

	List<PermissionInfo> getSignerFilter(String action) {
		List<String> dns = createWildcardDNs(SignatureResource.getString(DN_S));
		List<PermissionInfo> infos = new ArrayList<>();
		PermissionInfo info;
		for (int i = 0; i < dns.size(); ++i) {
			info = new PermissionInfo(AdminPermission.class.getName(), SIGNER
					+ "=" + dns.get(i), action);
			infos.add(info);
		}

		return infos;
	}

	private List<String> createWildcardDNs(String value) {
		List<String> result = new ArrayList<>();
		String semicolon = ";";

		int lastIndex = 0;
		int semicolonIndex = value.indexOf(semicolon);
		String element;
		String prefix;
		String suffix;
		while (semicolonIndex != -1) {
			prefix = value.substring(0, lastIndex);
			element = value.substring(lastIndex, semicolonIndex);
			suffix = value.substring(semicolonIndex);

			result
					.addAll(addVector(createWildcardRDNs(element), prefix,
							suffix));

			lastIndex = semicolonIndex + 1;
			semicolonIndex = value.indexOf(semicolon, lastIndex);
		}

		if (lastIndex > 0) {
			prefix = value.substring(0, lastIndex);
			element = value.substring(lastIndex);

			result.addAll(addVector(createWildcardRDNs(element), prefix, ""));
		}
		else {
			result.addAll(createWildcardRDNs(value));
		}

		return result;
	}

	private List<String> addVector(List<String> vec, String prefix,
			String suffix) {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < vec.size(); ++i) {
			result.add(prefix + vec.get(i) + suffix);
		}

		return result;
	}

	private List<String> createWildcardRDNs(String value) {
		List<String> result = new ArrayList<>();
		String comma = ",";
		String equal = "=";
		String asterisk = "*";

		int lastIndex = 0;
		int commaIndex = value.indexOf(comma, lastIndex);
		int equalIndex = value.indexOf(equal, lastIndex);

		while (commaIndex != -1) {
			result.add(asterisk + value.substring(commaIndex));
			if ((equalIndex != -1) && (equalIndex < commaIndex)) {
				result.add(value.substring(0, equalIndex + 1) + asterisk
						+ value.substring(commaIndex));
			}
			lastIndex = commaIndex + 1;
			commaIndex = value.indexOf(comma, lastIndex);
			equalIndex = value.indexOf(equal, lastIndex);
		}

		if (lastIndex > 0) {
			result.add(asterisk);
		}
		if (equalIndex > 0) {
			result.add(value.substring(0, equalIndex + 1) + asterisk);
		}

		return result;
	}
}

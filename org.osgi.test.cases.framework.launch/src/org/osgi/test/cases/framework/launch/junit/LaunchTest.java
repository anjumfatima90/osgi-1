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
package org.osgi.test.cases.framework.launch.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.connect.ConnectFrameworkFactory;
import org.osgi.framework.connect.ModuleConnector;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public abstract class LaunchTest extends OSGiTestCase {
	private static final Pattern	QUOTED_P			= Pattern
			.compile("^([\"'])(.*)\\1$");
	private static final String FRAMEWORK_FACTORY = "/META-INF/services/org.osgi.framework.launch.FrameworkFactory";
	private static final String		CONNECT_FRAMEWORK_FACTORY	= "/META-INF/services/org.osgi.framework.connect.ConnectFrameworkFactory";
	private static final String	STORAGEROOT	= "org.osgi.test.cases.framework.launch.storageroot";

	static private class FrameworkClassLoader extends URLClassLoader {
		// All java.* and org.osgi.* packages need to be parent only delegation
		private static final String[] PARENT_ONLY_DELEGATION = {
				"java.", "org.osgi."
		};

		public FrameworkClassLoader(URL[] urls, ClassLoader parent) {
			super(urls, parent);
			assertNotNull("Cannot have null parent.", parent);
		}

		@Override
		protected Class< ? > loadClass(String name, boolean resolve)
				throws ClassNotFoundException {
			Class< ? > clazz = findLoadedClass(name);
			if (clazz != null)
				return clazz;

			if (childFirst(name)) {
				try {
					clazz = findClass(name);
				} catch (ClassNotFoundException e) {
					// continue to parent
				}
			}
			if (clazz == null) {
				try {
					clazz = getParent().loadClass(name);
				} catch (ClassNotFoundException e) {
					// it may be a class from an extension; need to look locally
					if (!childFirst(name)) {
						clazz = findClass(name);
					}
				}
			}

			if (resolve) {
				resolveClass(clazz);
			}
			return clazz;
		}

		private static boolean childFirst(String name) {
			for (int i = PARENT_ONLY_DELEGATION.length - 1; i >= 0; i--) {
				if (name.startsWith(PARENT_ONLY_DELEGATION[i])) {
					return false;
				}
			}
			// all other packages are considered child first
			return true;

		}
	}

	static protected class LaunchFrameworkFactory
			implements FrameworkFactory, ConnectFrameworkFactory {
		private final String			frameworkFactoryClassName;
		private final String			connectFrameworkFactoryClassName;
		private FrameworkClassLoader	frameworkClassLoader;
		private FrameworkFactory		frameworkFactory;
		private ConnectFrameworkFactory	connectFrameworkFactory;

		public LaunchFrameworkFactory(String frameworkFactoryClassName,
				String connectFrameworkFactoryClassName) {
			this.frameworkFactoryClassName = frameworkFactoryClassName;
			this.connectFrameworkFactoryClassName = connectFrameworkFactoryClassName;
		}
		@Override
		public Framework newFramework(Map<String,String> configuration) {
			return getFrameworkFactory().newFramework(configuration);
		}

		@Override
		public Framework newFramework(Map<String,String> configuration,
				ModuleConnector connectFramework) {
			ConnectFrameworkFactory factory = getConnectFrameworkFactory();
			if (factory == null) {
				// TODO will need to move the connect tests to another project
				// if we make it optional
				fail("No ConnectFrameworkFactory");
			}
			return factory.newFramework(configuration, connectFramework);
		}
		public void close() {
			if (frameworkClassLoader != null) {
				try {
					frameworkClassLoader.close();
				} catch (IOException e) {
					DefaultTestBundleControl
							.trace("Error closing framework class loader: "
									+ e.getMessage());
					e.printStackTrace();
				}
				frameworkClassLoader = null;
				frameworkFactory = null;
			}
		}

		private FrameworkFactory getFrameworkFactory() {
			if (frameworkFactory != null) {
				return frameworkFactory;
			}
			try {
				if (frameworkClassLoader == null) {
					frameworkClassLoader = createFrameworkClassLoader();
				}
				@SuppressWarnings("unchecked")
				Class<FrameworkFactory> clazz = (Class<FrameworkFactory>) frameworkClassLoader
						.loadClass(frameworkFactoryClassName);
				frameworkFactory = clazz.getConstructor().newInstance();
			} catch (Exception e) {
				fail("Failed to get the framework constructor", e);
			}
			return frameworkFactory;
		}

		private ConnectFrameworkFactory getConnectFrameworkFactory() {
			if (connectFrameworkFactoryClassName == null) {
				return null;
			}
			if (connectFrameworkFactory != null) {
				return connectFrameworkFactory;
			}
			try {
				if (frameworkClassLoader == null) {
					frameworkClassLoader = createFrameworkClassLoader();
				}
				@SuppressWarnings("unchecked")
				Class<ConnectFrameworkFactory> clazz = (Class<ConnectFrameworkFactory>) frameworkClassLoader
						.loadClass(connectFrameworkFactoryClassName);
				connectFrameworkFactory = clazz.getConstructor().newInstance();
			} catch (Exception e) {
				fail("Failed to get the framework constructor", e);
			}
			return connectFrameworkFactory;
		}
	}

	protected LaunchFrameworkFactory	frameworkFactory;
	private List<String>			rootBundles	= new LinkedList<String>();
	protected String				rootStorageArea;

	protected void setUp() throws Exception {
		super.setUp();
		rootStorageArea = getStorageAreaRoot();
		assertNotNull("No storage area root found", rootStorageArea);
		File rootFile = new File(rootStorageArea);
		assertFalse(
				"Root storage area is not a directory: " + rootFile.getPath(),
				rootFile.exists() && !rootFile.isDirectory());
		if (!rootFile.isDirectory())
			assertTrue(
					"Could not create root directory: " + rootFile.getPath(),
					rootFile.mkdirs());
		String frameworkFactoryClassName = getFrameworkFactoryClassName();
		assertNotNull("Could not find framework factory class", frameworkFactoryClassName);
		String connectFrameworkFactoryClassName = getConnectFrameworkFactoryClassName(); // allow
																							// null
		frameworkFactory = new LaunchFrameworkFactory(
				frameworkFactoryClassName, connectFrameworkFactoryClassName);
		StringTokenizer st = new StringTokenizer(getProperty(
				"org.osgi.test.cases.framework.launch.bundles", ""), ",");
		rootBundles.clear();
		while (st.hasMoreTokens()) {
			String bundle = st.nextToken();
			assertNotNull(bundle);
			rootBundles.add(bundle);
		}
	}

	static FrameworkClassLoader createFrameworkClassLoader()
			throws MalformedURLException {

		// Since bnd 4.3.0 the framework is on the launcher.runpath system
		// property.
		// TODO Remove the old approach one day.
		String pathProp = System.getProperty("launcher.runpath",
				System.getProperty("java.class.path"));

		Matcher matcher = QUOTED_P.matcher(pathProp);
		if (matcher.matches()) {
			pathProp = matcher.group(2);
		}

		String[] classpaths = pathProp
				.split("\\s*[," + File.pathSeparator + "]\\s*");

		List<URL> frameworkImpl = new ArrayList<>();
		for (String classpath : classpaths) {
			File file = new File(classpath);
			if (!file.getName().startsWith("biz.aQute")) {
				frameworkImpl.add(file.toURI().toURL());
			}
		}
		return new FrameworkClassLoader(frameworkImpl.toArray(new URL[0]),
				LaunchTest.class.getClassLoader());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		frameworkFactory.close();
	}

	protected Framework createFramework(Map<String, String> configuration) {
		Framework framework = null;
		try {
			framework = frameworkFactory.newFramework(configuration);
		}
		catch (Exception e) {
			fail("Failed to construct the framework", e);
		}
		assertEquals("Wrong state for newly constructed framework", Bundle.INSTALLED, framework.getState());
		return framework;
	}
	
	protected URL getBundleInput(String bundle) {
		return getClass().getResource(bundle);
	}
	
	protected void initFramework(Framework framework,
			FrameworkListener... listeners) {
		boolean unintialized = (framework.getState() & (Framework.INSTALLED | Framework.RESOLVED)) != 0;
		try {
			if (listeners == null || listeners.length == 0) {
				framework.init();
			} else {
				framework.init(listeners);
			}
			assertNotNull("BundleContext is null after init", framework.getBundleContext());
		}
		catch (BundleException e) {
			fail("Unexpected BundleException initializing", e);
		}
		if (unintialized) {
			installRootBundles(framework);
		}
		assertEquals("Wrong framework state after init", Bundle.STARTING, framework.getState());
	}
	
	protected Bundle installBundle(Framework framework, String bundle) throws BundleException, IOException {
		return installBundle(framework, bundle, bundle);
	}
	
	protected Bundle installBundle(Framework framework, String bundle, String location) throws BundleException, IOException {
		BundleContext fwkContext = framework.getBundleContext();
		assertNotNull("Framework context is null", fwkContext);
		URL input = getBundleInput(bundle);
		assertNotNull("Cannot find resource: " + bundle, input);
		return fwkContext.installBundle(location, input.openStream());
	}
	
	protected void startFramework(Framework framework) {
		boolean unintialized = (framework.getState() & (Framework.INSTALLED | Framework.RESOLVED)) != 0;
		try {
			framework.start();
			assertNotNull("BundleContext is null after start", framework.getBundleContext());
		}
		catch (BundleException e) {
			fail("Unexpected BundleException initializing", e);
		}
		if (unintialized) {
			installRootBundles(framework);
		}
		assertEquals("Wrong framework state after init", Bundle.ACTIVE, framework.getState());

	}
	
	protected void stopFramework(Framework framework) {
		int previousState = framework.getState();
		try {
            framework.stop();
			FrameworkEvent event = framework.waitForStop(10000);
			assertNotNull("FrameworkEvent is null", event);
			assertEquals("Wrong event type", FrameworkEvent.STOPPED, event.getType());
			assertNull("BundleContext is not null after stop", framework.getBundleContext());
		}
		catch (BundleException e) {
			fail("Unexpected BundleException stopping", e);
		}
		catch (InterruptedException e) {
			fail("Unexpected InterruptedException waiting for stop", e);
		}
		// if the framework was not STARTING STOPPING or ACTIVE then we assume the waitForStop returned immediately with a FrameworkEvent.STOPPED
		// and does not change the state of the framework
		int expectedState = (previousState & (Bundle.STARTING | Bundle.ACTIVE | Bundle.STOPPING)) != 0 ? Bundle.RESOLVED : previousState;
		assertEquals("Wrong framework state after stop", expectedState,
				framework.getState());
	}
	
	private String getClassName(URL factoryService) throws IOException {
		InputStream in = factoryService.openStream();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			for (String line = br.readLine(); line != null; line=br.readLine()) {
				int pound = line.indexOf('#');
				if (pound >= 0)
					line = line.substring(0, pound);
				line.trim();
				if (!"".equals(line))
					return line;
			}
		} finally {
			try {
				if (br != null)
					br.close();
			}
			catch (IOException e) {
				// did our best; just ignore
			}
		}
		return null;
	}
	
	private String getFrameworkFactoryClassName() throws IOException {
		URL factoryService = getClass().getResource(FRAMEWORK_FACTORY);
		assertNotNull("Could not locate: " + FRAMEWORK_FACTORY, factoryService);
		return getClassName(factoryService);
	}

	private String getConnectFrameworkFactoryClassName() throws IOException {
		URL factoryService = getClass().getResource(CONNECT_FRAMEWORK_FACTORY);
		assertNotNull("Could not locate: " + CONNECT_FRAMEWORK_FACTORY,
				factoryService);
		return getClassName(factoryService);
	}

	private void installRootBundles(Framework framework) {
		List<Bundle> bundles = new LinkedList<Bundle>();

		BundleContext fwkContext = framework.getBundleContext();
		assertNotNull("Framework context is null", fwkContext);
		for (String bundle : rootBundles) {
			try {
				Bundle b = fwkContext.installBundle("file:" + bundle);
				assertNotNull("Cannot install bundle: " + bundle, b);
				System.out.println("installed bundle " + b.getSymbolicName()
						+ " " + b.getVersion());
				bundles.add(b);
			}
			catch (BundleException e) {
				e.printStackTrace();
				fail("Unexpected BundleException installing root ", e);
			}
		}

		for (Bundle b : bundles) {
			if (b.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
				try {
					b.start();
				}
				catch (BundleException e) {
					fail("Unexpected BundleException starting root ", e);
				}
				System.out.println("started bundle " + b.getSymbolicName());
			}
		}
	}

	private String getStorageAreaRoot() {
		String storageroot = getProperty(STORAGEROOT);
			assertNotNull("Must set property: " + STORAGEROOT, storageroot);
			return storageroot;
	}

	protected File getStorageArea(String testName, boolean delete) {
		File storageArea = new File(rootStorageArea, testName);
		if (delete) {
			assertTrue("Could not clean up storage area: " + storageArea.getPath(), delete(storageArea));
			assertTrue("Could not create storage area directory: " + storageArea.getPath(), storageArea.mkdirs());
		}
		return storageArea;
	}

	protected boolean delete(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				String list[] = file.list();
				if (list != null) {
					int len = list.length;
					for (int i = 0; i < len; i++)
						if (!delete(new File(file, list[i])))
							return false;
				}
			}
	
			return file.delete();
		}
		return (true);
	}

	protected Map<String, String> getConfiguration(String testName) {
		return getConfiguration(testName, true);
	}

	protected Map<String, String> getConfiguration(String testName, boolean delete) {
		Map<String, String> configuration = new HashMap<String, String>();
		if (testName != null)
			configuration.put(Constants.FRAMEWORK_STORAGE, getStorageArea(testName, delete).getAbsolutePath());
		return configuration;
	}
}

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

package org.osgi.test.cases.framework.junit.div;

import java.util.Dictionary;
import java.util.Locale;

import org.osgi.framework.Bundle;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Test the method Bundle.getHeaders() and Bundle.getHeaders(Locale).
 * 
 * @author hmm@cesar.org.br
 * 
 * @author $Id$
 */
public class ManifestLocalizationTests extends DefaultTestBundleControl {
	private static final String	basePkg										= DivTests.basePkg;

	private String[]			manifestHeadersKeys							= {
			"Bundle-Name", "Bundle-Description", "Bundle-Vendor",
			"Bundle-Version", "Bundle-DocURL", "Bundle-ContactAddress",
			"Bundle-Activator", "Bundle-Category", "Bundle-Copyright"		};

	private String[]			tb1_manifestHeadersValues					= {
			basePkg + "tb1", "Contains the manifest checked by the test case.",
			"Ericsson Radio Systems AB",
			"Improper value for bundle manifest version 2",
			"http://www.ericsson.com", "info@ericsson.com",
			basePkg + "tb1.CheckManifest",
			"should contain the bundle category",
			"should contain the bundle copyright"							};

	private String[]			tb8_manifestHeadersValues_default			= {
			basePkg + "tb8",
			"Contains the manifest headers localized by bundle.properties test case.",
			"CESAR.ORG", "1.0", "http://www.cesar.org.br", "info@cesar.org.br",
			basePkg + "tb8.CheckManifestGetHeaders",
			"Should contain the bundle category for tb8",
			"Should contain the bundle copyright for tb8"					};

	private String[]			tb9_manifestHeadersValues_en				= {
			basePkg + "tb9",
			"Contains the manifest headers localized by bundle_en.properties test case.",
			"CESAR.ORG", "1.0", "http://www.cesar.org.br", "info@cesar.org.br",
			basePkg + "tb9.CheckManifestGetHeadersLocale",
			"Should contain the bundle category for tb9",
			"Should contain the bundle copyright for tb9"					};

	private String[]			tb9_manifestHeadersValues_en_US				= {
			basePkg + "tb9",
			"Contains the manifest headers localized by bundle_en_US.properties test case.",
			"CESAR.ORG", "1.0", "http://www.cesar.org.br", "info@cesar.org.br",
			basePkg + "tb9.CheckManifestGetHeadersLocale",
			"Should contain the bundle category for tb9",
			"Should contain the bundle copyright for tb9"					};

	private String[]			tb9_manifestHeadersValues_rawHeaders		= {
			"%bundlename", "%bundledescription", "%bundlevendor", "1.0",
			"%docurl", "%contactinfo",
			basePkg + "tb9.CheckManifestGetHeadersLocale", "%bundlecategory",
			"%bundlecopyright"												};

	private String[]			tb9_manifestHeadersValues_pt_BR				= {
			basePkg + "tb9",
			"Contains the manifest headers localized by bundle_pt_BR.properties test case.",
			"CESAR.ORG", "1.0", "http://www.cesar.org.br", "info@cesar.org.br",
			basePkg + "tb9.CheckManifestGetHeadersLocale",
			"Should contain the bundle category for tb9",
			"Should contain the bundle copyright for tb9"					};

	private String[]			tb9_manifestHeadersValues_missingLocale		= {
			"bundlename", "bundledescription", "bundlevendor", "1.0", "docurl",
			"contactinfo", basePkg + "tb9.CheckManifestGetHeadersLocale",
			"bundlecategory", "bundlecopyright"								};

	private String[]			tb14_manifestHeadersKeys					= {
			"Bundle-Name", "Bundle-Description", "Bundle-Vendor",
			"Bundle-Version", "Bundle-DocURL", "Bundle-ContactAddress",
			"Bundle-Category", "Bundle-Copyright"							};

	private String[]			tb14_manifestHeadersValues_en_US			= {
			basePkg + "tb14",
			"Contains the manifest headers localized by bundle_en_US.properties test case.",
			"CESAR.ORG", "1.0.1", "http://www.cesar.org.br",
			"info@cesar.org.br", "Should contain the bundle category for tb14",
			"Should contain the bundle copyright for tb14"					};

	private String[]			tb14_manifestHeadersValues_pt_BR			= {
			basePkg + "tb14",
			"Contains the manifest headers localized by bundle_pt_BR.properties test case.",
			"CESAR.ORG", "1.0.1", "http://www.cesar.org.br",
			"info@cesar.org.br", "Should contain the bundle category for tb14",
			"Should contain the bundle copyright for tb14"					};

	private String[]			tb14_manifestHeadersValues_es_ES			= {
			basePkg + "tb23",
			"Contains the manifest headers localized by bundle_es_ES.properties test case.",
			"CESAR.ORG", "1.0.1", "http://www.cesar.org.br",
			"info@cesar.org.br", "Should contain the bundle category for tb23",
			"Should contain the bundle copyright for tb23"					};

	private String[]			tb14_manifestHeadersValues_missingLocale	= {
			"bundlename", "bundledescription", "bundlevendor", "1.0.1",
			"docurl", "contactinfo", "bundlecategory", "bundlecopyright"	};

	/**
	 * Run tests of this class
	 */
	private Locale				defaultLocale;

	protected void setUp() throws Exception {
		defaultLocale = Locale.getDefault();
	}

	protected void tearDown() throws Exception {
		Locale.setDefault(defaultLocale);
	}

	/**
	 * Tests manifest headers localization for a bundle that does not have
	 * locale file.
	 * 
	 * @spec Bundle.getHeaders()
	 */
	public void testGetHeaders001() throws Exception {

		Bundle tb1 = getContext().installBundle(getWebServer() + "div.tb1.jar");
		try {
			tb1.start();
			Dictionary<String,String> h = tb1.getHeaders();

			for (int i = 0; i < tb1_manifestHeadersValues.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb1_manifestHeadersValues[i], h
								.get(manifestHeadersKeys[i]));
			}
		}
		finally {
			tb1.uninstall();
		}
	}

	/**
	 * Tests manifest headers localization for a bundle that has the default
	 * locale properties file, as defined by
	 * Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME.
	 * 
	 * @spec Bundle.getHeaders()
	 */
	public void testGetHeaders002() throws Exception {

		Bundle tb8 = getContext().installBundle(getWebServer() + "div.tb8.jar");
		Dictionary<String,String> h;
		try {
			tb8.start();
			h = tb8.getHeaders();

			for (int i = 0; i < tb8_manifestHeadersValues_default.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb8_manifestHeadersValues_default[i], h
								.get(manifestHeadersKeys[i]));
			}

			tb8.stop();
			tb8.uninstall();
			// After the bundle has been uninstalled, it should return manifest
			// headers
			// localized for the default locale at the time the bundle was
			// uninstalled.
			h = tb8.getHeaders();
			tb8 = null;

		}
		finally {
			if (tb8 != null) {
				tb8.uninstall();
			}
		}

		for (int i = 0; i < tb8_manifestHeadersValues_default.length; i++) {
			assertEquals("Manifest header localization does not match",
					tb8_manifestHeadersValues_default[i], h
							.get(manifestHeadersKeys[i]));
		}
	}

	/**
	 * This method tests manifest headers localization for a bundle that has
	 * specific locale files including the default locale.
	 * 
	 * @spec Bundle.getHeaders()
	 */
	public void testGetHeaders003() throws Exception {
		// specify default locale
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb9 = getContext().installBundle(getWebServer() + "div.tb9.jar");
		try {

			tb9.start();
			Dictionary<String,String> h = tb9.getHeaders();

			for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb9_manifestHeadersValues_en_US[i], h
								.get(manifestHeadersKeys[i]));
			}

			// If the specified locale is null then the locale returned by
			// java.util.Locale.getDefault is used.
			h = tb9.getHeaders(null);
			for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb9_manifestHeadersValues_en_US[i], h
								.get(manifestHeadersKeys[i]));
			}

			// If the specified locale is the empty string, this method
			// will return the raw (unlocalized) manifest headers including any
			// leading ‘%’
			h = tb9.getHeaders("");

			for (int i = 0; i < tb9_manifestHeadersValues_rawHeaders.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb9_manifestHeadersValues_rawHeaders[i], h
								.get(manifestHeadersKeys[i]));
			}

			tb9.stop();
			tb9.uninstall();
			Locale.setDefault(new Locale("fr", "FR"));
			// After the bundle has been uninstalled, it should return manifest
			// headers
			// localized for the default locale at the time the bundle was
			// uninstalled.
			h = tb9.getHeaders();
			tb9 = null;
			for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb9_manifestHeadersValues_en_US[i], h
								.get(manifestHeadersKeys[i]));
			}
		}
		finally {
			if (tb9 != null) {
				tb9.uninstall();
			}
		}
	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files but does not include the default locale.
	 * 
	 * @spec Bundle.getHeaders()
	 */
	public void testGetHeaders004() throws Exception {
		Locale.setDefault(new Locale("pt", "BR"));
		Bundle tb9 = getContext().installBundle(getWebServer() + "div.tb9.jar");
		try {
			tb9.start();
			Dictionary<String,String> h = tb9.getHeaders();

			for (int i = 0; i < tb9_manifestHeadersValues_missingLocale.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb9_manifestHeadersValues_missingLocale[i], h
								.get(manifestHeadersKeys[i]));
			}
		}
		finally {
			tb9.uninstall();
		}
	}

	/**
	 * Tests manifest headers localization for a bundle that does not have
	 * locale file.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	public void testGetHeaders005() throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb1 = getContext().installBundle(getWebServer() + "div.tb1.jar");
		try {
			tb1.start();

			Dictionary<String,String> h = tb1.getHeaders("en_US");
			for (int i = 0; i < tb1_manifestHeadersValues.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb1_manifestHeadersValues[i], h
								.get(manifestHeadersKeys[i]));
			}
		}
		finally {
			tb1.uninstall();
		}
	}

	/**
	 * Tests manifest headers localization for a bundle that has the default
	 * locale file, as defined by
	 * Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	public void testGetHeaders006() throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb8 = getContext().installBundle(getWebServer() + "div.tb8.jar");
		try {
			tb8.start();
			Dictionary<String,String> h = tb8.getHeaders("en_US");

			for (int i = 0; i < tb8_manifestHeadersValues_default.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb8_manifestHeadersValues_default[i], h
								.get(manifestHeadersKeys[i]));
			}
		}
		finally {
			tb8.uninstall();
		}
	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files including the default locale.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	public void testGetHeaders007() throws Exception {
		Bundle tb9 = getContext().installBundle(getWebServer() + "div.tb9.jar");
		try {
			tb9.start();
			Dictionary<String,String> h = tb9.getHeaders("en");

			for (int i = 0; i < tb9_manifestHeadersValues_en.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb9_manifestHeadersValues_en[i], h
								.get(manifestHeadersKeys[i]));
			}
		}
		finally {
			tb9.uninstall();
		}
	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files but does not include locale requested.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	public void testGetHeaders008() throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb9 = getContext().installBundle(getWebServer() + "div.tb9.jar");
		try {
			tb9.start();
			Dictionary<String,String> h = tb9.getHeaders("pt_BR");

			for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb9_manifestHeadersValues_en_US[i], h
								.get(manifestHeadersKeys[i]));
			}
		}
		finally {
			tb9.uninstall();
		}
	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files but does not include the default locale.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	public void testGetHeaders009() throws Exception {
		Locale.setDefault(new Locale("pt", "BR"));
		Bundle tb9 = getContext().installBundle(getWebServer() + "div.tb9.jar");
		try {
			tb9.start();
			Dictionary<String,String> h = tb9.getHeaders("en");

			for (int i = 0; i < tb9_manifestHeadersValues_en.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb9_manifestHeadersValues_en[i], h
								.get(manifestHeadersKeys[i]));
			}

			tb9.stop();
			tb9.uninstall();
			Locale.setDefault(new Locale("en", "US"));
			// After the bundle has been uninstalled, it should return manifest
			// headers
			// localized for the default locale at the time the bundle was
			// uninstalled.
			h = tb9.getHeaders("en");
			tb9 = null;
			for (int i = 0; i < tb9_manifestHeadersValues_missingLocale.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb9_manifestHeadersValues_missingLocale[i], h
								.get(manifestHeadersKeys[i]));
			}
		}
		finally {
			if (tb9 != null) {
				tb9.uninstall();
			}
		}
	}

	/**
	 * Tests manifest localization when bundle is not on default location. Tests
	 * manifest localization for a fragment bundle.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	public void testGetHeaders010() throws Exception {

		Locale.setDefault(new Locale("pt", "BR"));
		// install host bundle
		Bundle tb9 = getContext().installBundle(getWebServer() + "div.tb9.jar");
		// install fragment bundle
		Bundle tb14 = getContext().installBundle(
				getWebServer() + "div.tb14.jar");

		try {

			// Start the bundles to force them to resolve.
			tb9.start();
			// tb14.start();

			// When searching for a localization file of a fragment bundle,
			// it must first look in the fragment’s host bundle (with the lowest
			// bundle id) and then look
			// in the host’s currently attached fragment bundles.
			Dictionary<String,String> h = tb14.getHeaders("pt_BR");

			for (int i = 0; i < tb14_manifestHeadersValues_pt_BR.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb14_manifestHeadersValues_pt_BR[i], h
								.get(tb14_manifestHeadersKeys[i]));
			}

			h = tb14.getHeaders("en_US");

			// Bug #385 indicates that this was invalid. The code assume
			// the tb9 default was returned because the bundles were not
			// started.
			// However,
			// the framework may resolve fragments at will. If the host/fragment
			// were resolved, a fragment must consult its host before looking in
			// itself.

			// manifest localization before bundle is resolved.
			for (int i = 0; i < tb14_manifestHeadersValues_en_US.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb14_manifestHeadersValues_en_US[i], h
								.get(tb14_manifestHeadersKeys[i]));
			}
		}
		finally {

			tb14.uninstall();
			tb9.uninstall();
		}
	}

	/**
	 * Tests manifest localization for a host bundle.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	public void testGetHeaders011() throws Exception {

		Locale.setDefault(new Locale("en", "US"));
		Bundle tb14 = getContext().installBundle(
				getWebServer() + "div.tb14.jar");
		Bundle tb9 = getContext().installBundle(getWebServer() + "div.tb9.jar");
		try {

			tb9.start();
			// When searching for a localization file of a host bundle,
			// it must first look in the bundle and then look in the currently
			// attached fragment bundles.
			Dictionary<String,String> h = tb9.getHeaders("pt_BR");

			for (int i = 0; i < tb9_manifestHeadersValues_pt_BR.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb9_manifestHeadersValues_pt_BR[i], h
								.get(manifestHeadersKeys[i]));
			}
		}
		finally {
			tb9.uninstall();
			tb14.uninstall();
		}
	}

	/**
	 * Tests manifest localization for a fragment bundle.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	public void testGetHeaders012() throws Exception {

		Locale.setDefault(new Locale("en", "US"));
		// install fragment bundle
		Bundle tb14 = getContext().installBundle(
				getWebServer() + "div.tb14.jar");
		// install host bundle
		Bundle tb9 = getContext().installBundle(getWebServer() + "div.tb9.jar");

		try {

			// When searching for a localization file of a fragment bundle,
			// it must first look in the fragment’s host bundle (with the lowest
			// bundle id) and then look
			// in the host’s currently attached fragment bundles.

			// resolve fragment bundle
			tb9.start();
			// manifest localization after bundle is resolved.
			Dictionary<String,String> h = tb14.getHeaders("en_US");
			for (int i = 0; i < tb14_manifestHeadersValues_en_US.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb14_manifestHeadersValues_en_US[i], h
								.get(tb14_manifestHeadersKeys[i]));
			}
		}
		finally {
			tb14.uninstall();
			tb9.stop();
			tb9.uninstall();
		}
	}

	/**
	 * Tests manifest localization for a fragment with multiple hosts bundle.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	public void testGetHeaders013() throws Exception {
		// default locale should exist in any of the following
		// bundles locale file
		Locale.setDefault(new Locale("fr", "CA"));
		Bundle tb14 = getContext().installBundle(
				getWebServer() + "div.tb14.jar");
		Bundle tb23 = getContext().installBundle(
				getWebServer() + "div.tb23.jar");
		Bundle tb9 = getContext().installBundle(getWebServer() + "div.tb9.jar");
		Bundle tb9a = getContext().installBundle(
				getWebServer() + "div.tb9a.jar");
		try {
			tb9.start();
			tb9a.start();
			// If a fragment is attached to more than one host, the search
			// will only include the first host (that is the host bundle with
			// the
			// lowest bundle id).
			Dictionary<String,String> h = tb14.getHeaders("fr_FR");

			for (int i = 0; i < tb14_manifestHeadersValues_missingLocale.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb14_manifestHeadersValues_missingLocale[i], h
								.get(tb14_manifestHeadersKeys[i]));
			}

			h = tb14.getHeaders("es_ES");

			for (int i = 0; i < tb14_manifestHeadersValues_es_ES.length; i++) {
				assertEquals("Manifest header localization does not match",
						tb14_manifestHeadersValues_es_ES[i], h
								.get(tb14_manifestHeadersKeys[i]));
			}
		}
		finally {
			tb9.stop();
			tb9a.stop();
			tb9.uninstall();
			tb9a.uninstall();
			tb14.uninstall();
			tb23.uninstall();
		}
	}
}

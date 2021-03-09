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

package org.osgi.test.cases.provisioning.junit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.provisioning.ProvisioningService;
import org.osgi.service.url.AbstractURLStreamHandlerService;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;
import org.osgi.test.support.tracker.Tracker;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A test case for the R3 Initial Provisioning service, with
 * file, http, and rsh url mappings.
 */
public class ProvisioningTests extends DefaultTestBundleControl {
	File							dir;			// Working dir for copying bundles
	ServiceTracker<ProvisioningService,ProvisioningService>	provisioning;	// Tracks
																			// provisioning
																			// service
	final boolean					debug = true;	// For debug info
	ServiceTracker<Bundle,Bundle>							bundles;		// Tracks
																			// started
																			// Ip
																			// bundles
	ServiceRegistration<URLStreamHandlerService>			registration;	// director:
																			// spid-test:
																			// stream
																			// handler
	String							query; 			// Query part from spid-test: url
	final String		httpServer;

	public ProvisioningTests() {
		httpServer = "http://" + Activator.hostName + ":" + Activator.hostPort
				+ "/";
	}

	/**
	 * Create a work directory and start a tracker for our loaded bundles.
	 *
	 * There is only one bundle that gets loaded during this test case.
	 * This bundle is simple.jar and will register its bundle in the
	 * service registry with test.case property.
	 */
	protected void setUp() throws InvalidSyntaxException {
		provisioning = new ServiceTracker<>(getContext(),
				ProvisioningService.class, null);
		provisioning.open();
		dir = getContext().getDataFile("work");
		dir.mkdir();
		bundles = new ServiceTracker<>(getContext(), getContext().createFilter(
				"(test.case=org.osgi.test.cases.provisioning)"),
			null );
		bundles.open();
		registration = installStreamHandler();
		reset();
	}

	private void reset() {
		/*
		 * For each test case, assure we have no test bundles installed. We
		 * assure that all installed bundles end with -prov.jar so we can find
		 * them.
		 */
		Bundle bndls[] = getContext().getBundles();
		for ( int i=0; i < bndls.length; i++ ) {
			if ( bndls[i].getLocation().endsWith("-prov.jar") ) {
				if ( debug )
					log("Uninstalling? " + bndls[i].getLocation());
				try {
					Bundle bundle = bndls[i];
					//
					// Some bundles might still be in
					// the starting mode if their thread was
					// yielded to us. So give them some time
					// to leave the start() method.
					//
					int n = 30;
					while ( bundle.getState() == Bundle.STARTING && n-->0)
						Sleep.sleep(Activator.TIMEOUT4);

					//
					// But now we kill it !
					//
					bundle.uninstall();
				}
				catch( Exception e ) {
					log( "In uninstalling " + e );
				}
			}
		}
		ServiceReference<Bundle> refs[] = bundles.getServiceReferences();
		assertTrue( "Bundles should have been uninstalled",  refs==null || refs.length==0);

		//
		// Reset the provisioning service
		//
		Hashtable<String,Object> information = new Hashtable<>();
		information.put( "provisioning.spid", "SPID:test%21" );
		information.put( "provisioning.rsh.secret", RSHTest.secret );
		getProvisioningService().setInformation( information );
	}

	/**
	 * Remove the stream handlers
	 */
	protected void tearDown() {
		reset();
		registration.unregister();
		provisioning.close();
	}


	private String getHttpServer() {
		return httpServer;
	}
	/*
	 * START OF TESTS
	 */

	/**
	 * Test if we actually have RSH stream handler installed. If
	 * read a file and check the contents. This reading will go
	 * through the RSH handler and thus passes all encryption/decryption
	 * routines. The RSH protocol is optional so this case
	 * fails silently if there is no such protocol
	 *
	 * @spec rsh
	 */
	public void testRSHFile() throws Exception {
		if ( ! rshAvailable() )
			return;

		URL ws = new URL(getHttpServer());
		URL rshurl = new URL( "rsh://" + ws.getHost() + ":" + ws.getPort() + "/test/rsh");

		// Check if we can get data from this
		InputStream		in = rshurl.openStream();
		assertNotNull( "Stream from RSH URL could not be opened", in );

		byte r [] = collect( in, 0 );
		if ( debug ) {
			FileOutputStream tout = new FileOutputStream("generated"
					+ File.separator
					+ "org.osgi.test.cases.provisioning.target-test.zip");
			tout.write( r );
			tout.close();
		}
		in.close();

		ZipInputStream	zin = new ZipInputStream( new ByteArrayInputStream(r) );
		ZipEntry entry = zin.getNextEntry();
		while ( entry != null) {
			byte [] data = collect(zin,0);
			if ( debug )
				log("Entry " + entry + " " + data.length);
			if ( entry.getName().equals( "clientfg") ) {
				assertNotNull( "clientfg entry must have ZIP extra field set", entry.getExtra() );
				assertEquals( "Type for clientfg entry must be utf-8", "text/plain;charset=utf-8", new String(entry.getExtra()) );
			} else
			if ( entry.getName().equals( "spid") ) {
				assertNotNull( "spid entry must have ZIP extra field set", entry.getExtra() );
				assertEquals( "ZIP content type is not", "text/plain;charset=utf-8", new String(entry.getExtra()) );
				assertEquals( "SPIDis should be set to", "SPID:test%21", new String( data ) );
			} else
			if ( entry.getName().equals( "time") ) {
				assertNotNull( "time entry must have ZIP extra field set", entry.getExtra() );
				assertEquals( "Time content type is not", "text/plain;charset=utf-8", new String(entry.getExtra()) );
			}
			entry = zin.getNextEntry();
		}
	}

	/**
	 * Test if we can load files through RSH
	 *
	 * The RSH server gives us an ipa file with sample.jar and
	 * will start it. It will also place the "time" parameter
	 * in the zip file.
	 * @spec rsh
	 */
	public void testProvisoningWithRsh1() throws Exception {
		if ( ! rshAvailable() )
			return;

		rshSecret( "SPID:abcd", RSHTest.secret );
	}

	public void testProvisoningWithRsh2() throws Exception {
		if (!rshAvailable())
			return;

		rshSecret( "SPID:large", RSHTest.largesecret );
	}

	public void testProvisoningWithRsh3() throws Exception {
		if (!rshAvailable())
			return;

		rshSecret( "SPID:abcd%21", RSHTest.secret );
	}

	private void rshSecret(String spid, byte[] secret) throws Exception {
		if ( ! rshAvailable() )
			return;

		assertNull( "Bundle may not be loaded", get("rsh.ipa") );

		Dictionary<String,Object> d = new Hashtable<>();
		d.put( "provisioning.spid", spid );
		d.put( "provisioning.rsh.secret", secret );
		getProvisioningService().addInformation( d );

		assertEquals( "Different spid was set (provisioning.spid)", spid, get("provisioning.spid"));
		assertTrue("Different secret was set (provisioning.rsh.secret)", Arrays
				.equals(secret, (byte[]) get("provisioning.rsh.secret")));

		URL ws = new URL(getHttpServer());
		String time = System.currentTimeMillis()+"";
		URL rshurl = new URL( "rsh://" + ws.getHost() + ":" + ws.getPort() + "/test/rsh?time="+time );
		loadFromURL( rshurl, "rsh.ipa");

		assertEquals( "Check if we loaded the thing", "rsh.ipa", get("rsh.ipa") );
	}

	/**
	 * Check if RSH is available
	 */
	boolean rshAvailable() throws InvalidSyntaxException {
		Collection<ServiceReference<URLStreamHandlerService>> refs = getContext()
				.getServiceReferences(URLStreamHandlerService.class,
			"(" + URLConstants.URL_HANDLER_PROTOCOL + "=rsh)" );
		String webserver = getHttpServer();
		if (refs == null || refs.isEmpty()
			|| ! webserver.startsWith("http:")) {
			log( "[No rsh protocol available]" );
			return false;
		}
		return true;
	}

	/**
	 * Test if all the constants have the right values.
	 *
	 * @spec ProvisioningService.MIME_BUNDLE
	 * @spec ProvisioningService.MIME_BUNDLE_URL
	 * @spec ProvisioningService.MIME_BYTE_ARRAY
	 * @spec ProvisioningService.MIME_STRING
	 * @spec ProvisioningService.PROVISIONING_AGENT_CONFIG
	 * @spec ProvisioningService.PROVISIONING_REFERENCE
	 * @spec ProvisioningService.PROVISIONING_ROOTX509
	 * @spec ProvisioningService.PROVISIONING_RSH_SECRET
	 * @spec ProvisioningService.PROVISIONING_SPID
	 * @spec ProvisioningService.PROVISIONING_START_BUNDLE
	 * @spec ProvisioningService.PROVISIONING_UPDATE_COUNT
	 */
	public void testConstants() {
		assertConstant("application/vnd.osgi.bundle", "MIME_BUNDLE",
				ProvisioningService.class);
		assertConstant("application/x-osgi-bundle", "MIME_BUNDLE_ALT",
				ProvisioningService.class);
		assertConstant("text/x-osgi-bundle-url", "MIME_BUNDLE_URL",
				ProvisioningService.class);
		assertConstant("application/octet-stream", "MIME_BYTE_ARRAY",
				ProvisioningService.class);
		assertConstant("text/plain;charset=utf-8", "MIME_STRING",
				ProvisioningService.class);
		assertConstant("provisioning.agent.config",
				"PROVISIONING_AGENT_CONFIG",
				ProvisioningService.class);
		assertConstant("provisioning.reference", "PROVISIONING_REFERENCE",
				ProvisioningService.class);
		assertConstant("provisioning.rootx509", "PROVISIONING_ROOTX509",
				ProvisioningService.class);
		assertConstant("provisioning.rsh.secret", "PROVISIONING_RSH_SECRET",
				ProvisioningService.class);
		assertConstant("provisioning.spid", "PROVISIONING_SPID",
				ProvisioningService.class);
		assertConstant("provisioning.start.bundle",
				"PROVISIONING_START_BUNDLE",
				ProvisioningService.class);
		assertConstant("provisioning.update.count",
				"PROVISIONING_UPDATE_COUNT", ProvisioningService.class);
	}

	/**
	 * The content can have UNICODE. This will test if we can
	 * actually place unicode characters in the content.
	 *
	 * @spec unicode
	 */
	public void testUnicode1() throws Exception {
		doTestUnicode( "unicode.ipa" );
	}

	public void testUnicode2() throws Exception {
		doTestUnicode( "x-type-unicode.ipa" );
	}

	private void doTestUnicode(String ipa) throws Exception {
		loadFromResource(ipa);
		assertEquals( "Must have loaded the unicode ipa", ipa, get("load-status"));
		assertEquals( "See if it has proper UNICODE characters", "v\u00d0\u00d9v", get( "unicode.string"));
	}

	/**
	 *  Check the dictionary manipulations. Mainly the fact that the
	 *  dictionary should not be mutable.
	 *
	 *  @spec ProvisioningService.getInformation()
	 *  @spec ProvisioningService.setInformation(Dictionary)
	 *  @spec ProvisioningService.addInformation(Dictionary)
	 *  @spec ProvisioningService.addInformation(ZipInputStream)
	 */

	public void testDictionary() throws Exception {
		ProvisioningService		ps = getProvisioningService();
		Dictionary<String,Object> d = ps.getInformation();

		assertNotNull( "Must have upate count", get("provisioning.update.count") );
		assertTrue( "Must be Integer", get("provisioning.update.count") instanceof Integer );

		try {
			d.put( "some.key", "some.value" );
			fail( "The dictionary should not allow put" );
		}
		catch( Exception e ) {
			assertException( "Put should throw UnsupportedOperationException", UnsupportedOperationException.class, e );
		}

		try {
			d.remove( "provisioning.update.count" );
			fail("The dictionary should not allow remove");
		}
		catch( Exception e ) {
			assertException( "remove should throw UnsupportedOperationException", UnsupportedOperationException.class, e );
		}

		int count = getCount();

		d = new Hashtable<>();
		d.put( "some.key.1", "some.value.1" );
		d.put( "some.key.2", "some.value.2" );
		ps.setInformation( d );

		d = ps.getInformation();
		assertEquals( "May only be 2 keys + update count in dict", 3, d.size() );

		d = new Hashtable<>();
		d.put( "some.key.1", "some.value.11" );	// overwrite
		d.put( "some.key.3", "some.value.3" );	// new
		ps.addInformation( d );

		d = ps.getInformation();
		assertEquals( "Nr 1", "some.value.11", get("some.key.1"));
		assertEquals( "Nr 2", "some.value.2", get("some.key.2"));
		assertEquals( "Nr 3", "some.value.3", get("some.key.3"));
		assertEquals( "May only be 3 keys + update count in dict", 4, d.size() );

		ZipInputStream		zip = new ZipInputStream(
			getClass().getResourceAsStream(
				"/ipa/keys-only.ipa"));
		ps.addInformation( zip );
		zip.close();
		assertEquals( "May only be 4 keys + update count + 2 status in dict", 7, d.size() );
		assertEquals( "Nr 1", "some.value.111", get("some.key.1"));
		assertEquals( "Nr 2", "some.value.22", get("some.key.2"));
		assertEquals( "Nr 3", "some.value.3", get("some.key.3"));
		assertEquals( "Nr 4", "some.value.4", get("some.key.4"));

		d = new Hashtable<>();
		ps.setInformation( d );
		assertNull( "nr 1 must be null", get("some.key.1") );
		assertNull( "nr 2 must be null", get("some.key.2") );
		assertNull( "nr 3 must be null", get("some.key.3") );
		assertNull( "nr 4 must be null", get("some.key.4") );

		assertEquals( "Update count must be 4 higher", count+4, getCount() );

		d = new Hashtable<>();
		Object [] invalidTypes = { Integer.valueOf(0), Byte.valueOf((byte)0), Character.valueOf((char)0), new Object[1],
			new int[0], new byte[][] { {0},{0} }, new char[0], new short[0],
			Short.valueOf((short)0) };

		for ( int i=0; i<invalidTypes.length; i++ ) {
			Object	 type = invalidTypes[i];
			d.put( "invalid.type", type );
			ps.addInformation( d );
			assertNull( type.getClass().getName() + " should be ignored", get("invalid.type") );
		}
		byte [] ok=new byte[1];
		d.put("invalid.type", ok );
		getProvisioningService().addInformation( d );
		assertNotNull( ok.getClass().getName() + " should be set", get("invalid.type") );

		for ( int i=0; i<invalidTypes.length; i++ ) {
			Object	 type = invalidTypes[i];
			d.put( "invalid.type", type );
			ps.addInformation( d );
			assertTrue(type.getClass().getName() + " should still be", Arrays
					.equals(ok, (byte[]) get("invalid.type")));
		}
	}


	/**
	 * Check if the changes to the dictionary are persistent. We will test this
	 * by stopping the provisioning service and verifiying the update count
	 * and a key set to the time.
	 *
	 * @spec ProvisioningService.setInformation(Dictionary)
	 * @spec ProvisioningService.getInformation()
	 * @spec ProvisioningService.updateCount
	 */

	public void testPersistence() throws Exception {
		ProvisioningService	ps = getProvisioningService();
		Dictionary<String,Object> dict = new Hashtable<>();
		String				time = System.currentTimeMillis()+"";

		dict.put( "persistence.test", time  );
		ps.setInformation( dict );
		int			count = getCount();

		ServiceReference<ProvisioningService> ref = getContext()
				.getServiceReference(ProvisioningService.class);
		assertNotNull( "There must be a service ref for the provisioning service", ref );
		Bundle bundle = ref.getBundle();
		bundle.stop();

		assertTrue( "Bundle should be stopped now ", (bundle.getState() & (Bundle.RESOLVED|Bundle.INSTALLED))!=0);
		assertNull( "Not be able to get provisioning service",
			getContext()
				.getServiceReference(ProvisioningService.class.getName()));

		bundle.start();
		assertEquals( "Check stored field against contents", time, get("persistence.test") );
		assertEquals( "Count should not be changed", count, getCount() );
	}

	/**
	 * Figure 78 allows both the key provisioning.start.bundle and
	 * provisioning.reference to be set in the same same go. So ipa-ref-start
	 * refers to another ipa file (simple.ipa) that loads a bundle,
	 * but also loads and starts a local bundle.
	 *
	 * @spec ProvisioningService.getInformation()
	 */

	public void testStartAndRef1() throws Exception {
		doTestStartAndRef( "ipa-ref-start.ipa" );	// refers to simple.ipa
	}

	public void testStartAndRef2() throws Exception {
		doTestStartAndRef( "x-type-ipa-ref-start.ipa" );	// refers to simple.ipa
	}

	private void doTestStartAndRef(String ipa) throws Exception {
		loadFromResource( ipa );	// refers to simple.ipa
		waitFor( "simple.ipa" );					// so sync until that the 2nd file is loaded

		Bundle local = findBundle("local-prov.jar");
		Bundle test0 = findBundle("test-0-prov.jar");

		assertEquals("Must have loaded simple.ipa", "simple.ipa", get("load-status") );
		assertEquals("Must have also loaded ipa-ref-start.ipa", "true", get(ipa));
		assertNotNull("Started local-prov.jar from ipa-ref-start.ipa", local );
		assertNotNull("Started test-0-prov.jar from simple.ipa", test0 );

		waitForBundleState("Installed local bundle must be started", local, Bundle.ACTIVE );
		waitForBundleState("Installed remote bundle must be started", test0, Bundle.ACTIVE );
		assertEquals( "Check provisioning.start.bundle property", "test-0-prov.jar", get("provisioning.start.bundle"));
	}

	void waitForBundleState(String string, Bundle bundle, int active) {
		long deadline = System.currentTimeMillis() + 15000;
		while (bundle.getState() != active ) try {
			if ( deadline <= System.currentTimeMillis() )
				assertEquals(string, bundle.getState(), active );
			else {
					Sleep.sleep(Activator.TIMEOUT5);
			}
		} catch( InterruptedException ie ) {
			// who cares
		}
	}

	/**
	 * Test if the provisioning service sends the SPID to the server. we
	 * have registered a "spid-test" url handler that we use. It will
	 * store the query part of a URL in the query field.
	 * @spec ProvisioningService.getInformation()
	 */

	public void testSPID1() throws IOException {
		doTestSPID("spid.ipa");
	}

	public void testSPID2() throws IOException {
		doTestSPID("x-type-spid.ipa");
	}

	private void doTestSPID(String ipa) throws IOException {
		assertNotNull( "26.2 The PROVISIONING_SPID key must contain the Service Platform Identifier", get("provisioning.spid"));

		query = null;
		loadFromURL( new URL("spid-test:" + ipa), ipa );

		assertEquals("We should have loaded spid.ipa", ipa, get("load-status") );
		assertNotNull( "26.7.3 No query part found in URL to server", query );
		assertTrue( "26.7.3 service_platform_id not found in query part of URL", query.indexOf( "service_platform_id") >= 0 );
	}

	/**
	 * Test if our own parameters are maintained when we give a URL
	 * where the Provisioning Service will add a SPID.
	 *
	 * @spec ProvisioningService.getInformation()
	 */
	public void testSPIDWithQuery1() throws IOException {
		doTestSPIDWithQuery("spid.ipa");
	}

	public void testSPIDWithQuery2() throws IOException {
		doTestSPIDWithQuery("x-type-spid.ipa");
	}

	private void doTestSPIDWithQuery(String ipa) throws IOException {
		query = null;
		loadFromURL( new URL("spid-test:" + ipa + "?foo=bar&abc=1"), ipa );

		assertEquals("We should have loaded spid.ipa", ipa, get("load-status") );
		assertNotNull( "26.7.3 Query part must be set by provisioning service via spid-test: URL", query );
		assertTrue( "26.7.3 URL Encoding: Must contain a service_platform_id", query.indexOf( "service_platform_id") >= 0 );
		assertTrue( "26.7.3 The provisioning service should also keep our parameters", query.indexOf( "foo=bar") >= 0 );
		assertTrue( "26.7.3 The provisioning service should also keep our parameters", query.indexOf( "abc=1") >= 0 );
	}



	/**
	 * Initial Provisioning : 26.2 Procedure
	 * <em>The provisioning service must install (but not start) all entries
	 * in the zip file that are typed in the extra field with bundle or bundle-url.
	 * In an entry named PROVISIONING_START_BUNDLE ... This designated bundle
	 * must be given all permission and started.</em>
	 * Load an ipa file with a reference that delays the loading of the
	 * JAR file. We want to assure that the start bundle is not to be started
	 * before it or others are loaded.
	 *
	 * @spec ProvisioningService.getInformation()
	 */
	public void testDelayReference1() throws Exception {
		doTestDelayReference("delay-ref.ipa");
	}

	public void testDelayReference2() throws Exception {
		doTestDelayReference("x-type-delay-ref.ipa");
	}

	private void doTestDelayReference(String ipa) throws Exception {
		loadFromResource(ipa);
		Bundle bundle = waitForBundle(); // this is simple-prov.jar
		assertNotNull( "Check if simple-prov.jar is started", bundle );

		Bundle delayed= findBundle("delay-prov.jar");
		assertNotNull( "Delayed bundle must also be installed",delayed );
		assertTrue( "Delayed bundle may not be started",
			(delayed.getState()&(Bundle.INSTALLED|Bundle.RESOLVED))!=0);
	}

	/**
	 * Test if an ipa file that contains a PROVISIONING_REFERENCE
	 * is correctly loading the first + referred file.
	 *
	 * @spec ProvisioningService.getInformation()
	 * @spec ProvisioningService.addInformation(Dictionary)
	 */
	public void testIPAReference1() throws Exception {
		doTestIPAReference( "ipa-ref.ipa" );
	}

	public void testIPAReference2() throws Exception {
		doTestIPAReference( "x-type-ipa-ref.ipa" );
	}

	public void doTestIPAReference(String ipa) throws Exception {
		int count = getCount();
		loadFromResource( ipa );
		doURL();
		assertNotNull("Check if ipa-ref,ipa was loaded", get(ipa));
		assertNotNull("Check if simple.ipa was loaded", get("simple.ipa"));
		assertEquals( "One addInformation and 2 iterations", count + 3, getCount() );
	}

	/**
	 * Test if it is possible to load a provisioning jar
	 * setting the PROVISIONING_REFERENCE to a file:
	 * URL. This file, simple.ipa contains a bundle and
	 * it has the start reference set. The simple.jar bundle
	 * should start.
	 * @spec ProvisioningService.getInformation()
	 * @spec ProvisioningService.addInformation(Dictionary)
	 */
	public void testFileLoad1() throws Exception {
		doTestFileLoad( "simple.ipa" );
	}

	public void testFileLoad2() throws Exception {
		doTestFileLoad( "x-type-simple.ipa" );
	}

	public void doTestFileLoad(String ipa) throws Exception {
		loadFromResource( ipa );
		doURL();
	}
	/**
	 * Test a download from the http server
	 * @spec ProvisioningService.addInformation(String)
	 */
	public void testHttpLoad1() throws Exception {
		loadFromURL(new URL(getHttpServer() + "test/ipa/simple.ipa"),
				"simple.ipa");
		doURL( );
	}

	public void testHttpLoad2() throws Exception {
		loadFromURL(new URL(getHttpServer() + "test/ipa/x-type-simple.ipa"),
				"x-type-simple.ipa");
		doURL( );
	}

	/**
	 * Test a file that only references a JAR by a URL
	 * @spec ProvisioningService.getInformation()
	 * @spec ProvisioningService.addInformation(Dictionary)
	 */
	public void testFileWithReference1() throws Exception {
		doTestFileWithReference( "ref.ipa" );
	}

	public void testFileWithReference2() throws Exception {
		doTestFileWithReference( "x-type-ref.ipa" );
	}

	public void testFileWithReference3() throws Exception {
		doTestFileWithReference( "x-ext-ref.ipa" );
	}

	private void doTestFileWithReference(String ipa) throws Exception {
		loadFromResource(ipa);
		doURL();
	}
	void doURL() throws Exception {
		Bundle test0 = waitForBundle();

		ServiceReference<Bundle> refs[] = bundles.getServiceReferences();
		assertTrue( "There should be only 1 bundle started", refs!= null && refs.length==1 );

		assertNotNull( "We should have installed test-0-prov.jar", test0 );
		assertEquals( "The name must be 'test-0-prov.jar'", test0.getLocation(), "test-0-prov.jar" );
		Bundle test1 = findBundle( "test-1-prov.jar");
		if (test1 == null)
			test1 = findBundle("test-1-prov.url");
		assertNotNull( "Testing install of  bundle test-1-prov", test1 );
		assertTrue( "Bundle test-1-prov.jar should not be uninstalled", test1.getState() != Bundle.UNINSTALLED );
		assertTrue( "Bundle test-1-prov.jar should not be starting", test1.getState() != Bundle.STARTING );
		assertTrue( "Bundle test-1-prov.jar should not be stopping", test1.getState() != Bundle.STOPPING );
		assertTrue( "Bundle test-1-prov.jar should not be active", test1.getState() != Bundle.ACTIVE );

		Bundle test2 = findBundle( "test-2-prov.jar" );
		assertNull( "test-2-prov.jar should not be loaded", test2 );
		Object text1 = get( "text-1");
		if (text1 == null)
			text1 = get( "text-1.txt");
		assertTrue( "The ipa file contained the text-1 key and is String", "TEST1".equals(text1));
		Object text2 = get( "text-2");
		if (text2 == null)
			text2 = get( "text-2.txt");
		assertTrue( "The ipa file contained the text-2 key and is String", "TEST2".equals(text2));

		//assertNull( "26.2 The name must not start with slash", get( "/text-3"));

		assertNull( "26.2 No bundle and bundle-url in in dictionary", get("test-0-prov.jar" ));
		assertNull( "26.2 No bundle and bundle-url in in dictionary", get("test-1-prov.jar" ));
		assertNull( "26.2 No bundle and bundle-url in in dictionary", get("test-2-prov.jar" ));

		Object object = get( "osgi.cert" );
		assertTrue( "Certificate must be binary ", object instanceof byte[] );
		byte [] actual = (byte[]) object;
		byte[] expected = collect(getClass().getResourceAsStream(
				"/www/osgi.cert"), 0);
		assertTrue("Verify binary cert is correctly handled", Arrays.equals(
				expected, actual));


		assertEquals( "Check for allpermision", "true", refs[0].getProperty("allpermission"));
		assertNull( "Check for config data", refs[0].getProperty("config.data"));
	}


	/**
	 * Load an ipa file with a reference that uses an unusal case for the
	 * mime type. According to rfc 2046, mime types are not case sensitive.
	 * There is a mistake in the spec because it refers to the mime type
	 * with upper and lower case URL/url. See issue #175
	 * @spec ProvisioningService.getInformation()
	 * @spec ProvisioningService.addInformation(Dictionary)
	 */
	public void testCaseReference1() throws Exception {
		doTestCaseReference( "case-ref.ipa" );
	}

	public void testCaseReference2() throws Exception {
		doTestCaseReference( "x-type-case-ref.ipa" );
	}

	private void doTestCaseReference(String ipa) throws Exception {
		loadFromResource(ipa);
		Bundle bundle = waitForBundle();
		assertNotNull( "Bundle with unusual case should be installed", bundle );
	}


	/********************************************************************************/
	/* UTILITIES
	/********************************************************************************/

	/**
	 * Get a value from the provisioning service.
	 */
	Object get( String key ) {
		return getProvisioningService().getInformation().get( key );
	}


	/**
	 * Anser the update counter.
	 */

	int getCount() {
		Integer i = (Integer) get("provisioning.update.count");
		assertNotNull( "Update count must always be set and an Integer", i );
		return i.intValue();
	}

	/**
	 * Utility function to use the provisioning service to install a
	 * a bundle.
	 */
	Bundle waitForBundle() {
		try {
			Bundle bundle = Tracker.waitForService(bundles,
					Activator.TIMEOUT1);
			return bundle;
		} catch( InterruptedException e ) {
			// empty
		}

		return null;
	}


	/**
	/* Find a bundle ending with a suffix
	/*/

	Bundle findBundle( String suffix ) {
	Bundle b[] = getContext().getBundles();
	for ( int i=0; i<b.length; i++ ) {
	if ( b[i].getLocation().endsWith(suffix) ) {
	return b[i];
	}
	}
	return null;
	}

	/**
	 * Get the provisioning service from the tracker, wait a bit
	 * when the service is temporarily absent. Will throw and
	 * IllegalStateException when it cannot obtain the service
	 * for 10 seconds.
	 */
	ProvisioningService getProvisioningService() {
		Object service = null;
		try {
			service = Tracker.waitForService(provisioning, Activator.TIMEOUT2);
		}
		catch (InterruptedException e) {// empty
		}
		if ( service == null )
			throw new IllegalStateException( "No Provisioning Service" );

		return (ProvisioningService) service;
	}



	/**
	 * Copy the input stream tot he output stream and close them
	 */
	void copyAndClose( InputStream in, OutputStream out ) throws IOException {
		try {
			byte buffer[] = new byte[1024];
			int sz = in.read(buffer);
			while ( sz > 0 ) {
				out.write( buffer, 0, sz );
				sz = in.read(buffer);
			}
		}
		finally {
			in.close();
			out.close();
		}
	}


	/**
	 * Read recursively from an inputstream, soring the
	 * result in a byte buffer.
	 */
	byte[] collect( InputStream in, int size ) throws IOException {
		byte buf[] = new byte[1024];
		int sz= in.read( buf );
		if ( sz <=0 ) {
			return new byte[size];
		}
		byte buffer[] = collect( in, size + sz );
		System.arraycopy( buf, 0, buffer, size, sz );
		return buffer;
	}

	/**
	 * Used to log info during debugging of the test case
	 */
	void debug( String msg ) {
		if ( debug )
			log(msg);
	}

	/**
	 * Load a file from the resources in the ip dictionary.
	 */
    @SuppressWarnings("deprecation")
	void loadFromResource( String ipaFile ) throws IOException {
		assertNull("The ipa file should not have been loaded here", get(ipaFile) );
		File		jar = new File(dir,ipaFile);
		copyAndClose(getClass().getResourceAsStream("/ipa/" + ipaFile),
			new FileOutputStream( jar )  );
		loadFromURL( jar.toURL(), ipaFile );
	}

	/**
	 * Load a file from a url in the ip dictionary.
	 */
	void loadFromURL( URL url, String ipaFile ) {
		ProvisioningService		provisioningService = getProvisioningService();
		Hashtable<String,Object> map = new Hashtable<>();
		map.put( ProvisioningService.PROVISIONING_REFERENCE, url.toString() );
		provisioningService.addInformation( map );
		if (ipaFile != null )
			waitFor( ipaFile );
	}


	/**
	 * Wait until the key appears.
	 */
	void waitFor( String ipaFile ) {
		int n = 600;
		try {
			while ( n-- > 0
				&& get( ipaFile ) == null )
				Sleep.sleep(Activator.TIMEOUT4);
		}
		catch (InterruptedException e) {
			// empty
		}
		if ( n < 0 )
			fail("File did not appear in time " + ipaFile );
	}


	/**
	 *
	 * We must test URL references in the ZIP file but we cannot
	 * assume a static web site (firewalls!) nor can we hardcode
	 * localhost or something. We therefore register a magic
	 * url handler that reverts "director:" URLs to the
	 * director web site, an address we know in run time. E.g.
	 * the ZIP file can now contain director:///service.ipa and this
	 * will be converted to (e.g.) http://localhost:8081/org.osgi..../service.ipa
	 * depending on the webserver setting.
	 */

	ServiceRegistration<URLStreamHandlerService> installStreamHandler() {
		URLStreamHandlerService		magic = new AbstractURLStreamHandlerService() {
			URLConnection ucon;
			public URLConnection openConnection(URL u) throws java.io.IOException{
				String file = u.getPath();
				if (file.endsWith("delay-prov.jar")) {
					try {
						Sleep.sleep(Activator.TIMEOUT2);
					}
					catch (InterruptedException e) {
						// empty
					}
				}
				if ( u.toString().startsWith("director:") ) {
					String path = getResourcePath(file) + file;
					URL newurl = getClass().getResource(path);
					if ( debug )
						log("Incoming " + u + " ->  " + newurl);
					return ucon = newurl.openConnection();
				} else {
					query = u.getQuery();
					int index = u.getFile().indexOf('?');
					if ( index < 0 )
						index = u.getFile().length();
					file = u.getFile().substring(0, index);
					if ( debug )
						log("Query = " + query + " " + u.getFile() + " " + file
								+ " " + u);
					URL url = getClass().getResource(
							getResourcePath(file) + file);
					ucon = url.openConnection();
					return ucon;
				}
			}
		};
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(URLConstants.URL_HANDLER_PROTOCOL,new String[]{"director","spid-test"});
		properties.put(Constants.SERVICE_RANKING,Integer.valueOf(100));
		return getContext().registerService(
				URLStreamHandlerService.class,
			magic,
			properties );
	}

	static String getResourcePath(String file) {
		return file.endsWith(".ipa") ? "/ipa/" : "/www/";
	}

}

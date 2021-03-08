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

package org.osgi.test.cases.framework.junit.frameworkutil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.FrameworkUtil;

import junit.framework.TestCase;

public class MatchDNChainTests extends TestCase {

	public void testMatch() {
		String dn1 = "cn=Bugs Bunny, o=ACME, c=US";
		String dn2 = "ou = Carrots, cn=Daffy Duck, o=ACME, c=US";
		String dn3 = "street = 9C\\, Avenue St. Drézéry, o=ACME, c=FR";
		List<String> chain = new ArrayList<>();
		chain.add(dn2);
		chain.add(dn1);

		assertMatchDNChain(dn1, dn1);

		assertMatchDNChain("*", dn1);
		assertMatchDNChain("*", dn2);
		assertMatchDNChain("*", dn3);
		assertNotMatchDNChain("*", chain);
        assertMatchDNChain("* ", dn1);
        assertMatchDNChain("* ", dn2);
        assertMatchDNChain("* ", dn3);
        assertNotMatchDNChain("* ", chain);
        assertMatchDNChain(" *", dn1);
        assertMatchDNChain(" *", dn2);
        assertMatchDNChain(" *", dn3);
        assertNotMatchDNChain(" *", chain);

		assertMatchDNChain("-", dn1);
		assertMatchDNChain("-", dn2);
		assertMatchDNChain("-", dn3);
		assertMatchDNChain("-", chain);
        assertMatchDNChain("- ", dn1);
        assertMatchDNChain("- ", dn2);
        assertMatchDNChain("- ", dn3);
        assertMatchDNChain("- ", chain);
        assertMatchDNChain(" -", dn1);
        assertMatchDNChain(" -", dn2);
        assertMatchDNChain(" -", dn3);
        assertMatchDNChain(" -", chain);


		assertMatchDNChain("*, c=US", dn1);
		assertMatchDNChain("*, c=US", dn2);
		assertNotMatchDNChain("*, c=US", dn3);
		assertMatchDNChain("*, o=ACME, c=US; *, c=US", chain);
        assertMatchDNChain("* , c=US ", dn1);
        assertMatchDNChain(" * , c=US", dn2);
        assertNotMatchDNChain(" * , c=US ", dn3);
        assertMatchDNChain("* , o=ACME , c=US ; *, c=US ", chain);

		assertMatchDNChain("*, o=ACME, c=US", dn1);
		assertMatchDNChain("*, o=ACME, c=US", dn2);
		assertNotMatchDNChain("*, o=ACME, c=US", dn3);
		assertMatchDNChain("*, o=ACME, c=US; *, o=ACME, c=US", chain);
		assertMatchDNChain("*; *, o=ACME, c=US", chain);
		assertMatchDNChain("-; *, o=ACME, c=US", chain);
        assertMatchDNChain("* ; * , o=ACME , c=US", chain);
        assertMatchDNChain("- ; * , o=ACME , c=US ", chain);

		assertMatchDNChain("*, o=*, c=US", dn1);
		assertMatchDNChain("*, o=*, c=US", dn2);
		assertNotMatchDNChain("*, o=*, c=US", dn3);
		assertMatchDNChain("*, o=*, c=US; *, o=ACME, c=US", chain);

		assertMatchDNChain("*, o=ACME, c=*", dn1);
		assertMatchDNChain("*, o=ACME, c=*", dn2);
		assertMatchDNChain("*, o=ACME, c=*", dn3);
		assertMatchDNChain("*, o=ACME, c=*; *, o=ACME, c=US", chain);
		assertMatchDNChain("*; *, o=ACME, c=*", chain);
		assertMatchDNChain("-; *, o=ACME, c=*", chain);

		assertNotMatchDNChain("o=ACME, c=US", dn1);
		assertNotMatchDNChain("o=ACME, c=US", dn2);
		assertNotMatchDNChain("o=ACME, c=US", dn3);
	}

	public void testEscape() {
		String dn = "cn=Bugs Bunny, o=ACME, c=US";

		assertMatchDNChain("*, o=\"AC;ME\", c=US",
				"cn=Bugs Bunny, o=AC\\;ME, c=US");
        assertMatchDNChain("*, o=\"AC ME\"",
                "cn=Bugs Bunny, o=AC ME");
		assertMatchDNChain("*, o=AC\\;ME, c=US",
				"cn=Bugs Bunny, o=AC\\;ME, c=US");
		assertNotMatchDNChain("*, o=\"AC;ME\", c=US", dn);
	}

	public void testInvalidPattern() {
		String dn = "cn=Bugs Bunny, o=ACME, c=US";

		assertInvalidMatch(null, dn);
		assertInvalidMatch("", dn);
        assertInvalidMatch("  ", dn);
		assertInvalidMatch("*bob", dn);
		assertInvalidMatch(";`´$.,@", dn);
		assertInvalidMatch("*, c=US\\", dn);
        assertInvalidMatch(";*, c=US", dn);
        assertInvalidMatch("*;;*, c=US", dn);
        assertInvalidMatch("*; ;*, c=US", dn);
        assertInvalidMatch("*, c=US;", dn);
        assertInvalidMatch("*, c=US; ", dn);
		assertInvalidMatch("*, c=\"US", dn);
        assertInvalidMatch("-, c=US", dn);
		assertInvalidMatch("*, cn=Bugs Bunny, o=ACME,", dn);
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	public void testInvalidDNChain() {
		String pattern = "-";

		assertInvalidMatch(pattern, (List<String>) null);
		assertInvalidMatch(pattern, "");
		assertInvalidMatch(pattern, "*bob");
		assertInvalidMatch(pattern, ";`´$.,@");
		assertInvalidMatch(pattern, "c=US\\");
		assertInvalidMatch(pattern, "c=\"US");
		assertInvalidMatch(pattern, "cn=Bugs Bunny, o=ACME,");
		assertInvalidMatch(pattern, "*, o=ACME, c=US");
		assertInvalidMatch(pattern, "-, o=ACME, c=US");
		assertInvalidMatch(pattern, "*");
		assertInvalidMatch(pattern, "-");

		List<Object> bad = new ArrayList<>();
		bad.add(Boolean.TRUE);
		assertInvalidMatch(pattern, (List) bad);
	}

	public static void assertMatchDNChain(String pattern, String dn) {
		List<String> dnChain = new ArrayList<>();
		dnChain.add(dn);
		assertMatchDNChain(pattern, dnChain);
	}

	public static void assertNotMatchDNChain(String pattern, String dn) {
		List<String> dnChain = new ArrayList<>();
		dnChain.add(dn);
		assertNotMatchDNChain(pattern, dnChain);
	}

	public static void assertMatchDNChain(String pattern,
			List<String> dnChain) {
		List<String> copy = new ArrayList<>(dnChain);
		assertTrue("did not match", FrameworkUtil.matchDistinguishedNameChain(
				pattern, dnChain));
		assertEquals(copy, dnChain);
	}

	public static void assertNotMatchDNChain(String pattern,
			List<String> dnChain) {
		List<String> copy = new ArrayList<>(dnChain);
		assertFalse("did match", FrameworkUtil.matchDistinguishedNameChain(
				pattern, dnChain));
		assertEquals(copy, dnChain);
	}

	public static void assertInvalidMatch(String pattern, String dn) {
		List<String> dnChain = new ArrayList<>();
		dnChain.add(dn);
		assertInvalidMatch(pattern, dnChain);
	}

	public static void assertInvalidMatch(String pattern,
			List<String> dnChain) {
		List<String> copy = dnChain == null ? null : new ArrayList<>(dnChain);
		try {
			FrameworkUtil.matchDistinguishedNameChain(pattern, dnChain);
			fail("invalid pattern or chain");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		assertEquals(copy, dnChain);
	}
}

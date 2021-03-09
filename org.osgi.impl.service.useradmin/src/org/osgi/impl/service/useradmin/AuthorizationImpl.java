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
package org.osgi.impl.service.useradmin;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;

/**
 * {@link org.osgi.service.useradmin.Authorization}implementation.
 */
public class AuthorizationImpl implements Authorization {
	/**
	 * Link to the UserAdmin creating this Authorization instance.
	 */
	private UserAdminImpl	ua;
	/**
	 * The user this Authorization context was created for.
	 */
	protected User			user;
	/**
	 * The UserAdmin version number for which this instance's caches are valid.
	 */
	protected long			ua_version;
	/**
	 * Cache holding the roles checked for implication sofar. Must be recomputed
	 * if the UserAdmin database is changed.
	 */
	private Hashtable<Role,Boolean>	cache	= new Hashtable<>();
	/**
	 * The working set of roles, i.e., the roles that we currently are working
	 * on. Used for loop detection.
	 */
	private Vector<Role>			working	= new Vector<>();
	/**
	 * The UserAdmin will set the first element of this array to false when it
	 * is going away. All references to the UserAdmin from Authorization objects
	 * must be guarded by a check that the UserAdmin is still alive.
	 */
	private boolean[]		alive;

	protected AuthorizationImpl(UserAdminImpl ua, User user) {
		this.ua = ua;
		this.user = user;
		this.alive = ua.alive;
		// Reset cache when the user manager is updated
		ua_version = ua.version;
	}

	/**
	 * Gets the name of user this AuthorizationImpl was created for.
	 * Implementation of
	 * {@link org.osgi.service.useradmin.Authorization#getName}.
	 */
	@Override
	public String getName() {
		// Invalidate this object if the UserAdmin has disappeared.
		if (!alive[0])
			return null;
		return user == null ? null : user.getName();
	}

	/**
	 * Checks if this Authorization context implies the specified role.
	 * Implementation of
	 * {@link org.osgi.service.useradmin.Authorization#hasRole}.
	 */
	@Override
	public boolean hasRole(String name) {
		// Invalidate this object if the UserAdmin has disappeared.
		if (!alive[0])
			return false;
		// Synchronize on the UserAdmin to ensure no changes are made
		// while we are working.
		synchronized (ua) {
			checkCacheValidity();
			Role r = ua.getRole(name);
			return r != null && ((RoleImpl) r).impliedBy(this);
		}
	}

	/**
	 * Gets the roles implied by this Authorization context. Implementation of
	 * {@link org.osgi.service.useradmin.Authorization#getRoles}.
	 */
	@Override
	public String[] getRoles() {
		// Invalidate this object if the UserAdmin has disappeared.
		if (!alive[0])
			return null;
		// Synchronize on the UserAdmin to ensure no changes are made
		// while we are working.
		synchronized (ua) {
			checkCacheValidity();
			Role[] roles = null;
			try {
				roles = ua.getRoles(null);
			}
			catch (InvalidSyntaxException e) { /* Impossible */
			}
			for (int i = 0; i < roles.length; i++) {
				// The following call will update the cache.
				((RoleImpl) roles[i]).impliedBy(this);
			}
			// Now check the updated cache
			Vector<String> v = new Vector<>();
			for (Enumeration<Role> en = cache.keys(); en.hasMoreElements();) {
				Role role = en.nextElement();
				if (cache.get(role).booleanValue()) {
					String name = role.getName();
					if (!name.equals(Role.USER_ANYONE))
						v.addElement(role.getName());
				}
			}
			String[] g = new String[v.size()];
			v.copyInto(g);
			return g;
		}
	}

	/* -------- Protected and private methods -------- */
	protected Boolean cachedHaveRole(Role role) {
		return cache.get(role);
	}

	protected void workingOnRole(Role role) {
		working.addElement(role);
	}

	protected boolean isWorkingOnRole(Role role) {
		return working.contains(role);
	}

	protected boolean cacheHaveRole(Role role, boolean have) {
		cache.put(role, Boolean.valueOf(have));
		working.removeElement(role);
		return have;
	}

	private void checkCacheValidity() {
		if (ua_version != ua.version) {
			invalidateCache();
		}
	}

	private void invalidateCache() {
		ua_version = ua.version;
		cache = new Hashtable<>();
		working = new Vector<>();
	}
}

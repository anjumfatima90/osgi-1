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
import java.util.Vector;

import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;

/**
 * {@link org.osgi.service.useradmin.Group}implementation.
 *  
 */
public class GroupImpl extends UserImpl implements Group {
	/**
	 * The basic members of this group.
	 */
	protected Vector<String>	basic_members;
	/**
	 * The required members of this group.
	 */
	protected Vector<String>	required_members;

	/**
	 * Provide a constructor to be used by extending classes.
	 */
	protected GroupImpl(UserAdminImpl ua, String name, int type) {
		super(ua, name, type);
		basic_members = new Vector<>();
		required_members = new Vector<>();
	}

	/**
	 * Group-specific constructor.
	 */
	protected GroupImpl(UserAdminImpl ua, String name) {
		this(ua, name, Role.GROUP);
	}

	/**
	 * Adds a basic member to this Group. Implementation of
	 * {@link org.osgi.service.useradmin.Group#addMember}.
	 */
	@Override
	public boolean addMember(Role role) {
		ua.checkPermission(ua.adminPermission);
		if (!(role instanceof RoleImpl) || ((RoleImpl) role).ua != ua)
			throw new IllegalArgumentException("Bad role");
		@SuppressWarnings("hiding")
		String name = role.getName();
		if (basic_members.contains(name) || required_members.contains(name))
			return false;
		basic_members.addElement(name);
		ua.save();
		return true;
	}

	/**
	 * Adds a required member to this Group. Implementation of
	 * {@link org.osgi.service.useradmin.Group#addRequiredMember}.
	 */
	@Override
	public boolean addRequiredMember(Role role) {
		ua.checkPermission(ua.adminPermission);
		if (!(role instanceof RoleImpl) || ((RoleImpl) role).ua != ua)
			throw new IllegalArgumentException("Bad role");
		@SuppressWarnings("hiding")
		String name = role.getName();
		if (basic_members.contains(name) || required_members.contains(name))
			return false;
		required_members.addElement(name);
		ua.save();
		return true;
	}

	/**
	 * Removes a member from this Group. Implementation of
	 * {@link org.osgi.service.useradmin.Group#removeMember}.
	 */
	@Override
	public boolean removeMember(Role role) {
		ua.checkPermission(ua.adminPermission);
		if (role == null || !(role instanceof RoleImpl)
				|| ((RoleImpl) role).ua != ua)
			throw new IllegalArgumentException("Bad role");
		@SuppressWarnings("hiding")
		String name = role.getName();
		synchronized (this) {
			boolean removed = basic_members.remove(name)
					|| required_members.remove(name);
			if (removed)
				ua.save();
			return removed;
		}
	}

	/**
	 * Gets the basic members of Group. Implementation of
	 * {@link org.osgi.service.useradmin.Group#getMembers}.
	 */
	@Override
	public Role[] getMembers() {
		Role[] rs = new Role[basic_members.size()];
		if (rs.length == 0)
			return null;
		Enumeration<String> en = basic_members.elements();
		for (int i = 0; en.hasMoreElements(); i++) {
			rs[i] = ua.getRole(en.nextElement());
		}
		return rs;
	}

	/**
	 * Gets the required members members of Group. Implementation of
	 * {@link org.osgi.service.useradmin.Group#getRequiredMembers}.
	 */
	@Override
	public Role[] getRequiredMembers() {
		Role[] rs = new Role[required_members.size()];
		if (rs.length == 0)
			return null;
		Enumeration<String> en = required_members.elements();
		for (int i = 0; en.hasMoreElements(); i++) {
			rs[i] = ua.getRole(en.nextElement());
		}
		return rs;
	}

	/* -------------- Protected methods -------------- */
	/**
	 * Checks if this group is implied by the specified authorization context.
	 * 
	 * @overrides Role#impliedBy
	 */
	@Override
	protected boolean impliedBy(AuthorizationImpl auth) {
		Boolean b = auth.cachedHaveRole(this);
		if (b != null)
			return b.booleanValue();
		if (auth.isWorkingOnRole(this)) {
			System.err.println("Loop in role database: role " + getName()
					+ " refers to itself.");
			return auth.cacheHaveRole(this, false);
		}
		/* Not yet cached */
		auth.workingOnRole(this);
		synchronized (this) {
			// First check that all required roles are implied.
			for (Enumeration<String> en = required_members.elements(); en
					.hasMoreElements();) {
				RoleImpl role = (RoleImpl) ua
						.getRole(en.nextElement());
				if (!role.impliedBy(auth)) {
					return auth.cacheHaveRole(this, false);
				}
			}
			// Next check that at least one basic role is implied.
			for (Enumeration<String> en = basic_members.elements(); en
					.hasMoreElements();) {
				RoleImpl role = (RoleImpl) ua
						.getRole(en.nextElement());
				if (role.impliedBy(auth)) {
					return auth.cacheHaveRole(this, true);
				}
			}
			// No basic roles implied -> this role not implied
			return auth.cacheHaveRole(this, false);
		}
	}

	/**
	 * Removes references to the specified role.
	 * 
	 * @overrides Role#removeReferenceTo
	 */
	@Override
	protected void removeReferenceTo(RoleImpl role) {
		basic_members.remove(role.getName());
		required_members.remove(role.getName());
	}
}

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

package org.osgi.service.cdi.runtime.dto;

import java.util.List;

import org.osgi.dto.DTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.cdi.MaximumCardinality;
import org.osgi.service.cdi.runtime.dto.template.ReferenceTemplateDTO;

/**
 * A snapshot of the runtime state of a {@link ComponentInstanceDTO component} reference
 * dependency
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ReferenceDTO extends DTO {
	/**
	 * The template of this reference.
	 * <p>
	 * Must not be {@code null}
	 */
	public ReferenceTemplateDTO	template;

	/**
	 * The runtime minimum cardinality of the dependency.
	 * <p>
	 * <ul>
	 * <li>If {@link #template
	 * template}.{@link ReferenceTemplateDTO#maximumCardinality
	 * maximumCardinality} is {@link MaximumCardinality#ONE ONE} the value must
	 * be either 0 or 1.</li>
	 * <li>If {@link #template
	 * template}.{@link ReferenceTemplateDTO#maximumCardinality
	 * maximumCardinality} is {@link MaximumCardinality#MANY MANY} the value
	 * must be from 0 to {@link Integer#MAX_VALUE}.
	 * </ul>
	 */
	public int			minimumCardinality;

	/**
	 * Indicates the runtime target filter used in addition to the
	 * {@link #template template}.{@link ReferenceTemplateDTO#serviceType
	 * serviceType} to match services.
	 */
	public String		targetFilter;

	/**
	 * The list of service references that match this reference.
	 * <p>
	 * Must not be {@code null}
	 * <p>
	 * Can be empty when there are no matching services.
	 * <p>
	 * This dependency is satisfied when {@link #minimumCardinality
	 * minimumCardinality} {@code <= matches.size() <=}
	 * {@link MaximumCardinality#toInt()} where the maximum cardinality can be
	 * obtained from the associated {@link ReferenceTemplateDTO}.
	 */
	public List<ServiceReferenceDTO>	matches;
}

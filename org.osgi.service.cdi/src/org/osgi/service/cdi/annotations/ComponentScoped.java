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

package org.osgi.service.cdi.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Scope;

/**
 * This scope is used to declare a bean who's lifecycle is determined by the
 * state of it's OSGi dependencies and the {@link SingleComponent
 * SingleComponent(s)} and {@link FactoryComponent FactoryComponent(s)} that may
 * reference it through injection.
 *
 * @author $Id$
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Scope
@Target({
		FIELD, METHOD, PARAMETER, TYPE
})
public @interface ComponentScoped {

	/**
	 * Support inline instantiation of the {@link ComponentScoped} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<ComponentScoped>
			implements ComponentScoped {

		/**
		 * Default instance.
		 */
		public static final ComponentScoped	INSTANCE			= new Literal();

		private static final long			serialVersionUID	= 1L;

	}

}

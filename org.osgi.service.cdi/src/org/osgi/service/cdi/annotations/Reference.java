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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Annotation used on injection points informing the CDI container that the
 * injection should apply a service obtained from the OSGi registry.
 * <p>
 * *
 * @see "Reference Annotation"
 * @author $Id$
 */
@Documented
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
public @interface Reference {

	/**
	 * A marker type used in {@link Reference#value} to indicate that a
	 * reference injection point may accept any service type(s).
	 * <p>
	 * The injection point service type must be specified as {@link Object}.
	 * <p>
	 * The value must be specified by itself.
	 * <p>
	 * For example:
	 *
	 * <pre>
	 * &#64;Inject
	 * &#64;Reference(value = Any.class, target = "(bar=baz)")
	 * List&lt;Object&gt; services;
	 * </pre>
	 */
	public static final class Any {/**/}

	/**
	 * Support inline instantiation of the {@link Reference} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<Reference>
			implements Reference {

		private static final long serialVersionUID = 1L;

		/**
		 * @param service
		 * @param target
		 * @return instance of {@link Reference}
		 */
		public static final Literal of(
				Class<?> service,
				String target) {

			return new Literal(service, target);
		}

		private Literal(
				Class<?> service,
				String target) {
			_service = service;
			_target = target;
		}

		@Override
		public Class<?> value() {
			return _service;
		}

		@Override
		public String target() {
			return _target;
		}

		private final Class<?>	_service;
		private final String	_target;

	}

	/**
	 * Specify the type of the service for this reference.
	 * <p>
	 * If not specified, the type of the service for this reference is derived from
	 * the injection point type.
	 * <p>
	 * If a value is specified it must be type compatible with (assignable to) the
	 * service type derived from the injection point type, otherwise a definition
	 * error will result.
	 */
	@Nonbinding
	Class<?> value() default Object.class;

	/**
	 * The target property for this reference.
	 *
	 * <p>
	 * If not specified, no target property is set.
	 */
	@Nonbinding
	String target() default "";
}

/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.cdi.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import org.osgi.service.cdi.CdiConstants;

/**
 * Annotation used in collaboration with {@link ComponentScoped} to specify
 * singleton configurations and their policy.
 *
 * @author $Id$
 */
@Documented
@Qualifier
@Repeatable(PIDs.class)
@RequireCdiComponentRuntime
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
public @interface PID {

	/**
	 * Values available for use with {@link #policy policy}.
	 */
	public static enum Policy {
		/**
		 * The configuration is not required.
		 */
		OPTIONAL,
		/**
		 * The configuration is required.
		 */
		REQUIRED
	}

	/**
	 * Support inline instantiation of the {@link PID} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<PID> implements PID {

		private static final long serialVersionUID = 1L;

		/**
		 * @param pid the configuration pid
		 * @param policy the policy of the configuration
		 * @return an instance of {@link PID}
		 */
		public static final Literal of(String pid, Policy policy) {
			return new Literal(pid, policy);
		}

		private Literal(String pid, Policy policy) {
			_pid = pid;
			_policy = policy;
		}

		@Override
		public String value() {
			return _pid;
		}

		@Override
		public Policy policy() {
			return _policy;
		}

		private final String _pid;
		private final Policy	_policy;

	}

	/**
	 * The configuration PID for the configuration of this Component.
	 *
	 * <p>
	 * The value specifies a configuration PID who's configuration properties are
	 * available at injection points in the component.
	 *
	 * <p>
	 * A special string (<code>"$"</code>) can be used to specify the name of the
	 * component as a configuration PID. The {@link CdiConstants#CDI_COMPONENT_NAME
	 * CDI_COMPONENT_NAME} constant holds this special string.
	 *
	 * <p>
	 * For example:
	 *
	 * <pre>
	 * {@code @PID(CDI_COMPONENT_NAME)}
	 * </pre>
	 */
	String value() default CdiConstants.CDI_COMPONENT_NAME;

	/**
	 * The configuration policy associated with this PID.
	 *
	 * <p>
	 * Controls how the configuration must be satisfied depending on the presence
	 * and type of a corresponding Configuration object in the OSGi Configuration
	 * Admin service. Corresponding configuration is a Configuration object where
	 * the PID is equal to {@link PID#value() value}.
	 *
	 * <p>
	 * If not specified, the configuration is not required.
	 */
	Policy policy() default Policy.OPTIONAL;

}
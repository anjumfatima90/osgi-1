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
package org.osgi.test.cases.webcontainer.util.validate;

/**
 * @version $Rev$ $Date$
 * 
 *          validator interface
 */
public interface Validator {

    public static final String WEB_CONTEXT_PATH = "Web-ContextPath";
    public static final String WEB_JSP_EXTRACT_LOCATION = "Web-JSPExtractLocation";
    
    public void validate() throws Exception;
}

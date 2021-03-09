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

package org.osgi.test.cases.transaction.util;

import javax.transaction.Synchronization;

/**
 * @version $Rev$ $Date$
 */
public class SimpleSynchronization implements Synchronization {
	
	private String valueBefore = "";
	private String valueAfter = "";
	private final String AFTER = "-after";
	private final String BEFORE = "-before";
	
    public void beforeCompletion() {
        valueBefore = valueBefore + BEFORE;
    }
    
    public void afterCompletion(int status) {
        valueAfter = valueAfter + AFTER;
    }
	
    public String getBeforeValue() {
        return valueBefore;
    }
    
    public String getAfterValue() {
        return valueAfter;
    }
}

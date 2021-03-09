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
 * this class is used to represent one simple manifest import package or export package,
 * such as packageName;version=1.0 or packageName;version="1.0".  This class has not been
 * enhanced to cover version ranges or other attributes
 *
 */
public class ManifestPackage {

    private String packageName;
    private VersionRange packageVersion;
    private static final String VERSIONATTRIBUTE = "version=";
    
    public ManifestPackage(String name) {
        // parse name into packageName and packageVersion
        name = name.trim();
        String[] values = name.split(";");
        if (values.length > 0) {
            this.packageName = values[0];
        } 
        for (int i = 1; i < values.length ; i++) {
            int j = values[i].indexOf(VERSIONATTRIBUTE);
            if ( j > -1) {
                String version = values[i].substring(j + VERSIONATTRIBUTE.length());
                // let's remove double quotes for example version="2.0.0"
                version = version.trim();
                if (version.startsWith("\"") && version.endsWith("\"")) {
                    version = version.substring(1, version.length() - 1);
                }

                int value = version.indexOf(",");
                if (value > 0) {
                    this.packageVersion = new VersionRange(version);   
                } else {
                    this.packageVersion = new VersionRange(version, true);
                }
            }
        }
    }
    
    public String getPackageName() {
        return this.packageName;
    }
    
    public VersionRange getPackageVersionRange() {
        if (this.packageVersion == null) {
            return new VersionRange("0.0.0");
        } else {
            return this.packageVersion;
        }
    }
}

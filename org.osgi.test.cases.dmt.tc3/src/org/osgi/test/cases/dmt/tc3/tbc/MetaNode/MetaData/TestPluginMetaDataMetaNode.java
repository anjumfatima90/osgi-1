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

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Abr 14, 2005  Luiz Felipe Guimaraes
 * 1			 Implement Meg TCK
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc3.tbc.MetaNode.MetaData;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

public class TestPluginMetaDataMetaNode implements MetaNode {

	private boolean	 canAdd;
	private boolean	 canDelete;
	private boolean	 canGet;
	private boolean	 canReplace;
	private boolean	 canExecute;
	private DmtData  defaultValue;
	private String   description;
    private int      format;
	private boolean	 isLeaf;	
    private boolean  isZeroOccurrenceAllowed;
    private int      max;
    private int      maxOccurrence;
    private int      min;
	private String[] mimeTypes;
    private int      scope;
	private DmtData[] validValues;
	private String[] validNames;

	

    public TestPluginMetaDataMetaNode() {
        this.isLeaf        = false;
        this.format        = DmtData.FORMAT_NODE;
        this.canAdd        = true;
        this.canDelete     = true;
        this.canGet        = true;
        this.canReplace    = true;
        this.canExecute    = true;
        this.defaultValue  = null;
        this.description   = "";
        this.isZeroOccurrenceAllowed = false;
        this.max 		   = Integer.MAX_VALUE;
        this.maxOccurrence = Integer.MAX_VALUE;
        this.min 		   = Integer.MIN_VALUE; 
        this.mimeTypes 	   = null;
        this.scope = MetaNode.DYNAMIC;
        this.validValues   = null;
        this.validNames    = null;
        
    }
 

    @Override
	public boolean can(int operation) {
        switch(operation) {
        case CMD_DELETE:  return canDelete;
        case CMD_ADD:     return canAdd;
        case CMD_GET:     return canGet;
        case CMD_REPLACE: return canReplace;
        case CMD_EXECUTE: return canExecute;
        }
        return false;
    }       	

    @Override
	public boolean isLeaf() {
		return isLeaf;
	}

	@Override
	public int getScope() {
		return scope;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getMaxOccurrence() {
		return maxOccurrence;
	}

	@Override
	public boolean isZeroOccurrenceAllowed() {
		return isZeroOccurrenceAllowed;
	}

	@Override
	public DmtData getDefault() {
		return defaultValue;
	}

	@Override
	public double getMax() {
		return max;
	}

	@Override
	public double getMin() {
		return min;
	}

    @Override
	public String[] getValidNames() {
        return validNames;
    }
    
	@Override
	public DmtData[] getValidValues() {
		return validValues;
	}

	@Override
	public int getFormat() {
		return format;
	}

    public String getNamePattern() {
        return null;
    }
    
	public String getPattern() {
		return null;
	}

	@Override
	public String[] getMimeTypes() {
		return mimeTypes;
	}
	/**
	 * @param canAdd The canAdd to set.
	 */
	public void setCanAdd(boolean canAdd) {
		this.canAdd = canAdd;
	}
	/**
	 * @param canDelete The canDelete to set.
	 */
	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
	/**
	 * @param canGet The canGet to set.
	 */
	public void setCanGet(boolean canGet) {
		this.canGet = canGet;
	}
	/**
	 * @param canReplace The canReplace to set.
	 */
	public void setCanReplace(boolean canReplace) {
		this.canReplace = canReplace;
	}
    /**
     * @param canExecute The canExecute to set.
     */
    public void setCanExecute(boolean canExecute) {
        this.canExecute = canExecute;
    }
	/**
	 * @param defaultValue The defaultValue to set.
	 */
	public void setDefaultValue(DmtData defaultValue) {
		this.defaultValue = defaultValue;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @param format The format to set.
	 */
	public void setFormat(int format) {
		this.format = format;
	}
	/**
	 * @param isLeaf The isLeaf to set.
	 */
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	/**
	 * @param isZeroOccurrenceAllowed The isZeroOccurrenceAllowed to set.
	 */
	public void setZeroOccurrenceAllowed(boolean isZeroOccurrenceAllowed) {
		this.isZeroOccurrenceAllowed = isZeroOccurrenceAllowed;
	}
	/**
	 * @param max The max to set.
	 */
	public void setMax(int max) {
		this.max = max;
	}
	/**
	 * @param maxOccurrence The maxOccurrence to set.
	 */
	public void setMaxOccurrence(int maxOccurrence) {
		this.maxOccurrence = maxOccurrence;
	}
	/**
	 * @param mimeTypes The mimeTypes to set.
	 */
	public void setMimeTypes(String[] mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	/**
	 * @param min The min to set.
	 */
	public void setMin(int min) {
		this.min = min;
	}
	/**
	 * @param scope The scope to set.
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}
	/**
	 * @param validNames The validNames to set.
	 */
	public void setValidNames(String[] validNames) {
		this.validNames = validNames;
	}
	/**
	 * @param validValues The validValues to set.
	 */
	public void setValidValues(DmtData[] validValues) {
		this.validValues = validValues;
	}


	@Override
	public boolean isValidValue(DmtData value) {
	    if (validValues==null)
	        return true;
	    
		for (int i=0;i<validValues.length;i++) {
			if (value.equals(validValues[i])) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isValidName(String name) {
	    if (validNames==null)
	        return true;
	    
		for (int i=0;i<validNames.length;i++) {
			if (name.equals(validNames[i])) {
				return true;
			}
		}
		return false;
	}


	@Override
	public String[] getExtensionPropertyKeys() {
		return null;
	}


	@Override
	public Object getExtensionProperty(String key) {
		return null;
	}


	@Override
	public String[] getRawFormatNames() {
		return null;
	}

}

/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.switchyard.component.soap;

import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.MTOMFeature;

import org.switchyard.component.soap.config.model.SOAPBindingModel;

/**
 * Feature booleans.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class Feature {

    private Boolean _addressingEnabled = false;
    private Boolean _addressingRequired = false;
    private Boolean _mtomEnabled = false;

    /**
     * Check if addressing feature is enabled.
     * 
     * @return true if addressing is enabled, false otherwise
     */
    public Boolean isAddressingEnabled() {
        return _addressingEnabled;
    }

    /**
     * Set if addressing is enabled.
     * 
     * @param enabled true if addressing is enabled, false otherwise
     */
    public void setAddressingEnabled(Boolean enabled) {
        _addressingEnabled = enabled;
    }

    /**
     * Check if addressing feature is required.
     * 
     * @return true if addressing is required, false otherwise
     */
    public Boolean isAddressingRequired() {
        return _addressingRequired;
    }

    /**
     * Set if addressing is required.
     * 
     * @param required true if addressing is required, false otherwise
     */
    public void setAddressingRequired(Boolean required) {
        _addressingRequired = required;
    }

    /**
     * Check if MTOM feature is enabled.
     * 
     * @return true if MTOM is enabled, false otherwise
     */
    public Boolean isMtomEnabled() {
        return _mtomEnabled;
    }

    /**
     * Set if MTOM is enabled.
     * 
     * @param enabled true if addressing is enabled, false otherwise
     */
    public void setMtomEnabled(Boolean enabled) {
        _mtomEnabled = enabled;
    }

    /**
     * Create Addressing feature.
     * 
     * @return the Addressing feature
     */
    public AddressingFeature getAddressing() {
        return new AddressingFeature(_addressingEnabled, _addressingRequired);
    }

    /**
     * Create MTOM feature from config.
     * 
     * @param config the soap config
     * @return the MTOM feature
     */
    public MTOMFeature getMtom(SOAPBindingModel config) {
        if (config.getMtomConfig() == null) {
            return new MTOMFeature(_mtomEnabled);
        }
        MTOMFeature mtom = null;
        if (config.getMtomConfig().getThreshold() != null) {
            mtom = new MTOMFeature(true, config.getMtomConfig().getThreshold());
        } else if (config.getMtomConfig().isEnabled() != null) {
            mtom = new MTOMFeature(config.getMtomConfig().isEnabled());
        } else {
            mtom = new MTOMFeature(_mtomEnabled);
        }
        return mtom;
    }

    /**
     * Returns a String representation of this class.
     * 
     * @return the String representation
     */
    public String toString() {
        return "[AddressingEnabled:" + _addressingEnabled + ", AddressingRequired:" +_addressingRequired + ", MtomEnabled:" + _mtomEnabled + "]";
    }
}

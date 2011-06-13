/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */


package org.switchyard.component.camel.config.model.direct.v1;

import java.net.URI;

import org.switchyard.component.camel.config.model.direct.CamelDirectBindingModel;
import org.switchyard.component.camel.config.model.v1.NameValueModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of AtomBindingModel.
 */
public class V1CamelDirectBindingModel extends V1BaseCamelBindingModel 
                implements CamelDirectBindingModel {
    
    /**
     * Camel endpoint type.
     */
    public static final String DIRECT = "direct";

    /**
     * Camel endpoint configuration values.
     */
    private static final String NAME            = "name";
    
    /**
     * Create a new CamelDirectBindingModel.
     */
    public V1CamelDirectBindingModel() {
        super(DIRECT);
        setModelChildrenOrder(
                NAME);
    }
    
    /**
     * Create a CamelDirectBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelDirectBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getName() {
        return getConfig(NAME);
    }
    
    @Override
    public V1CamelDirectBindingModel setName(String name) {
        setConfig(NAME, name);
        return this;
    }

    @Override
    public URI getComponentURI() {
        // base URI without params
        String uriStr = DIRECT + "://" + getConfig(NAME);
        return URI.create(uriStr.toString());
    }
    
    private String getConfig(String configName) {
        Configuration config = getModelConfiguration().getFirstChild(configName);
        if (config != null) {
            return config.getValue();
        } else {
            return null;
        }
    }
    
    private void setConfig(String name, String value) {
        Configuration config = getModelConfiguration().getFirstChild(name);
        if (config != null) {
            // set an existing config value
            config.setValue(value);
        } else {
            // create the config model and set the value
            NameValueModel model = new NameValueModel(name);
            model.setValue(value);
            setChildModel(model);
        }
    }
    
}

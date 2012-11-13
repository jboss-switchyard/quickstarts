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
package org.switchyard.component.camel.config.model.jms.v1;

import java.net.URI;

import org.switchyard.component.camel.config.model.generic.v1.V1GenericMqBindingModel;
import org.switchyard.component.camel.config.model.jms.CamelJmsBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of {@link V1CamelJmsBindingModel}.
 */
public class V1CamelJmsBindingModel extends V1GenericMqBindingModel implements CamelJmsBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String JMS = "jms";

    /**
     * Create a new CamelJmsBindingModel.
     */
    public V1CamelJmsBindingModel() {
        super(JMS);
    }

    /**
     * Create a binding model from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param descriptor The switchyard descriptor instance.
     */
    public V1CamelJmsBindingModel(Configuration config, Descriptor descriptor) {
        super(config, descriptor);
    }

    @Override
    public URI getComponentURI() {
        return getComponentURI(JMS);
    }

}

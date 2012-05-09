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
package org.switchyard.component.camel.config.model.file.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.camel.config.model.file.CamelFileProducerBindingModel;
import org.switchyard.component.camel.config.model.generic.v1.V1GenericFileProducerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A binding for Camel's file component, for producer configurations.
 * 
 * @author Mario Antollini
 */
public class V1CamelFileProducerBindingModel extends V1GenericFileProducerBindingModel implements
        CamelFileProducerBindingModel {

    /**
     * Create a new V1CamelFileProducerBindingModel.
     */
    public V1CamelFileProducerBindingModel() {
        super(new QName(V1CamelFileBindingModel.PRODUCE));
    }

    /**
     * Create a V1CamelFileProducerBindingModel from the specified configuration
     * and descriptor.
     * 
     * @param config
     *            The switchyard configuration instance.
     * @param desc
     *            The switchyard descriptor instance.
     */
    public V1CamelFileProducerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

}

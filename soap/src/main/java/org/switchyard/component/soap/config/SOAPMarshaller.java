/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.component.soap.config;

import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;

/**
 * Marshaller for SOAP Gateway configurations.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SOAPMarshaller extends BaseMarshaller {

    /**
     * Construct a SOAP Model Marshaller with help of a Descriptor.
     * 
     * @param desc the Descriptor 
     */
    public SOAPMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads a SOAP Model configuration.
     * 
     * @param config the configuration
     * @return the SOAP Binding Model 
     */
    @Override
    public Model read(Configuration config) {
        Descriptor desc = getDescriptor();
        if (config.getName().startsWith(BindingModel.BINDING)) {
            return new SOAPBindingModel(config, desc);
        }
        return super.read(config);
    }
}

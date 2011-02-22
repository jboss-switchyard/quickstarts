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
package org.switchyard.component.bean.config.model.v1;

import org.switchyard.component.bean.config.model.BeanComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.v1.V1CompositeMarshaller;

/**
 * A CompositeMarshaller which can also create BeanComponentImplementationModels.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1BeanMarshaller extends V1CompositeMarshaller {

    private static final String NAME = ComponentImplementationModel.IMPLEMENTATION + "." + BeanComponentImplementationModel.BEAN;

    /**
     * Required constructor called via reflection.
     * 
     * @param desc the Descriptor
     */
    public V1BeanMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads in the Configuration, looking for "implementation.bean".
     * If not found, it falls back to the super class (V1CompositeMarshaller).
     * 
     * @param config the Configuration
     * @return the Model
     */
    @Override
    public Model read(Configuration config) {
        if (config.getName().equals(NAME)) {
            return new V1BeanComponentImplementationModel(config, getDescriptor());
        }
        return super.read(config);
    }

}

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
package org.switchyard.component.bpel.config.model.v1;

import org.switchyard.component.bpel.config.model.BPELComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.v1.V1CompositeMarshaller;

/**
 * A CompositeMarshaller which can also create BPELComponentImplementationModels.
 *
 */
public class V1BPELMarshaller extends V1CompositeMarshaller {

    /**
     * The complete local name ("implementation.bpel").
     */
    private static final String IMPLEMENTATION_BPEL= ComponentImplementationModel.IMPLEMENTATION + "." + BPELComponentImplementationModel.BPEL;

    /**
     * Required constructor called via reflection.
     *
     * @param desc the Descriptor
     */
    public V1BPELMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads in the Configuration, looking for "implementation.bpel".
     * If not found, it falls back to the super class (V1CompositeMarshaller).
     *
     * @param config the Configuration
     * @return the Model
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        if (IMPLEMENTATION_BPEL.equals(name)) {
            return new V1BPELComponentImplementationModel(config, getDescriptor());
        }
        return super.read(config);
    }

}

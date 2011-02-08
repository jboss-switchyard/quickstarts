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
package org.switchyard.config.model.composite.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.Descriptor;
import org.switchyard.config.model.BaseModelMarshaller;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.ExternalServiceModel;
import org.switchyard.config.model.composite.ImplementationModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.composite.ReferenceModel;

/**
 * V1CompositeModelMarshaller.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1CompositeModelMarshaller extends BaseModelMarshaller {

    public V1CompositeModelMarshaller(Descriptor desc) {
        super(desc);
    }

    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (name.equals(CompositeModel.COMPOSITE)) {
            return new V1CompositeModel(config, desc);
        } else if (name.equals(ExternalServiceModel.SERVICE)) {
            if (config.getFirstChildStartsWith(BindingModel.BINDING) != null) {
                return new V1ExternalServiceModel(config, desc);
            } else {
                return new V1InternalServiceModel(config, desc);
            }
        } else if (name.startsWith(BindingModel.BINDING)) {
            return new V1BindingModel(config, desc);
        } else if (name.equals(ComponentModel.COMPONENT)) {
            return new V1ComponentModel(config, desc);
        } else if (name.startsWith(ImplementationModel.IMPLEMENTATION)) {
            return new V1ImplementationModel(config, desc);
        } else if (name.startsWith(InterfaceModel.INTERFACE)) {
            return new V1InterfaceModel(config, desc);
        } else if (name.equals(ReferenceModel.REFERENCE)) {
            return new V1ReferenceModel(config, desc);
        }
        return null;
    }

}

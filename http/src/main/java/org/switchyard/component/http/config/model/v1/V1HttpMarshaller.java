/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.http.config.model.v1;

import org.switchyard.component.common.selector.config.model.v1.V1CommonBindingMarshaller;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;
import org.switchyard.config.model.composer.v1.V1MessageComposerModel;
import org.switchyard.config.model.composite.BindingModel;

/**
 * Marshaller for HTTP Gateway configurations.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class V1HttpMarshaller extends V1CommonBindingMarshaller {

    /**
     * Construct a HTTP Model Marshaller with help of a Descriptor.
     * 
     * @param desc the Descriptor 
     */
    public V1HttpMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads a HTTP Model configuration.
     * 
     * @param config the configuration
     * @return the HTTP Binding Model 
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        if (name.startsWith(BindingModel.BINDING)) {
            return new HttpBindingModel(config, getDescriptor());
        }
        if (name.equals(ContextMapperModel.CONTEXT_MAPPER)) {
            return new V1ContextMapperModel(config, getDescriptor());
        }
        if (name.equals(MessageComposerModel.MESSAGE_COMPOSER)) {
            return new V1MessageComposerModel(config, getDescriptor());
        }
        return super.read(config);
    }
}

/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.hornetq.config.model.v1;

import org.switchyard.component.common.selector.config.model.v1.V1CommonBindingMarshaller;
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.HornetQConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQConnectorConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQDiscoveryGroupConfigModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;
import org.switchyard.config.model.composer.v1.V1MessageComposerModel;
import org.switchyard.config.model.domain.PropertiesModel;
import org.switchyard.config.model.domain.PropertyModel;
import org.switchyard.config.model.domain.v1.V1PropertiesModel;
import org.switchyard.config.model.domain.v1.V1PropertyModel;

/**
 * A Version 1.0 Model Marshaller.
 *  
 * @author Daniel Bevenius
 *
 */
public class V1ModelMarshaller extends V1CommonBindingMarshaller {

    /**
     * Required constructor called via reflection.
     * 
     * @param desc the Descriptor
     */
    public V1ModelMarshaller(final Descriptor desc) {
        super(desc);
    }

    @Override
    public Model read(final Configuration config) {
        final String name = config.getName();
        
        if (name.startsWith(HornetQBindingModel.BINDING)) {
           return new V1HornetQBindingModel(config, getDescriptor());
        }
        
        if (name.startsWith(HornetQConfigModel.CONFIG)) {
            return new V1HornetQConfigModel(config, getDescriptor());
        }
        
        if (name.startsWith(HornetQDiscoveryGroupConfigModel.DISCOVERY_GROUP)) {
            return new V1DiscoveryGroupConfigModel(config, getDescriptor());
        }
        
        if (name.startsWith(HornetQConnectorConfigModel.CONNECTOR)) {
            return new V1ConnectorConfigModel(config, getDescriptor());
        }
        
        if (name.startsWith(PropertiesModel.PROPERTIES)) {
            return new V1PropertiesModel(config, getDescriptor());
        }
        
        if (name.startsWith(PropertyModel.PROPERTY)) {
            return new V1PropertyModel(config, getDescriptor());
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

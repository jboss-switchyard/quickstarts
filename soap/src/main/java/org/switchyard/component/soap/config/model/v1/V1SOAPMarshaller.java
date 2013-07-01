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

package org.switchyard.component.soap.config.model.v1;

import org.switchyard.component.soap.config.model.EndpointConfigModel;
import org.switchyard.component.soap.config.model.InterceptorModel;
import org.switchyard.component.soap.config.model.InterceptorsModel;
import org.switchyard.component.soap.config.model.SOAPNameValueModel.SOAPName;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.PropertyModel;
import org.switchyard.config.model.property.v1.V1PropertiesModel;
import org.switchyard.config.model.property.v1.V1PropertyModel;

/**
 * Marshaller for SOAP Gateway configurations.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class V1SOAPMarshaller extends BaseMarshaller {

    /**
     * Construct a SOAP Model Marshaller with help of a Descriptor.
     * 
     * @param desc the Descriptor 
     */
    public V1SOAPMarshaller(Descriptor desc) {
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
        String name = config.getName();
        if (name.startsWith(BindingModel.BINDING)) {
            return new V1SOAPBindingModel(config, desc);
        } else if (name.equals(ContextMapperModel.CONTEXT_MAPPER)) {
            return new V1SOAPContextMapperModel(config, desc);
        } else if (name.equals(MessageComposerModel.MESSAGE_COMPOSER)) {
            return new V1SOAPMessageComposerModel(config, desc);
        } else if (name.equals(SOAPName.mtom.name())) {
            return new V1MtomModel(config, desc);
        } else if (name.equals(SOAPName.proxy.name())) {
            return new V1ProxyModel(config, desc);
        } else if (name.equals(EndpointConfigModel.ENDPOINT_CONFIG)) {
            return new V1EndpointConfigModel(config, desc);
        } else if (name.equals(InterceptorsModel.IN_INTERCEPTORS) || name.equals(InterceptorsModel.OUT_INTERCEPTORS)) {
            return new V1InterceptorsModel(config, desc);
        } else if (name.equals(InterceptorModel.INTERCEPTOR)) {
            return new V1InterceptorModel(config, desc);
        } else if (name.equals(PropertiesModel.PROPERTIES)) {
            return new V1PropertiesModel(config, desc);
        } else if (name.equals(PropertyModel.PROPERTY)) {
            return new V1PropertyModel(config, desc);
        } else {
            for (SOAPName n : SOAPName.values()) {
                if (n.name().equals(name)) {
                    return new V1SOAPNameValueModel(config, desc);
                }
            }
        }
        return null;
    }
}

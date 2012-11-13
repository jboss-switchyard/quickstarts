/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.component.camel.config.model.amqp.v1;

import java.net.URI;

import org.switchyard.component.camel.config.model.amqp.CamelAmqpBindingModel;
import org.switchyard.component.camel.config.model.generic.v1.V1GenericMqBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of {@link CamelAmqpBindingModel}.
 * 
 * @author: <a href="mailto:eduardo.devera@gmail.com">Eduardo de Vera</a>
 */
public class V1CamelAmqpBindingModel extends V1GenericMqBindingModel implements CamelAmqpBindingModel {

    /**
     * Camel component prefix / binding prefix.
     */
    public static final String AMQP = "amqp";

    /**
     * Default consturctor, creates binding using only prefix.
     */
    public V1CamelAmqpBindingModel() {
        super(AMQP);
    }

    /**
     * Create a binding from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param descriptor The switchyard descriptor instance.
     */
    public V1CamelAmqpBindingModel(Configuration config, Descriptor descriptor) {
        super(config, descriptor);
    }

    @Override
    public URI getComponentURI() {
        return getComponentURI(AMQP);
    }

}

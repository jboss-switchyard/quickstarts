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
package org.switchyard.component.camel.jpa.model.v1;

import static org.switchyard.component.camel.jpa.model.Constants.JPA_NAMESPACE_V1;

import org.switchyard.component.camel.common.model.v1.V1BaseCamelModel;
import org.switchyard.component.camel.jpa.model.CamelJpaProducerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of jpa producer binding model.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelJpaProducerBindingModel extends V1BaseCamelModel
    implements CamelJpaProducerBindingModel {

    private static final String FLUS_ON_SEND = "flushOnSend";
    private static final String USE_PERSIST = "usePersist";

    /**
     * Create a binding from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelJpaProducerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);

        setModelChildrenOrder(FLUS_ON_SEND, USE_PERSIST);
    }

    /**
     * Creates new binding model.
     */
    public V1CamelJpaProducerBindingModel() {
        super(V1CamelJpaBindingModel.PRODUCE, JPA_NAMESPACE_V1);
    }

    @Override
    public Boolean isFlushOnSend() {
        return getBooleanConfig(FLUS_ON_SEND);
    }

    @Override
    public V1CamelJpaProducerBindingModel setFlushOnSend(Boolean flushOnSend) {
        return setConfig(FLUS_ON_SEND, flushOnSend);
    }

    @Override
    public Boolean isUsePersist() {
        return getBooleanConfig(USE_PERSIST);
    }

    @Override
    public V1CamelJpaProducerBindingModel setUsePersist(Boolean usePersist) {
        return setConfig(USE_PERSIST, usePersist);
    }

}

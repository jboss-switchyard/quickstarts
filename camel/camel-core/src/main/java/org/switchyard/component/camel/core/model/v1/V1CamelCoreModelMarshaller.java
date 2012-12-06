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
package org.switchyard.component.camel.core.model.v1;

import static org.switchyard.component.camel.core.model.Constants.CORE_NAMESPACE_V1;

import org.switchyard.component.camel.common.marshaller.BaseModelMarshaller;
import org.switchyard.component.camel.common.marshaller.ModelCreator;
import org.switchyard.component.camel.core.model.direct.v1.V1CamelDirectBindingModel;
import org.switchyard.component.camel.core.model.mock.v1.V1CamelMockBindingModel;
import org.switchyard.component.camel.core.model.seda.v1.V1CamelSedaBindingModel;
import org.switchyard.component.camel.core.model.timer.v1.V1CamelTimerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A Marshaler that is able to read a {@link Configuration} and populate a
 * corresponding Model to the configuration informations.
 * <p>
 * 
 * @author Daniel Bevenius
 */
public class V1CamelCoreModelMarshaller extends BaseModelMarshaller {

    /**
     * Sole constructor.
     * 
     * @param desc The switchyard descriptor.
     */
    public V1CamelCoreModelMarshaller(Descriptor desc) {
        super(desc, CORE_NAMESPACE_V1);

        registerBinding(V1CamelBindingModel.URI, new ModelCreator<V1CamelBindingModel>() {
            @Override
            public V1CamelBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelBindingModel(config, descriptor);
            }
        });
        registerBinding(V1CamelDirectBindingModel.DIRECT, new ModelCreator<V1CamelDirectBindingModel>() {
            @Override
            public V1CamelDirectBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelDirectBindingModel(config, descriptor);
            }
        });
        registerBinding(V1CamelTimerBindingModel.TIMER, new ModelCreator<V1CamelTimerBindingModel>() {
            @Override
            public V1CamelTimerBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelTimerBindingModel(config, descriptor);
            }
        });
        registerBinding(V1CamelSedaBindingModel.SEDA, new ModelCreator<V1CamelSedaBindingModel>() {
            @Override
            public V1CamelSedaBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelSedaBindingModel(config, descriptor);
            }
        });
        registerBinding(V1CamelMockBindingModel.MOCK, new ModelCreator<V1CamelMockBindingModel>() {
            @Override
            public V1CamelMockBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelMockBindingModel(config, descriptor);
            }
        });
    }

}

/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
package org.switchyard.component.camel.amqp.model.v1;

import org.switchyard.component.camel.common.marshaller.BaseModelMarshaller;
import org.switchyard.component.camel.common.marshaller.ModelCreator;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

import static org.switchyard.component.camel.amqp.model.Constants.AMQP_NAMESPACE_V1;

/**
 * AMQP model marshaller.
 */
public class V1CamelAmqpModelMarshaller extends BaseModelMarshaller {

    /**
     * Creates new marshaller.
     * 
     * @param desc Descriptor
     */
    public V1CamelAmqpModelMarshaller(Descriptor desc) {
        super(desc, AMQP_NAMESPACE_V1);

        registerBinding(V1CamelAmqpBindingModel.AMQP, new ModelCreator<V1CamelAmqpBindingModel>() {
            @Override
            public V1CamelAmqpBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelAmqpBindingModel(config, descriptor);
            }
        });
    }

}

/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.camel.jpa.deploy;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;

import org.apache.camel.ProducerTemplate;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.handler.OutboundHandler;
import org.switchyard.component.camel.jpa.model.CamelJpaBindingModel;
import org.switchyard.component.common.composer.MessageComposer;

/**
 * Outbound handler for JPA binding. Resolve EntityManagerFactory instance in case it runs on OSGi container.
 */
public class CamelJpaOutboundHandler extends OutboundHandler {

    /**
     * A constructor. @see OutboundHandler#OutboundHandler(CamelBindingModel, SwitchYardCamelContext, MessageComposer, ProducerTemplate, ServiceDomain)
     * @param binding binding model
     * @param context camel context
     * @param messageComposer message composer
     * @param producerTemplate producer template
     * @param domain service domain
     */
    public CamelJpaOutboundHandler(CamelJpaBindingModel binding,
            SwitchYardCamelContext context,
            MessageComposer<CamelBindingData> messageComposer,
            ProducerTemplate producerTemplate, ServiceDomain domain) {
        super(binding, context, messageComposer, producerTemplate, domain);

        EntityManagerFactory emf = EntityManagerFactoryLocator.locateEntityManagerFactory(binding.getPersistenceUnit(), new HashMap<String,String>());
        if (emf != null) {
            context.getWritebleRegistry().put(EntityManagerFactory.class.getName(), emf);
        }
    }

    /**
     * A constructor. @see OutboundHandler#OutboundHandler(CamelBindingModel, SwitchYardCamelContext, MessageComposer, ServiceDomain)
     * @param binding binding
     * @param context camel context
     * @param messageComposer message composer
     * @param domain service domain
     */
    public CamelJpaOutboundHandler(CamelJpaBindingModel binding,
            SwitchYardCamelContext context,
            MessageComposer<CamelBindingData> messageComposer,
            ServiceDomain domain) {
        this(binding, context, messageComposer, null, domain);
    }
}

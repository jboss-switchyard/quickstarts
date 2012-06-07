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
 * 02110_1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.bus.camel.processors;

import java.util.List;

import org.apache.camel.Processor;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.handlers.AddressingHandler;
import org.switchyard.handlers.PolicyHandler;
import org.switchyard.handlers.SecurityHandler;
import org.switchyard.handlers.TransactionHandler;
import org.switchyard.handlers.TransformHandler;
import org.switchyard.handlers.ValidateHandler;

/**
 * Enumeration representing processors used by exchange bus.
 */
public enum Processors {

    /**
     * Processor handling custom handlers registered in domain.
     */
    DOMAIN_HANDLERS {
        @Override
        public Processor create(ServiceDomain domain) {
            return wrap(domain.getHandlers());
        }
    },
    /**
     * Addressing handler.
     */
    ADDRESSING {
        @Override
        public Processor create(ServiceDomain domain) {
            return wrap(new AddressingHandler(domain));
        }
    },
    /**
     * Transaction handling processor.
     */
    TRANSACTION_HANDLER {
        @Override
        public Processor create(ServiceDomain domain) {
            return wrap(new TransactionHandler());
        }
    },
    /**
     * Security processor wrapper.
     */
    SECURITY {
        @Override
        public Processor create(ServiceDomain domain) {
            return wrap(new SecurityHandler(domain.getServiceSecurity()));
        }
    },
    /**
     * Policy handler.
     */
    GENERIC_POLICY {
        @Override
        public Processor create(ServiceDomain domain) {
            return wrap(new PolicyHandler());
        }
    },
    /**
     * Validation processor wrapper.
     */
    VALIDATION {
        @Override
        public Processor create(ServiceDomain domain) {
            return wrap(new ValidateHandler(domain.getValidatorRegistry()));
        }
    },
    /**
     * Transformation handler.
     */
    TRANSFORMATION {
        @Override
        public Processor create(ServiceDomain domain) {
            return wrap(new TransformHandler(domain.getTransformerRegistry()));
        }
    },
    /**
     * Processor calling service provider.
     */
    PROVIDER_CALLBACK {
        @Override
        public Processor create(ServiceDomain domain) {
            return new ProviderProcessor();
        }
    },
    /**
     * Reply chain handler.
     */
    CONSUMER_CALLBACK {
        @Override
        public Processor create(ServiceDomain domain) {
            return new ConsumerCallbackProcessor(new TransformHandler(domain.getTransformerRegistry()));
        }
    };

    /**
     * Creates new processor for given Service Domain.
     * 
     * @param domain Service domain.
     * @return Processor instance.
     */
    public Processor create(ServiceDomain domain) {
        throw new IllegalArgumentException();
    }

    /**
     * Wraps handler into camel Processor.
     * 
     * @param handler Handler to wrap.
     * @return Wrapping processor.
     */
    private static Processor wrap(ExchangeHandler handler) {
        return new HandlerProcessor(handler);
    }

    /**
     * Wraps handler collection into single camel Processor.
     * 
     * @param handlers Handler to wrap.
     * @return Wrapping processor.
     */
    private static Processor wrap(List<ExchangeHandler> handlers) {
        return new HandlerProcessor(handlers);
    }

}

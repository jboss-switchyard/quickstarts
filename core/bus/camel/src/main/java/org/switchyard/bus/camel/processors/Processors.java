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
package org.switchyard.bus.camel.processors;

import org.apache.camel.Processor;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangeInterceptor;
import org.switchyard.ServiceDomain;
import org.switchyard.bus.camel.BusMessages;
import org.switchyard.handlers.AddressingHandler;
import org.switchyard.handlers.PolicyHandler;
import org.switchyard.handlers.ProviderHandler;
import org.switchyard.handlers.SecurityHandler;
import org.switchyard.handlers.SecurityHandler.SecurityAction;
import org.switchyard.handlers.TransactionHandler;
import org.switchyard.handlers.TransformHandler;
import org.switchyard.handlers.ValidateHandler;

/**
 * Enumeration representing processors used by exchange bus.
 */
public enum Processors {

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
     * Security process wrapper.
     */
    SECURITY_PROCESS {
        @Override
        public Processor create(ServiceDomain domain) {
            return wrap(new SecurityHandler(domain, SecurityAction.PROCESS));
        }
    },
    /**
     * Security cleanup wrapper.
     */
    SECURITY_CLEANUP {
        @Override
        public Processor create(ServiceDomain domain) {
            return wrap(new SecurityHandler(domain, SecurityAction.CLEANUP));
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
            return wrap(new ProviderHandler(domain));
        }
    },
    /**
     * Reply chain handler.
     */
    CONSUMER_CALLBACK {
        @Override
        public Processor create(ServiceDomain domain) {
            return new ConsumerCallbackProcessor();
        }
    },
    /**
     * Processor which invokes consumer interceptors.
     */
    CONSUMER_INTERCEPT {
        @Override
        public Processor create(ServiceDomain domain) {
            return new InterceptProcessor(ExchangeInterceptor.CONSUMER, domain);
        }
    },
    /**
     * Processor which invokes provider interceptors.
     */
    PROVIDER_INTERCEPT {
        @Override
        public Processor create(ServiceDomain domain) {
            return new InterceptProcessor(ExchangeInterceptor.PROVIDER, domain);
        }
    },
    /**
     * Special case processor called as first in part of route responsible
     * for exception handling.
     */
    ERROR_HANDLING {
        @Override
        public Processor create(ServiceDomain domain) {
            return new ErrorHandlingProcessor();
        }
    };

    /**
     * Creates new processor for given Service Domain.
     * 
     * @param domain Service domain.
     * @return Processor instance.
     */
    public Processor create(ServiceDomain domain) {
        throw BusMessages.MESSAGES.methodMustBeOverridden();
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
}

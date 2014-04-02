/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.camel.netty.binding;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeSecurity;

/**
 * A POJO Service implementation.
 *
 * @author Lukasz Dywicki
 */
public class GreetingServiceBean implements GreetingService {

    private static final String MSG = ":: %s :: Hello %s! (caller principal=%s, in roles? 'friend'=%s 'enemy'=%s)";

    private final Logger _logger;
    private final String _type;

    @Inject
    private Exchange exchange;

    /**
     * Creates new service bean.
     * 
     * @param type Type of service.
     */
    protected GreetingServiceBean(String type) {
        _logger = Logger.getLogger(getClass());
        _type = type;
    }

    @Override
    public final void greet(final String name) {
        ExchangeSecurity es = exchange.getSecurity();
        String msg = String.format(MSG, _type, name, es.getCallerPrincipal(), es.isCallerInRole("friend"), es.isCallerInRole("enemy"));
        _logger.info(msg);
    }

}

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
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
package org.switchyard.quickstarts.demo.security.propagation.basic;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeSecurity;
import org.switchyard.component.bean.Service;

@Service(BackEndService.class)
public class BackEndServiceBean implements BackEndService {

    private static final Logger LOGGER = Logger.getLogger(BackEndServiceBean.class);
    private static final String MSG = ":: BackEndService :: process => %s (caller principal=%s, in roles? 'friend'=%s 'enemy'=%s)";

    @Inject
    private Exchange exchange;

    @Override
    public String process(String in) {
        ExchangeSecurity es = exchange.getSecurity();
        String msg = String.format(MSG, in, es.getCallerPrincipal(), es.isCallerInRole("friend"), es.isCallerInRole("enemy"));
        LOGGER.info(msg);
        return "Processed by BackEndService: " + in;
    }

}

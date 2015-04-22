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
package org.switchyard.quickstarts.bean.service;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import org.switchyard.Exchange;
import org.switchyard.ExchangeInterceptor;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;

/**
 * This is an example of an exchange interceptor which can be used to inject code
 * around targets during a message exchange.  This example updates the content of 
 * OrderAck after the provider has generated a response.
 */
@Named("UpdateStatus")
public class OrderInterceptor implements ExchangeInterceptor {

    @Override
    public void before(String target, Exchange exchange) throws HandlerException {
        // Not interested in doing anything before the provider is invoked
    }

    @Override
    public void after(String target, Exchange exchange) throws HandlerException {
        // We only want to intercept successful replies from OrderService
        if (exchange.getProvider().getName().getLocalPart().equals("OrderService")
            && ExchangeState.OK.equals(exchange.getState())) {
            OrderAck orderAck = exchange.getMessage().getContent(OrderAck.class);
            orderAck.setStatus(orderAck.getStatus() + " [intercepted]");
        }
    }

    @Override
    public List<String> getTargets() {
        return Arrays.asList(PROVIDER);
    }

}

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
package org.switchyard.bus.camel.handler;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeInterceptor;
import org.switchyard.HandlerException;
import org.switchyard.Scope;

/**
 * Captures content types used on an exchange.
 */
public class TypeInterceptor implements ExchangeInterceptor {
    
    private QName inType;
    private QName outType;
    

    @Override
    public void before(String target, Exchange exchange) throws HandlerException {
        inType = (QName)exchange.getContext().getProperty(Exchange.CONTENT_TYPE, Scope.MESSAGE).getValue();
    }

    @Override
    public void after(String target, Exchange exchange) throws HandlerException {
        outType = (QName)exchange.getContext().getProperty(Exchange.CONTENT_TYPE, Scope.MESSAGE).getValue();

    }

    @Override
    public List<String> getTargets() {
        return Arrays.asList(PROVIDER);
    }

    public QName getInType() {
        return inType;
    }
    
    public QName getOutType() {
        return outType;
    }

}

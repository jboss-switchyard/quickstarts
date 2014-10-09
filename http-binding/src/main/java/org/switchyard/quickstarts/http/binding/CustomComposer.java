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
package org.switchyard.quickstarts.http.binding;

import org.switchyard.Exchange;

import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.component.http.composer.HttpBindingData;
import org.switchyard.component.http.composer.HttpContextMapper;
import org.switchyard.component.http.composer.HttpMessageComposer;

/**
 * Composes/decomposes Http messages.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class CustomComposer extends HttpMessageComposer {

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpBindingData decompose(Exchange exchange, HttpBindingData target) throws Exception {
        Object content = exchange.getMessage().getContent();
        if ((content instanceof String) && (content.equals(""))) {
            exchange.getContext().setProperty(HttpContextMapper.HTTP_RESPONSE_STATUS, 404).addLabels(new String[] { EndpointLabel.HTTP.label() });
        }
        target = super.decompose(exchange, target);
        return target;
    }

}

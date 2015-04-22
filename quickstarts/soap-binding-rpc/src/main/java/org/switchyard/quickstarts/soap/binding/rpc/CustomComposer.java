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
package org.switchyard.quickstarts.soap.binding.rpc;

import org.switchyard.Exchange;

import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.component.soap.composer.SOAPBindingData;
import org.switchyard.component.soap.composer.SOAPContextMapper;
import org.switchyard.component.soap.composer.SOAPMessageComposer;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Composes/decomposes SOAP messages.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class CustomComposer extends SOAPMessageComposer {

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingData decompose(Exchange exchange, SOAPBindingData target) throws Exception {
        Element messageElement = exchange.getMessage().getContent(Element.class);
        String response = getElementValue(messageElement, "return");
        if (response.contains("500")) {
            exchange.getContext().setProperty(SOAPContextMapper.HTTP_RESPONSE_STATUS, 500).addLabels(new String[] { EndpointLabel.HTTP.label() });
        }
        target = super.decompose(exchange, target);
        return target;
    }

    private String getElementValue(Element parent, String elementName) {
        String value = null;
        NodeList nodes = parent.getElementsByTagName(elementName);
        if (nodes.getLength() > 0) {
            value = nodes.item(0).getChildNodes().item(0).getNodeValue();
        }
        return value;
    }

}

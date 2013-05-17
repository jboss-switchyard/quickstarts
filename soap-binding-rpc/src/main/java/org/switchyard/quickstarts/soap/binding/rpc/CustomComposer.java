/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.quickstarts.soap.binding.rpc;

import org.switchyard.Exchange;
import org.switchyard.Message;

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
            exchange.getContext().setProperty(SOAPContextMapper.HTTP_RESPONSE_STATUS, 500).addLabels(new String[]{EndpointLabel.HTTP.label()});
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

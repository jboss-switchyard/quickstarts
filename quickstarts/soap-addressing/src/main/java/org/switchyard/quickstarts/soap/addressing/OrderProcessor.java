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
package org.switchyard.quickstarts.soap.addressing;

import javax.inject.Named;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;

@Named("orderProcessor")
public class OrderProcessor implements Processor {

    /**
     * Creates new processor.
     */
    public OrderProcessor() {
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ServiceDomain domain = ((SwitchYardCamelContext)exchange.getContext()).getServiceDomain();
        String socketAddr = (String)domain.getProperty("soapClientPort");
        String socketAddrFault = (String)domain.getProperty("httpPort");

        Order order = exchange.getIn().getBody(Order.class);
        System.out.println("Received Order " + order.getItem() + " with quantity " + order.getQuantity() + ".");
        if (order.getItem().equals("Airbus")) {
            throw new UnknownItem("Sorry, Airbus is no longer available with us!");
        }

        SOAPHeaderElement messageId = (SOAPHeaderElement) exchange.getIn().getHeaders().get("{http://www.w3.org/2005/08/addressing}MessageID");
        if (messageId == null) {
            messageId = (SOAPHeaderElement) exchange.getIn().getHeaders().get("{http://www.w3.org/2005/08/addressing}messageid");
        }

        exchange.getOut().getHeaders().put("{http://www.w3.org/2005/08/addressing}ReplyTo", "http://localhost:" + socketAddr + "/soap-addressing/client/ResponseService");
        exchange.getOut().getHeaders().put("{http://www.w3.org/2005/08/addressing}RelatesTo", messageId.getValue());
        exchange.getOut().getHeaders().put("{http://www.w3.org/2005/08/addressing}FaultTo", "http://localhost:" + socketAddrFault + "/soap-addressing/fault/FaultService");

        exchange.getOut().setBody(order);
    }

}

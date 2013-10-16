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

package org.switchyard.quickstarts.soap.addressing;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.switchyard.common.xml.XMLHelper;
import org.w3c.dom.Element;

@Named("orderProcessor")
public class OrderProcessor implements Processor {

    /**
     * Creates new processor.
     */
    public OrderProcessor() {
    }

    @Override
    public void process(Exchange exchange) throws Exception {
    	String socketAddr = System.getProperty("org.switchyard.component.http.standalone.port");
    	if (socketAddr==null) {
    		socketAddr = "8080";
    	}
    	
        Order order = exchange.getIn().getBody(Order.class);
        System.out.println("Received Order " + order.getItem() + " with quantity " + order.getQuantity() + ".");
        Map<String, Object> headers = exchange.getIn().getHeaders();

        if (order.getItem().equals("Airbus")) {
            throw new UnknownItem("Sorry, Airbus is no longer available with us!");
        }

        SOAPHeaderElement messageId = (SOAPHeaderElement)exchange.getIn().getHeaders().get("{http://www.w3.org/2005/08/addressing}MessageID");
        if (messageId == null) {
            messageId = (SOAPHeaderElement)exchange.getIn().getHeaders().get("{http://www.w3.org/2005/08/addressing}messageid");
        }
        String replyToStr = "<wsa:ReplyTo xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
                            + "<wsa:Address>http://localhost:" + socketAddr + "/soap-addressing/client/ResponseService</wsa:Address>"
                            + "</wsa:ReplyTo>";
        Element replyTo = XMLHelper.getDocumentFromString(replyToStr).getDocumentElement();
        String faultToStr = "<wsa:FaultTo xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
                            + "<wsa:Address>http://localhost:" + socketAddr + "/soap-addressing/fault/FaultService</wsa:Address>"
                            + "</wsa:FaultTo>";
        Element faultTo = XMLHelper.getDocumentFromString(faultToStr).getDocumentElement();
        String relatesToStr = "<wsa:RelatesTo xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
                            + messageId.getValue()
                            + "</wsa:RelatesTo>";
        Element relatesTo = XMLHelper.getDocumentFromString(relatesToStr).getDocumentElement();

        exchange.getOut().getHeaders().put("{http://www.w3.org/2005/08/addressing}replyto", replyTo);
        exchange.getOut().getHeaders().put("{http://www.w3.org/2005/08/addressing}relatesto", relatesTo);
        exchange.getOut().getHeaders().put("{http://www.w3.org/2005/08/addressing}faultto", faultTo);
        exchange.getOut().setBody(order);
    }

}

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
                            + "<wsa:Address>http://localhost:8080/soap-addressing/client/ResponseService</wsa:Address>"
                            + "</wsa:ReplyTo>";
        Element replyTo = XMLHelper.getDocumentFromString(replyToStr).getDocumentElement();
        String faultToStr = "<wsa:FaultTo xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
                            + "<wsa:Address>http://localhost:8080/soap-addressing/fault/FaultService</wsa:Address>"
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

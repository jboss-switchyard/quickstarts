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

import java.util.Map;

import javax.inject.Named;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.switchyard.common.xml.XMLHelper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Named("warehouseProcessor")
public class WarehouseProcessor implements Processor {

    /**
     * Creates new processor.
     */
    public WarehouseProcessor() {
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Order order = exchange.getIn().getBody(Order.class);
        SOAPHeaderElement replyTo = (SOAPHeaderElement)exchange.getIn().getHeaders().get("{http://www.w3.org/2005/08/addressing}ReplyTo");
        SOAPHeaderElement faultTo = (SOAPHeaderElement)exchange.getIn().getHeaders().get("{http://www.w3.org/2005/08/addressing}FaultTo");
        SOAPHeaderElement relatesTo = (SOAPHeaderElement)exchange.getIn().getHeaders().get("{http://www.w3.org/2005/08/addressing}RelatesTo");
        Map<String, Object> headers = exchange.getIn().getHeaders();

        String address = null;

        if (order.getItem().equals("Guardian Angel")) {
            throw new ItemNotAvailable("Sorry, all " + order.getItem() + "s are sold out!");
        } else {
            String toStr = "<wsa:To xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
                                + ((Node)replyTo.getChildElements().next()).getFirstChild().getNodeValue()
                                + "</wsa:To>";
            Element to = XMLHelper.getDocumentFromString(toStr).getDocumentElement();

            exchange.getOut().getHeaders().put("{http://www.w3.org/2005/08/addressing}relatesto", relatesTo);
            exchange.getOut().getHeaders().put("{http://www.w3.org/2005/08/addressing}to", to);
            OrderResponse response = new OrderResponse();
            response.setReturn("Order " + order.getItem() + " with quantity " + order.getQuantity() + " accepted.");
            exchange.getOut().setBody(response);
        }

    }

}

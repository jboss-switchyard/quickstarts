/*
 * 2012 Red Hat Inc. and/or its affiliates and other contributors.
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

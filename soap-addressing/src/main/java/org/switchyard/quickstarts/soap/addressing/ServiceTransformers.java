/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-1.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.quickstarts.soap.addressing;

import java.io.StringReader;

import javax.inject.Named;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.annotations.Transformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Named("Transformers")
public class ServiceTransformers {

    @Transformer(from = "{urn:switchyard-quickstart:soap-addressing:1.0}order")
    public Order transformToOrder(Element from) {
        Order order = new Order();
        order.setItem(getElementValue(from, "item"));
        order.setQuantity(Integer.valueOf(getElementValue(from, "quantity")));
        return order;
    }

    @Transformer(from = "{urn:switchyard-quickstart:soap-addressing:1.0}orderResponse")
    public OrderResponse transformToOrderResponse(Element from) {
        OrderResponse orderAck = new OrderResponse();
        orderAck.setReturn(getElementValue(from, "return"));
        return orderAck;
    }

    @Transformer(from = "{urn:switchyard-quickstart:soap-addressing:1.0}ItemNotAvailable")
    public ItemNotAvailable transformToItemNotAvailable(Element from) {
    	String message = getElementValue(from, "faultstring");
    	return new ItemNotAvailable(message);
    }

    @Transformer(to = "{urn:switchyard-quickstart:soap-addressing:1.0}orderResponse")
    public Element transformFromOrderResponse(OrderResponse orderAck) {
        StringBuffer ackXml = new StringBuffer()
            .append("<orderResponse xmlns=\"urn:switchyard-quickstart:soap-addressing:1.0\">")
            .append("<return>")
            .append(orderAck.getReturn())
            .append("</return>")
            .append("</orderResponse>");

        return toElement(ackXml.toString());
    }

    @Transformer(to = "{urn:switchyard-quickstart:soap-addressing:1.0}order")
    public Element transformFromOrder(Order order) {
        StringBuffer ackXml = new StringBuffer()
            .append("<orders:order xmlns:orders=\"urn:switchyard-quickstart:soap-addressing:1.0\">")
            .append("<item>" + order.getItem() + "</item>")
            .append("<quantity>" + order.getQuantity() + "</quantity>")
            .append("</orders:order>");

        return toElement(ackXml.toString());
    }

    @Transformer(to = "{urn:switchyard-quickstart:soap-addressing:1.0}ItemNotAvailable")
    public Element transformFromItemNotAvailable(ItemNotAvailable fault) {
        StringBuffer ackXml = new StringBuffer()
            .append("<SOAP-ENV:Fault xmlns:ns4=\"http://www.w3.org/2003/05/soap-envelope\">")
            .append("<faultcode>SOAP-ENV:Server</faultcode>")
            .append("<faultstring>" + fault.getMessage() + "</faultstring>")
            .append("</SOAP-ENV:Fault>");

        return toElement(ackXml.toString());
    }

    /**
     * Returns the text value of a child node of parent.
     */
    private String getElementValue(Element parent, String elementName) {
        String value = null;
        NodeList nodes = parent.getElementsByTagName(elementName);
        if (nodes.getLength() > 0) {
            value = nodes.item(0).getChildNodes().item(0).getNodeValue();
        }
        return value;
    }

    private Element toElement(String xml) {
        DOMResult dom = new DOMResult();
        try {
            TransformerFactory.newInstance().newTransformer().transform(new StreamSource(new StringReader(xml)), dom);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ((Document) dom.getNode()).getDocumentElement();
    }
}

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
package org.switchyard.quickstarts.demo.multiapp.service;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.annotations.Transformer;
import org.switchyard.quickstarts.demo.multiapp.Order;
import org.switchyard.quickstarts.demo.multiapp.OrderAck;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.StringReader;

public class Transformers {

    // Element names in XML document
    private static final String ORDER_ID = "orderId";
    private static final String ITEM_ID = "itemId";
    private static final String QUANTITY = "quantity";

    /**
     * Transform from a DOM element to an Order instance.
     * <p/>
     * No need to specify the "to" type because Order is a concrete type.
     * @param from Order element.
     * @return Order instance.
     */
    @Transformer(from = "{urn:switchyard-quickstart-demo:multiapp:1.0}submitOrder")
    public Order transform(Element from) {
        return new Order()
            .setOrderId(getElementValue(from, ORDER_ID))
            .setItemId(getElementValue(from, ITEM_ID))
            .setQuantity(Integer.valueOf(getElementValue(from, QUANTITY)));
    }

    /**
     * Transform from an OrderAck to an Element.
     * <p/>
     * No need to specify the "from" type because OrderAck is a concrete type.
     * @param orderAck OrderAck.
     * @return Order response element.
     */
    @Transformer(to = "{urn:switchyard-quickstart-demo:multiapp:1.0}submitOrderResponse")
    public Element transform(OrderAck orderAck) {
        StringBuffer ackXml = new StringBuffer()
            .append("<orders:submitOrderResponse xmlns:orders=\"urn:switchyard-quickstart-demo:multiapp:1.0\">")
            .append("<orderAck>")
            .append("<orderId>" + orderAck.getOrderId() + "</orderId>")
            .append("<accepted>" + orderAck.isAccepted() + "</accepted>")
            .append("<status>" + orderAck.getStatus() + "</status>")
            .append("</orderAck>")
            .append("</orders:submitOrderResponse>");

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
            TransformerFactory.newInstance().newTransformer().transform(
                new StreamSource(new StringReader(xml)), dom);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ((Document) dom.getNode()).getDocumentElement();
    }
}

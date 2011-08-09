/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
 */

package org.switchyard.quickstarts.demos.orders;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.annotations.Transformer;
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
    @Transformer(from = "{urn:switchyard-quickstart-demo:orders:1.0}submitOrder")
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
    @Transformer(to = "{urn:switchyard-quickstart-demo:orders:1.0}submitOrderResponse")
    public Element transform(OrderAck orderAck) {
        StringBuffer ackXml = new StringBuffer()
            .append("<orders:submitOrderResponse xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">")
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

        return ((Document)dom.getNode()).getDocumentElement();
    }
}

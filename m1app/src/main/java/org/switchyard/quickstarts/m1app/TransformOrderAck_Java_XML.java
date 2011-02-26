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

package org.switchyard.quickstarts.m1app;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.transform.BaseTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TransformOrderAck_Java_XML extends BaseTransformer<OrderAck, Element> {

    // Message types being transformed
    public static final QName FROM_TYPE = 
        new QName("java:org.switchyard.quickstarts.m1app.OrderAck");
    public static final QName TO_TYPE = 
        new QName("urn:switchyard-quickstarts:m1app:1.0", "submitOrderResponse");

    public TransformOrderAck_Java_XML() {
        super(FROM_TYPE, TO_TYPE);
    }
    
    @Override
    public Element transform(OrderAck orderAck) {
        StringBuffer ackXml = new StringBuffer()
            .append("<orderAck xmlns=\"urn:switchyard-quickstarts:m1app:1.0\">")
            .append("<orderId>" + orderAck.getOrderId() + "</orderId>")
            .append("<accepted>" + orderAck.isAccepted() + "</accepted>")
            .append("<status>" + orderAck.getStatus() + "</status>")
            .append("</orderAck>");
        
        return toElement(ackXml.toString());
    }
    
    private Element toElement(String xml) {
        DOMResult dom = new DOMResult();
        try {
            TransformerFactory.newInstance().newTransformer().transform(
                    new StreamSource(new StringReader(xml)), dom);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
        return ((Document)dom.getNode()).getDocumentElement();
    }
}

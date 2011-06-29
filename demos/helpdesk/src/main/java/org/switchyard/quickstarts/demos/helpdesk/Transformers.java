/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.quickstarts.demos.helpdesk;

import java.io.StringReader;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.annotations.Transformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class Transformers {

    @Transformer(from = "{urn:switchyard-quickstart-demo:helpdesk:1.0}openTicket")
    public Ticket transform(Element from) {
       return new Ticket().setId(getElementValue(from, "id"));
    }

    @Transformer(to = "{urn:switchyard-quickstart-demo:helpdesk:1.0}openTicketResponse")
    public Element transformToElement(TicketAck ticketAck) {
        StringBuilder ackXml = new StringBuilder()
            .append("<helpdesk:openTicketResponse xmlns:helpdesk=\"urn:switchyard-quickstart-demo:helpdesk:1.0\">")
            .append(    "<ticketAck>")
            .append(        "<id>" + ticketAck.getId() + "</id>")
            .append(        "<received>" + ticketAck.isReceived() + "</received>")
            .append(    "</ticketAck>")
            .append("</helpdesk:openTicketResponse>");
        return toElement(ackXml.toString());
    }

    @Transformer
    public Ticket transformToTicket(TicketAck ticketAck) {
        return new Ticket().setId(ticketAck.getId());
    }

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
        return ((Document)dom.getNode()).getDocumentElement();
    }

}

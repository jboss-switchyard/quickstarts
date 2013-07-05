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
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class Transformers {

    @Transformer(from = "{urn:switchyard-quickstart-demo:helpdesk:1.0}openTicket")
    public Ticket transform(Element from) {
        Ticket ticket = new Ticket();
        ticket.setId(getElementValue(from, "id"));
        return ticket;
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
        Ticket ticket = new Ticket();
        ticket.setId(ticketAck.getId());
        return ticket;
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

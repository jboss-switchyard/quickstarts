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
package org.switchyard.quickstarts.demo.policy.security.cert;

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

    @Transformer(from = "{urn:switchyard-quickstart-demo:policy-security-cert:0.1.0}doWork")
    public Work transform(Element from) {
        Work work = new Work();
        work.setCommand(getElementValue(from, "command"));
        return work;
    }

    @Transformer(to = "{urn:switchyard-quickstart-demo:policy-security-cert:0.1.0}doWorkResponse")
    public Element transformToElement(WorkAck workAck) {
        StringBuilder ackXml = new StringBuilder()
            .append("<policy-security-cert:doWorkResponse xmlns:policy-security-cert=\"urn:switchyard-quickstart-demo:policy-security-cert:0.1.0\">")
            .append("<workAck>")
            .append("<command>" + workAck.getCommand() + "</command>")
            .append("<received>" + workAck.isReceived() + "</received>")
            .append("</workAck>")
            .append("</policy-security-cert:doWorkResponse>");
        return toElement(ackXml.toString());
    }

    @Transformer
    public Work transformToWork(WorkAck workAck) {
        Work work = new Work();
        work.setCommand(workAck.getCommand());
        return work;
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
        return ((Document) dom.getNode()).getDocumentElement();
    }

}

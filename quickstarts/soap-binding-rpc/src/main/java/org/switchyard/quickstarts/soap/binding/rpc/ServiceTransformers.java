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
package org.switchyard.quickstarts.soap.binding.rpc;

import java.io.StringReader;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.annotations.Transformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ServiceTransformers {

    private static String SOAP_RESPONSE_TEMPLATE =
        "<ns2:sayHelloResponse xmlns:ns2=\"urn:switchyard-quickstart:soap-binding-rpc:1.0\">"
            + "    <return>%s</return>"
            + "</ns2:sayHelloResponse>";

    private static String SOAP_TEMPLATE_EXTERNAL =
        "<ns2:sayHello xmlns:ns2=\"urn:switchyard-quickstart:external:1.0\">"
            + "    <toWhom>%s</toWhom>"
            + "    <language>%s</language>"
            + "    <day>%s</day>"
            + "</ns2:sayHello>";

    private static String SOAP_RESPONSE_TEMPLATE_EXTERNAL =
        "<ns2:sayHelloResponse xmlns:ns2=\"urn:switchyard-quickstart:external:1.0\">"
            + "    <return>%s</return>"
            + "</ns2:sayHelloResponse>";

    // Taken care of by JAXB annotations
    /*@Transformer(to = "{urn:switchyard-quickstart:soap-binding-rpc:1.0}sayHello")
    public Element transform(SayHello wrapper) throws Exception {
        return toElement(String.format(SOAP_TEMPLATE, wrapper.getToWhom(), wrapper.getLanguage()));
    }*/

    @Transformer(to = "{urn:switchyard-quickstart:external:1.0}sayHello")
    public Element transformExternal(SayHello wrapper) throws Exception {
        return toElement(String.format(SOAP_TEMPLATE_EXTERNAL, wrapper.getToWhom(), wrapper.getLanguage(), "Sunday"));
    }

    @Transformer(from = "{urn:switchyard-quickstart:soap-binding-rpc:1.0}sayHello")
    public SayHello transform(Element soap) throws Exception {
        SayHello wrapper = new SayHello();
        wrapper.setToWhom(getElementValue(soap, "toWhom"));
        wrapper.setLanguage(getElementValue(soap, "language"));
        return wrapper;
    }

    @Transformer(from = "{urn:switchyard-quickstart:external:1.0}sayHello")
    public SayHelloExternal transformExternalRequest(Element soap) throws Exception {
        SayHelloExternal wrapper = new SayHelloExternal();
        wrapper.setToWhom(getElementValue(soap, "toWhom"));
        wrapper.setLanguage(getElementValue(soap, "language"));
        wrapper.setDay(getElementValue(soap, "day"));
        return wrapper;
    }

    @Transformer(to = "{urn:switchyard-quickstart:soap-binding-rpc:1.0}sayHelloResponse")
    public Element transformInternalResponse(String response) throws Exception {
        return toElement(String.format(SOAP_RESPONSE_TEMPLATE, response));
    }

    @Transformer(to = "{urn:switchyard-quickstart:external:1.0}sayHelloResponse")
    public Element transformExternalResponse(String response) throws Exception {
        return toElement(String.format(SOAP_RESPONSE_TEMPLATE_EXTERNAL, response));
    }

    @Transformer(from = "{urn:switchyard-quickstart:external:1.0}sayHelloResponse")
    public String transformExternalResponse(Element soap) throws Exception {
        return getElementValue(soap, "return");
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

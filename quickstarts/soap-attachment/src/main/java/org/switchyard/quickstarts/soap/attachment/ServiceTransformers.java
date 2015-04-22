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
package org.switchyard.quickstarts.soap.attachment;

import java.io.StringReader;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.annotations.Transformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ServiceTransformers {

    private static String SOAP_RESPONSE_TEMPLATE =
        "<ns2:echoImageResponse xmlns:ns2=\"urn:switchyard-quickstart:soap-attachment:1.0\">"
            + "cid:%s"
            + "</ns2:echoImageResponse>";

    private static String SOAP_EXTERNAL_TEMPLATE =
        "<ns2:echoImage xmlns:ns2=\"urn:switchyard-quickstart:soap-attachment-external:1.0\">"
            + "cid:%s"
            + "</ns2:echoImage>";

    private static String SOAP_EXTERNAL_RESPONSE_TEMPLATE =
        "<ns2:echoImageResponse xmlns:ns2=\"urn:switchyard-quickstart:soap-attachment-external:1.0\">"
            + "cid:%s"
            + "</ns2:echoImageResponse>";

    @Transformer(to = "{urn:switchyard-quickstart:soap-attachment:1.0}echoImageResponse")
    public Element transformInternal(String fileName) throws Exception {
        return toElement(String.format(SOAP_RESPONSE_TEMPLATE, fileName));
    }

    @Transformer(from = "{urn:switchyard-quickstart:soap-attachment:1.0}echoImage")
    public String transformInternal(Element soap) throws Exception {
        String fileName = soap.getTextContent();
        if (fileName.startsWith("cid:")) {
            fileName = fileName.substring(4);
        }
        return fileName;
    }

    @Transformer(to = "{urn:switchyard-quickstart:soap-attachment-external:1.0}echoImage")
    public Element transformInternaltoExternal(String fileName) throws Exception {
        return toElement(String.format(SOAP_EXTERNAL_TEMPLATE, fileName));
    }

    @Transformer(to = "{urn:switchyard-quickstart:soap-attachment-external:1.0}echoImageResponse")
    public Element transformExternal(String fileName) throws Exception {
        return toElement(String.format(SOAP_EXTERNAL_RESPONSE_TEMPLATE, fileName));
    }

    @Transformer(from = "{urn:switchyard-quickstart:soap-attachment-external:1.0}echoImage")
    public String transformExternal(Element soap) throws Exception {
        String fileName = soap.getTextContent();
        if (fileName.startsWith("cid:")) {
            fileName = fileName.substring(4);
        }
        return fileName;
    }

    @Transformer(from = "{urn:switchyard-quickstart:soap-attachment-external:1.0}echoImageResponse")
    public String transformExternaltoInternal(Element soap) throws Exception {
        String fileName = soap.getTextContent();
        if (fileName.startsWith("cid:")) {
            fileName = fileName.substring(4);
        }
        return fileName;
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

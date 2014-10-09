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
package org.switchyard.quickstarts.soap.mtom;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.StringReader;

import javax.imageio.ImageIO;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.annotations.Transformer;
import org.switchyard.common.codec.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ServiceTransformers {

    private static String SOAP_RESPONSE_TEMPLATE =
        "<imageResponse xmlns=\"urn:switchyard-quickstart:soap-mtom:1.0\">"
            + "<xop:Include xmlns:xop=\"http://www.w3.org/2004/08/xop/include\" href=\"cid:%s\"/>"
            + "</imageResponse>";

    private static String SOAP_EXTERNAL_REQUEST_TEMPLATE =
        "<ns2:image xmlns:ns2=\"urn:switchyard-quickstart:soap-mtom-external:1.0\">"
            + "<xop:Include xmlns:xop=\"http://www.w3.org/2004/08/xop/include\" href=\"cid:%s\"/>"
            + "</ns2:image>";

    private static String SOAP_EXTERNAL_RESPONSE_TEMPLATE =
        "<ns2:imageResponse xmlns:ns2=\"urn:switchyard-quickstart:soap-mtom-external:1.0\">"
            + "<xop:Include xmlns:xop=\"http://www.w3.org/2004/08/xop/include\" href=\"cid:%s\"/>"
            + "</ns2:imageResponse>";

    @Transformer(from = "{urn:switchyard-quickstart:soap-mtom:1.0}image")
    public Image transformInternalRequest(Element soap) throws Exception {
        String imageString = soap.getTextContent();
        return ImageIO.read(new ByteArrayInputStream(Base64.decode(imageString)));
    }

    @Transformer(to = "{urn:switchyard-quickstart:soap-mtom-external:1.0}image")
    public Element transformInternaltoExternalRequest(String imageName) throws Exception {
        return toElement(String.format(SOAP_EXTERNAL_REQUEST_TEMPLATE, imageName));
    }

    @Transformer(from = "{urn:switchyard-quickstart:soap-mtom-external:1.0}image")
    public Image transformExternalRequest(Element soap) throws Exception {
        String imageString = soap.getTextContent();
        return ImageIO.read(new ByteArrayInputStream(Base64.decode(imageString)));
    }

    @Transformer(to = "{urn:switchyard-quickstart:soap-mtom-external:1.0}imageResponse")
    public Element transformExternaltoInternalResponse(String imageName) throws Exception {
        return toElement(String.format(SOAP_EXTERNAL_RESPONSE_TEMPLATE, imageName));
    }

    @Transformer(from = "{urn:switchyard-quickstart:soap-mtom-external:1.0}imageResponse")
    public Image transformExternalResponse(Element soap) throws Exception {
        String imageString = soap.getTextContent();
        return ImageIO.read(new ByteArrayInputStream(Base64.decode(imageString)));
    }

    @Transformer(to = "{urn:switchyard-quickstart:soap-mtom:1.0}imageResponse")
    public Element transformInternalResponse(String imageName) throws Exception {
        return toElement(String.format(SOAP_RESPONSE_TEMPLATE, imageName));
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

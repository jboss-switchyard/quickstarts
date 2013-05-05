/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.quickstarts.soap.attachment;

import java.io.StringReader;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.annotations.Transformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ServiceTransformers {

    private static String SOAP_TEMPLATE =
              "<ns2:echoImage xmlns:ns2=\"urn:switchyard-quickstart:soap-attachment:1.0\">"
            + "cid:%s"
            + "</ns2:echoImage>";

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

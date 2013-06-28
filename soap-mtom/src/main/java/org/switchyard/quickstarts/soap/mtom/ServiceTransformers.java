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

package org.switchyard.quickstarts.soap.mtom;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.annotations.Transformer;
import org.switchyard.common.codec.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

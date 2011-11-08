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
package org.switchyard.quickstarts.camel.soap.proxy;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.annotations.Transformer;
import org.switchyard.exception.SwitchYardException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ProxyTransforms {


    @Transformer(from = "{urn:switchyard-quickstart:camel-soap-proxy:1.0}reverse")
    public String transformReverseToString(Element reverse) {
        return toString(reverse);
    }

    @Transformer(to = "{urn:switchyard-quickstart:camel-soap-proxy:1.0}reverseResponse")
    public Element transformReverseResponseToElement(String reverseResponse) {
        return toElement(reverseResponse);
    }

    private String toString(Element elem) {
        try {
            javax.xml.transform.Transformer t = TransformerFactory.newInstance().newTransformer();
            StringWriter sw = new StringWriter();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(elem), new StreamResult(sw));
            String xml = sw.toString();
            return xml;
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }

    private Element toElement(String xml) {
        try {
            DOMResult dom = new DOMResult();
            javax.xml.transform.Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform(new StreamSource(new StringReader(xml)), dom);
            return ((Document)dom.getNode()).getDocumentElement();
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }

}

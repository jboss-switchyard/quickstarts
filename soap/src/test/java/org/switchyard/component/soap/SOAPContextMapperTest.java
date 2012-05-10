/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.soap;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPBinding;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.Context;
import org.switchyard.component.soap.composer.SOAPContextMapper;
import org.switchyard.component.soap.composer.SOAPHeadersType;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.config.Configuration;
import org.switchyard.config.ConfigurationPuller;
import org.switchyard.internal.DefaultContext;
import org.w3c.dom.Element;

/**
 * SOAPContextMapperTest.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public class SOAPContextMapperTest {

    private static final QName FIRST_NAME = new QName("urn:names:1.0", "first");
    private static final QName LAST_NAME = new QName("urn:names:1.0", "last");

    private SOAPMessage newSourceMessage() throws Exception {
        SOAPMessage source = SOAPUtil.createMessage(SOAPBinding.SOAP11HTTP_BINDING);
        source.getSOAPHeader().addChildElement(FIRST_NAME).setValue("John");
        source.getSOAPHeader().addChildElement(LAST_NAME).setValue("Doe");
        return source;
    }

    private SOAPMessage newTargetMessage() throws Exception {
        SOAPMessage target = SOAPUtil.createMessage(SOAPBinding.SOAP11HTTP_BINDING);
        return target;
    }

    private Object getPropertyValue(Context context, QName qname) {
        return context.getPropertyValue(qname.toString());
    }

    private Element getChildElement(SOAPMessage message, QName qname) throws Exception {
        return (Element)message.getSOAPHeader().getChildElements(qname).next();
    }

    private Configuration newConfiguration(String xml) throws Exception {
        return new ConfigurationPuller().pull(new StringReader(xml));
    }

    private String toString(Element element) throws Exception {
        return new ConfigurationPuller().pull(element).toString();
    }

    @Test
    public void testDefaultContextMapping() throws Exception {
        testStringContextMapping(null);
    }

    @Test
    public void testValueContextMapping() throws Exception {
        testStringContextMapping(SOAPHeadersType.VALUE);
    }

    private void testStringContextMapping(SOAPHeadersType soapHeadersType) throws Exception {
        SOAPContextMapper mapper = new SOAPContextMapper();
        mapper.setSOAPHeadersType(soapHeadersType);
        Context context = new DefaultContext();
        // test mapFrom
        SOAPMessage source = newSourceMessage();
        mapper.mapFrom(source, context);
        Assert.assertEquals("John", getPropertyValue(context, FIRST_NAME));
        Assert.assertEquals("Doe", getPropertyValue(context, LAST_NAME));
        // test mapTo
        SOAPMessage target = newTargetMessage();
        mapper.mapTo(context, target);
        Assert.assertEquals("John", getChildElement(target, FIRST_NAME).getTextContent());
        Assert.assertEquals("Doe", getChildElement(target, LAST_NAME).getTextContent());
    }

    @Test
    public void testXmlContextMapping() throws Exception {
        SOAPContextMapper mapper = new SOAPContextMapper();
        mapper.setSOAPHeadersType(SOAPHeadersType.XML);
        Context context = new DefaultContext();
        // test mapFrom
        SOAPMessage source = newSourceMessage();
        mapper.mapFrom(source, context);
        Assert.assertEquals("<first xmlns=\"urn:names:1.0\">John</first>", getPropertyValue(context, FIRST_NAME));
        Assert.assertEquals("<last xmlns=\"urn:names:1.0\">Doe</last>", getPropertyValue(context, LAST_NAME));
        // test mapTo
        SOAPMessage target = newTargetMessage();
        mapper.mapTo(context, target);
        Assert.assertEquals("John", getChildElement(target, FIRST_NAME).getTextContent());
        Assert.assertEquals("Doe", getChildElement(target, LAST_NAME).getTextContent());
    }

    @Test
    public void testConfigContextMapping() throws Exception {
        SOAPContextMapper mapper = new SOAPContextMapper();
        mapper.setSOAPHeadersType(SOAPHeadersType.CONFIG);
        Context context = new DefaultContext();
        // test mapFrom
        SOAPMessage source = newSourceMessage();
        mapper.mapFrom(source, context);
        Assert.assertEquals(newConfiguration("<first xmlns=\"urn:names:1.0\">John</first>"), getPropertyValue(context, FIRST_NAME));
        Assert.assertEquals(newConfiguration("<last xmlns=\"urn:names:1.0\">Doe</last>"), getPropertyValue(context, LAST_NAME));
        // test mapTo
        SOAPMessage target = newTargetMessage();
        mapper.mapTo(context, target);
        Assert.assertEquals("John", getChildElement(target, FIRST_NAME).getTextContent());
        Assert.assertEquals("Doe", getChildElement(target, LAST_NAME).getTextContent());
    }

    @Test
    public void testDomContextMapping() throws Exception {
        SOAPContextMapper mapper = new SOAPContextMapper();
        mapper.setSOAPHeadersType(SOAPHeadersType.DOM);
        Context context = new DefaultContext();
        // test mapFrom
        SOAPMessage source = newSourceMessage();
        mapper.mapFrom(source, context);
        Assert.assertEquals("<first xmlns=\"urn:names:1.0\">John</first>", toString((Element)getPropertyValue(context, FIRST_NAME)));
        Assert.assertEquals("<last xmlns=\"urn:names:1.0\">Doe</last>", toString((Element)getPropertyValue(context, LAST_NAME)));
        // test mapTo
        SOAPMessage target = newTargetMessage();
        mapper.mapTo(context, target);
        Assert.assertEquals("John", getChildElement(target, FIRST_NAME).getTextContent());
        Assert.assertEquals("Doe", getChildElement(target, LAST_NAME).getTextContent());
    }

}

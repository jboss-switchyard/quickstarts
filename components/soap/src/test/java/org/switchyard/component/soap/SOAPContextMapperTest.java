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
package org.switchyard.component.soap;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPBinding;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.Context;
import org.switchyard.Scope;
import org.switchyard.component.soap.composer.SOAPBindingData;
import org.switchyard.component.soap.composer.SOAPContextMapper;
import org.switchyard.component.soap.composer.SOAPHeadersType;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.config.Configuration;
import org.switchyard.config.ConfigurationPuller;
import org.switchyard.internal.CompositeContext;
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

    private Context newSourceContext() {
        CompositeContext source = newContext();
        source.setProperty(FIRST_NAME.toString(), "John");
        source.setProperty(LAST_NAME.toString(), "Doe");
        return source;
    }

    private SOAPMessage newTargetMessage() throws Exception {
        SOAPMessage target = SOAPUtil.createMessage(SOAPBinding.SOAP11HTTP_BINDING);
        return target;
    }

    private CompositeContext newContext() {
        CompositeContext source = new CompositeContext();
        source.setContext(Scope.MESSAGE, new DefaultContext(Scope.MESSAGE));
        source.setContext(Scope.EXCHANGE, new DefaultContext());
        return source;
    }

    private Object getPropertyValue(Context context, QName qname) {
        Object o = context.getProperty(qname.toString(), Scope.MESSAGE).getValue();
        return o;
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
        // test mapFrom
        SOAPMessage sourceMessage = newSourceMessage();
        Context targetContext = newContext();
        mapper.mapFrom(new SOAPBindingData(sourceMessage), targetContext);
        Assert.assertEquals("John", getPropertyValue(targetContext, FIRST_NAME));
        Assert.assertEquals("Doe", getPropertyValue(targetContext, LAST_NAME));
        // test mapTo
        Context sourceContext = newSourceContext();
        SOAPMessage targetMessage = newTargetMessage();
        mapper.mapTo(sourceContext, new SOAPBindingData(targetMessage));
        Assert.assertEquals("John", getChildElement(targetMessage, FIRST_NAME).getTextContent());
        Assert.assertEquals("Doe", getChildElement(targetMessage, LAST_NAME).getTextContent());
    }

    @Test
    public void testXmlContextMapping() throws Exception {
        SOAPContextMapper mapper = new SOAPContextMapper();
        mapper.setSOAPHeadersType(SOAPHeadersType.XML);
        // test mapFrom
        SOAPMessage sourceMessage = newSourceMessage();
        Context targetContext = newContext();
        mapper.mapFrom(new SOAPBindingData(sourceMessage), targetContext);
        Assert.assertEquals("<first xmlns=\"urn:names:1.0\">John</first>", getPropertyValue(targetContext, FIRST_NAME));
        Assert.assertEquals("<last xmlns=\"urn:names:1.0\">Doe</last>", getPropertyValue(targetContext, LAST_NAME));
        // test mapTo
        Context sourceContext = newSourceContext();
        SOAPMessage targetMessage = newTargetMessage();
        mapper.mapTo(sourceContext, new SOAPBindingData(targetMessage));
        Assert.assertEquals("John", getChildElement(targetMessage, FIRST_NAME).getTextContent());
        Assert.assertEquals("Doe", getChildElement(targetMessage, LAST_NAME).getTextContent());
    }

    @Test
    public void testConfigContextMapping() throws Exception {
        SOAPContextMapper mapper = new SOAPContextMapper();
        mapper.setSOAPHeadersType(SOAPHeadersType.CONFIG);
        // test mapFrom
        SOAPMessage sourceMessage = newSourceMessage();
        Context targetContext = newContext();
        mapper.mapFrom(new SOAPBindingData(sourceMessage), targetContext);
        Assert.assertEquals(newConfiguration("<first xmlns=\"urn:names:1.0\">John</first>"), getPropertyValue(targetContext, FIRST_NAME));
        Assert.assertEquals(newConfiguration("<last xmlns=\"urn:names:1.0\">Doe</last>"), getPropertyValue(targetContext, LAST_NAME));
        // test mapTo
        Context sourceContext = newSourceContext();
        SOAPMessage targetMessage = newTargetMessage();
        mapper.mapTo(sourceContext, new SOAPBindingData(targetMessage));
        Assert.assertEquals("John", getChildElement(targetMessage, FIRST_NAME).getTextContent());
        Assert.assertEquals("Doe", getChildElement(targetMessage, LAST_NAME).getTextContent());
    }

    @Test
    public void testDomContextMapping() throws Exception {
        SOAPContextMapper mapper = new SOAPContextMapper();
        mapper.setSOAPHeadersType(SOAPHeadersType.DOM);
        // test mapFrom
        SOAPMessage sourceMessage = newSourceMessage();
        Context targetContext = newContext();
        mapper.mapFrom(new SOAPBindingData(sourceMessage), targetContext);
        Assert.assertEquals("<first xmlns=\"urn:names:1.0\">John</first>", toString((Element)getPropertyValue(targetContext, FIRST_NAME)));
        Assert.assertEquals("<last xmlns=\"urn:names:1.0\">Doe</last>", toString((Element)getPropertyValue(targetContext, LAST_NAME)));
        // test mapTo
        Context sourceContext = newSourceContext();
        SOAPMessage targetMesssage = newTargetMessage();
        mapper.mapTo(sourceContext, new SOAPBindingData(targetMesssage));
        Assert.assertEquals("John", getChildElement(targetMesssage, FIRST_NAME).getTextContent());
        Assert.assertEquals("Doe", getChildElement(targetMesssage, LAST_NAME).getTextContent());
    }

}

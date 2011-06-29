/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.transform.jaxb.internal;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.transform.AbstractTransformerTestCase;
import org.switchyard.transform.Transformer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class JAXBTransformerTest extends AbstractTransformerTestCase {

    @Test
    public void test_createFromServiceInterface() throws IOException, SAXException {
        List<Transformer<?,?>> transformers = JAXBTransformerFactory.newTransformers(OrderService.class);

        Assert.assertEquals(2, transformers.size());
        JAXBUnmarshalTransformer unmarshalTransformer = (JAXBUnmarshalTransformer) transformers.get(0);
        JAXBMarshalTransformer marshalTransformer = (JAXBMarshalTransformer) transformers.get(1);

        Assert.assertEquals("purchaseOrder", unmarshalTransformer.getFrom().toString());
        Assert.assertEquals(JavaService.toMessageType(POType.class), unmarshalTransformer.getTo());
        Assert.assertEquals(JavaService.toMessageType(POType.class), marshalTransformer.getFrom());
        Assert.assertEquals("purchaseOrder", marshalTransformer.getTo().toString());

        DefaultMessage message = new DefaultMessage();

        message.setContent(new InputSource(new StringReader(PO_XML)));

        // Transform XML to Java POType and back to XML...
        unmarshalTransformer.transform(message);
        marshalTransformer.transform(message);

        // Check the round trip...
        String resultXML = message.getContent(String.class);
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.compareXML(PO_XML, resultXML);
    }

    @Test
    public void test_configRead() throws IOException {
        Transformer unmarshalingTransformer = getTransformer("switchyard-config-01.xml");
        Assert.assertEquals("A", unmarshalingTransformer.getFrom().toString());
        Assert.assertEquals("java:org.switchyard.transform.jaxb.internal.POType", unmarshalingTransformer.getTo().toString());

        Transformer marshalingTransformer = getTransformer("switchyard-config-02.xml");
        Assert.assertEquals("java:org.switchyard.transform.jaxb.internal.POType", marshalingTransformer.getFrom().toString());
        Assert.assertEquals("A", marshalingTransformer.getTo().toString());
    }

    @Test
    public void test_createMissingFactoryMethodMessage() throws IOException, SAXException {
        String message = JAXBTransformerFactory.createMissingFactoryMethodMessage(
                USAddress.class,
                ObjectFactory.class);

        Assert.assertTrue(message.startsWith("JAXB Type 'org.switchyard.transform.jaxb.internal.USAddress' does not have a JAXBElement factory method defined in org.switchyard.transform.jaxb.internal.ObjectFactory.  The supported JAXBElement factory methods are for types:"));
    }

    private static final String PO_XML = "<?xml version=\"1.0\"?>\n" +
            "<purchaseOrder orderDate=\"1999-10-20\">\n" +
            "    <shipTo country=\"US\">\n" +
            "        <name>Alice Smith</name>\n" +
            "        <street>123 Maple Street</street>\n" +
            "        <city>Cambridge</city>\n" +
            "        <state>MA</state>\n" +
            "        <zip>12345</zip>\n" +
            "    </shipTo>\n" +
            "    <billTo country=\"US\">\n" +
            "        <name>Robert Smith</name>\n" +
            "        <street>8 Oak Avenue</street>\n" +
            "        <city>Cambridge</city>\n" +
            "        <state>MA</state>\n" +
            "        <zip>12345</zip>\n" +
            "    </billTo>\n" +
            "    <items>\n" +
            "        <item partNum=\"242-NO\" >\n" +
            "            <productName>Nosferatu - Special Edition (1929)</productName>\n" +
            "            <quantity>5</quantity>\n" +
            "            <USPrice>19.99</USPrice>\n" +
            "        </item>\n" +
            "        <item partNum=\"242-MU\" >\n" +
            "            <productName>The Mummy (1959)</productName>\n" +
            "            <quantity>3</quantity>\n" +
            "            <USPrice>19.98</USPrice>\n" +
            "        </item>\n" +
            "        <item partNum=\"242-GZ\" >\n" +
            "            <productName>Godzilla and Mothra: Battle for Earth/Godzilla vs. King Ghidora</productName>\n" +
            "            <quantity>3</quantity>\n" +
            "            <USPrice>27.95</USPrice>\n" +
            "        </item>\n" +
            "    </items>\n" +
            "</purchaseOrder>";
}

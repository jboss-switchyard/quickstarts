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
package org.switchyard.config;

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;

/**
 * ConfigurationTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ConfigurationTests {

    private static final String FROM_XML = "/org/switchyard/config/ConfigurationTests-From.xml";
    private static final String TO_XML = "/org/switchyard/config/ConfigurationTests-To.xml";
    private static final String FROM_OVERRIDES_TO_XML = "/org/switchyard/config/ConfigurationTests-FromOverridesTo.xml";
    private static final String FROM_DOESNT_OVERRIDE_TO_XML = "/org/switchyard/config/ConfigurationTests-FromDoesntOverrideTo.xml";
    private static final String NAMESPACES_XML = "/org/switchyard/config/ConfigurationTests-Namespaces.xml";
    private static final String ORDER_CHILDREN_UNORDERED_XML = "/org/switchyard/config/ConfigurationTests-OrderChildren-Unordered.xml";
    private static final String ORDER_CHILDREN_ORDERED_XML = "/org/switchyard/config/ConfigurationTests-OrderChildren-Ordered.xml";
    private static final String CDATA_VALUE_XML = "/org/switchyard/config/ConfigurationTests-CdataValue.xml";

    private ConfigurationPuller _cfg_puller;
    private StringPuller _str_puller;

    @Before
    public void before() {
        _cfg_puller = new ConfigurationPuller();
        _str_puller = new StringPuller();
    }

    @After
    public void after() {
        _cfg_puller = null;
        _str_puller = null;
    }

    @Test
    public void testCreateEmptyConfig() throws Exception {
        String name = "root";
        Configuration config = _cfg_puller.pull(new QName(name));
        Assert.assertEquals(name, config.getName());
        Assert.assertEquals(new QName(name), config.getQName());
    }

    @Test
    public void testMerge() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff;
        String from_overrides_to_xml = _str_puller.pull(FROM_OVERRIDES_TO_XML, getClass());
        diff = XMLUnit.compareXML(from_overrides_to_xml, merge(true));
        Assert.assertTrue(diff.toString(), diff.identical());
        String from_doesnt_override_to_xml = _str_puller.pull(FROM_DOESNT_OVERRIDE_TO_XML, getClass());
        diff = XMLUnit.compareXML(from_doesnt_override_to_xml, merge(false));
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    private String merge(boolean fromOverridesTo) throws Exception {
        Configuration from_config = _cfg_puller.pull(FROM_XML, getClass());
        Configuration to_config = _cfg_puller.pull(TO_XML, getClass());
        Configuration merged_config = Configurations.merge(from_config, to_config, fromOverridesTo).setChildrenOrder("my", "his", "mythology").orderChildren();
        return merged_config.toString();
    }

    @Test
    public void testParenthood() throws Exception {
        Configuration parent = _cfg_puller.pull(NAMESPACES_XML, getClass());
        Assert.assertFalse(parent.hasParent());
        Assert.assertNull(parent.getParent());
        Configuration child = parent.getFirstChild("two");
        Assert.assertTrue(child.hasParent());
        Assert.assertNotNull(child.getParent());
        Assert.assertEquals("one", child.getParent().getName());
        Assert.assertSame(child.getParent(), child.getParent());
    }

    @Test
    public void testQNames() throws Exception {
        QName expected = new QName("http://foo.org", "bar", "foo");
        QName actual = _cfg_puller.pull(expected).getQName();
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(expected.getNamespaceURI(), actual.getNamespaceURI());
        Assert.assertEquals(expected.getLocalPart(), actual.getLocalPart());
        Assert.assertEquals(expected.getPrefix(), actual.getPrefix());
    }

    @Test
    public void testNamespaceCollections() throws Exception {
        Configuration config = _cfg_puller.pull(NAMESPACES_XML, getClass());
        Set<String> n_set = config.getNamespaces();
        Assert.assertEquals(5, n_set.size());
        Map<String,String> np_map = config.getNamespacePrefixMap();
        Assert.assertEquals(5, np_map.size());
        Map<String,String> pn_map = config.getPrefixNamespaceMap();
        Assert.assertEquals(5, pn_map.size());
    }

    @Test
    public void testNamespacesValues() throws Exception {
        Configuration config_one = _cfg_puller.pull(NAMESPACES_XML, getClass());
        Assert.assertEquals("urn:test", config_one.getAttribute("targetNamespace"));
        Assert.assertEquals("http://a.org/a.xsd", config_one.getQName().getNamespaceURI());
        Assert.assertEquals("bar", config_one.getAttribute("foo"));
        Assert.assertEquals("stuff", config_one.getFirstChild("two").getValue());
        Assert.assertEquals("stuff", config_one.getFirstChild(new QName("http://b.org/b.xsd", "two")).getValue());
        Assert.assertEquals("whiz", config_one.getFirstChild("two").getAttribute("baz"));
        Assert.assertEquals("girl", config_one.getFirstChild(new QName("http://b.org/b.xsd", "two")).getAttribute(new QName("http://c.org/c.xsd", "boy")));
        Configuration config_three = config_one.getChildren().get(1);
        Assert.assertEquals("metal", config_three.getValue());
        Assert.assertEquals("woman", config_three.getAttribute(new QName("http://b.org/b.xsd", "man")));
        Assert.assertEquals("robot", config_three.getAttribute("toy"));
        Configuration config_four = config_one.getChildren().get(2);
        QName nuts = config_four.getAttributeAsQName(new QName("http://b.org/b.xsd", "nuts"));
        Assert.assertEquals("http://b.org/b.xsd", nuts.getNamespaceURI());
        Assert.assertEquals("bolts", nuts.getLocalPart());
        Assert.assertEquals("b", nuts.getPrefix());
        QName pins = config_four.getAttributeAsQName("pins");
        Assert.assertEquals("http://c.org/c.xsd", pins.getNamespaceURI());
        Assert.assertEquals("needles", pins.getLocalPart());
        Assert.assertEquals("c", pins.getPrefix());
        QName pipes = config_four.getAttributeAsQName("pipes");
        Assert.assertEquals("", pipes.getNamespaceURI());
        Assert.assertEquals("rusted", pipes.getLocalPart());
        Assert.assertEquals("", pipes.getPrefix());
    }

    @Test
    public void testAttributeAsQName() throws Exception {
        Configuration config = _cfg_puller.pull(NAMESPACES_XML, getClass());
        config.setAttributeAsQName("testB", new QName("http://b.org/b.xsd", "B", "b"));
        Assert.assertEquals("b:B", config.getAttribute("testB"));
        QName testB = config.getAttributeAsQName("testB");
        Assert.assertEquals("http://b.org/b.xsd", testB.getNamespaceURI());
        Assert.assertEquals("B", testB.getLocalPart());
        Assert.assertEquals("b", testB.getPrefix());
        config.setAttribute("testB", "{http://b.org/b.xsd}B");
        Assert.assertEquals("{http://b.org/b.xsd}B", config.getAttribute("testB"));
        testB = config.getAttributeAsQName("testB");
        Assert.assertEquals("http://b.org/b.xsd", testB.getNamespaceURI());
        Assert.assertEquals("B", testB.getLocalPart());
        Assert.assertEquals("b", testB.getPrefix());
        QName qnameC = new QName("http://c.org/c.xsd", "testC", "c");
        config.setAttributeAsQName(qnameC, new QName("", "C", "c"));
        Assert.assertEquals("c:C", config.getAttribute(qnameC));
        QName testC = config.getAttributeAsQName(qnameC);
        Assert.assertEquals("http://c.org/c.xsd", testC.getNamespaceURI());
        Assert.assertEquals("C", testC.getLocalPart());
        Assert.assertEquals("c", testC.getPrefix());
        config.setAttributeAsQName("testZ", new QName("http://z.org/z.xsd", "Z", ""));
        Assert.assertEquals("{http://z.org/z.xsd}Z", config.getAttribute("testZ"));
        QName testZ = config.getAttributeAsQName("testZ");
        Assert.assertEquals("http://z.org/z.xsd", testZ.getNamespaceURI());
        Assert.assertEquals("Z", testZ.getLocalPart());
        Assert.assertEquals("", testZ.getPrefix());
    }

    @Test
    public void testOrderChildren() throws Exception {
        Configuration root = _cfg_puller.pull(ORDER_CHILDREN_UNORDERED_XML, getClass());
        root.setChildrenOrder("one", "two", "three", "four");
        Configuration one = root.getFirstChild("one");
        one.setChildrenOrder("a", "b", "c");
        Configuration three = root.getFirstChild("three");
        three.setChildrenOrder("c", "b", "a");
        String actual_xml = root.orderChildren().toString();
        String expected_xml = _str_puller.pull(ORDER_CHILDREN_ORDERED_XML, getClass());
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(expected_xml, actual_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testCdataValue() throws Exception {
        Configuration config = _cfg_puller.pull(CDATA_VALUE_XML, getClass());
        Assert.assertEquals("test-simple-value", config.getFirstChild("simple").getValue());
        Assert.assertEquals("test-complex-value", config.getFirstChild("complex").getValue());
    }
}

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

package org.switchyard.transform.internal.smooks;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.v1.V1SmooksTransformModel;
import org.switchyard.transform.smooks.internal.SmooksTransformFactory;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class XMLBindingTransformerTest {

    @Test
    public void test_xml2java() throws IOException, SAXException {
        Transformer transformer = getTransformer("sw-config-xmlb-01.xml");

        // XML to Java
        Person person = (Person) transformer.transform("<person name='Max' age='50' />");
        Assert.assertEquals("Max", person.getName());
        Assert.assertEquals(50, person.getAge());
    }

    @Test
    public void test_java2xml() throws IOException, SAXException {
        Transformer transformer = getTransformer("sw-config-xmlb-02.xml");

        // Java to XML
        Person person = new Person().setName("George").setAge(80);
        String xml = (String) transformer.transform(person);
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual("<person name='George' age='80' />", xml);
    }

    @Test
    public void test_invalid_xml2java_config() throws IOException, SAXException {
        V1SmooksTransformModel model = (V1SmooksTransformModel) new V1SmooksTransformModel().setTransformType("XML2JAVA").setFrom(new QName("a")).setTo(new QName("b"));
        model.setConfig("/org/switchyard/transform/internal/smooks/smooks-config-01.xml");
        try {
            SmooksTransformFactory.newTransformer(model);
        } catch (RuntimeException e) {
            Assert.assertEquals("Invalid XML2JAVA binding configuration.  No <jb:bean> configurations found.", e.getMessage());
        }
    }

    @Test
    public void test_invalid_java2xml_config() throws IOException, SAXException {
        V1SmooksTransformModel model = (V1SmooksTransformModel) new V1SmooksTransformModel().setTransformType("JAVA2XML").setFrom(new QName("a")).setTo(new QName("b"));
        model.setConfig("/org/switchyard/transform/internal/smooks/smooks-config-01.xml");
        try {
            SmooksTransformFactory.newTransformer(model);
        } catch (RuntimeException e) {
            Assert.assertEquals("Invalid JAVA2XML binding configuration.  No <jb:bean> configurations found.", e.getMessage());
        }
    }

    @Test
    public void test_java2xml_invalid_type() throws IOException, SAXException {
        Transformer transformer = getTransformer("sw-config-xmlb-02.xml");

        try {
            transformer.transform("wrong type");
        } catch (RuntimeException e) {
            Assert.assertEquals("Cannot transform to XML.  Input type is 'java.lang.String' but should be 'org.switchyard.transform.internal.smooks.Person'.", e.getMessage());
        }
    }

    private Transformer getTransformer(String config) throws IOException {
        InputStream swConfigStream = Classes.getResourceAsStream(config, getClass());

        if (swConfigStream == null) {
            Assert.fail("null config stream.");
        }

        SwitchYardModel switchyardConfig = new ModelResource<SwitchYardModel>().pull(swConfigStream);
        TransformsModel transforms = switchyardConfig.getTransforms();

        V1SmooksTransformModel transformModel = (V1SmooksTransformModel) transforms.getTransforms().get(0);

        if (transformModel == null) {
            Assert.fail("No smooks config.");
        }

        return SmooksTransformFactory.newTransformer(transformModel);
    }
}

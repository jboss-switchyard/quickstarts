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

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.Java2XmlTransformModel;
import org.switchyard.transform.config.model.Xml2JavaTransformModel;
import org.switchyard.transform.config.model.v1.V1Java2XmlTransformModel;
import org.switchyard.transform.config.model.v1.V1Xml2JavaTransformModel;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;

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
        Xml2JavaTransformModel model = (Xml2JavaTransformModel) new V1Xml2JavaTransformModel().setFrom(new QName("a")).setTo(new QName("b"));
        model.setConfig("/org/switchyard/transform/internal/smooks/smooks-config-01.xml");
        try {
            getTransformer(model);
        } catch (RuntimeException e) {
            Assert.assertEquals("Invalid XML2JAVA binding configuration.  No <jb:bean> configurations found.", e.getMessage());
        }
    }

    @Test
    public void test_invalid_java2xml_config() throws IOException, SAXException {
        Java2XmlTransformModel model = (Java2XmlTransformModel) new V1Java2XmlTransformModel().setFrom(new QName("a")).setTo(new QName("b"));
        model.setConfig("/org/switchyard/transform/internal/smooks/smooks-config-01.xml");
        try {
            getTransformer(model);
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
        InputStream swConfigStream = getClass().getResourceAsStream(config);

        if (swConfigStream == null) {
            Assert.fail("null config stream.");
        }

        SwitchYardModel switchyardConfig = (SwitchYardModel) new ModelResource().pull(swConfigStream);
        TransformsModel transforms = switchyardConfig.getTransforms();

        TransformModel transformModel = transforms.getTransforms().get(0);

        if (transformModel == null) {
            Assert.fail("No smooks config.");
        }

        return getTransformer(transformModel);
    }

    private Transformer getTransformer(TransformModel transformModel) {
        Transformer transformer;

        if (transformModel instanceof Xml2JavaTransformModel) {
            transformer = SmooksTransformFactory.newTransformer((Xml2JavaTransformModel) transformModel);
        } else {
            transformer = SmooksTransformFactory.newTransformer((Java2XmlTransformModel)transformModel);
        }

        if (!(transformer instanceof XMLBindingTransformer)) {
            Assert.fail("Not an instance of XMLBindingTransformer.");
        }

        return transformer;
    }
}

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

package org.switchyard.transform.internal.smooks;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.transform.AbstractTransformerTestCase;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.TransformNamespace;
import org.switchyard.transform.config.model.v1.V1SmooksTransformModel;
import org.switchyard.transform.smooks.internal.SmooksTransformFactory;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class XMLBindingTransformerTest extends AbstractTransformerTestCase {

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
        V1SmooksTransformModel model = (V1SmooksTransformModel)new V1SmooksTransformModel(TransformNamespace.DEFAULT.uri()).setTransformType("XML2JAVA").setFrom(new QName("a")).setTo(new QName("b"));
        model.setConfig("/org/switchyard/transform/internal/smooks/smooks-config-01.xml");
        try {
            new SmooksTransformFactory().newTransformer(null, model);
        } catch (RuntimeException e) {
        	boolean exceptionMatch = e.getMessage().contains("SWITCHYARD016812");
            Assert.assertTrue(exceptionMatch);
        }
    }

    @Test
    public void test_invalid_java2xml_config() throws IOException, SAXException {
        V1SmooksTransformModel model = (V1SmooksTransformModel)new V1SmooksTransformModel(TransformNamespace.DEFAULT.uri()).setTransformType("JAVA2XML").setFrom(new QName("a")).setTo(new QName("b"));
        model.setConfig("/org/switchyard/transform/internal/smooks/smooks-config-01.xml");
        try {
            new SmooksTransformFactory().newTransformer(null, model);
        } catch (RuntimeException e) {
        	boolean exceptionMatch = e.getMessage().contains("SWITCHYARD016812");
        	Assert.assertTrue(exceptionMatch);
        }
    }

    @Test
    public void test_java2xml_invalid_type() throws IOException, SAXException {
        Transformer transformer = getTransformer("sw-config-xmlb-02.xml");

        try {
            transformer.transform("wrong type");
        } catch (RuntimeException e) {
        	boolean exceptionMatch = e.getMessage().contains("SWITCHYARD016806");
        	Assert.assertTrue(exceptionMatch);
        }
    }
}
